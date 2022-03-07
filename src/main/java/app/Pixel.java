package app;

import java.util.Objects;
import java.util.Random;

public class Pixel {
  private int r;
  private int g;
  private int b;
  private Centroide centroide;
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

  public Centroide getCentroide() {
    return centroide;
  }

  public void setCentroide(Centroide centroide) {
    this.centroide = centroide;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double distanciaEuclidiana(Pixel ponto) {
    return Math.sqrt(
        Math.pow((double) r - ponto.getR(), 2)
            + Math.pow((double) g - ponto.getG(), 2)
            + Math.pow((double) b - ponto.getB(), 2));
  }

  public void atualizarPontoMedio(Pixel ponto) {
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
