package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Classe {
    private Pixel centro;
    private List<Pixel> pixelsAssociados;

    public Classe(Pixel centro) {
        this.centro = centro;
        this.pixelsAssociados = new ArrayList<>();
    }

    public Pixel getCentro() {
        return centro;
    }

    public void setCentro(Pixel centro) {
        this.centro = centro;
    }

    public List<Pixel> getPixelsAssociados() {
        return pixelsAssociados;
    }

    public void setPixelsAssociados(List<Pixel> pixelsAssociados) {
        this.pixelsAssociados = pixelsAssociados;
    }

    public void addPonto(Pixel ponto) {
        this.pixelsAssociados.add(ponto);
        ponto.setClasse(this);
    }

    public void atualizarCentro() {
        if(!pixelsAssociados.isEmpty()) {
            var novoCentro = new Pixel();
            var somatorioR = 0;
            var somatorioG = 0;
            var somatorioB = 0;
            for(Pixel ponto : pixelsAssociados) {
                somatorioR += ponto.getR();
                somatorioG += ponto.getG();
                somatorioB += ponto.getB();
            }
            novoCentro.setR(somatorioR / pixelsAssociados.size());
            novoCentro.setG(somatorioG / pixelsAssociados.size());
            novoCentro.setB(somatorioB / pixelsAssociados.size());
            setCentro(novoCentro);
        }
    }

    public void limparLista() {
        setPixelsAssociados(new ArrayList<>());
    }

    @Override
    public String toString() {
        return "Classe{" +
                "centro=" + centro +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classe classe = (Classe) o;
        return Objects.equals(getCentro(), classe.getCentro());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCentro());
    }
}
