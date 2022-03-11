package app;

import java.util.ArrayList;
import java.util.List;

public class Classe {
    private Pixel centro;
    private List<Pixel> pontosAssociados;

    public Classe(Pixel centro) {
        this.centro = centro;
        this.pontosAssociados = new ArrayList<>();
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
        ponto.setClasse(this);
    }

    public void atualizarCentro() {
        if(!pontosAssociados.isEmpty()) {
            var novoCentro = new Pixel();
            var somatorioR = 0;
            var somatorioG = 0;
            var somatorioB = 0;
            for(Pixel ponto : pontosAssociados) {
                somatorioR += ponto.getR();
                somatorioG += ponto.getG();
                somatorioB += ponto.getB();
            }
            novoCentro.setR(somatorioR / pontosAssociados.size());
            novoCentro.setG(somatorioG / pontosAssociados.size());
            novoCentro.setB(somatorioB / pontosAssociados.size());
            setCentro(novoCentro);
        }
    }

    public void limparLista() {
        setPontosAssociados(new ArrayList<>());
    }

    @Override
    public String toString() {
        return "Classe{" +
                "centro=" + centro +
                '}';
    }
}
