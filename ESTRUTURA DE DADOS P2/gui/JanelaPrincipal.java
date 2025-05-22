package gui;

import simulador.*;
import model.CaminhaoPequeno;
import model.Zona;
import estruturas.Fila;
import estruturas.Lista;
import config.Configuracao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class JanelaPrincipal extends JFrame {
    private Simulador simulador;
    private PainelSimulacao painelSimulacao;
    private JTextArea logEventos;
    private JPanel painelEstatisticas;
    private JPanel painelControles;
    private Timer timer;
    private boolean pausado;
    private Configuracao config;

    public JanelaPrincipal(Simulador simulador, Configuracao config) {
        this.simulador = simulador;
        this.config = config;
        this.pausado = true;
        configurarJanela();
        configurarComponentes();
        configurarTimer();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarLayout();
            }
        });
    }

    private void configurarJanela() {
        setTitle("Simulador de Coleta de Resíduos - Teresina");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));
    }

    private void configurarComponentes() {
        painelSimulacao = new PainelSimulacao(simulador);
        painelSimulacao.setBackground(new Color(245, 245, 245));
        add(painelSimulacao, BorderLayout.CENTER);

        painelControles = new JPanel();
        painelControles.setBackground(new Color(50, 205, 50));
        configurarControles();
        add(painelControles, BorderLayout.NORTH);

        logEventos = new JTextArea(8, 50);
        logEventos.setEditable(false);
        logEventos.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(logEventos);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        add(scrollLog, BorderLayout.SOUTH);

        painelEstatisticas = new JPanel();
        painelEstatisticas.setLayout(new BoxLayout(painelEstatisticas, BoxLayout.Y_AXIS));
        painelEstatisticas.setBackground(new Color(240, 248, 255));
        JScrollPane scrollEstatisticas = new JScrollPane(painelEstatisticas);
        scrollEstatisticas.setPreferredSize(new Dimension(300, 0));
        scrollEstatisticas.setBorder(BorderFactory.createTitledBorder("Dashboard"));
        add(scrollEstatisticas, BorderLayout.EAST);

        atualizarEstatisticas();
    }

    private void configurarControles() {
        painelControles.setLayout(new FlowLayout(FlowLayout.LEFT));
        Font botaoFonte = new Font("Arial", Font.BOLD, 12);

        JButton btnIniciar = new JButton("Iniciar");
        JButton btnPausar = new JButton("Pausar");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCarregar = new JButton("Carregar");
        JButton btnFinalizar = new JButton("Finalizar");
        JButton btnReiniciar = new JButton("Reiniciar");
        JButton btnConfigurar = new JButton("Configurar");

        JButton[] botoes = {btnIniciar, btnPausar, btnSalvar,  btnFinalizar,  btnConfigurar};
        for (JButton botao : botoes) {
            botao.setFont(botaoFonte);
            botao.setBackground(new Color(135, 206, 250));
            botao.setForeground(Color.BLACK);
            botao.setFocusPainted(false);
            botao.setMargin(new Insets(5, 10, 5, 10));
        }

        btnIniciar.addActionListener(e -> iniciarSimulacao());
        btnPausar.addActionListener(e -> pausarSimulacao());
        btnSalvar.addActionListener(e -> salvarSimulacao());
        btnFinalizar.addActionListener(e -> finalizarSimulacao());
        btnConfigurar.addActionListener(e -> abrirPainelConfiguracao());

        painelControles.add(btnIniciar);
        painelControles.add(btnPausar);
        painelControles.add(btnSalvar);
        painelControles.add(btnCarregar);
        painelControles.add(btnFinalizar);
        painelControles.add(btnReiniciar);
        painelControles.add(btnConfigurar);
    }

    private void configurarTimer() {
        timer = new Timer(1000, (ActionEvent e) -> {
            if (!pausado) {
                simulador.avancarSimulacao(60);
                painelSimulacao.repaint();
                atualizarEstatisticas();
                if (simulador.isChuva()) {
                    logEventos.append("Condição climática: Chuva\n");
                }
                // Exibe tempo no formato HH:MM
                int minutosTotais = (int) simulador.getTempoSimulacao();
                int horas = minutosTotais / 60;
                int minutos = minutosTotais % 60;
                logEventos.append(String.format("Tempo: %02d:%02d | Lixo coletado: %.2f t\n",
                        horas, minutos, simulador.getLixoColetadoTotal()));
                Lista<Evento> historico = simulador.getHistoricoEventos();
                if (!historico.estaVazia()) {
                    Evento ultimoEvento = historico.get(historico.tamanho() - 1);
                    logEventos.append(String.format("Evento: %s | Tempo: %02d:%02d | Quantidade: %.2f\n",
                            ultimoEvento.getTipo(), (int) (ultimoEvento.getTempo() / 60), (int) (ultimoEvento.getTempo() % 60), ultimoEvento.getQuantidade()));
                }
                if (simulador.getTempoSimulacao() >= config.getTempoTotalSimulacao()) {
                    finalizarSimulacao();
                }
            }
        });
    }

    private void ajustarLayout() {
        int width = getWidth();
        painelEstatisticas.setPreferredSize(new Dimension(width / 4, 0));
        logEventos.setRows(Math.max(5, getHeight() / 100));
        painelSimulacao.revalidate();
        painelEstatisticas.revalidate();
        revalidate();
        repaint();
    }

    private void iniciarSimulacao() {
        pausado = false;
        timer.start();
        logEventos.append("Simulação iniciada\n");
    }

    private void pausarSimulacao() {
        pausado = true;
        timer.stop();
        logEventos.append("Simulação pausada\n");
    }

    private void salvarSimulacao() {
        try (FileWriter writer = new FileWriter("simulacao.txt")) {
            writer.write("=== RELATÓRIO DE SIMULAÇÃO ===\n\n");
            writer.write(String.format("Lixo Coletado Total: %.2f toneladas%n", simulador.getLixoColetadoTotal()));
            writer.write(String.format("Lixo Coletado por Caminhões Pequenos: %.2f toneladas%n", simulador.getLixoColetadoPequenos()));
            writer.write(String.format("Caminhões Grandes Acionados: %d%n", simulador.getCaminhoesGrandesAcionados()));
            writer.write(String.format("Tempo Médio de Espera: %.2f minutos%n", simulador.getTempoMedioEspera()));
            writer.write("\n=== ESTATÍSTICAS POR ZONA ===\n");
            Zona[] zonas = simulador.getZonas();
            double[] lixoPorZona = simulador.getLixoColetadoPorZona();
            for (int i = 0; i < zonas.length; i++) {
                writer.write(String.format("Zona %-10s: %.2f toneladas coletadas%n", zonas[i].getNome(), lixoPorZona[i]));
            }
            // Inclui estatísticas das estações
            writer.write("\n=== ESTATÍSTICAS DAS ESTAÇÕES ===\n");
            writer.write(simulador.getEstatisticasEstacoes());

            writer.write("\n=== ESTATÍSTICAS DE LIXO ACUMULADO POR ZONA ===\n");
             for (int i = 0; i < zonas.length; i++) {
            writer.write(String.format("Zona %-10s: %.2f toneladas acumuladas%n", zonas[i].getNome(), zonas[i].getLixoAcumulado()));
        }



            writer.write("\n=== ESTATÍSTICAS POR CAMINHÃO PEQUENO ===\n");
            Fila<CaminhaoPequeno> caminhoesP = simulador.getCaminhoesPequenos();
            for (int i = 0; i < caminhoesP.tamanho(); i++) {
                CaminhaoPequeno caminhao = caminhoesP.desenfileirar();
                writer.write(String.format("Caminhão Pequeno %d:%n", i + 1));
                writer.write(String.format("  Lixo Coletado por Zona:%n"));
                double[] lixoZona = caminhao.getLixoPorZona();
                String[] nomesZonas = {"Sul", "Norte", "Centro", "Leste", "Sudeste"};
                for (int j = 0; j < lixoZona.length; j++) {
                    writer.write(String.format("    %s: %.2f t%n", nomesZonas[j], lixoZona[j]));
                }
                writer.write(String.format("  Viagens Realizadas: %d%n", caminhao.getViagensRealizadas()));
                writer.write(String.format("  Tempo Total de Viagem: %.2f min%n", caminhao.getTempoTotalViagem()));
                writer.write(String.format("  Tempo Total em Operação: %.2f min%n", caminhao.getTempoTotalOperacao()));
                caminhoesP.enfileirar(caminhao);
            }

            writer.write("\n=== HISTÓRICO DE EVENTOS ===\n");
            Lista<Evento> historico = simulador.getHistoricoEventos();
            if (historico.estaVazia()) {
                writer.write("Nenhum evento registrado\n");
            } else {
                for (Evento evento : historico) {
                    int horas = (int) (evento.getTempo() / 60);
                    int minutos = (int) (evento.getTempo() % 60);
                    String detalhes = String.format(
                            "Evento: %-15s | Tempo: %02d:%02d | Quantidade: %.2f | ",
                            evento.getTipo(), horas, minutos, evento.getQuantidade());
                    if (evento.getZona() != null) {
                        detalhes += "Zona: " + evento.getZona().getNome() + " | ";
                    }
                    if (evento.getCaminhaoPequeno() != null) {
                        detalhes += "Caminhão Pequeno | ";
                    }
                    if (evento.getCaminhaoGrande() != null) {
                        detalhes += "Caminhão Grande | ";
                    }
                    if (evento.getEstacao() != null) {
                        detalhes += "Estação: " + evento.getEstacao().getNome();
                    }
                    writer.write(detalhes + "\n");
                }
            }

            logEventos.append("Simulação salva em simulacao.txt\n");
        } catch (IOException e) {
            logEventos.append("Erro ao salvar: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

   

    private void finalizarSimulacao() {
        pausado = true;
        timer.stop();
        logEventos.append(String.format("Simulação finalizada. Caminhões grandes acionados: %d\n",
                simulador.getCaminhoesGrandesAcionados()));
    }

    private void reiniciarSimulacao() {
        simulador.reiniciarSimulacao();
        pausado = true;
        timer.stop();
        painelSimulacao.repaint();
        atualizarEstatisticas();
        logEventos.append("Simulação reiniciada\n");
    }

    private void abrirPainelConfiguracao() {
        JDialog dialog = new JDialog(this, "Configurações", true);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JTextField[] campos = new JTextField[17];
        String[] labels = {
                "Lixo Mínimo Sul (t):",
                "Lixo Máximo Sul (t):",
                "Lixo Mínimo Norte (t):",
                "Lixo Máximo Norte (t):",
                "Lixo Mínimo Centro (t):",
                "Lixo Máximo Centro (t):",
                "Lixo Mínimo Leste (t):",
                "Lixo Máximo Leste (t):",
                "Lixo Mínimo Sudeste (t):",
                "Lixo Máximo Sudeste (t):",
                "Nº Caminhões Pequenos:",
                "Limite Mínimo Viagens:",
                "Limite Máximo Viagens:",
                "Tempo Total Simulação (min):",
                "Tolerância Espera Pequeno (min):",
                "Tolerância Espera Grande (min):",
                "Limite Fila Estação:"
        };
        int[] valores = {
                config.getLixoMinimoSul(), config.getLixoMaximoSul(),
                config.getLixoMinimoNorte(), config.getLixoMaximoNorte(),
                config.getLixoMinimoCentro(), config.getLixoMaximoCentro(),
                config.getLixoMinimoLeste(), config.getLixoMaximoLeste(),
                config.getLixoMinimoSudeste(), config.getLixoMaximoSudeste(),
                config.getNumCaminhoesPequenos(), config.getLimiteViagensMin(),
                config.getLimiteViagensMax(), config.getTempoTotalSimulacao(),
                config.getToleranciaEsperaPequeno(), config.getToleranciaEsperaGrande(),
                config.getLimiteFilaEstacao()
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(label);
            campos[i] = new JTextField(String.valueOf(valores[i]));
            campos[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            painel.add(campos[i]);
            painel.add(Box.createVerticalStrut(6));
        }

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalvar.addActionListener(e -> {
            try {
                Properties props = new Properties();
                props.setProperty("lixoMinimoSul", campos[0].getText());
                props.setProperty("lixoMaximoSul", campos[1].getText());
                props.setProperty("lixoMinimoNorte", campos[2].getText());
                props.setProperty("lixoMaximoNorte", campos[3].getText());
                props.setProperty("lixoMinimoCentro", campos[4].getText());
                props.setProperty("lixoMaximoCentro", campos[5].getText());
                props.setProperty("lixoMinimoLeste", campos[6].getText());
                props.setProperty("lixoMaximoLeste", campos[7].getText());
                props.setProperty("lixoMinimoSudeste", campos[8].getText());
                props.setProperty("lixoMaximoSudeste", campos[9].getText());
                props.setProperty("numCaminhoesPequenos", campos[10].getText());
                props.setProperty("limiteViagensMin", campos[11].getText());
                props.setProperty("limiteViagensMax", campos[12].getText());
                props.setProperty("tempoTotalSimulacao", campos[13].getText());
                props.setProperty("toleranciaEsperaPequeno", campos[14].getText());
                props.setProperty("toleranciaEsperaGrande", campos[15].getText());
                props.setProperty("limiteFilaEstacao", campos[16].getText());

                try (FileOutputStream out = new FileOutputStream("config.properties")) {
                props.store(out, "Configurações da Simulação");
                }
                config.carregarConfiguracao("config.properties");

                dialog.dispose();
                logEventos.append("Configurações atualizadas\n");
            } catch (Exception ex) {
                logEventos.append("Erro na configuração: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        painel.add(Box.createVerticalStrut(10));
        painel.add(btnSalvar);

        JScrollPane scroll = new JScrollPane(painel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        dialog.setContentPane(scroll);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void atualizarEstatisticas() {
        painelEstatisticas.removeAll();
        painelEstatisticas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("=== DASHBOARD ===");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        painelEstatisticas.add(titulo);
        painelEstatisticas.add(Box.createVerticalStrut(10));

        int minutosTotais = (int) simulador.getTempoSimulacao();
        int horas = minutosTotais / 60;
        int minutos = minutosTotais % 60;
        adicionarEstatistica("Tempo Simulação", String.format("%02d:%02d", horas, minutos), "");
        adicionarEstatistica("Lixo Coletado Total", simulador.getLixoColetadoTotal(), "ton");
        adicionarEstatistica("Tempo Médio Espera", simulador.getTempoMedioEspera(), "min");
        adicionarEstatistica("Eficiência Caminhão", simulador.getEficienciaCaminhao(), "t/viagem");
        adicionarEstatistica("Tempo Ocioso", simulador.getTempoOcioso(), "min");
        adicionarEstatistica("Caminhões Grandes", simulador.getCaminhoesGrandesAcionados(), "");
        adicionarEstatistica("Emissões CO2", simulador.getEmissoesCO2Total(), "kg");

        JLabel zonasTitulo = new JLabel("Por Zona:");
        zonasTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        painelEstatisticas.add(Box.createVerticalStrut(10));
        painelEstatisticas.add(zonasTitulo);

        for (int i = 0; i < simulador.getZonas().length; i++) {
            adicionarEstatistica(
                    simulador.getZonas()[i].getNome(),
                    simulador.getLixoColetadoPorZona()[i],
                    "ton");
        }

        painelEstatisticas.revalidate();
        painelEstatisticas.repaint();
    }

    private void adicionarEstatistica(String nome, double valor, String unidade) {
        JLabel label = new JLabel(String.format(
                "<html><b>%s:</b> %.2f %s</html>",
                nome, valor, unidade));
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        painelEstatisticas.add(label);
        painelEstatisticas.add(Box.createVerticalStrut(5));
    }

    private void adicionarEstatistica(String nome, int valor, String unidade) {
        JLabel label = new JLabel(String.format(
                "<html><b>%s:</b> %d %s</html>",
                nome, valor, unidade));
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        painelEstatisticas.add(label);
        painelEstatisticas.add(Box.createVerticalStrut(5));
    }

    private void adicionarEstatistica(String nome, String valor, String unidade) {
        JLabel label = new JLabel(String.format(
                "<html><b>%s:</b> %s %s</html>",
                nome, valor, unidade));
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        painelEstatisticas.add(label);
        painelEstatisticas.add(Box.createVerticalStrut(5));
    }
}