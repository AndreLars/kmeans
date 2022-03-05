package app;

import java.util.ArrayList;
import java.util.List;

public class Centroide {
    private int c;
    private Rgb centro;
    private List<Rgb> pontosAssociados;

    public Centroide() {
    }

    public Centroide(int c, Rgb centro) {
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

    public Rgb getCentro() {
        return centro;
    }

    public void setCentro(Rgb centro) {
        this.centro = centro;
    }

    public List<Rgb> getPontosAssociados() {
        return pontosAssociados;
    }

    public void setPontosAssociados(List<Rgb> pontosAssociados) {
        this.pontosAssociados = pontosAssociados;
    }

    public void addPonto(Rgb ponto) {
        this.pontosAssociados.add(ponto);
        ponto.setCentroide(this);
    }

    public void atualizarCentro() {
        var novoCentro = new Rgb();
        for(Rgb ponto : pontosAssociados) {
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
