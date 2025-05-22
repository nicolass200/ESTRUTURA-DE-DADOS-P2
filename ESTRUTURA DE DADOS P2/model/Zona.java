package model;

import java.util.Random;

public class Zona {
    private String nome;
    private double lixoAcumulado;
    private double x, y;
    private int lixoMinimoDiario;
    private int lixoMaximoDiario;
    private Random random;

    public Zona(String nome, int lixoMinimoDiario, int lixoMaximoDiario) {
        this.nome = nome;
        this.lixoAcumulado = 0;
        this.x = 0;
        this.y = 0;
        this.lixoMinimoDiario = lixoMinimoDiario;
        this.lixoMaximoDiario = lixoMaximoDiario;
        this.random = new Random();
    }

   
public void gerarLixo() {
    // Gera taxa diária aleatória entre o mínimo e máximo
    double taxaDiaria = random.nextDouble() * (lixoMaximoDiario - lixoMinimoDiario)
                        + lixoMinimoDiario;
    // Converte para quantidade por hora (1/24 do dia)
    double quantidadePorHora = taxaDiaria / 24.0;
    // Garante não negativa
    quantidadePorHora = Math.max(0, quantidadePorHora);
    // Acumula no total
    lixoAcumulado += quantidadePorHora;
}


    public double coletarLixo(double quantidade) {
        double coletado = Math.min(quantidade, lixoAcumulado);
        lixoAcumulado -= coletado;
        if (lixoAcumulado < 0) {
            lixoAcumulado = 0;
        }
        return coletado;
    }

    public void setPosicao(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String getNome() { return nome; }
    public double getLixoAcumulado() { return lixoAcumulado; }
    public double getX() { return x; }
    public double getY() { return y; }
}