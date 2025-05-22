package model;

import java.util.Random;

public class CaminhaoPequeno {
    private double capacidade;
    private double cargaAtual;
    private int viagensRealizadas;
    private int limiteViagensDiarias;
    private double x, y;
    private String destino;
    private int horarioInicio;
    private int horarioFim;
    private String estacaoAtribuida;
    private double[] lixoPorZona;
    private double tempoTotalViagem;
    private double tempoTotalOperacao;
    private double tempoEmRota;
    private double TempoEsperaNaFila;

    public CaminhaoPequeno(double capacidade, int limiteViagensMin, int limiteViagensMax) {
        this.capacidade = capacidade;
        this.cargaAtual = 0;
        this.viagensRealizadas = 0;
        this.limiteViagensDiarias = new Random().nextInt(limiteViagensMax - limiteViagensMin + 1) + limiteViagensMin;
        this.x = 0;
        this.y = 0;
        this.destino = null;
        this.lixoPorZona = new double[5];
        this.tempoTotalViagem = 0;
        this.tempoTotalOperacao = 0;
        this.tempoEmRota = 0;
        gerarHorarioTrabalho();
        atribuirEstacao();
    }

    
private void gerarHorarioTrabalho() {
    // Set working hours to cover the full simulation day (00:00 to 24:00) to allow multiple trips
    horarioInicio = 0; // Start at 00:00
    horarioFim = 24 * 60; // End at 24:00 (1440 minutes)
}

    private void atribuirEstacao() {
        estacaoAtribuida = new Random().nextBoolean() ? "Estação A" : "Estação B";
    }

    public boolean podeRealizarViagem(int tempoAtual) {
        return viagensRealizadas < limiteViagensDiarias && cargaAtual < capacidade &&
               tempoAtual >= horarioInicio && tempoAtual <= horarioFim;
    }

    public void carregar(double quantidade, int zonaIdx) {
        if (zonaIdx < 0 || zonaIdx >= lixoPorZona.length) {
            System.err.println("Índice de zona inválido: " + zonaIdx);
            return;
        }
        if (quantidade < 0) {
            System.err.println("Quantidade de lixo inválida: " + quantidade);
            return;
        }
        double espaçoDisponivel = capacidade - cargaAtual;
        double quantidadeCarregada = Math.min(quantidade, espaçoDisponivel);
        cargaAtual += quantidadeCarregada;
        lixoPorZona[zonaIdx] += quantidadeCarregada;
        viagensRealizadas++;
        System.out.println("Caminhão: Carregado " + quantidadeCarregada + " t na zona " + zonaIdx + ", Carga atual " + cargaAtual + " t");
    }

    public double descarregar() {
        double quantidade = cargaAtual;
        cargaAtual = 0;
        tempoEmRota = 0;
        System.out.println("Caminhão: Descarregado " + quantidade + " t");
        return quantidade;
    }

    public boolean estaCheio() {
        return cargaAtual >= capacidade * 0.95;
    }

    public void adicionarTempoViagem(double tempo) {
        this.tempoTotalViagem += tempo;
        this.tempoTotalOperacao += tempo;
        this.tempoEmRota += tempo;
    }

    public void adicionarTempoOperacao(double tempo) {
        this.tempoTotalOperacao += tempo;
    }

    public void setPosicao(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moverPara(double destinoX, double destinoY, double tempoViagem) {
        this.x = destinoX;
        this.y = destinoY;
        adicionarTempoViagem(tempoViagem);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getDestino() { return destino; }
    public double getCapacidade() { return capacidade; }
    public double getCargaAtual() { return cargaAtual; }
    public int getViagensRealizadas() { return viagensRealizadas; }
    public int getLimiteViagensDiarias() { return limiteViagensDiarias; }
    public int getHorarioInicio() { return horarioInicio; }
    public int getHorarioFim() { return horarioFim; }
    public String getEstacaoAtribuida() { return estacaoAtribuida; }
    public double[] getLixoPorZona() { return lixoPorZona; }
    public double getTempoTotalViagem() { return tempoTotalViagem; }
    public double getTempoTotalOperacao() { return tempoTotalOperacao; }
    public double getTempoEmRota() { return tempoEmRota; }
    public double getTempoEsperaNaFila() { return TempoEsperaNaFila; }
    public void setTempoEsperaNaFila(double tempoEsperaNaFila) { TempoEsperaNaFila = tempoEsperaNaFila; }
}