package gui;

import simulador.*;
import model.Zona;
import model.EstacaoTransferencia;
import model.CaminhaoPequeno;
import model.CaminhaoGrande;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import estruturas.Fila;

public class PainelSimulacao extends JPanel {
    private Simulador simulador;

    public PainelSimulacao(Simulador simulador) {
        this.simulador = simulador;
        setPreferredSize(new Dimension(1000, 700));
        setBackground(new Color(245, 245, 245));
    }

    public void setSimulador(Simulador simulador) {
        this.simulador = simulador;
    }

    public Simulador getSimulador() {
        return simulador;
    }

    public void atualizar() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Zona[] zonas = simulador.getZonas();
        for (Zona zona : zonas) {
            g2d.setColor(new Color(30, 144, 255));
            g2d.fillRect((int)zona.getX(), (int)zona.getY(), 60, 60);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(zona.getNome(), (int)zona.getX() + 5, (int)zona.getY() + 20);
            g2d.drawString(String.format("%.1f t", zona.getLixoAcumulado()), (int)zona.getX() + 5, (int)zona.getY() + 40);
        }

        EstacaoTransferencia[] estacoes = simulador.getEstacoes();
        for (EstacaoTransferencia estacao : estacoes) {
            g2d.setColor(new Color(220, 20, 60));
            g2d.fillRect((int)estacao.getX(), (int)estacao.getY(), 80, 50);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString(estacao.getNome(), (int)estacao.getX() + 5, (int)estacao.getY() + 15);
            g2d.drawString("P: " + estacao.getFilaCaminhoesPequenos().tamanho(), (int)estacao.getX() + 5, (int)estacao.getY() + 30);
            g2d.drawString("G: " + estacao.getFilaCaminhoesGrandes().tamanho(), (int)estacao.getX() + 5, (int)estacao.getY() + 45);
            g2d.drawString(String.format("Lixo: %.1f t", estacao.getLixoTransferido()), (int)estacao.getX() + 5, (int)estacao.getY() + 60);
        }

        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(800, 600, 80, 50);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Aterro", 805, 625);

        Fila<CaminhaoPequeno> caminhoesP = simulador.getCaminhoesPequenos();
        for (int i = 0; i < caminhoesP.tamanho(); i++) {
            CaminhaoPequeno caminhao = caminhoesP.getElemento(i);
            g2d.setColor(new Color(255, 215, 0));
            g2d.fill(new Ellipse2D.Double(caminhao.getX(), caminhao.getY(), 25, 25));
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(String.format("%.1f t", caminhao.getCargaAtual()), (int)caminhao.getX(), (int)caminhao.getY() + 10);
            if (caminhao.getDestino() != null) {
                g2d.drawString(caminhao.getDestino(), (int)caminhao.getX(), (int)caminhao.getY() + 25);
                g2d.setColor(new Color(255, 165, 0, 100));
                if (caminhao.getDestino().equals("Aterro")) {
                    g2d.draw(new Line2D.Double(caminhao.getX() + 12.5, caminhao.getY() + 12.5, 840, 625));
                } else {
                    for (Zona zona : zonas) {
                        if (zona.getNome().equals(caminhao.getDestino())) {
                            g2d.draw(new Line2D.Double(caminhao.getX() + 12.5, caminhao.getY() + 12.5,
                                    zona.getX() + 30, zona.getY() + 30));
                            break;
                        }
                    }
                    for (EstacaoTransferencia estacao : estacoes) {
                        if (estacao.getNome().equals(caminhao.getDestino())) {
                            g2d.draw(new Line2D.Double(caminhao.getX() + 12.5, caminhao.getY() + 12.5,
                                    estacao.getX() + 40, estacao.getY() + 25));
                            break;
                        }
                    }
                }
            }
        }

        Fila<CaminhaoGrande> caminhoesG = simulador.getCaminhoesGrandes();
        for (int i = 0; i < caminhoesG.tamanho(); i++) {
            CaminhaoGrande caminhao = caminhoesG.getElemento(i);
            g2d.setColor(new Color(255, 140, 0));
            g2d.fill(new Ellipse2D.Double(caminhao.getX(), caminhao.getY(), 35, 35));
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(String.format("%.1f t", caminhao.getCargaAtual()), (int)caminhao.getX(), (int)caminhao.getY() + 15);
        }
    }
}