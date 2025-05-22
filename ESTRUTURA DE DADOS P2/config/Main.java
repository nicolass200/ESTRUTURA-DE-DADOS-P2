package config;

import gui.JanelaPrincipal;
import simulador.Simulador;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Configuracao config = new Configuracao();
        try {
            config.carregarConfiguracao("config.properties");
        } catch (Exception e) {
            System.err.println("Erro ao carregar configuração: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            config.carregarConfiguracao("config.properties");
            Simulador simulador = new Simulador(config, null);
            JanelaPrincipal janela = new JanelaPrincipal(simulador, config);
            janela.setVisible(true);
        });
    }
}
