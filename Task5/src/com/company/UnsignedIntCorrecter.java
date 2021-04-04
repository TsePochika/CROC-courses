package com.company;

import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Использует поток ввода для получения UnsignedInt числа.
 */
public class UnsignedIntCorrecter {
    private final Scanner IN;

    public UnsignedIntCorrecter(Scanner in) {
        Objects.requireNonNull(in);
        this.IN = in;
    }

    /**
     * Получает с консоли ввод числа типа UnsignedInt, соответствующий условию
     *
     * @param inputMes          сообщение при вводе числа
     * @param parseErrorMsg     сообщение об ошибке при вводе любого другого типа, кроме UnsignedInt
     * @param conditionErrorMsg сообщение об ошибке, при не соответсятвии ввода условию
     * @param condition         условие, которому должен соответсвовать ввод. При не соответствии выводится {@code conditionErrorMsg}
     * @return UnsignedInt, соответсятвующий условию
     */
    public int inputUnsignedInt(String inputMes,
                                String parseErrorMsg,
                                String conditionErrorMsg,
                                Predicate<Integer> condition) {
        boolean inCorrect, isParsed, isCond;
        String errorMsg = "";
        int intInput;

        do {
            System.out.print(inputMes);
            intInput = tryParseUnsignedInt(IN.nextLine());

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
     * Пытается преобразовать строку в UnsignedInt
     *
     * @param input строка, для преобразования
     * @return UnsignedInt, если преобразование прошло успешно, иначе {@code -1}
     */
    public int tryParseUnsignedInt(String input) {
        int outNum;
        try {
            outNum = Integer.parseUnsignedInt(input);
        } catch (NumberFormatException e) {
            outNum = -1;
        }
        return outNum;
    }
}
