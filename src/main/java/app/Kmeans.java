package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(Kmeans.class);

    public void calcularKmeans(String path, int k) throws IOException {
        var img = readPathImg(path);
        inicializarClassesAleatorios(k);
        List<Classe> classesIteracaoAnterior = inicializarClassesParaComparacao(k);
        while (centrosNaoConvergiram(classesIteracaoAnterior, k)) {
            classes.forEach(Classe::limparLista);
            for (Pixel pixel : dados) {
                definirClasseDeUmPixel(pixel);
            }
            atualizarCentros();
        }
        printClasses();
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

    private List<Classe> inicializarClassesParaComparacao(int k) {
        return IntStream.range(0, k)
                .mapToObj(classe -> new Classe(Pixel.random()))
                .collect(Collectors.toList());
    }

    private boolean centrosNaoConvergiram(List<Classe> classesIteracaoAnterior, int k) {
        boolean naoConvergiram = !classesIteracaoAnterior.equals(classes);
        for (int i = 0; i < k; i++) {
            classesIteracaoAnterior.get(i).setCentro(classes.get(i).getCentro());
        }
        return naoConvergiram;
    }

    private void definirClasseDeUmPixel(Pixel pixel) {
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

    private void printClasses() {
        for (int c = 0; c < classes.size(); c++) {
            LOGGER.info("Classe {}: {}", c, classes.get(c));
        }
    }

    private void atualizarCentros() {
        for (Classe clase : classes) {
            clase.atualizarCentro();
        }
    }

    private void gerarImagem(BufferedImage img, String path, int c) throws IOException {
        BufferedImage outImg = getBufferedImage(img, dados);
        var arquivoSaida =
                String.format(
                        "KMeans_output_file=%s_K=%d.png",
                        path.replace("img/", "").split("\\.")[0], c);
        URL url = Main.class.getClassLoader().getResource(PATH_SAIDA);
        ImageIO.write(outImg, "png", new File(url.getPath() + arquivoSaida));
        LOGGER.info(MENSAGEM_SAIDA, arquivoSaida);
    }

    private BufferedImage getBufferedImage(BufferedImage img, List<Pixel> dadosKnn) {
        var outImg =
                new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
