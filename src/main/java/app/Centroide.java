package app;

import java.util.ArrayList;
import java.util.List;

public class Centroide {
    private int c;
    private Pixel centro;
    private List<Pixel> pontosAssociados;

    public Centroide() {
    }

    public Centroide(int c, Pixel centro) {
        this.c = c;
        this.centro = centro;
        this.pontosAssociados = new ArrayList<>();
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public Pixel getCentro() {
        return centro;
    }

    public void setCentro(Pixel centro) {
        this.centro = centro;
    }

    public List<Pixel> getPontosAssociados() {
        return pontosAssociados;
    }

    public void setPontosAssociados(List<Pixel> pontosAssociados) {
        this.pontosAssociados = pontosAssociados;
    }

    public void addPonto(Pixel ponto) {
        this.pontosAssociados.add(ponto);
        ponto.setCentroide(this);
    }

    public void atualizarCentro() {
        var novoCentro = new Pixel();
        for(Pixel ponto : pontosAssociados) {
            novoCentro.atualizarPontoMedio(ponto);
        }
        setCentro(novoCentro);
    }

    public void limparLista() {
        setPontosAssociados(new ArrayList<>());
    }

    @Override
    public String toString() {
        return "Centroide{" +
                "k=" + c +
                ", centro=" + centro +
                '}';
    }
}
