package app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class Kmeans {
  static List<Pixel> dados = new ArrayList<>();
  static List<Pixel> dadosKnn = new ArrayList<>();
  static Map<Pixel, Pixel> mapDadosKnn = new HashMap<>();
  static List<Classe> classes = new ArrayList<>();
  static final String PATH_SAIDA = "output/";
  static final String MENSAGEM_SAIDA = "Imagem gerada na pasta build/resources/main/output";
  static int K;
  static int C;
  static final Random rand = new Random();
  static final SortByReversePixelDistance SORT_BY_REVERSE_PIXEL_DISTANCE =
    new SortByReversePixelDistance();

  public static void main(String[] args) throws IOException {
    var scanner = new Scanner(System.in);
    System.out.println("Insira o valor de K");
    K = Integer.parseInt(scanner.nextLine());
    System.out.println("Insira o numero de Classes");
    C = Integer.parseInt(scanner.nextLine());
    while (true) {
      var dadosRef = construirDadosReferenciaKNN();
      printMenu();
      int input = Integer.parseInt(scanner.nextLine());
      if (input == 1) {
        var path = readInput(scanner);
        calcularKmeans(K, path);
      } else if (input == 2) {
        var path = readInput(scanner);
        gerarImagemAPartirDosClassesKmeans(path);
      } else if (input == 3) {
        var path = readInput(scanner);
        calcularKnn(path, dadosRef);
      } else if (input == -1) {
        break;
      }
    }
  }

  private static LinkedHashSet<Pixel> construirDadosReferenciaKNN() throws IOException {
    var refRoad = readRefImg("ref/road.jpeg");
    categorizarRef(refRoad);
    var refWoods = readRefImg("ref/woods.jpeg");
    categorizarRef(refWoods);
    var dadosRef = new LinkedHashSet<>(refRoad);
    dadosRef.addAll(refWoods);
    classes = new ArrayList<>();
    return dadosRef;
  }

  private static void printMenu() {
    System.out.println("========MENU========");
    System.out.println("1: Kmeans");
    System.out.println("2: Gerar Imagem");
    System.out.println("3: KNN");
    System.out.println("-1: Parar Programa");
  }

  private static String readInput(Scanner scanner) {
    System.out.println("Escreva o nome do arquivo e extensão:");
    var path = new StringBuilder("img/");
    var nomeArquivo = scanner.nextLine();
    path.append(nomeArquivo);
    return path.toString();
  }

  private static void calcularKmeans(int k, String path) throws IOException {
    var img = readPathImg(path);
    inicializarClassesAleatorios(C);
    for (int i = 0; i < k; i++) {
      classes.forEach(Classe::limparLista);
      for (Pixel ponto : dados) {
        definirClasseDeUmPonto(ponto);
      }
      printClasses(i);
    }
    gerarImagem(img);
  }

  private static void gerarImagemAPartirDosClassesKmeans(String path)
          throws IOException {
    var img = readPathImg(path);
    for (Pixel ponto : dados) {
      definirClasseDeUmPonto(ponto);
    }
    gerarImagem(img);
  }

  private static void calcularKnn(String path, LinkedHashSet<Pixel> dadosRef) throws IOException {
    URL url = Kmeans.class.getClassLoader().getResource(path);
    var img = ImageIO.read(new File(url.getPath()));
    dadosKnn.clear();
    mapDadosKnn.clear();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        var color = new Color(img.getRGB(x, y), true);
        var pixel = new Pixel(color.getRed(), color.getGreen(), color.getBlue());
        if (mapDadosKnn.containsKey(pixel)) {
          dadosKnn.add(mapDadosKnn.get(pixel));
        } else {
          PriorityQueue<Pixel> fila = new PriorityQueue<>(K, SORT_BY_REVERSE_PIXEL_DISTANCE);
          for (Pixel dadoRef : dadosRef) {
            dadoRef.setDistance(pixel.distanciaEuclidiana(dadoRef));
            fila.offer(dadoRef);
            if (fila.size() > K) {
              fila.poll();
            }
          }
          var qtdClasseVizinhosMaisProximos = new HashMap<Classe, Integer>();
          var qtdMaisFrequente = 0;
          Classe categoriaMaisFrequente = null;
          while (Objects.nonNull(fila.peek())) {
            var classe = fila.poll().getClasse();
            qtdClasseVizinhosMaisProximos.put(
                    classe, qtdClasseVizinhosMaisProximos.getOrDefault(classe, 0) + 1);
            var qtd = qtdClasseVizinhosMaisProximos.getOrDefault(classe, 0);
            if (qtd > qtdMaisFrequente) {
              qtdMaisFrequente = qtd;
              categoriaMaisFrequente = classe;
            }
          }
          pixel.setClasse(categoriaMaisFrequente);
          dadosKnn.add(pixel);
          mapDadosKnn.put(pixel, pixel);
        }
      }
    }
    BufferedImage outImg = getBufferedImage(img, dadosKnn);
    final var arquivoSaida = "output" + rand.nextInt() + ".png";
    url = Kmeans.class.getClassLoader().getResource(PATH_SAIDA);
    ImageIO.write(outImg, "png", new File(url.getPath() + arquivoSaida));
    System.out.println(MENSAGEM_SAIDA);
  }

  private static void categorizarRef(ArrayList<Pixel> refWoods) {
    classes.clear();
    inicializarClassesAleatorios(1);
    for (Pixel ponto : refWoods) {
      definirClasseDeUmPonto(ponto);
    }
    classes.forEach(Classe::atualizarCentro);
  }

  private static ArrayList<Pixel> readRefImg(String path) throws IOException {
    URL url = Kmeans.class.getClassLoader().getResource(path);
    var img = ImageIO.read(new File(url.getPath()));
    var ref = new ArrayList<Pixel>();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        var color = new Color(img.getRGB(x, y), true);
        var rgb = new Pixel(color.getRed(), color.getGreen(), color.getBlue());
        ref.add(rgb);
      }
    }
    return ref;
  }

  private static BufferedImage getBufferedImage(BufferedImage img, List<Pixel> dadosKnn) {
    var outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    var dadosIterator = dadosKnn.iterator();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        preencherOutImg(img, outImg, dadosIterator, y, x);
      }
    }
    return outImg;
  }

  private static BufferedImage readPathImg(String path) throws IOException {
    URL url = Kmeans.class.getClassLoader().getResource(path);
    var img = ImageIO.read(new File(url.getPath()));
    dados.clear();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        var color = new Color(img.getRGB(x, y), true);
        var rgb = new Pixel(color.getRed(), color.getGreen(), color.getBlue());
        dados.add(rgb);
      }
    }
    return img;
  }

  private static void gerarImagem(BufferedImage img) throws IOException {
    BufferedImage outImg = getBufferedImage(img, dados);
    final var arquivoSaida = "output" + rand.nextInt() + ".png";
    URL url = Kmeans.class.getClassLoader().getResource(PATH_SAIDA);
    ImageIO.write(outImg, "png", new File(url.getPath() + arquivoSaida));
    System.out.println(MENSAGEM_SAIDA);
  }

  private static void preencherOutImg(
    BufferedImage img, BufferedImage outImg, Iterator<Pixel> dadosIterator, int y, int x) {
    if (dadosIterator.hasNext()) {
      int pixel = img.getRGB(x, y);
      var color = new Color(pixel, true);
      var centro = dadosIterator.next().getClasse().getCentro();
      int p = getPixel(color.getAlpha(), centro.getR(), centro.getG(), centro.getB());
      outImg.setRGB(x, y, p);
    }
  }

  private static int getPixel(int a, int r, int g, int b) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  private static void definirClasseDeUmPonto(Pixel ponto) {
    var menorDist = Double.MAX_VALUE;
    Classe maisProx = null;
    for (Classe classe : classes) {
      var dist = ponto.distanciaEuclidiana(classe.getCentro());
      if (dist < menorDist) {
        menorDist = dist;
        maisProx = classe;
      }
    }
    if (maisProx != null) {
      maisProx.addPonto(ponto);
    }
  }

  private static void printClasses(int i) {
    i++;
    System.out.println("K = " + i);
    for (int c = 0; c < classes.size(); c++) {
      classes.get(c).atualizarCentro();
      System.out.println("Classe " + c + ": " + classes.get(c));
    }
  }

  private static void inicializarClassesAleatorios(int numeroClasses) {
    for (int i = 0; i < numeroClasses; i++) {
      classes.add(new Classe(Pixel.random()));
    }
  }
}
