package org.example;

import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<String> inputFileNames = new ArrayList<>();
        while (inputFileNames.isEmpty()) {
            System.out.print("Введите имена файлов для обработки, разделенные пробелом: ");
            String[] potentialFileNames = scanner.nextLine().split("\\s+");
            for (String fileName : potentialFileNames) {
                if (Files.exists(Paths.get(fileName))) {
                    inputFileNames.add(fileName);
                } else {
                    System.out.println("Файл " + fileName + " не существует. Пожалуйста, введите корректные пути к файлам.");
                    inputFileNames.clear();
                    break;
                }
            }
        }

        String outputDir = ".";
        String prefix = "";
        boolean appendMode = false;
        boolean shortStats = false;
        boolean fullStats = false;

        while (true) {
            System.out.println("Выберите команду: ");
            System.out.println("-o  - Введите путь для выходных файлов (по умолчанию текущая папка)");
            System.out.println("-p  - Введите префикс для выходных файлов (по умолчанию пусто)");
            System.out.println("-a  - Режим добавления в существующие файлы (по умолчанию нет)");
            System.out.println("-s  - Вывести краткую статистику");
            System.out.println("-f  - Вывести полную статистику");
            System.out.println("-start  - Обработать файлы");

            String choice = scanner.nextLine();


            switch (choice) {
                case "-o":
                    System.out.print("Введите путь для выходных файлов (по умолчанию текущая папка): ");
                    outputDir = scanner.nextLine().trim();
                    if (outputDir.isEmpty()) {
                        outputDir = ".";
                    }
                    break;

                case "-p":
                    System.out.print("Введите префикс для выходных файлов (по умолчанию пусто): ");
                    prefix = scanner.nextLine().trim();
                    break;

                case "-a":
                    appendMode = true;
                    System.out.println("Включен режим записи в существующие файлы. ");
                    break;

                case "-s":
                    shortStats = true;
                    System.out.println("Будет выведена краткая статистика.");
                    break;

                case "-f":
                    fullStats = true;
                    System.out.println("Будет выведена полная статистика.");
                    break;

                case "-start":
                    FileFilterUtility utility = new FileFilterUtility(inputFileNames, outputDir, prefix, appendMode, fullStats, shortStats);
                    utility.processFiles();
                    System.out.print("Программа отработала успешно!");
                    return;

                default:
                    System.out.println("Некорректная команда. Повторите ввод.");
                    break;
            }
        }
    }
}