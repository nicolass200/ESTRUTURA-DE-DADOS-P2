package model;

import estruturas.Fila;

public class EstacaoTransferencia {
    private String nome;
    private Fila<CaminhaoPequeno> filaCaminhoesPequenos;
    private Fila<CaminhaoGrande> filaCaminhoesGrandes;
    private double x, y;
    private String[] zonasAtendidas;
    private double lixoTransferido;
    private int caminhoesPequenosProcessados;
    private int caminhoesGrandesProcessados;

    public EstacaoTransferencia(String nome, String[] zonasAtendidas) {
        this.nome = nome;
        this.filaCaminhoesPequenos = new Fila<>();
        this.filaCaminhoesGrandes = new Fila<>();
        this.x = 0;
        this.y = 0;
        this.zonasAtendidas = zonasAtendidas;
        this.lixoTransferido = 0;
        this.caminhoesPequenosProcessados = 0;
        this.caminhoesGrandesProcessados = 0;
    }

    public void adicionarCaminhaoPequeno(CaminhaoPequeno caminhao, double tempoViagem) {
        filaCaminhoesPequenos.enfileirar(caminhao);
        caminhoesPequenosProcessados++;
    }

    public void adicionarCaminhaoGrande(CaminhaoGrande caminhao) {
        filaCaminhoesGrandes.enfileirar(caminhao);
        caminhoesGrandesProcessados++;
    }

    public void adicionarLixoTransferido(double quantidade) {
        this.lixoTransferido += quantidade;
    }

    public CaminhaoPequeno getCaminhaoPequeno(int indice) {
        return filaCaminhoesPequenos.getElemento(indice);
    }

    public Fila<CaminhaoPequeno> getFilaCaminhoesPequenos() {
        return filaCaminhoesPequenos;
    }

    public Fila<CaminhaoGrande> getFilaCaminhoesGrandes() {
        return filaCaminhoesGrandes;
    }

    public String getNome() {
        return nome;
    }

    public String[] getZonasAtendidas() {
        return zonasAtendidas;
    }

    public double getLixoTransferido() {
        return lixoTransferido;
    }

    public int getCaminhoesPequenosProcessados() {
        return caminhoesPequenosProcessados;
    }

    public int getCaminhoesGrandesProcessados() {
        return caminhoesGrandesProcessados;
    }

    public void setPosicao(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}