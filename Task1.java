package com.company;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    static Scanner in = new Scanner(System.in);
//Немного задание поменял. Вместо двух делителей можно задать несколько, а диапазон выбирать
    public static void main(String[] args) {

        final String divider = "\s*->\s*";
        final var pattern = Pattern.compile("^[0-9]+" + divider + "[a-zA-Z]+$");
        final String rules =
                "\nВходные данные должны соответствовать паттерну:\n" +
                        "\tделитель -> слово при кратности делителю (пробелы можно опустить)\n" +
                        "\tне использовать специальные символы в составе слова\n" +
                        "\tпример: 3 -> fizz\n";
        boolean isContinue = true;
        while (isContinue) {
            System.out.println("Диапазон значений");
            String errorIntMsg = "Это не число\n\n";
            long leftEdge = correctLong("Левая граница: ", errorIntMsg);
            long rightEdge = correctLong("Правая граница: ", errorIntMsg);
            System.out.println("\nЗадание кратности");
            long divNumber = correctLong("Кол-во делителей: ", errorIntMsg);
            var divsAndWords = new HashMap<Long, String>();
            System.out.print(rules);
            in.nextLine();
            String input;
            boolean inCorrect;
            for (int i = 0; i < divNumber; i++) {
                do {
                    System.out.printf("%d. ", i + 1);
                    if (inCorrect = !pattern.matcher(input = in.nextLine().trim()).find())
                        System.out.printf("Не верный ввод\n%s\n", rules);

                } while (inCorrect);

                var temp = input.split(divider);
                divsAndWords.put(Long.parseLong(temp[0]), temp[1]);
            }

            boolean isDefault = false;
            long i = leftEdge;
            int choice = correctInt(
                    "\nВыбор метода: " +
                            "\n1. Обычный" +
                            "\n2. Эксперементальный\n" +
                            "Пункт: ", errorIntMsg);
            do {
                switch (choice) {
                    default:
                        isDefault = true;
                        System.out.print("\nТакого пункта нет");
                        break;
                    case 1:
                        for (String s : MyTools.experimentFizzBuzz(divsAndWords, leftEdge, rightEdge)) {
                            System.out.printf("%d) %s\n", i, s);
                            i++;
                        }
                        break;
                    case 2:
                        for (String s : MyTools.fizzbuzz(divsAndWords, leftEdge, rightEdge)) {
                            System.out.printf("%d) %s\n", i, s);
                            i++;
                        }
                        break;
                }
            } while (isDefault);
            isContinue = correctInt(
                    "\nВыбрать заново?" +
                            "\n1. Да" +
                            "\n2. Нет" +
                            "\nПункт: ", errorIntMsg) == 1 ? true : false;
        }
    }

    /**
     * Метод для получения с консоли только int значения.
     * Для определения unsignedInteger использует isUnsignedInt(String).
     * Выводит указанные сообщения при вводе и выводе.
     *
     * @param inputMsg сообщение для ввода.
     * @param errorMsg сообщение об ошибке ввода.
     * @see MyTools#isUnsignedLong(String)
     */
    public static int correctInt(String inputMsg, String errorMsg) {
        String input;
        boolean inCorrect;
        do {
            System.out.print(inputMsg);
            if (inCorrect = !MyTools.isUnsignedInt(input = in.next()))
                System.out.print(errorMsg);
        } while (inCorrect);
        return Integer.parseUnsignedInt(input);
    }
    /**
     * Метод для получения с консоли только unsignedLong значения.
     * Для определения unsignedLong использует isUnsignedLong(String).
     * Выводит указанные сообщения при вводе и выводе.
     *
     * @param inputMsg сообщение для ввода.
     * @param errorMsg сообщение об ошибке ввода.
     * @see MyTools#isUnsignedLong(String)
     */
    public static long correctLong(String inputMsg, String errorMsg) {
        String input;
        boolean inCorrect;
        do {
            System.out.print(inputMsg);
            if (inCorrect = !MyTools.isUnsignedLong(input = in.next()))
                System.out.print(errorMsg);
        } while (inCorrect);
        return Long.parseUnsignedLong(input);
    }

}

class MyTools {
    // Изначально думал, что если найду другие способы определения кратности числа, то смогу ускорить работу алгоритма на относительно больших числах,
    // но слишком поздно обнаружил, что в итоге сложность алгоритма в лоб почти такая же, что и у того, который использует различные численные алгоритмы.
    // Пока мне не известы методы, которые помогли бы мне хорошо поднять скорость работы, поэтому оставлю оба варианта метода fizzbuzz.
    // Тот, что через факторизацию, будет эксперементальным

    public static String[] fizzbuzz(HashMap<Long, String> map, long leftEdge, long rightEdge) {
        var words = new String[(int) (rightEdge - leftEdge) + 1];
        int index;
        for (long i = leftEdge; i <= rightEdge; i++) {
            index = (int) (i - leftEdge);
            for (long k : map.keySet()) {
                if (i % k == 0) {
                    if (words[index] == null) words[index] = "";
                    words[index] += map.get(k);
                }
            }
            if (words[index] == null) {
                words[index] = String.valueOf(i);
            }
        }
        return words;
    }

