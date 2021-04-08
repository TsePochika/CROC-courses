package com.company;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;

/** Программа для решения задачи:
 * <blockquote><pre>
 * В виде строки задан относительный путь в файловой системе, в котором:
 * "." означает текущую директорию;
 * ".." означает родительскую директорию по отношению к текущей;
 * "/" используется в качестве разделителя директорий.
 *
 * Реализовать функцию, выполняющую "нормализацию" заданного пути,
 * то есть, удаляющую из него лишние директории
 * с учетом переходов "." и "..".
 *
 * Пример:
 * [in]
 * "КРОК/task_6_2/src/./../../task_6_1/../../../мемы/котики"
 * [out]
 * "../мемы/котики"
 * </pre></blockquote>
 */
public class Main {
    public static final String DEF_PATH = "КРОК/task_6_2/src/./../../task_6_1/../../../мемы/котики";
    public static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        //region init
        final String[] subMenu = new String[]{
                "\n1.Корректировка через RegExp",
                "2.Корректировка через LinkedList",
        };
        final String[] mainMenu = new String[]{
                "\n1.Корректировка заранее заданного пути",
                "2.Ввод пути для корректировки",
                "3.Выход"
        };

        final String inputTemplate = "\n" + """
                Путь должен соответствовать следующим требованиям:
                1. Имена директорий и символы перехода разделяются знаком "/".
                2. В именах директорий не должно быть пробелов и следующих знаков:
                                         / ? < > : " *
                3. Если в имени директроии содержится пробел, то его следует заметить знаком "_".
                4. В именах директорий разрешается ставить точку только в начале и серидие.
                5. Конец пути не должен содержать пустой знак разделения.
                """;
        final String inputMsg = "Путь: ";
        final String outputMsg = "Упрощенный путь: ";
        final String menuInputMsg = "Пункт: ";
        final String menuErrMsg = "Нет такого пункта\n";
        final String menuParseMsg = "Это не число\n";
        final Predicate<Integer> mainMCond = i -> i >= 1 && i <= mainMenu.length;
        final Predicate<Integer> subMCond = i -> i >= 1 && i <= mainMenu.length;
        final UnsignedIntCorrecter unsignedIntCorrecter = new UnsignedIntCorrecter(in);
        //endregion
        //region menu and actions
        boolean isContinue = true;
        while (isContinue) {
            Arrays.stream(mainMenu).forEach(System.out::println);
            int mainChoice = unsignedIntCorrecter.inputUnsignedInt(menuInputMsg, menuParseMsg, menuErrMsg, mainMCond);
            String simplePath = switch (mainChoice) {
                case 1 -> {
                    System.out.println(inputMsg + DEF_PATH);
                    Arrays.stream(subMenu).forEach(System.out::println);
                    int subChoice = unsignedIntCorrecter.inputUnsignedInt(menuInputMsg, menuParseMsg, menuErrMsg, subMCond);
                    yield selectSimplifyMethod(subChoice, DEF_PATH);
                }
                case 2 -> {
                    boolean inCorrect;
                    String sPath = null;
                    Arrays.stream(subMenu).forEach(System.out::println);
                    int subChoice = unsignedIntCorrecter.inputUnsignedInt(menuInputMsg, menuParseMsg, menuErrMsg, subMCond);
                    System.out.print(inputTemplate);
                    do {
                        System.out.print(inputMsg);
                        try {
                            sPath = selectSimplifyMethod(subChoice, in.nextLine());
                            inCorrect = false;
                        } catch (NullPointerException | IllegalArgumentException ex) {
                            System.out.println(ex.getMessage());
                            inCorrect = true;
                        }
                    } while (inCorrect);

                    yield sPath;
                }
                default -> null;
            };
            if (isContinue = !Objects.isNull(simplePath)) {
                System.out.println(outputMsg + simplePath);
            }
        }
        //endregion
    }

    public static String selectSimplifyMethod(int choice, String path) throws NullPointerException, IllegalArgumentException {
        return switch (choice) {
            case 1 -> FilePathSimplifier.simplifyByRegExp(path);
            case 2 -> FilePathSimplifier.simplifyByLinkedList(path);
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        };
    }
}
