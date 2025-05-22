package simulador;

import model.Zona;
import model.EstacaoTransferencia;
import model.CaminhaoPequeno;
import model.CaminhaoGrande;
import config.Configuracao;
import estruturas.Fila;
import estruturas.Lista;
import gui.PainelSimulacao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class Simulador {
    private Configuracao config;
    private Zona[] zonas;
    private EstacaoTransferencia[] estacoes;
    private Fila<CaminhaoPequeno> caminhoesPequenos;
    private Fila<CaminhaoGrande> caminhoesGrandes;
    private Lista<Evento> historicoEventos;
    private double tempoSimulacao;
    private double lixoColetadoTotal;
    private double lixoColetadoPequenos;
    private double lixoColetadoGrandes;
    private double[] lixoColetadoPorZona;
    private double tempoMedioEspera;
    private double tempoOcioso;
    private double emissoesCO2Total;
    private int caminhoesGrandesAcionados;
    private boolean chuva;
    private Random random;
    private PainelSimulacao painelSimulacao;

    public Simulador(Configuracao config, PainelSimulacao painel) {
        this.config = config;
        this.painelSimulacao = painel;
        inicializarSimulacao();
    }

    private void inicializarSimulacao() {
        random = new Random();

        zonas = new Zona[] {
            new Zona("Sul", config.getLixoMinimoSul(), config.getLixoMaximoSul()),
            new Zona("Norte", config.getLixoMinimoNorte(), config.getLixoMaximoNorte()),
            new Zona("Centro", config.getLixoMinimoCentro(), config.getLixoMaximoCentro()),
            new Zona("Leste", config.getLixoMinimoLeste(), config.getLixoMaximoLeste()),
            new Zona("Sudeste", config.getLixoMinimoSudeste(), config.getLixoMaximoSudeste())
        };

        estacoes = new EstacaoTransferencia[] {
            new EstacaoTransferencia("Estação A", new String[]{"Sul", "Sudeste"}),
            new EstacaoTransferencia("Estação B", new String[]{"Norte", "Centro", "Leste"})
        };

        caminhoesPequenos = new Fila<>();
        for (int i = 0; i < config.getNumCaminhoesPequenos(); i++) {
            double capacidade = config.getCapacidadesCaminhoesPequenos()[random.nextInt(config.getCapacidadesCaminhoesPequenos().length)];
            caminhoesPequenos.enfileirar(new CaminhaoPequeno(capacidade, config.getLimiteViagensMin(), config.getLimiteViagensMax()));
        }

        caminhoesGrandes = new Fila<>();
        for (int i = 0; i < config.getNumCaminhoesGrandesInicial(); i++) {
            caminhoesGrandes.enfileirar(new CaminhaoGrande(config.getCapacidadeCaminhaoGrande(), config.getToleranciaEsperaGrande()));
        }

        historicoEventos = new Lista<>();
        lixoColetadoPorZona = new double[zonas.length];
        reiniciarSimulacao();
    }

    public void reiniciarSimulacao() {
        tempoSimulacao = 0;
        lixoColetadoTotal = 0;
        lixoColetadoPequenos = 0;
        lixoColetadoGrandes = 0;
        tempoMedioEspera = 0;
        tempoOcioso = 0;
        emissoesCO2Total = 0;
        caminhoesGrandesAcionados = config.getNumCaminhoesGrandesInicial();
        chuva = false;
        historicoEventos.limpar();

        // Define posições fixas para as zonas
        zonas[0].setPosicao(200, 200); // Sul
        zonas[1].setPosicao(200, 400); // Norte
        zonas[2].setPosicao(500, 300); // Centro
        zonas[3].setPosicao(700, 200); // Leste
        zonas[4].setPosicao(700, 400); // Sudeste

        // Define posições fixas para as estações
        estacoes[0].setPosicao(300, 500); // Estação A
        estacoes[1].setPosicao(600, 500); // Estação B

        for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
            CaminhaoPequeno caminhao = caminhoesPequenos.desenfileirar();
            caminhao.setPosicao(50 + random.nextInt(50), 50 + random.nextInt(50));
            caminhoesPequenos.enfileirar(caminhao);
        }

        for (int i = 0; i < caminhoesGrandes.tamanho(); i++) {
            CaminhaoGrande caminhao = caminhoesGrandes.desenfileirar();
            caminhao.setPosicao(50 + random.nextInt(50), 50 + random.nextInt(50));
            caminhoesGrandes.enfileirar(caminhao);
        }

        if (painelSimulacao != null) {
            painelSimulacao.atualizar();
        }
    }

 public void avancarSimulacao(double deltaTempo) {
    tempoSimulacao += deltaTempo;

    System.out.println("TempoSimulacao=" + tempoSimulacao + ", CaminhoesPequenos=" + caminhoesPequenos.tamanho() +
                       ", CaminhoesGrandesAcionados=" + caminhoesGrandesAcionados);

    // Geração de lixo nas zonas
    for (Zona zona : zonas) {
        zona.gerarLixo();
    }

    // Operação de caminhões pequenos
    for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
        CaminhaoPequeno caminhao = caminhoesPequenos.getElemento(i);
        if (caminhao.podeRealizarViagem((int)tempoSimulacao)) {
            if (caminhao.getDestino() == null) {
                Zona zonaDestino = escolherZonaParaColeta(caminhao);
                if (zonaDestino != null) {
                    caminhao.setDestino(zonaDestino.getNome());
                    double tempoViagem = calcularTempoViagem(caminhao.getX(), caminhao.getY(), zonaDestino.getX(), zonaDestino.getY());
                    caminhao.moverPara(zonaDestino.getX(), zonaDestino.getY(), tempoViagem);
                    double quantidade = zonaDestino.coletarLixo(caminhao.getCapacidade() - caminhao.getCargaAtual());
                    caminhao.carregar(quantidade, getIndiceZona(zonaDestino));
                    lixoColetadoTotal += quantidade;
                    lixoColetadoPequenos += quantidade;
                    lixoColetadoPorZona[getIndiceZona(zonaDestino)] += quantidade;
                    registrarEvento("Coleta", tempoSimulacao, quantidade, zonaDestino, caminhao, null, null);
                    System.out.println("Caminhão " + caminhao.hashCode() + ": Coletou " + quantidade + " t na zona " + 
                                       zonaDestino.getNome() + ", Viagens=" + caminhao.getViagensRealizadas() + "/" + 
                                       caminhao.getLimiteViagensDiarias() + ", Carga=" + caminhao.getCargaAtual() + "/" + 
                                       caminhao.getCapacidade() + ", TempoViagem=" + tempoViagem);
                } else {
                    System.out.println("Caminhão " + caminhao.hashCode() + ": Nenhuma zona com lixo disponível");
                }
            } else if (caminhao.estaCheio() || caminhao.getTempoEmRota() >= config.getTempoMaximoRota()) {
                EstacaoTransferencia estacao = getEstacaoPorNome(caminhao.getEstacaoAtribuida());
                if (estacao.getFilaCaminhoesPequenos().tamanho() < config.getLimiteFilaEstacao()) {
                    double tempoViagem = calcularTempoViagem(caminhao.getX(), caminhao.getY(), estacao.getX(), estacao.getY());
                    caminhao.moverPara(estacao.getX(), estacao.getY(), tempoViagem);
                    estacao.adicionarCaminhaoPequeno(caminhao, tempoViagem);
                    double quantidade = caminhao.descarregar();
                    estacao.adicionarLixoTransferido(quantidade);
                    caminhao.setDestino(null);
                    caminhao.setPosicao(estacao.getX(), estacao.getY());
                    caminhao.setTempoEsperaNaFila(0);
                    registrarEvento("Transferência", tempoSimulacao, quantidade, null, caminhao, null, estacao);
                    System.out.println("Caminhão " + caminhao.hashCode() + ": Descarregou " + quantidade + " t na " + 
                                       estacao.getNome() + ", Viagens=" + caminhao.getViagensRealizadas() + "/" + 
                                       caminhao.getLimiteViagensDiarias() + ", Carga=" + caminhao.getCargaAtual() + 
                                       ", TempoViagem=" + tempoViagem + ", TempoEspera=" + caminhao.getTempoEsperaNaFila());
                } else {
                    System.out.println("Caminhão " + caminhao.hashCode() + ": Fila cheia na " + estacao.getNome());
                }
            }
        } else {
            System.out.println("Caminhão " + caminhao.hashCode() + ": Não pode realizar viagem - " +
                               "Viagens=" + caminhao.getViagensRealizadas() + "/" + caminhao.getLimiteViagensDiarias() +
                               ", Carga=" + caminhao.getCargaAtual() + "/" + caminhao.getCapacidade() +
                               ", Tempo=" + tempoSimulacao + ", Horario=" + caminhao.getHorarioInicio() + "-" + 
                               caminhao.getHorarioFim());
        }
    }

    // Processamento das filas nas estações
    for (EstacaoTransferencia estacao : estacoes) {
        Fila<CaminhaoPequeno> filaPeq = estacao.getFilaCaminhoesPequenos();
        filaPeq.incrementarEspera(deltaTempo);
        while (!filaPeq.estaVazia()) {
            CaminhaoPequeno head = filaPeq.getElemento(0);
            if (head.getTempoEsperaNaFila() >= config.getLimiteEsperaPequeno() &&
                caminhoesGrandesAcionados < config.getNumCaminhoesGrandesInicial() * 3) {
                // Promove para caminhão grande when wait time exceeds threshold
                filaPeq.desenfileirar();
                head.setTempoEsperaNaFila(0);
                head.setDestino(null);
                head.setPosicao(estacao.getX(), estacao.getY());
                CaminhaoGrande cg = new CaminhaoGrande(config.getCapacidadeCaminhaoGrande(), config.getToleranciaEsperaGrande());
                estacao.getFilaCaminhoesGrandes().enfileirar(cg);
                caminhoesGrandesAcionados++;
                registrarEvento("Promoção", tempoSimulacao, 0, null, head, cg, estacao);
                System.out.println("Caminhão " + head.hashCode() + ": Promovido um caminhão grande na " + 
                                   estacao.getNome() + ", TotalGrandes=" + caminhoesGrandesAcionados + 
                                   ", LixoTransferido=" + estacao.getLixoTransferido());
            } else {
                // Processa o caminhão pequeno normalmente
                CaminhaoPequeno caminhao = filaPeq.desenfileirar();
                caminhao.setDestino(null);
                caminhao.setTempoEsperaNaFila(0);
                caminhao.setPosicao(estacao.getX(), estacao.getY());
                tempoMedioEspera = (
                    tempoMedioEspera * (estacao.getCaminhoesPequenosProcessados() - 1) + caminhao.getTempoEmRota()
                ) / estacao.getCaminhoesPequenosProcessados();
                System.out.println("Caminhão " + caminhao.hashCode() + ": Processado na " + estacao.getNome() + 
                                   ", Viagens=" + caminhao.getViagensRealizadas() + "/" + 
                                   caminhao.getLimiteViagensDiarias() + ", Carga=" + caminhao.getCargaAtual() + 
                                   ", LixoTransferido=" + estacao.getLixoTransferido());
            }
        }

        // Processa caminhões grandes na fila para descarregar no aterro
        Fila<CaminhaoGrande> filaGrande = estacao.getFilaCaminhoesGrandes();
        if (!filaGrande.estaVazia()) {
            filaGrande.incrementarEspera(deltaTempo);
            CaminhaoGrande caminhao = filaGrande.desenfileirar();
            double tempoViagem = calcularTempoViagem(caminhao.getX(), caminhao.getY(), 800, 600);
            caminhao.moverPara(800, 600, tempoViagem);
            double quantidade = caminhao.descarregarNoAterro();
            lixoColetadoGrandes += quantidade;
            registrarEvento("Descarregamento Aterro", tempoSimulacao, quantidade, null, null, caminhao, null);
            System.out.println("Caminhão Grande " + caminhao.hashCode() + ": Descarregou " + quantidade + " t no aterro");
        }
    }

    // Verificar necessidade de novos caminhões grandes
    for (EstacaoTransferencia estacao : estacoes) {
        if (estacao.getLixoTransferido() > 20 && // Lowered threshold
            estacao.getFilaCaminhoesPequenos().tamanho() >= config.getLimiteFilaEstacao() * 0.8 && // Near full queue
            caminhoesGrandesAcionados < config.getNumCaminhoesGrandesInicial() * 3) {
            acionarCaminhaoGrande(estacao);
            System.out.println("Acionado Caminhão Grande na " + estacao.getNome() + 
                               ", TotalGrandes=" + caminhoesGrandesAcionados + 
                               ", LixoTransferido=" + estacao.getLixoTransferido());
        }
    }

    if (painelSimulacao != null) {
        painelSimulacao.atualizar();
    }
}

    private void acionarCaminhaoGrande(EstacaoTransferencia estacao) {
        CaminhaoGrande caminhao = new CaminhaoGrande(config.getCapacidadeCaminhaoGrande(), config.getToleranciaEsperaGrande());
        double quantidade = Math.min(estacao.getLixoTransferido(), caminhao.getCapacidade());
        caminhao.carregar(quantidade);
        estacao.adicionarLixoTransferido(-quantidade);
        double tempoViagem = calcularTempoViagem(estacao.getX(), estacao.getY(), 800, 600);
        caminhao.moverPara(800, 600, tempoViagem);
        caminhoesGrandes.enfileirar(caminhao);
        caminhoesGrandesAcionados++;
        registrarEvento("Acionamento Caminhão Grande", tempoSimulacao, quantidade, null, null, caminhao, estacao);
    }

    private double calcularTempoViagem(double x1, double y1, double x2, double y2) {
        double distancia = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        boolean pico = isHorarioPico((int)tempoSimulacao);
        double tempoMin = pico ? config.getTempoViagemPicoMin() : config.getTempoViagemForaPicoMin();
        double tempoMax = pico ? config.getTempoViagemPicoMax() : config.getTempoViagemForaPicoMax();
        return (distancia / 100) * (tempoMin + random.nextDouble() * (tempoMax - tempoMin));
    }

    private Zona escolherZonaParaColeta(CaminhaoPequeno caminhao) {
        EstacaoTransferencia estacao = getEstacaoPorNome(caminhao.getEstacaoAtribuida());
        Zona melhorZona = null;
        double menorDistancia = Double.MAX_VALUE;
        for (String zonaNome : estacao.getZonasAtendidas()) {
            for (Zona zona : zonas) {
                if (zona.getNome().equals(zonaNome) && zona.getLixoAcumulado() > 0) {
                    double distancia = Math.sqrt(Math.pow(caminhao.getX() - zona.getX(), 2) + Math.pow(caminhao.getY() - zona.getY(), 2));
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        melhorZona = zona;
                    }
                }
            }
        }
        return melhorZona;
    }

    private EstacaoTransferencia getEstacaoPorNome(String nome) {
        for (EstacaoTransferencia estacao : estacoes) {
            if (estacao.getNome().equals(nome)) {
                return estacao;
            }
        }
        return estacoes[0];
    }

    private int getIndiceZona(Zona zona) {
        for (int i = 0; i < zonas.length; i++) {
            if (zonas[i] == zona) {
                return i;
            }
        }
        return -1;
    }

    private void registrarEvento(String tipo, double tempo, double quantidade, Zona zona,
                                CaminhaoPequeno caminhaoP, CaminhaoGrande caminhaoG, EstacaoTransferencia estacao) {
        Evento evento = new Evento(tipo, tempo, quantidade);
        if (zona != null) evento.setZona(zona);
        if (caminhaoP != null) evento.setCaminhaoPequeno(caminhaoP);
        if (caminhaoG != null) evento.setCaminhaoGrande(caminhaoG);
        if (estacao != null) evento.setEstacao(estacao);
        historicoEventos.adicionar(evento);
    }

    public void carregarSimulacao(String arquivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            reiniciarSimulacao();
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.startsWith("Lixo Coletado Total:")) {
                    lixoColetadoTotal = Double.parseDouble(linha.split(":")[1].trim().split(" ")[0]);
                } else if (linha.startsWith("Caminhões Grandes Acionados:")) {
                    caminhoesGrandesAcionados = Integer.parseInt(linha.split(":")[1].trim());
                }
            }
            if (painelSimulacao != null) {
                painelSimulacao.atualizar();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar simulação: " + e.getMessage());
        }
    }

    public String getEstatisticasEstacoes() {
        StringBuilder sb = new StringBuilder();
        for (EstacaoTransferencia estacao : estacoes) {
            sb.append(String.format("Estação %s:%n", estacao.getNome()));
            sb.append(String.format("  Lixo Transferido: %.2f t%n", estacao.getLixoTransferido()));
            sb.append(String.format("  Caminhões Pequenos Processados: %d%n", estacao.getCaminhoesPequenosProcessados()));
            sb.append(String.format("  Caminhões Grandes Processados: %d%n", estacao.getCaminhoesGrandesProcessados()));
        }
        return sb.toString();
    }

    public boolean isHorarioPico(int tempo) {
        int hora = (tempo / 60) % 24;
        for (int i = 0; i < config.getHorariosPicoInicio().length; i++) {
            int inicio = config.getHorariosPicoInicio()[i];
            int fim = config.getHorariosPicoFim()[i];
            if (hora >= inicio && hora < fim) {
                return true;
            }
        }
        return false;
    }

    public double getEficienciaCaminhao() {
        int totalViagens = 0;
        for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
            totalViagens += caminhoesPequenos.getElemento(i).getViagensRealizadas();
        }
        return totalViagens > 0 ? lixoColetadoPequenos / totalViagens : 0;
    }

    public String getTempoSimulacaoFormatado() {
        int horas = (int) (tempoSimulacao / 60);
        int minutos = (int) (tempoSimulacao % 60);
        return String.format("%02d:%02d", horas, minutos);
    }

    public void setPainelSimulacao(PainelSimulacao painel) {
        this.painelSimulacao = painel;
    }

    public Zona[] getZonas() { return zonas; }
    public EstacaoTransferencia[] getEstacoes() { return estacoes; }
    public Fila<CaminhaoPequeno> getCaminhoesPequenos() { return caminhoesPequenos; }
    public Fila<CaminhaoGrande> getCaminhoesGrandes() { return caminhoesGrandes; }
    public Lista<Evento> getHistoricoEventos() { return historicoEventos; }
    public double getTempoSimulacao() { return tempoSimulacao; }
    public double getLixoColetadoTotal() { return lixoColetadoTotal; }
    public double getLixoColetadoPequenos() { return lixoColetadoPequenos; }
    public double getLixoColetadoGrandes() { return lixoColetadoGrandes; }
    public double[] getLixoColetadoPorZona() { return lixoColetadoPorZona; }
    public double getTempoMedioEspera() { return tempoMedioEspera; }
    public double getTempoOcioso() { return tempoOcioso; }
    public double getEmissoesCO2Total() { return emissoesCO2Total; }
    public int getCaminhoesGrandesAcionados() { return caminhoesGrandesAcionados; }
    public boolean isChuva() { return chuva; }
}