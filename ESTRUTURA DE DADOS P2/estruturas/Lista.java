package estruturas;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Lista<T> implements Iterable<T> {
    private class Nodo {
        T dado;
        Nodo proximo;
        Nodo anterior;

        Nodo(T dado) {
            this.dado = dado;
            this.proximo = null;
            this.anterior = null;
        }
    }

    private Nodo inicio;
    private Nodo fim;
    private int tamanho;

    public Lista() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void adicionar(T elemento) {
        Nodo novo = new Nodo(elemento);
        if (estaVazia()) {
            inicio = novo;
        } else {
            fim.proximo = novo;
            novo.anterior = fim;
        }
        fim = novo;
        tamanho++;
    }

    public T removerUltimo() {
        if (estaVazia()) {
            throw new IllegalStateException("Lista vazia");
        }
        T elemento = fim.dado;
        fim = fim.anterior;
        if (fim != null) {
            fim.proximo = null;
        } else {
            inicio = null;
        }
        tamanho--;
        return elemento;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

    public T get(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        Nodo atual;
        if (indice < tamanho / 2) {
            atual = inicio;
            for (int i = 0; i < indice; i++) {
                atual = atual.proximo;
            }
        } else {
            atual = fim;
            for (int i = tamanho - 1; i > indice; i--) {
                atual = atual.anterior;
            }
        }
        return atual.dado;
    }

    public T remover(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + indice);
        }
        if (indice == 0) {
            return removerPrimeiro();
        } else if (indice == tamanho - 1) {
            return removerUltimo();
        }
        Nodo atual = getNodo(indice);
        atual.anterior.proximo = atual.proximo;
        atual.proximo.anterior = atual.anterior;
        tamanho--;
        return atual.dado;
    }

    public T removerPrimeiro() {
        if (estaVazia()) {
            throw new IllegalStateException("Lista vazia");
        }
        T elemento = inicio.dado;
        inicio = inicio.proximo;
        if (inicio != null) {
            inicio.anterior = null;
        } else {
            fim = null;
        }
        tamanho--;
        return elemento;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo atual = inicio;

            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dado = atual.dado;
                atual = atual.proximo;
                return dado;
            }
        };
    }

    public Object[] toArray() {
        Object[] array = new Object[tamanho];
        Nodo atual = inicio;
        for (int i = 0; i < tamanho; i++) {
            array[i] = atual.dado;
            atual = atual.proximo;
        }
        return array;
    }

    public void limpar() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    public boolean contem(T elemento) {
        Nodo atual = inicio;
        while (atual != null) {
            if (atual.dado.equals(elemento)) {
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    private Nodo getNodo(int indice) {
        Nodo atual;
        if (indice < tamanho / 2) {
            atual = inicio;
            for (int i = 0; i < indice; i++) {
                atual = atual.proximo;
            }
        } else {
            atual = fim;
            for (int i = tamanho - 1; i > indice; i--) {
                atual = atual.anterior;
            }
        }
        return atual;
    }
}