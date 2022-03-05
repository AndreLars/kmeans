package app;

import java.util.Random;

public class Rgb {
  private int r;
  private int g;
  private int b;
  private Centroide centroide;

  private static final Random random = new Random();

  public static Rgb random() {
    return new Rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
  }

  public Rgb() {}

  public Rgb(int r, int g, int b) {
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

  public Centroide getCentroide() {
    return centroide;
  }

  public void setCentroide(Centroide centroide) {
    this.centroide = centroide;
  }

  public double distanciaEuclidiana(Rgb ponto) {
    return Math.sqrt(
        Math.pow((double) r - ponto.getR(), 2)
            + Math.pow((double) g - ponto.getG(), 2)
            + Math.pow((double) b - ponto.getB(), 2));
  }

  public void atualizarPontoMedio(Rgb ponto) {
    setR((getR() + ponto.getR()) / 2);
    setG((getG() + ponto.getG()) / 2);
    setB((getB() + ponto.getB()) / 2);
  }

  @Override
  public String toString() {
    return "Rgb{" +
            "r=" + r +
            ", g=" + g +
            ", b=" + b +
            '}';
  }
}
