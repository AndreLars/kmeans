package app;

import java.io.IOException;
import java.util.Scanner;

public class Main {
  public static final String PATH_SAIDA = "output/";
  public static final String MENSAGEM_SAIDA = "Imagem gerada no caminho build/resources/main/output/%s";
  public static final Kmeans kmeans = new Kmeans();
  public static final Knn knn = new Knn();

  public static void main(String[] args) throws IOException {
    var scanner = new Scanner(System.in);
    var dadosRef = knn.construirDadosReferenciaKNN();
    boolean stop = false;
    while (!stop) {
      printMenu();
      int input = Integer.parseInt(scanner.nextLine());
      switch (input) {
        case 1: {
          System.out.println("Insira o valor de Classes/Categorias do K-Means");
          int c = Integer.parseInt(scanner.nextLine());
          var path = readInput(scanner);
          kmeans.calcularKmeans(path, c);
          break;
        }
        case 2: {
          System.out.println("Insira o valor de vizinhos do KNN");
          int k = Integer.parseInt(scanner.nextLine());
          var path = readInput(scanner);
          knn.calcularKnn(path, dadosRef, k);
          break;
        }
        default:
          stop = true;
          break;
      }
    }
  }

  private static void printMenu() {
    System.out.println("========MENU========");
    System.out.println("1: Kmeans");
    System.out.println("2: KNN");
    System.out.println("-1: Parar Programa");
  }

  private static String readInput(Scanner scanner) {
    System.out.println("Escreva o nome do arquivo e extensão:");
    var path = new StringBuilder("img/");
    var nomeArquivo = scanner.nextLine();
    path.append(nomeArquivo);
    return path.toString();
  }
}
