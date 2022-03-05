package app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Kmeans {
  static List<Rgb> dados = new ArrayList<>();
  static List<Centroide> centroides = new ArrayList<>();
  static final String PATH_FILE = "/home/lars/Fatec/Fabricio/roads/img5.jpeg";
  static final String PATH_SAIDA = "/home/lars/Fatec/Fabricio/pixels/road_google_maps_6.png";
  static final String MENSAGEM_SAIDA = "Imagem gerada no path: " + PATH_SAIDA;

  public static void main(String[] args) throws IOException {
    calcularKmeans(4);
    definirCentroideMaisProximoEGerarImagem();
  }

  private static BufferedImage readPathImg() throws IOException {
    File file = new File(PATH_FILE);
    BufferedImage img = ImageIO.read(file);
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        int pixel = img.getRGB(x, y);
        Color color = new Color(pixel, true);
        var rgb = new Rgb();
        rgb.setR(color.getRed());
        rgb.setG(color.getGreen());
        rgb.setB(color.getBlue());
        dados.add(rgb);
      }
    }
    return img;
  }

  private static void gerarImagem(BufferedImage img) throws IOException {
    var outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    var dadosIterator = dados.iterator();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        preencherOutImg(img, outImg, dadosIterator, y, x);
      }
    }
    File f = new File(PATH_SAIDA);
    ImageIO.write(outImg, "png", f);
    System.out.println(MENSAGEM_SAIDA);
  }

  private static void preencherOutImg(
      BufferedImage img, BufferedImage outImg, Iterator<Rgb> dadosIterator, int y, int x) {
    if (dadosIterator.hasNext()) {
      int pixel = img.getRGB(x, y);
      Color color = new Color(pixel, true);
      var centro = dadosIterator.next().getCentroide().getCentro();
      int a = color.getAlpha();
      int r = centro.getR();
      int g = centro.getG();
      int b = centro.getB();
      int p = getPixel(a, r, g, b);
      outImg.setRGB(x, y, p);
    }
  }

  private static int getPixel(int a, int r, int g, int b) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  private static void calcularKmeans(int k) throws IOException {
    BufferedImage img = readPathImg();
    inicializarCentroidesAleatorios(2);
    for (int i = 0; i < k; i++) {
      centroides.forEach(Centroide::limparLista);
      for (Rgb ponto : dados) {
        definirCentroideDeUmPonto(ponto);
      }
      printCentroides(i);
    }
    gerarImagem(img);
  }

  private static void definirCentroideMaisProximoEGerarImagem() throws IOException {
    readPathImg();
    inicializarCentroides();
    for (Rgb ponto : dados) {
      definirCentroideDeUmPonto(ponto);
    }
  }

  private static void definirCentroideDeUmPonto(Rgb ponto) {
    var menorDist = Double.MAX_VALUE;
    Centroide maisProx = null;
    for (Centroide centroide : centroides) {
      var dist = ponto.distanciaEuclidiana(centroide.getCentro());
      if (dist < menorDist) {
        menorDist = dist;
        maisProx = centroide;
      }
    }
    if (maisProx != null) {
      maisProx.addPonto(ponto);
    }
  }

  private static void printCentroides(int i) {
    i++;
    System.out.println("K = " + i);
    for (int c = 0; c < centroides.size(); c++) {
      centroides.get(c).atualizarCentro();
      System.out.println("Centroide " + c + ": " + centroides.get(c));
    }
  }

  private static void inicializarCentroidesAleatorios(int numeroCentroides) {
    for (int i = 0; i < numeroCentroides; i++) {
      centroides.add(new Centroide(i + 1, Rgb.random()));
    }
  }

  private static void inicializarCentroides() {
    centroides.add(new Centroide(1, new Rgb(4, 7, 13)));
    centroides.add(new Centroide(2, new Rgb(49, 54, 42)));
  }
}
