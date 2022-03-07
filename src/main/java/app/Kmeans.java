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
  static List<Pixel> dados = new ArrayList<>();
  static Set<Pixel> setDados = new HashSet<>();
  static List<Pixel> dadosKnn = new ArrayList<>();
  static Set<Pixel> setDadosKnn = new HashSet<>();
  static List<Centroide> centroides = new ArrayList<>();
  static final String PATH_SAIDA = "output/";
  static final String MENSAGEM_SAIDA = "Imagem gerada na pasta target/classes/output";
  static final int K = 5;
  static final int C = 2;
  static final Random rand = new Random();
  static final SortByReversePixelDistance SORT_BY_REVERSE_PIXEL_DISTANCE = new SortByReversePixelDistance();

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
      } else if (input == 3) {
        System.out.println("Escreva o nome do arquivo e extensão:");
        var path = new StringBuilder("img/");
        var nomeArquivo = scanner.nextLine();
        path.append(nomeArquivo);
        calcularKnn(path);
      } else if (input == -1) {
        break;
      }
    }
  }

  private static void calcularKnn(StringBuilder path) throws IOException {
    URL url = Kmeans.class.getClassLoader().getResource(path.toString());
    var img = ImageIO.read(new File(url.getPath()));
    dadosKnn.clear();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        var color = new Color(img.getRGB(x, y), true);
        var pixel = new Pixel(color.getRed(), color.getGreen(), color.getBlue());
        if(setDadosKnn.contains(pixel)) {
          setDadosKnn.stream().filter(pixel::equals).findFirst().ifPresent(p -> {
            pixel.setCentroide(p.getCentroide());
            dadosKnn.add(pixel);
          });
        } else {
          PriorityQueue<Pixel> pq = new PriorityQueue<>(K, SORT_BY_REVERSE_PIXEL_DISTANCE);
          for (Pixel dado : setDados) {
            dado.setDistance(pixel.distanciaEuclidiana(dado));
            pq.offer(dado);
            if(pq.size() > K) {
              pq.poll();
            }
          }
          var qtdCentroideVizinhosMaisProximos = new HashMap<Centroide, Integer>();
          var qtdMaior = 0;
          Centroide maior = null;
          while (Objects.nonNull(pq.peek())) {
            var centroide = pq.poll().getCentroide();
            qtdCentroideVizinhosMaisProximos.put(centroide, qtdCentroideVizinhosMaisProximos.getOrDefault(centroide, 0) + 1);
            var qtd = qtdCentroideVizinhosMaisProximos.getOrDefault(centroide, 0);
            if (qtd > qtdMaior) {
              qtdMaior = qtd;
              maior = centroide;
            }
          }
          pixel.setCentroide(maior);
          dadosKnn.add(pixel);
          setDadosKnn.add(pixel);
        }
      }
    }
    var outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    var dadosIterator = dadosKnn.iterator();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        preencherOutImg(img, outImg, dadosIterator, y, x);
      }
    }
    final var arquivoSaida = "output" + rand.nextInt() + ".png";
    url = Kmeans.class.getClassLoader().getResource(PATH_SAIDA);
    ImageIO.write(outImg, "png", new File(url.getPath() + arquivoSaida));
    System.out.println(MENSAGEM_SAIDA);
  }

  private static void printMenu() {
    System.out.println("========MENU========");
    System.out.println("1: Kmeans");
    System.out.println("2: Gerar Imagem");
    System.out.println("3: KNN");
    System.out.println("-1: Parar Programa");
  }

  private static BufferedImage readPathImg(StringBuilder path) throws IOException {
    URL url = Kmeans.class.getClassLoader().getResource(path.toString());
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
          BufferedImage img, BufferedImage outImg, Iterator<Pixel> dadosIterator, int y, int x) {
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
      for (Pixel ponto : dados) {
        definirCentroideDeUmPonto(ponto);
      }
      printCentroides(i);
    }
    gerarImagem(img);
  }

  private static void definirCentroideMaisProximoEGerarImagem(StringBuilder path)
      throws IOException {
    var img = readPathImg(path);
    for (Pixel ponto : dados) {
      definirCentroideDeUmPonto(ponto);
    }
    gerarImagem(img);
  }

  private static void definirCentroideDeUmPonto(Pixel ponto) {
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
      setDados.add(ponto);
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
      centroides.add(new Centroide(i + 1, Pixel.random()));
    }
  }
}
