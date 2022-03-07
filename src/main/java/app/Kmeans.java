package app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class Kmeans {
  static List<Rgb> dados = new ArrayList<>();
  static List<Centroide> centroides = new ArrayList<>();
  static final String PATH_SAIDA = "output/";
  static final String MENSAGEM_SAIDA = "Imagem gerada na pasta target/classes/output";
  static final int K = 4;
  static final int C = 2;
  static final Random rand = new Random();

  public static void main(String[] args) throws IOException {
    var scanner = new Scanner(System.in);
    while (true) {
      printMenu();
      int input = Integer.parseInt(scanner.nextLine());
      if (input == 1) {
        System.out.println("Escreva o nome do arquivo e extensão:");
        var path = new StringBuilder("img/");
        var nomeArquivo = scanner.nextLine();
        path.append(nomeArquivo);
        calcularKmeans(K, path);
      } else if (input == 2) {
        System.out.println("Escreva o nome do arquivo e extensão:");
        var path = new StringBuilder("img/");
        var nomeArquivo = scanner.nextLine();
        path.append(nomeArquivo);
        definirCentroideMaisProximoEGerarImagem(path);
      } else if (input == -1) {
        break;
      }
    }
  }

  private static void printMenu() {
    System.out.println("========MENU========");
    System.out.println("1: Kmeans");
    System.out.println("2: Gerar Imagem");
    System.out.println("-1: Parar Programa");
  }

  private static BufferedImage readPathImg(StringBuilder path) throws IOException {
    URL url = Kmeans.class.getClassLoader().getResource(path.toString());
    var img = ImageIO.read(new File(url.getPath()));
    dados.clear();
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
    final var arquivoSaida = "output" + rand.nextInt() + ".png";
    URL url = Kmeans.class.getClassLoader().getResource(PATH_SAIDA);
    ImageIO.write(outImg, "png", new File(url.getPath() + arquivoSaida));
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

  private static void calcularKmeans(int k, StringBuilder path) throws IOException {
    var img = readPathImg(path);
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

  private static void definirCentroideMaisProximoEGerarImagem(StringBuilder path)
      throws IOException {
    var img = readPathImg(path);
    for (Rgb ponto : dados) {
      definirCentroideDeUmPonto(ponto);
    }
    gerarImagem(img);
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
}
