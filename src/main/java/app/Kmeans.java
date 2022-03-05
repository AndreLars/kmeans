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
  static final int K = 4;
  static final int C = 2;

  public static void main(String[] args) throws IOException {
    calcularKmeans(K);
    definirCentroideMaisProximoEGerarImagem();
  }

  private static BufferedImage readPathImg() throws IOException {
    var img = ImageIO.read(new File(PATH_FILE));
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        var color = new Color(img.getRGB(x, y), true);
        var rgb = new Rgb(color.getRed(), color.getGreen(), color.getBlue());
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
    ImageIO.write(outImg, "png", new File(PATH_SAIDA));
    System.out.println(MENSAGEM_SAIDA);
  }

  private static void preencherOutImg(
      BufferedImage img, BufferedImage outImg, Iterator<Rgb> dadosIterator, int y, int x) {
    if (dadosIterator.hasNext()) {
      int pixel = img.getRGB(x, y);
      var color = new Color(pixel, true);
      var centro = dadosIterator.next().getCentroide().getCentro();
      int p = getPixel(color.getAlpha(), centro.getR(), centro.getG(), centro.getB());
      outImg.setRGB(x, y, p);
    }
  }

  private static int getPixel(int a, int r, int g, int b) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  private static void calcularKmeans(int k) throws IOException {
    var img = readPathImg();
    inicializarCentroidesAleatorios(C);
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
