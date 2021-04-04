package com.company;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

/**
 * Программа для выполнения задачи:
 * Посчитать количество пробелов в строке, используя метод
 * {@link Matcher#match(String, String)}.
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
        final String[] mMenu = new String[]{
                "\n1.Подсчет пробелов в заранее заданной строке",
                "2.Подсчет количества вхождений символа в указанной строке",
                "3.Выход"};
        final String[] sMenu = new String[]{
                "\n1.Редактирующий входные данные",
                "2.Не редактирующий входные данные"};
        final String stringForSearchMsg = "\nСтрока для поиска: ";
        final String charForSearchMsg = "Подстрока для поиска: ";
        final String inputMsg = "Пункт: ";
        final String parseErrorMsg = "Это не число\n";
        final String condErrorMsg = "Нет такого пункта\n";
        final String nullExMsg = "Не должно быть пусто\n";
        final String illExMsg = "Должен быть один символ\n";
        final String countMatcherMsg = "\nКол-во вхождений: %d\n";
        final String timeMsg = "Затраченое время: %s сек. %s мсек.\n";
        final Predicate<Integer> mCondition = i -> i >= 1 && i <= mMenu.length;
        final Predicate<Integer> sCondition = i -> i >= 1 && i <= sMenu.length;
        final UnsignedIntCorrecter unsignedIntCorrecter = new UnsignedIntCorrecter(in);
        //endregion
        //region main action
        boolean isContinue = true;
        Tuple<Integer, String[]> result;
        while (isContinue) {
            Arrays.stream(mMenu).forEach(System.out::println);
            int choice = unsignedIntCorrecter.inputUnsignedInt(inputMsg, parseErrorMsg, condErrorMsg, mCondition);
            Arrays.stream(sMenu).forEach(System.out::println);
            boolean isEditingMethod = 1 == unsignedIntCorrecter.inputUnsignedInt(inputMsg, parseErrorMsg, condErrorMsg, sCondition);
            result = switch (choice) {
                case 1 -> tryCountMatch(stringForSearchMsg, charForSearchMsg, INPUT_STRING, TEMPLATE, isEditingMethod, nullExMsg, illExMsg);
                case 2 -> tryCountMatch(stringForSearchMsg, charForSearchMsg, "", "", isEditingMethod, nullExMsg, illExMsg);
                default -> null;
            };
            if (isContinue = !Objects.isNull(result)) {
                System.out.printf(countMatcherMsg, result.getLeftValue());
                System.out.printf(timeMsg, result.getRightValue()[0], result.getRightValue()[1]);
            }
        }
        //endregion
    }

    /**
     * Метод-обертка, который
     * на основе {@code isEditingMethod} сообщает {@link WrapMatcher#selectCountMatcher(String, String, boolean)},
     * какой метод займется подсчетом количества вхождений {@code templateStr} в {@code inStr}, обрабатывая выбрасываемые исключения
     *
     * @param strInputMsg      сообщене при вводе {@code inStr}
     * @param templateInputMsg сообщене при вводе {@code templateStr}
     * @param inStr            строка для поиска
     * @param templateStr      шаблон поиска. Должен состоять из одного символа
     * @param isEditingMethod  при {@code true} используется метод {@link WrapMatcher#editingCountMatcher(String, String)},
     *                         {@code false} - {@link WrapMatcher#countMatcher(String, String)}
     * @param nullExMsg        сообщение при срабатывании {@code NullPointerException}.
     *                         Исключение бросается при {@code inStr = null} и {@code templateStr = null}
     * @param illExMsg         сообщение при срабатывании {@code IllegalArgumentException}.
     *                         Исключение бросается при {@code templateStr.length() != 1}
     * @return кортеж с результатами типа {@code Tuple<Integer, String[]>},
     * где левое значение - кол-во входов {@code templateStr} в {@code inStr},
     * а правое - затраченное время, где первый элемент массива - секунды, а второй - миллисекунды
     */
    public static Tuple<Integer, String[]> tryCountMatch(String strInputMsg,
                                                         String templateInputMsg,
                                                         String inStr,
                                                         String templateStr,
                                                         boolean isEditingMethod,
                                                         String nullExMsg,
                                                         String illExMsg) {
        boolean inCorrect, isEdCountMatcher = (inStr.isEmpty()) || (templateStr.isEmpty());
        Tuple<Integer, String[]> results = null;
        do {
            if (isEdCountMatcher) {
                System.out.print(strInputMsg);
                inStr = in.nextLine();
                System.out.print(templateInputMsg);
                templateStr = in.nextLine();
            } else {
                System.out.println(strInputMsg + inStr);
                System.out.println(templateInputMsg + templateStr);
            }
            try {
                results = WrapMatcher.selectCountMatcher(inStr, templateStr, isEditingMethod);
                inCorrect = false;
            } catch (ExecutionException | InterruptedException concurrentEx) {
                System.out.println(concurrentEx.getMessage());
                inCorrect = true;
            } catch (NullPointerException nullPointerException) {
                System.out.print(nullExMsg);
                inCorrect = true;
            } catch (IllegalArgumentException illegalArgumentException) {
                //так как в Matcher.match(String, String) выбрасывается исключение IllegalArgumentException
                // с сообщением на английском, то решил сделать это сообщение задаваемым. На самом деле, не знаю как лучше писать для заданий
                System.out.print(illExMsg);
                inCorrect = true;
            }
        } while (inCorrect);
        return results;
    }


}
