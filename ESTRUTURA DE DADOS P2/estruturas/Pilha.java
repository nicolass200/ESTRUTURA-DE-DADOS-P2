package estruturas;

public class Pilha<T> {
    private class Nodo {
        T dado;
        Nodo proximo;

        Nodo(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private Nodo topo;
    private int tamanho;

    public Pilha() {
        this.topo = null;
        this.tamanho = 0;
    }

    public void empilhar(T elemento) {
        Nodo novo = new Nodo(elemento);
        novo.proximo = topo;
        topo = novo;
        tamanho++;
    }

    public T desempilhar() {
        if (estaVazia()) {
            throw new IllegalStateException("Pilha vazia");
        }
        T elemento = topo.dado;
        topo = topo.proximo;
        tamanho--;
        return elemento;
    }

    public  boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }
}