package com.company.Void;

import java.util.*;

/**
 * <p> Генератор уникального натурального числа из нескольких
 * <p>Реализация функции сопряжения Кантора
 * <p>See <a href="https://en.wikipedia.org/wiki/Pairing_function">Pairing function</a>
 */
public class UniqueIdGenerator {

    public static long pairing(long a, long b) {
        long firstPair = a + b;
        long secondPair = firstPair + 1;
        return ((firstPair * secondPair) / 2) + b;
    }

    public static long pairingMulti(Queue<Long> numbsForPairing) {
        if (numbsForPairing.size() < 2) {
            throw new IllegalArgumentException();
        }

        long firstNum = numbsForPairing.poll();
        long secondNum = numbsForPairing.poll();
        long result = UniqueIdGenerator.pairing(firstNum, secondNum);

        while (numbsForPairing.size() != 0) {
            result = UniqueIdGenerator.pairing(result, numbsForPairing.poll());
        }
        return result;
    }

    public static Queue<Long> unPairingMulti(long pairingResult, int numbsCount) {
        InputErrorHandling.requireUnsignedLong(pairingResult);
        Deque<Long> result = new ArrayDeque<>();

        Tuple<Long, Long> complexPair = UniqueIdGenerator.unPairing(pairingResult);
        if (numbsCount == 2) {
            result.push(complexPair.getRight());
            result.push(complexPair.getLeft());
        } else if (numbsCount > 2) {
            for (int i = 1; i < numbsCount; i++) {
                result.push(complexPair.getRight());
                if (i == numbsCount - 1) {
                    result.push(complexPair.getLeft());
                }
                complexPair = UniqueIdGenerator.unPairing(complexPair.getLeft());
            }
        }
        return result;
    }

    public static Tuple<Long, Long> unPairing(long pairingResult) {
        double w = Math.floor((Math.sqrt(8 * pairingResult + 1) - 1) / 2);
        double t = (w * (w + 1)) / 2;
        double y = pairingResult - t;
        double x = w - y;
        return new Tuple<>((long) x, (long) y);
    }
}
