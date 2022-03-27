package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static final String PATH_SAIDA = "output/";
    public static final String MENSAGEM_SAIDA =
            "Imagem gerada no caminho build/resources/main/output/{}";
    private static final Kmeans KMEANS = new Kmeans();
    private static final Knn KNN = new Knn();
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        var scanner = new Scanner(System.in);
        var dadosRef = KNN.construirDadosReferenciaKNN();
        boolean stop = false;
        while (!stop) {
            printMenu();
            int input = Integer.parseInt(scanner.nextLine());
            switch (input) {
                case 1:
                    {
                        LOGGER.info("Insira o valor de Classes/Categorias do K-Means");
                        int k = Integer.parseInt(scanner.nextLine());
                        var path = readInput(scanner);
                        KMEANS.calcularKmeans(path, k);
                        break;
                    }
                case 2:
                    {
                        LOGGER.info("Insira o valor de vizinhos do KNN");
                        int k = Integer.parseInt(scanner.nextLine());
                        var path = readInput(scanner);
                        KNN.calcularKnn(path, dadosRef, k);
                        break;
                    }
                default:
                    stop = true;
                    break;
            }
        }
    }

    private static void printMenu() {
        LOGGER.info("========MENU========");
        LOGGER.info("1: Kmeans");
        LOGGER.info("2: KNN");
        LOGGER.info("-1: Parar Programa");
    }

    private static String readInput(Scanner scanner) {
        LOGGER.info("Escreva o nome do arquivo e extensão:");
        var path = new StringBuilder("img/");
        var nomeArquivo = scanner.nextLine();
        path.append(nomeArquivo);
        return path.toString();
    }
}
