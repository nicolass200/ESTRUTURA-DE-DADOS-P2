package model;

public class CaminhaoGrande {
    private double capacidade;
    private double cargaAtual;
    private double tempoEspera;
    private double toleranciaEspera;
    private double x, y;
    private double emissoesCO2;
    private double tempoTotalViagem;
    private double tempoTotalOperacao;
    private double tempoEmRota;

    public CaminhaoGrande(double capacidade, double toleranciaEspera) {
        this.capacidade = capacidade;
        this.cargaAtual = 0;
        this.tempoEspera = 0;
        this.toleranciaEspera = toleranciaEspera;
        this.x = 0;
        this.y = 0;
        this.emissoesCO2 = 0;
        this.tempoTotalViagem = 0;
        this.tempoTotalOperacao = 0;
        this.tempoEmRota = 0;
    }

    public void adicionarTempoViagem(double tempo) {
        this.tempoTotalViagem += tempo;
        this.tempoTotalOperacao += tempo;
        this.tempoEmRota += tempo;
    }

    public void adicionarTempoOperacao(double tempo) {
        this.tempoTotalOperacao += tempo;
    }

    public void setToleranciaEspera(double tolerancia) {
        this.toleranciaEspera = tolerancia;
    }

    public void carregar(double quantidade) {
        cargaAtual = Math.min(cargaAtual + quantidade, capacidade);
        emissoesCO2 += quantidade * 0.1;
    }

    public double descarregarNoAterro() {
        double quantidade = cargaAtual;
        cargaAtual = 0;
        tempoEspera = 0;
        tempoEmRota = 0;
        return quantidade;
    }

    public void incrementarEspera(double delta) {
        tempoEspera += delta;
    }

    public boolean estaCheio() {
        return cargaAtual >= capacidade * 0.95;
    }

    public void setPosicao(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moverPara(double destinoX, double destinoY, double tempoViagem) {
        // Simula movimentação gradual (atualiza posição diretamente por simplicidade)
        this.x = destinoX;
        this.y = destinoY;
        adicionarTempoViagem(tempoViagem);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getCapacidade() { return capacidade; }
    public double getCargaAtual() { return cargaAtual; }
    public double getTempoEspera() { return tempoEspera; }
    public double getToleranciaEspera() { return toleranciaEspera; }
    public double getEmissoesCO2() { return emissoesCO2; }
    public double getTempoTotalViagem() { return tempoTotalViagem; }
    public double getTempoTotalOperacao() { return tempoTotalOperacao; }
    public double getTempoEmRota() { return tempoEmRota; }
}