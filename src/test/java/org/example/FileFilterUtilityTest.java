package org.example;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.*;

public class FileFilterUtilityTest {
    private List<String> inputFileNames;
    private String outputDir;
    private String prefix;
    private boolean appendMode;
    private boolean fullStats;
    private boolean shortStats;

    private File tempDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("testDir").toFile();
        outputDir = tempDir.getAbsolutePath();
        prefix = "test_";
        appendMode = false;
        fullStats = true;
        shortStats = false;

        // Create temporary input files
        inputFileNames = new ArrayList<>();
        createTempFile("input1.txt", "123\nabc\n45.67\n");
        createTempFile("input2.txt", "789\nxyz\n0.12\n");
    }

    @After
    public void tearDown() {
        for (String fileName : inputFileNames) {
            new File(fileName).delete();
        }
        for (File file : tempDir.listFiles()) {
            file.delete();
        }
        tempDir.delete();
    }

    private void createTempFile(String fileName, String content) throws IOException {
        File tempFile = new File(tempDir, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(content);
        }
        inputFileNames.add(tempFile.getAbsolutePath());
    }

    @Test
    public void testProcessFiles() {
        FileFilterUtility utility = new FileFilterUtility(inputFileNames, outputDir, prefix, appendMode, fullStats, shortStats);
        utility.processFiles();

        // Check integers file
        File integersFile = new File(outputDir, prefix + "integers.txt");
        assertTrue(integersFile.exists());
        assertFileContent(integersFile, "123\n789\n");

        // Check floats file
        File floatsFile = new File(outputDir, prefix + "floats.txt");
        assertTrue(floatsFile.exists());
        assertFileContent(floatsFile, "45.67\n0.12\n");

        // Check strings file
        File stringsFile = new File(outputDir, prefix + "strings.txt");
        assertTrue(stringsFile.exists());
        assertFileContent(stringsFile, "abc\nxyz\n");
    }

    @Test
    public void testProcessLine() {
        FileFilterUtility utility = new FileFilterUtility(inputFileNames, outputDir, prefix, appendMode, fullStats, shortStats);

        utility.processLine("123");
        utility.processLine("abc");
        utility.processLine("45.67");

        assertEquals(List.of(123), utility.getIntegers());
        assertEquals(List.of(45.67), utility.getFloats());
        assertEquals(List.of("abc"), utility.getStrings());
    }

    @Test
    public void testWriteOutputFiles() throws IOException {
        FileFilterUtility utility = new FileFilterUtility(inputFileNames, outputDir, prefix, appendMode, fullStats, shortStats);

        utility.processLine("123");
        utility.processLine("abc");
        utility.processLine("45.67");

        utility.writeOutputFiles();

        // Check integers file
        File integersFile = new File(outputDir, prefix + "integers.txt");
        assertTrue(integersFile.exists());
        assertFileContent(integersFile, "123\n");

        // Check floats file
        File floatsFile = new File(outputDir, prefix + "floats.txt");
        assertTrue(floatsFile.exists());
        assertFileContent(floatsFile, "45.67\n");

        // Check strings file
        File stringsFile = new File(outputDir, prefix + "strings.txt");
        assertTrue(stringsFile.exists());
        assertFileContent(stringsFile, "abc\n");
    }

    @Test
    public void testPrintStatistics() {
        FileFilterUtility utility = new FileFilterUtility(inputFileNames, outputDir, prefix, appendMode, fullStats, shortStats);

        utility.processLine("123");
        utility.processLine("abc");
        utility.processLine("45.67");

        // Redirect output to a byte array stream
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        utility.printStatistics();

        String expectedOutput = "Статистика:\n" +
                "Целые числа: 1\n" +
                "Мин: 123, Макс: 123, Сумма: 123, Среднее: 123.0\n" +
                "Вещественные числа: 1\n" +
                "Мин: 45.67, Макс: 45.67, Сумма: 45.67, Среднее: 45.67\n" +
                "Строки: 1\n" +
                "Мин длина: 3, Макс длина: 3\n";

        String actualOutput = outContent.toString().replace("\r\n", "\n").replace("\r", "\n");
        expectedOutput = expectedOutput.replace("\r\n", "\n").replace("\r", "\n");

        assertEquals(expectedOutput, actualOutput);
    }

    private void assertFileContent(File file, String expectedContent) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            content = content.replace("\r\n", "\n").replace("\r", "\n");
            expectedContent = expectedContent.replace("\r\n", "\n").replace("\r", "\n");
            assertEquals(expectedContent, content);
        } catch (IOException e) {
            fail("Ошибка при чтении файла " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }
}