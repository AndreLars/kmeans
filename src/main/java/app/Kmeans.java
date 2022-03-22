package app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static app.Main.MENSAGEM_SAIDA;
import static app.Main.PATH_SAIDA;

public class Kmeans {
  private final List<Classe> classes = new ArrayList<>();
  private final List<Pixel> dados = new ArrayList<>();

  public void calcularKmeans(String path, int k) throws IOException {
    var img = readPathImg(path);
    inicializarClassesAleatorios(k);
    List<Classe> classesIteracaoAnterior = inicializarClassesParaComparacao(k);
    int i = 0;
    while (centrosNaoConvergiram(classesIteracaoAnterior, k)) {
      classes.forEach(Classe::limparLista);
      for (Pixel ponto : dados) {
        definirClasseDeUmPonto(ponto);
      }
      printClasses(i);
      i++;
    }
    gerarImagem(img, path, k);
  }

  private BufferedImage readPathImg(String path) throws IOException {
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

  private void inicializarClassesAleatorios(int numeroClasses) {
    for (int i = 0; i < numeroClasses; i++) {
      classes.add(new Classe(Pixel.random()));
    }
  }

  private ArrayList<Classe> inicializarClassesParaComparacao(int c) {
    return IntStream.range(0, c)
        .mapToObj(classe -> new Classe(Pixel.random()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private boolean centrosNaoConvergiram(List<Classe> classesIteracaoAnterior, int k) {
    boolean naoConvergiram = !classesIteracaoAnterior.equals(classes);
    IntStream.range(0, k)
        .forEachOrdered(i -> classesIteracaoAnterior.get(i).setCentro(classes.get(i).getCentro()));
    return naoConvergiram;
  }

  private void definirClasseDeUmPonto(Pixel pixel) {
    var menorDist = Double.MAX_VALUE;
    Classe maisProx = null;
    for (Classe classe : classes) {
      var dist = pixel.distanciaEuclidiana(classe.getCentro());
      if (dist < menorDist) {
        menorDist = dist;
        maisProx = classe;
      }
    }
    if (maisProx != null) {
      maisProx.addPixel(pixel);
    }
  }

  private void printClasses(int i) {
    i++;
    System.out.println("Iteracao = " + i);
    for (int c = 0; c < classes.size(); c++) {
      classes.get(c).atualizarCentro();
      System.out.println("Classe " + c + ": " + classes.get(c));
    }
  }

  private void gerarImagem(BufferedImage img, String path, int c) throws IOException {
    BufferedImage outImg = getBufferedImage(img, dados);
    var arquivoSaida = String.format("output_file=%s_C=%d.png", path.replace("img/", "").split("\\.")[0], c);
    URL url = Main.class.getClassLoader().getResource(PATH_SAIDA);
    ImageIO.write(outImg, "png", new File(url.getPath() + arquivoSaida));
    System.out.println(String.format(MENSAGEM_SAIDA, arquivoSaida));
  }

  private BufferedImage getBufferedImage(BufferedImage img, List<Pixel> dadosKnn) {
    var outImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    var dadosIterator = dadosKnn.iterator();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        preencherOutImg(img, outImg, dadosIterator, y, x);
      }
    }
    return outImg;
  }

  private void preencherOutImg(
      BufferedImage img, BufferedImage outImg, Iterator<Pixel> dadosIterator, int y, int x) {
    if (dadosIterator.hasNext()) {
      int pixel = img.getRGB(x, y);
      var color = new Color(pixel, true);
      var centro = dadosIterator.next().getClasse().getCentro();
      int p = getPixel(color.getAlpha(), centro.getR(), centro.getG(), centro.getB());
      outImg.setRGB(x, y, p);
    }
  }

  private int getPixel(int a, int r, int g, int b) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }
}
