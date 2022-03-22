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
import java.util.Set;

import static app.Main.MENSAGEM_SAIDA;
import static app.Main.PATH_SAIDA;

public class Knn {
  private final List<Classe> classes = new ArrayList<>();
  private final List<Pixel> dadosKnn = new ArrayList<>();
  private final Map<Pixel, Pixel> dadosJaProcessados = new HashMap<>();
  private static final SortByReversePixelDistance SORT_BY_REVERSE_PIXEL_DISTANCE =
      new SortByReversePixelDistance();

  public Set<Pixel> construirDadosReferenciaKNN() throws IOException {
    var refRoad = readRefImg("ref/road.jpeg");
    classificarReferencia(refRoad);
    var refWoods = readRefImg("ref/woods.jpeg");
    classificarReferencia(refWoods);
    var dadosRef = new LinkedHashSet<>(refRoad);
    dadosRef.addAll(refWoods);
    classes.clear();
    return dadosRef;
  }

  private ArrayList<Pixel> readRefImg(String path) throws IOException {
    URL url = Main.class.getClassLoader().getResource(path);
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

  private void classificarReferencia(ArrayList<Pixel> refWoods) {
    classes.clear();
    classes.add(new Classe(Pixel.random()));
    for (Pixel ponto : refWoods) {
      definirClasseDeUmPonto(ponto);
    }
    classes.forEach(Classe::atualizarCentro);
  }

  private void definirClasseDeUmPonto(Pixel ponto) {
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
      maisProx.addPixel(ponto);
    }
  }

  public void calcularKnn(String path, Set<Pixel> dadosRef, int k) throws IOException {
    URL url = Main.class.getClassLoader().getResource(path);
    var img = ImageIO.read(new File(url.getPath()));
    dadosKnn.clear();
    dadosJaProcessados.clear();
    for (int y = 0; y < img.getHeight(); y++) {
      for (int x = 0; x < img.getWidth(); x++) {
        var color = new Color(img.getRGB(x, y), true);
        var pixel = new Pixel(color.getRed(), color.getGreen(), color.getBlue());
        if (dadosJaProcessados.containsKey(pixel)) {
          dadosKnn.add(dadosJaProcessados.get(pixel));
        } else {
          PriorityQueue<Pixel> fila = getKVizinhosMaisProximos(dadosRef, pixel, k);
          Classe classeMaisFrequente = getClasseMaisFrequente(fila);
          pixel.setClasse(classeMaisFrequente);
          dadosKnn.add(pixel);
          dadosJaProcessados.put(pixel, pixel);
        }
      }
    }
    var outImg = getBufferedImage(img, dadosKnn);
    var arquivoSaida =
        String.format("output_file=%s_K=%d.png", path.replace("img/", "").split("\\.")[0], k);
    url = Main.class.getClassLoader().getResource(PATH_SAIDA);
    ImageIO.write(outImg, "png", new File(url.getPath() + arquivoSaida));
    var stringSaida = String.format(MENSAGEM_SAIDA, arquivoSaida);
    System.out.println(stringSaida);
  }

  private PriorityQueue<Pixel> getKVizinhosMaisProximos(Set<Pixel> dadosRef, Pixel pixel, int k) {
    var vizinhosMaisProximos = new PriorityQueue<>(k, SORT_BY_REVERSE_PIXEL_DISTANCE);
    for (Pixel dadoRef : dadosRef) {
      dadoRef.setDistance(pixel);
      vizinhosMaisProximos.offer(dadoRef);
      if (vizinhosMaisProximos.size() > k) {
        vizinhosMaisProximos.poll();
      }
    }
    return vizinhosMaisProximos;
  }

  private Classe getClasseMaisFrequente(PriorityQueue<Pixel> fila) {
    var mapClasseQtd = new HashMap<Classe, Integer>();
    int qtdMaisFrequente = 0;
    Classe classeMaisFrequente = null;
    while (Objects.nonNull(fila.peek())) {
      var classe = fila.poll().getClasse();
      var qtdAtualOuZero = mapClasseQtd.getOrDefault(classe, 0);
      mapClasseQtd.put(classe, qtdAtualOuZero + 1);
      var qtd = mapClasseQtd.get(classe);
      if (qtd > qtdMaisFrequente) {
        qtdMaisFrequente = qtd;
        classeMaisFrequente = classe;
      }
    }
    return classeMaisFrequente;
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
