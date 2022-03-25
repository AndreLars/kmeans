package app;

import java.util.Objects;
import java.util.Random;

public class Pixel {
    private int r;
    private int g;
    private int b;
    private Classe classe;
    private double distance;

    private static final Random random = new Random();

    public static Pixel random() {
        return new Pixel(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public Pixel() {}

    public Pixel(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(Pixel pixel) {
        this.distance = distanciaEuclidiana(pixel);
    }

    public double distanciaEuclidiana(Pixel pixel) {
        return Math.sqrt(
                Math.pow((double) r - pixel.getR(), 2)
                        + Math.pow((double) g - pixel.getG(), 2)
                        + Math.pow((double) b - pixel.getB(), 2));
    }

    @Override
    public String toString() {
        return "Rgb{" + "r=" + r + ", g=" + g + ", b=" + b + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pixel pixel = (Pixel) o;
        return r == pixel.r && g == pixel.g && b == pixel.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
