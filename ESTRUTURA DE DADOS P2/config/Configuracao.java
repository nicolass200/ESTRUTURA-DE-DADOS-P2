package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracao {
    // Parâmetros de geração de lixo por zona
    private int lixoMinimoSul, lixoMaximoSul;
    private int lixoMinimoNorte, lixoMaximoNorte;
    private int lixoMinimoCentro, lixoMaximoCentro;
    private int lixoMinimoLeste, lixoMaximoLeste;
    private int lixoMinimoSudeste, lixoMaximoSudeste;

    // Caminhões pequenos
    private int numCaminhoesPequenos;
    private double[] capacidadesCaminhoesPequenos = {2.0, 4.0, 8.0, 10.0};
    private int limiteViagensMin, limiteViagensMax;
    private int tempoViagemPicoMin, tempoViagemPicoMax;
    private int tempoViagemForaPicoMin, tempoViagemForaPicoMax;
    private double limiteEsperaPequeno;

    // Caminhões grandes
    private int numCaminhoesGrandesInicial;
    private int capacidadeCaminhaoGrande;
    private int toleranciaEsperaPequeno, toleranciaEsperaGrande;
    private int limiteFilaEstacao;

    // Simulação e horários de pico
    private int tempoTotalSimulacao;
    private int[] horariosPicoInicio = {6, 17};
    private int[] horariosPicoFim    = {9, 20};
    private int impactoChuva;
    private int tempoMaximoRota;

    /** Construtor: carrega automaticamente de config.properties */
    public Configuracao() {
        carregarConfiguracao("config.properties");
    }

    /** Carrega (ou recarrega) todas as propriedades do arquivo indicado */
    public void carregarConfiguracao(String arquivo) {
        Properties props = new Properties();
        InputStream input = null;
        try {
            File f = new File(arquivo);
            if (f.exists()) {
                input = new FileInputStream(f);
            } else {
                input = getClass().getClassLoader().getResourceAsStream(arquivo);
                if (input == null) {
                    throw new FileNotFoundException("Não achou " + arquivo);
                }
            }
            props.load(input);

            // Zonas
            lixoMinimoSul     = validarParametroInt(props, "lixoMinimoSul",     20,  "Zona Sul mínimo",     0, Integer.MAX_VALUE);
            lixoMaximoSul     = validarParametroInt(props, "lixoMaximoSul",     50,  "Zona Sul máximo",     lixoMinimoSul, Integer.MAX_VALUE);
            lixoMinimoNorte   = validarParametroInt(props, "lixoMinimoNorte",   20,  "Zona Norte mínimo",   0, Integer.MAX_VALUE);
            lixoMaximoNorte   = validarParametroInt(props, "lixoMaximoNorte",   50, "Zona Norte máximo",   lixoMinimoNorte, Integer.MAX_VALUE);
            lixoMinimoCentro  = validarParametroInt(props, "lixoMinimoCentro",  20, "Zona Centro mínimo",  0, Integer.MAX_VALUE);
            lixoMaximoCentro  = validarParametroInt(props, "lixoMaximoCentro",  50, "Zona Centro máximo",  lixoMinimoCentro, Integer.MAX_VALUE);
            lixoMinimoLeste   = validarParametroInt(props, "lixoMinimoLeste",   20,  "Zona Leste mínimo",   0, Integer.MAX_VALUE);
            lixoMaximoLeste   = validarParametroInt(props, "lixoMaximoLeste",   50, "Zona Leste máximo",   lixoMinimoLeste, Integer.MAX_VALUE);
            lixoMinimoSudeste = validarParametroInt(props, "lixoMinimoSudeste", 20,  "Zona Sudeste mínimo", 0, Integer.MAX_VALUE);
            lixoMaximoSudeste = validarParametroInt(props, "lixoMaximoSudeste", 50, "Zona Sudeste máximo", lixoMinimoSudeste, Integer.MAX_VALUE);

            // Caminhões pequenos
            numCaminhoesPequenos       = validarParametroInt(props, "numCaminhoesPequenos",   40, "Qtd. caminhões pequenos", 1, 100);
            limiteViagensMin           = validarParametroInt(props, "limiteViagensMin",        5, "Viagens mínimas",         1, 50);
            limiteViagensMax           = validarParametroInt(props, "limiteViagensMax",        7, "Viagens máximas",         limiteViagensMin, 50);
            tempoViagemPicoMin         = validarParametroInt(props, "tempoViagemPicoMin",     20, "Pico tempo mínimo",       0, Integer.MAX_VALUE);
            tempoViagemPicoMax         = validarParametroInt(props, "tempoViagemPicoMax",     40, "Pico tempo máximo",       tempoViagemPicoMin, Integer.MAX_VALUE);
            tempoViagemForaPicoMin     = validarParametroInt(props, "tempoViagemForaPicoMin", 10, "Fora pico mínimo",        0, Integer.MAX_VALUE);
            tempoViagemForaPicoMax     = validarParametroInt(props, "tempoViagemForaPicoMax", 20, "Fora pico máximo",        tempoViagemForaPicoMin, Integer.MAX_VALUE);

            // Caminhões grandes e estações
            numCaminhoesGrandesInicial = validarParametroInt(props, "numCaminhoesGrandesInicial", 2,  "Grandes iniciais",     1, 50);
            capacidadeCaminhaoGrande   = validarParametroInt(props, "capacidadeCaminhaoGrande",   20, "Capacidade grande",    10, 50);
            toleranciaEsperaPequeno     = validarParametroInt(props, "toleranciaEsperaPequeno",     30, "Tolerância pequeno",    0, Integer.MAX_VALUE);
            toleranciaEsperaGrande      = validarParametroInt(props, "toleranciaEsperaGrande",      60, "Tolerância grande",     0, Integer.MAX_VALUE);
            limiteFilaEstacao           = validarParametroInt(props, "limiteFilaEstacao",           5,  "Limite fila estação",   1, 50);

            // Simulação geral
            tempoTotalSimulacao = validarParametroInt(props, "tempoTotalSimulacao", 1440, "Tempo total simulação (min)", 60, 10080);
            tempoMaximoRota     = validarParametroInt(props, "tempoMaximoRota",     60, "Tempo máximo em rota",       60, 240);

            // Horários de pico (duas janelas)
            String[] inicios = props.getProperty("horariosPicoInicio", "6,17").split(",");
            String[] fins    = props.getProperty("horariosPicoFim",    "9,20").split(",");
            horariosPicoInicio[0] = Integer.parseInt(inicios[0]);
            horariosPicoFim[0]    = Integer.parseInt(fins[0]);
            horariosPicoInicio[1] = Integer.parseInt(inicios[1]);
            horariosPicoFim[1]    = Integer.parseInt(fins[1]);

        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar configurações: " + e.getMessage(), e);
        } finally {
            if (input != null) {
                try { input.close(); }
                catch (IOException ignored) {}
            }
        }
    }

    // Validação genérica de inteiro
    private int validarParametroInt(Properties props, String chave, int padrao,
                                    String desc, int min, int max) {
        try {
            int v = Integer.parseInt(props.getProperty(chave, "" + padrao));
            if (v < min || v > max) {
                throw new IllegalArgumentException(
                    String.format("%s deve estar entre %d e %d (valor: %d)", desc, min, max, v));
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(desc + " inválido: " + props.getProperty(chave), ex);
        }
    }

    // =======================
    // Getters para todos os parâmetros
    // =======================
    public int    getLixoMinimoSul()       { return lixoMinimoSul; }
    public int    getLixoMaximoSul()       { return lixoMaximoSul; }
    public int    getLixoMinimoNorte()     { return lixoMinimoNorte; }
    public int    getLixoMaximoNorte()     { return lixoMaximoNorte; }
    public int    getLixoMinimoCentro()    { return lixoMinimoCentro; }
    public int    getLixoMaximoCentro()    { return lixoMaximoCentro; }
    public int    getLixoMinimoLeste()     { return lixoMinimoLeste; }
    public int    getLixoMaximoLeste()     { return lixoMaximoLeste; }
    public int    getLixoMinimoSudeste()   { return lixoMinimoSudeste; }
    public int    getLixoMaximoSudeste()   { return lixoMaximoSudeste; }
    public int    getNumCaminhoesPequenos(){ return numCaminhoesPequenos; }
    public double[] getCapacidadesCaminhoesPequenos() { return capacidadesCaminhoesPequenos; }
    public int    getLimiteViagensMin()    { return limiteViagensMin; }
    public int    getLimiteViagensMax()    { return limiteViagensMax; }
    public int    getTempoViagemPicoMin()  { return tempoViagemPicoMin; }
    public int    getTempoViagemPicoMax()  { return tempoViagemPicoMax; }
    public int    getTempoViagemForaPicoMin() { return tempoViagemForaPicoMin; }
    public int    getTempoViagemForaPicoMax() { return tempoViagemForaPicoMax; }
    public int    getNumCaminhoesGrandesInicial() { return numCaminhoesGrandesInicial; }
    public int    getCapacidadeCaminhaoGrande()   { return capacidadeCaminhaoGrande; }
    public int    getToleranciaEsperaPequeno()     { return toleranciaEsperaPequeno; }
    public int    getToleranciaEsperaGrande()      { return toleranciaEsperaGrande; }
    public int    getLimiteFilaEstacao()           { return limiteFilaEstacao; }
    public int    getTempoTotalSimulacao()         { return tempoTotalSimulacao; }
    public int[]  getHorariosPicoInicio()         { return horariosPicoInicio; }
    public int[]  getHorariosPicoFim()            { return horariosPicoFim; }
    public int    getImpactoChuva()               { return impactoChuva; }
    public int    getTempoMaximoRota()            { return tempoMaximoRota; }
    public double getLimiteEsperaPequeno()        { return limiteEsperaPequeno; }

    // =======================
    // Setters para atualização em runtime
    // =======================
    public void atualizarLimiteViagens(int min, int max) {
        this.limiteViagensMin = min;
        this.limiteViagensMax = max;
    }
    public void atualizarLimiteFilaEstacao(int limite) {
        this.limiteFilaEstacao = limite;
    }
    public void atualizarHorariosPico(int inicio1, int fim1, int inicio2, int fim2) {
        this.horariosPicoInicio[0] = inicio1;
        this.horariosPicoFim[0]    = fim1;
        this.horariosPicoInicio[1] = inicio2;
        this.horariosPicoFim[1]    = fim2;
    }
    public void setImpactoChuva(int impacto) {
        this.impactoChuva = impacto;
    }
    // … demais setters conforme necessidade da GUI …
}
