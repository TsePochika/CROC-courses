package com.company;

/**
 * Простой кортеж из 2-х значений.
 * @param <T> тип первого параметра
 * @param <T1> тип второго параметра
 */
public class Tuple<T, T1> {
    private final T leftValue;
    private final T1 rightValue;

    public Tuple(T leftValue, T1 rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public T1 getRightValue() {
        return rightValue;
    }

    public T getLeftValue() {
        return leftValue;
    }
}
