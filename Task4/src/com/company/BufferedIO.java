package com.company;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BufferedIO {
    private final String path;
    /**
     * Знак перевода строки, для использования в консоли
     */
    public static final String LINESEPARATOR = "¶";
    /**
     * Системный знак перевода строки
     */
    public static final String SYSTEMLINESEPARATOR = System.getProperty("line.separator");
    /**
     * Регулярное выражения для поиска слов в строках.
     * Словами считаются последовательности  букв, несодержащие цифры, а также разделяемые служебными знаками
     */
    private static final Pattern REGEX = Pattern.compile("\\s*\\b[a-zA-Zа-яА-Я]+\\b\\s*");

    private boolean isEdited = false;
    private int wordsCount = 0;
    private final List<String> buffer = new LinkedList<>();

    /**
     * Конструктор с задаваемым путем для файла
     *
     * @param path путь к файлу
     * @throws IOException при неудачной попытке открыть файл
     */
    public BufferedIO(String path) throws IOException {
        try (var fileReader = new FileReader(path, StandardCharsets.UTF_8)) {
            this.path = path;
            String nextLine;
            BufferedReader in = new BufferedReader(fileReader);
            while (Objects.nonNull(nextLine = in.readLine())) {
                buffer.add(nextLine);
                wordsCount += countWordsByRegex(nextLine);
            }
        }
    }

    /**
     * Конструктор, который берет данные из заранее сделаного мною файла
     *
     * @throws IOException при неудачной попытке открыть файл
     */
    public BufferedIO() throws IOException {
        path = System.getProperty("user.dir") + "\\src\\com\\company\\data.txt";
        try (var fileReader = new FileReader(path, StandardCharsets.UTF_8)) {
            String nextLine;
            BufferedReader in = new BufferedReader(fileReader);
            while (Objects.nonNull(nextLine = in.readLine())) {
                buffer.add(nextLine);
                wordsCount += countWordsByRegex(nextLine);
            }
        }
    }

    /**
     * Подсчтывает количество слов в строке, используя регулярное выражение {@link #REGEX}
     *
     * @param lineForMatch строка для поиска
     * @return количество слов в строке
     */
    private int countWordsByRegex(String lineForMatch) {
        int countWords = 0;
        Matcher matcher = REGEX.matcher(lineForMatch);
        while (matcher.find()) {
            countWords++;
        }
        return countWords;
    }

    /**
     * Добавляет новый текст в файл
     * Ставит {@code isEdited = true}
     *
     * @param text     текст для добавления
     * @param isAppend {@code true} - добавляет в буфер текст
     *                 {@code false}- очищает буфер идобавляет новый текст
     */
    public void addTextInFile(String text, boolean isAppend) {
        if (!isAppend) {
            buffer.clear();
            wordsCount = 0;
        }
        for (String s : text.split(LINESEPARATOR)) {
            buffer.add(s);
            wordsCount += countWordsByRegex(s);
        }
        isEdited = true;
    }

    /**
     * Запись всего буфера в файл
     * Если файл не найден, то будет предпринята попытка его создать
     *
     * @throws IOException при проблемах с записью в файлб, либо невозножно создать файл, если он не существует
     */
    public void writeInFile() throws IOException {
        try (var fileWriter = new FileWriter(path, false)) {
            for (String line : buffer) {
                fileWriter.write(line + SYSTEMLINESEPARATOR);
            }
        }
    }

    /**
     * Удаление из буфера строки по её номеру
     *
     * @param id номер строки
     * @throws IndexOutOfBoundsException если номер лежит вне границах буфера
     */
    public void removeLine(int id) throws IndexOutOfBoundsException {
        String removedLine = buffer.remove(id);
        wordsCount -= countWordsByRegex(removedLine);
        isEdited = true;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public List<String> getFileOut() {
        return buffer;
    }

    public String getPath() {
        return path;
    }

    public int getWordsCount() {
        return wordsCount;
    }
}
