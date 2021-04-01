package com.company;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Программа для выполнения задачи:
 * Посчитать количество пробелов в строке, используя метод
 * {@link Matcher#match(String, String)}
 */
public class Main {
    public static final Scanner in = new Scanner(System.in);

    public static final String INPUT_STRING = "\n" + """
            Невежество есть мать промышленности, как и суеверий.
            Сила размышления и воображения подвержена ошибкам; но привычка двигать рукой или ногой
            не зависит ни от того, ни от другого. Поэтому мануфактуры лучше всего процветают там, где
            наиболее подавлена духовная жизнь, так что мастерская может рассматриваться как машина,
            части которой составляют люди.""";
    public static final String TEMPLATE = " ";

    public static void main(String[] args) {
        //region initialize
        final String[] menu = new String[]{
                "\n1.Подсчет пробелов в заранее заданной строке",
                "2.Подсчет количества вхождений символа в указанной строке",
                "3.Выход"};
        final String stringForSearchMsg = "\nСтрока для поиска: ";
        final String charForSearchMsg = "Подстрока для поиска: ";
        final String inputMsg = "Пункт: ";
        final String parseErrorMsg = "Это не число\n";
        final String condErrorMsg = "Нет такого пункта\n";
        final String countMatcherMsg = "\nКол-во вхождений: %d\n";
        final String timeMsg = "Затраченое время: %s сек. %s мсек.\n";
        final Predicate<Integer> condition = i -> i >= 1 && i <= menu.length;
        //endregion
        //region main action
        boolean isContinue = true;
        while (isContinue) {
            //region menu and action
            Arrays.stream(menu).forEach(System.out::println);
            long currentTime = 0;
            int result = 0, choice = inputUnsignedInt(inputMsg, parseErrorMsg, condErrorMsg, condition);
            switch (choice) {
                case 1 -> {
                    System.out.println(stringForSearchMsg + INPUT_STRING);
                    System.out.println(charForSearchMsg + String.format("\"%s\"", TEMPLATE));
                    currentTime = System.currentTimeMillis();
                    result = countMatcher(INPUT_STRING, TEMPLATE);
                }
                case 2 -> {
                    in.nextLine();//очищаем сканер

                    String stringForSearch, charForSearch;
                    System.out.print(stringForSearchMsg);
                    stringForSearch = in.nextLine();
                    boolean inCorrect;
                    do {
                        try {
                            System.out.print(charForSearchMsg);
                            charForSearch = in.nextLine();
                            currentTime = System.currentTimeMillis();
                            result = countMatcher(stringForSearch, charForSearch);
                            inCorrect = false;
                        } catch (IllegalArgumentException | NullPointerException e) {
                            System.out.println(e.getMessage());
                            inCorrect = true;
                        }
                    } while (inCorrect);
                }
                case 3 -> isContinue = false;
            }
            //endregion
            //region timer
            if (isContinue) {
                System.out.printf(countMatcherMsg, result);
                long now = System.currentTimeMillis();
                String[] secAndMiles = String.valueOf((double) (now - currentTime) / 1000).split("\\.");
                System.out.printf(timeMsg, secAndMiles[0], secAndMiles[1]);
            }
            //endregion
        }
        //endregion
    }

    /**
     * Ускореение для супер поисковика
     *
     * @param inString       строка для поиска
     * @param stringForFound символ для подсчета количества вхождений
     * @return количество вхождений {@code stringForFound} в {@code inString}
     * @throws IllegalArgumentException при пустом {@code stringForFound} или имеющим более одного символа
     * @throws NullPointerException     при {@code stringForFound = null}
     */
    public static int countMatcher(String inString, String stringForFound) throws IllegalArgumentException, NullPointerException {
        //как я понял, нельзя менять Matcher.match(String, String) и считать вхождения каким-либо другим образом,
        //про запрет на изменения входной строки не услышал.
        inString = inString.replaceAll("[^" + stringForFound + "]", "");//удаляем все символы, кроме нужных для подсчета
        return Matcher.match(inString, stringForFound) //если во входной строке есть хотя бы одно вхождение нужного символа
                ? (int) inString.subSequence(0, inString.length()).chars().//то представляем входную строку как поток её символов в ASCII кодировке
                parallel().//распараллеливаем поток символов на части и отдаем по куску каждому ядру. Эти части собирутся в конце всех операций
                mapToObj(p -> Matcher.match(String.valueOf((char) p), stringForFound)).//применяем метод поиска к каждому символу,переведенному из ASCII кодировки
                filter(s -> s).count()//выбираем только те случаи, которые вернули true и подсчитываем их
                : 0;//если вхождений нет
    }

    /**
     * Получает с консоли ввод числа типа UnsignedInt, соответствующий условию
     *
     * @param inputMes          сообщение при вводе числа
     * @param parseErrorMsg     сообщение об ошибке при вводе любого другого типа, кроме UnsignedInt
     * @param conditionErrorMsg сообщение об ошибке, при не соответсятвии ввода условию
     * @param condition         условие, которому должен соответсвовать ввод. При не соответствии выводится сообщение об ошибке
     * @return UnsignedInt, соответсятвующий условию
     */
    public static int inputUnsignedInt(String inputMes, String parseErrorMsg, String conditionErrorMsg, Predicate<Integer> condition) {
        boolean inCorrect, isParsed, isCond;
        String errorMsg = "";
        int intInput;

        do {
            System.out.print(inputMes);
            intInput = tryParseUnsignedInt(in.next());

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
     * Пытается преобразовать строку в unsignedInt
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
