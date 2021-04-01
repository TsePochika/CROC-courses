package com.company;

import java.io.*;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Программа для выполнения задачи:
 * В текстовом файле слова могут быть разделены одним или несколькими пробелами, или символами перевода строки.
 * Необходимо реализовать программу, считающую количество слов в файле и выводящую результат на экран.
 * Путь к файлу задается первым аргументом командной строки (args[0]).
 * В случае, если аргумент не задан – кидать IllegalArgumentException.
 * При ошибке открытия файла сообщать об этом в консоль без вывода стектрейса.
 * <p>
 * Пример:
 * <p>
 * [in]
 * Забыл   Панкрат  Кондратьевич домкрат,
 * А без домкрату ну  не  поднять на тракте трактор.
 * <p>
 * [out]
 * 13
 */
public class Main {

    public static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static BufferedIO io = null;

    public static void main(String[] arg) {
        boolean isContinue = false;

        try {
            io = newBufferedIO(arg);
            isContinue = true;
        } catch (IOException e) {
            System.out.println("Файл не найден либо путь поврежден.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        if (isContinue) {
            //region init
            final String[] menu = new String[]{
                    "\n1.Подстчет слов в файле",
                    "2.Добавление строки в файл",
                    "3.Перезапись файла",
                    "4.Удалене строки",
                    "5.Выход"
            };
            final String newLineHelp = "Для перевода строки использовать ¶ (Alt+20)\n";
            final String intInput = "Пункт: ";
            final String stringInput = "Введите строку: ";
            final String nanMsg = "Это не число\n";
            final String condErrorMsg = "Нет такого пункта\n";
            final String ifNullMsg = "Строка пуста\n";
            final String lineNumInput = "Номер строки: ";
            final String outOfBoundMsg = "Нет строки с таким номером";
            final String wordsCount = "Кол-во слов: ";
            final String fileNameStr = String.format("\nВ файле %s следующий текст:\n", new File(io.getPath()).getName());
            final Predicate<Integer> intCondition = i -> i >= 1 && i <= menu.length;
            //endregion
            //region menu and actions
            while (isContinue) {
                Arrays.stream(menu).forEach(System.out::println);
                int choice = inputUnsignedInt(intInput, ifNullMsg, nanMsg, condErrorMsg, intCondition);
                switch (choice) {
                    case 1 -> {
                        displayLines(fileNameStr);
                        System.out.println(wordsCount + io.getWordsCount());
                    }
                    case 2, 3 -> {
                        System.out.print(newLineHelp);
                        io.addTextInFile(inputCorrectString(stringInput, ifNullMsg), (2 == choice));
                    }
                    case 4 -> {
                        displayLines(fileNameStr);
                        tryRemoveLineFromFile(lineNumInput, ifNullMsg, nanMsg, outOfBoundMsg);
                    }
                    case 5 -> isContinue = false;
                }
            }
            //endregion
            //region write in file
            if (io.isEdited()) {
                try {
                    io.writeInFile();
                } catch (IOException e) {
                    System.out.println("Не возможно записать текст в файл");
                }
            }
            //endregion
        }
    }

    public static void displayLines(String str) {
        System.out.print(str);
        final int[] num = {0};
        io.getFileOut().forEach(s -> System.out.printf("%d)%s\n", ++num[0], s));
    }

    /**
     * Инициализация объекта класса BufferedIO с заданным путем к файлу, через аргуметы командной строки
     *
     * @param arg аргументы командной строки
     * @return новый объект {@code BufferedIO}
     * @throws IOException              при неудачной попытке открыть файл по указанному пути
     * @throws IllegalArgumentException при отсутствии аргумента командной строки с путем к файлу с данными
     */
    public static BufferedIO newBufferedIO(String[] arg) throws IOException, IllegalArgumentException {
        BufferedIO bufferedIO;
        if (arg.length > 0 && arg[0].isEmpty()) {
            bufferedIO = new BufferedIO(arg[0]);
        } else {
            throw new IllegalArgumentException("Путь к файлу, в качестве аргумента командной строке, не задан");
        }
        return bufferedIO;
    }

    /**
     * Пытается удалить выбранную строку из буфера файла
     * При вводе несуществующего номера строки, обрабатывает IndexOutOfBoundsException, выводя {@code errorMsg}
     *
     * @param inputMsg       сообщение при вводе
     * @param msgIfInputNull сообщение об ошибке, при вводе пустой строки
     * @param parseErrorMsg  сообщение об ошибке при вводе любого другого типа, кроме UnsignedInt
     * @param errorMsg       сообщение при срабатывании {@code IndexOutOfBoundsException}
     * @throws {@code IndexOutOfBoundsException} если был введен не существующий номер строки
     */
    public static void tryRemoveLineFromFile(String inputMsg,
                                             String msgIfInputNull,
                                             String parseErrorMsg,
                                             String errorMsg) {
        boolean inCorrect;
        do {
            try {
                io.removeLine(inputUnsignedInt(inputMsg, msgIfInputNull, parseErrorMsg) - 1);
                inCorrect = false;
            } catch (IndexOutOfBoundsException e) {
                System.out.println(errorMsg);
                inCorrect = true;
            }
        } while (inCorrect);
    }

    /**
     * Получает ввод с консоли через BufferedReader, обрабатывая IOException
     *
     * @return Строка с консоли, либо пустая строка, если было выброшено IOException
     */
    public static String tryGetString() {
        String input;
        try {
            input = in.readLine();
        } catch (IOException e) {
            input = "";
        }
        return input;
    }

    /**
     * Получает ввод непустой строки
     *
     * @param inputMsg       сообщение при вводе
     * @param msgIfInputNull сообщение об ошибке, при вводе пустой строки
     * @return непустая строка
     */
    public static String inputCorrectString(String inputMsg,
                                            String msgIfInputNull) {
        String input;
        boolean inCorrect;
        do {
            System.out.print(inputMsg);
            input = tryGetString();
            if (inCorrect = input.isEmpty()) {
                System.out.print(msgIfInputNull);
            }
        } while (inCorrect);
        return input;
    }

    /**
     * Получает с консоли ввод числа типа UnsignedInt, соответствующий условию
     *
     * @param inputMes          сообщение при вводе числа
     * @param msgIfInputNull    сообщение об ошибке, при вводе пустой строки
     * @param parseErrorMsg     сообщение об ошибке при вводе любого другого типа, кроме UnsignedInt
     * @param conditionErrorMsg сообщение об ошибке, при не соответсятвии ввода условию
     * @param condition         условие, которому должен соответсвовать ввод. При не соответствии выводится сообщение об ошибке
     * @return UnsignedInt, соответсятвующий условию
     * @see #tryParseUnsignedInt(String)
     * @see #inputCorrectString(String, String)
     */
    public static int inputUnsignedInt(String inputMes,
                                       String msgIfInputNull,
                                       String parseErrorMsg,
                                       String conditionErrorMsg,
                                       Predicate<Integer> condition) {
        int intInput;
        boolean inCorrect, isParsed, isCond;
        String errorMsg = "";
        do {
            intInput = tryParseUnsignedInt(inputCorrectString(inputMes, msgIfInputNull));

            isParsed = (-1 != intInput);
            isCond = condition.test(intInput);

            if (!isParsed) {
                errorMsg = parseErrorMsg;
            } else if (!isCond) {
                errorMsg = conditionErrorMsg;
            }

            if (inCorrect = (!isParsed || !isCond)) {
                System.out.print(errorMsg);
            }
        } while (inCorrect);
        return intInput;
    }

    /**
     * Получает с консоли ввод числа типа UnsignedInt
     *
     * @param inputMes       сообщение при вводе числа
     * @param msgIfInputNull сообщение об ошибке, при вводе пустой строки
     * @param parseErrorMsg  сообщение об ошибке при вводе любого другого типа, кроме unsignedInt
     * @return UnsignedInt
     */
    public static int inputUnsignedInt(String inputMes,
                                       String msgIfInputNull,
                                       String parseErrorMsg) {
        int intInput;
        boolean inCorrect;
        do {
            intInput = tryParseUnsignedInt(inputCorrectString(inputMes, msgIfInputNull));
            if (inCorrect = (-1 == intInput)) {
                System.out.print(parseErrorMsg);
            }
        } while (inCorrect);
        return intInput;
    }

    /**
     * Пытается преобразовать строку в UnsignedInt
     *
     * @param input строка, для преобразования
     * @return UnsignedInt, если преобразование прошло успешно, иначе {@code -1}
     */
    public static int tryParseUnsignedInt(String input) {
        int outNum;
        try {
            outNum = Integer.parseUnsignedInt(input);
        } catch (NumberFormatException e) {
            outNum = -1;
        }
        return outNum;
    }
}