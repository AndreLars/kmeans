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

    public void setPixelsAssociados(List<Pixel> pixelsAssociados) {
        this.pixelsAssociados = pixelsAssociados;
    }

    public void addPixel(Pixel ponto) {
        this.pixelsAssociados.add(ponto);
        ponto.setClasse(this);
    }

    public void atualizarCentro() {
        if (!pixelsAssociados.isEmpty()) {
            int somR = 0;
            int somG = 0;
            int somB = 0;
            for (Pixel pixel : pixelsAssociados) {
                somR += pixel.getR();
                somG += pixel.getG();
                somB += pixel.getB();
            }
            int size = pixelsAssociados.size();
            setCentro(new Pixel(somR / size, somG / size, somB / size));
        }
    }

    public void limparLista() {
        setPixelsAssociados(new ArrayList<>());
    }

    @Override
    public String toString() {
        return "Classe{" + "centro=" + centro + '}';
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