    public static String[] experimentFizzBuzz(HashMap<Long, String> map, long leftEdge, long rightEdge) {
        var words = new String[(int) (rightEdge - leftEdge) + 1];
        long index;
        String matchWords;
        for (long i = leftEdge; i <= rightEdge; i++) {
            index = i - leftEdge;
            var factors = factorization(i);
            matchWords = "";
            for (Long k : map.keySet()) { //На этом месте и осознал, что другой алгоритм придумать не могу
                if (isPrime(k)) {
                    if (binSearch(factors, k) != -1) {
                        matchWords += map.get(k);
                    }
                } else {
                    if (i % k == 0) { //Тут ничего не выдумывал
                        matchWords += map.get(k);
                    }
                }
            }
            if (matchWords == "") matchWords = String.valueOf(i);
            words[(int) index] = matchWords;
        }
        return words;
    }

    /**
     * Метод для получения делителей числа.
     * Является реализацией алгоритма перебора делителей.
     * Использует проверку на простоту.
     *
     * @param num число для разложения.
     * @see MyTools#isPrime(long).
     */
    public static ArrayList<Long> factorization(long num) {
        var factors = new ArrayList<Long>();

        if (isPrime(num)) {
            factors.add(num);
            return factors;
        }
        // Двигаемся квадратами, минуя четные числа (кроме 2),
        // и попутно сокращаем факторизируемое число на его очередной делитель.
        // В худшем случае временная сложность будет O(sqrt(n))
        for (long i = 2; i * i <= num; i += 2) {
            if (isPrime(i) && num % i == 0) {
                factors.add(i);
                num /= i;
                // Если число было кратно 2, то сбрасываем до 0 счетчик цикла,
                // чтобы сокращенное число снова проверить на кратность 2
                i -= 2;
            }
            if (isPrime(num)) {
                factors.add(num);
                return factors;
            }
            // Как только число больше не кратно 2,
            // то возвращаем циклу прежний ход по нечетным возможным делителям
            if (i == 2) i--;
        }
        return factors;
    }

    /**
     * Метод для определения простоты числа.
     * Является реалзацией теста Миллера - Рабина.
     * Использует алгоритм быстрой сортировки по модулю
     *
     * @param num натуральное неотрицательно число для проверки
     * @see MyTools#expMod(long, long, long)
     */
    public static boolean isPrime(long num) {
        if (num == 1 || num == 2) return true;
        if (num % 2 == 0) return false;

        int s = 0; // Кол-во двоек в составе числа
        long d = num - 1; // Остаток
        // Представление числа num - 1 = 2^s*d
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }

        Random random = new Random();
        int roundNum = (int) Math.round(Math.log(num)) + 1; // Кол-во раундов рекомендуется брать log(num)
        long randExp;
        for (int i = 0; i < roundNum; i++) {
            randExp = expMod(2 + ((long) random.nextDouble() * (num - 2)), d, num);
            // Если хотя бы одно из условый выполняется, то
            // полученное число можно считать свидетелем
            // простоты и переходить к следующему
            if (randExp == 1 || randExp == num - 1) continue;
            for (int j = 1; j <= s; j++) {
                // Если число по окончанию цикла не стало ни свидетелем просоты,
                // ни указало на свою составность, то оно составное
                if (j == s) return false;
                randExp = expMod(randExp, 2, num);
                if (randExp == 1) return false;
                if (randExp == num - 1) break;
            }
        }
        return true;
    }

    /**
     * Метод для быстрого возведения числа в степень по модулю.
     * Взятат схема слева напрво.
     * Степень переводится в двоичное представление и, в зависимости от
     * значения разряда, производит либо умножение квадрата предыдущего результата на число,
     * возводимое в степень, либо просто берет квадрат пред. результата, при этом всем, берет
     * остаток от деления на каждом шаге.
     * Использует, встроенную в язык, длинную арифметику.
     *
     * @param num число для возведения
     * @param exp степень, в которую нужно возвести число
     * @param mod модуль
     */
    public static Long expMod(long num, long exp, long mod) {
        var binaryN = Long.toBinaryString(exp).toCharArray();
        var result = BigInteger.ONE;
        var bigNum = BigInteger.valueOf(num);
        var bigMod = BigInteger.valueOf(mod);
        for (int i = 0; i < binaryN.length; i++) {
            result = ((binaryN[i] == '1') ? result.multiply(result).multiply(bigNum) : result.multiply(result)).mod(bigMod);
        }
        return Long.parseLong(result.toString());

    }

    /**
     * Метод для поиска значения в отсортированном массиве.
     * Является реализацей алгоритма бинарного поиска.
     *
     * @param arr массив для поиска значения
     * @param num искомое число
     */
    public static long binSearch(ArrayList<Long> arr, long num) {
        long result = -1;
        int rightEdge = arr.size() - 1, leftEdge = 0, mid;
        while (leftEdge <= rightEdge) {
            mid = (rightEdge + leftEdge) / 2;
            if (arr.get(mid) == num) {
                result = arr.get(mid);
                break;
            } else if (arr.get(mid) > num) { // В зависимости от результата сравнения
                rightEdge = mid - 1;         // двигаем границы поиска, исключая
            } else if (arr.get(mid) < num) { // пограниченые элементы.
                leftEdge = mid + 1;
            }
        }
        return result;
    }

    public static boolean isUnsignedLong(String input) {
        try {
            Long.parseUnsignedLong(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isUnsignedInt(String input) {
        try {
            Integer.parseUnsignedInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
