package estruturas;

public class Fila<T> {
    private class Nodo {
        T dado;
        Nodo proximo;
        double tempoEspera;

        Nodo(T dado) {
            this.dado = dado;
            this.proximo = null;
            this.tempoEspera = 0;
        }
    }

    private Nodo inicio;
    private Nodo fim;
    private int tamanho;

    public Fila() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void enfileirar(T elemento) {
        Nodo novo = new Nodo(elemento);
        if (estaVazia()) {
            inicio = novo;
        } else {
            fim.proximo = novo;
        }
        fim = novo;
        tamanho++;
    }

    public T desenfileirar() {
        if (estaVazia()) {
            throw new IllegalStateException("Fila vazia");
        }
        T elemento = inicio.dado;
        inicio = inicio.proximo;
        tamanho--;
        if (estaVazia()) {
            fim = null;
        }
        return elemento;
    }

    public T getElemento(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        Nodo atual = inicio;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual.dado;
    }

    public void incrementarEspera(double delta) {
        Nodo atual = inicio;
        while (atual != null) {
            atual.tempoEspera += delta;
            atual = atual.proximo;
        }
    }

    public double getTempoEsperaPrimeiro() {
        return estaVazia() ? 0 : inicio.tempoEspera;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }
}