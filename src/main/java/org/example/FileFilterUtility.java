package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileFilterUtility {
    private List<String> inputFileNames;
    private String outputDir;
    private String prefix;
    private boolean appendMode;
    private boolean fullStats;
    private boolean shortStats;

    private List<Integer> integers = new ArrayList<>();
    private List<Double> floats = new ArrayList<>();
    private List<String> strings = new ArrayList<>();

    public FileFilterUtility(List<String> inputFileNames, String outputDir, String prefix, boolean appendMode, boolean fullStats, boolean shortStats) {
        this.inputFileNames = inputFileNames;
        this.outputDir = outputDir;
        this.prefix = prefix;
        this.appendMode = appendMode;
        this.fullStats = fullStats;
        this.shortStats = shortStats;
    }

    public List<Integer> getIntegers() {
        return integers;
    }

    public List<Double> getFloats() {
        return floats;
    }

    public List<String> getStrings() {
        return strings;
    }




    public void processFiles() {
        for (String inputFileName : inputFileNames) {
            processFile(inputFileName);
        }

        writeOutputFiles();
        printStatistics();
    }

    private void processFile(String inputFileName) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + inputFileName + ": " + e.getMessage());
        }
    }

    void processLine(String line) {
        try {
            integers.add(Integer.parseInt(line));
        } catch (NumberFormatException e1) {
            try {
                floats.add(Double.parseDouble(line));
            } catch (NumberFormatException e2) {
                strings.add(line);
            }
        }
    }

    void writeOutputFiles() {
        writeFile("integers.txt", integers);
        writeFile("floats.txt", floats);
        writeFile("strings.txt", strings);
    }

    private <T> void writeFile(String fileName, List<T> data) {
        if (data.isEmpty()) return;

        Path filePath = Paths.get(outputDir, prefix + fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                appendMode ? new StandardOpenOption[]{StandardOpenOption.APPEND, StandardOpenOption.CREATE}
                        : new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING})) {
            for (T item : data) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл " + filePath + ": " + e.getMessage());
        }
    }

    void printStatistics() {
        if (shortStats || fullStats) {
            System.out.println("Статистика:");
            printIntegerStatistics();
            printFloatStatistics();
            printStringStatistics();
        }
    }

    private void printIntegerStatistics() {
        if (integers.isEmpty()) return;

        System.out.println("Целые числа: " + integers.size());
        if (fullStats) {
            int min = Collections.min(integers);
            int max = Collections.max(integers);
            int sum = integers.stream().mapToInt(Integer::intValue).sum();
            double avg = sum / (double) integers.size();
            System.out.println("Мин: " + min + ", Макс: " + max + ", Сумма: " + sum + ", Среднее: " + avg);
        }
    }

    private void printFloatStatistics() {
        if (floats.isEmpty()) return;

        System.out.println("Вещественные числа: " + floats.size());
        if (fullStats) {
            double min = Collections.min(floats);
            double max = Collections.max(floats);
            double sum = floats.stream().mapToDouble(Double::doubleValue).sum();
            double avg = sum / floats.size();
            System.out.println("Мин: " + min + ", Макс: " + max + ", Сумма: " + sum + ", Среднее: " + avg);
        }
    }

    private void printStringStatistics() {
        if (strings.isEmpty()) return;

        System.out.println("Строки: " + strings.size());
        if (fullStats) {
            int minLength = strings.stream().mapToInt(String::length).min().orElse(0);
            int maxLength = strings.stream().mapToInt(String::length).max().orElse(0);
            System.out.println("Мин длина: " + minLength + ", Макс длина: " + maxLength);
        }
    }
}