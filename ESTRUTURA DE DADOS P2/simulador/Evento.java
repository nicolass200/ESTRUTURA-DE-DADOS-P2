package simulador;

import model.Zona;
import model.CaminhaoPequeno;
import model.CaminhaoGrande;
import model.EstacaoTransferencia;

public class Evento {
    private String tipo;
    private double tempo;
    private double quantidade;
    private Zona zona;
    private CaminhaoPequeno caminhaoPequeno;
    private CaminhaoGrande caminhaoGrande;
    private EstacaoTransferencia estacao;

    public Evento(String tipo, double tempo, double quantidade) {
        this.tipo = tipo;
        this.tempo = tempo;
        this.quantidade = quantidade;
        this.zona = null;
        this.caminhaoPequeno = null;
        this.caminhaoGrande = null;
        this.estacao = null;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public void setCaminhaoPequeno(CaminhaoPequeno caminhao) {
        this.caminhaoPequeno = caminhao;
    }

    public void setCaminhaoGrande(CaminhaoGrande caminhao) {
        this.caminhaoGrande = caminhao;
    }

    public void setEstacao(EstacaoTransferencia estacao) {
        this.estacao = estacao;
    }

    public String getTipo() { return tipo; }
    public double getTempo() { return tempo; }
    public double getQuantidade() { return quantidade; }
    public Zona getZona() { return zona; }
    public CaminhaoPequeno getCaminhaoPequeno() { return caminhaoPequeno; }
    public CaminhaoGrande getCaminhaoGrande() { return caminhaoGrande; }
    public EstacaoTransferencia getEstacao() { return estacao; }

    public String getTempoFormatado() {
        int horas = (int) (tempo / 60);
        int minutos = (int) (tempo % 60);
        return String.format("%02d:%02d", horas, minutos);
    }
}