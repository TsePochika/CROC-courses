package com.company;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * Обертка-ускоритель для класса {@link Matcher}.
 */
public class WrapMatcher {
    /**
     * регудярное выражение для разделения строки по буквам
     */
    private static final String REGEXFORSPLIT = "\\.?";
    /**
     * количество потоков, для распараллеливани задачи
     */
    private static final int NUMOFTHREADS = 400;
    /* Задал константой, так как, при зависимости от длины строки,
     * кол-во потоков может быть слишком большим.
     * Это можно обойти, однако алгоритмы для определения оптимального числа
     * могут прибавить времени к общему времени подсчета количества вхождений
     */

    /**
     * Супер ускореение для супер поисковика
     *
     * @param inString    строка для поиска
     * @param templateStr символ для подсчета количества вхождений
     * @return количество вхождений {@code templateStr} в {@code inString}
     * @throws IllegalArgumentException при  {@code templateStr.length()!=1}
     * @throws NullPointerException     при {@code templateStr = null} либо при {@code inString = null}
     * @throws ExecutionException       при возникщей исключительной ситуации при параллельном вычислении, используя {@link ForkJoinPool#submit(Runnable)}
     * @throws InterruptedException     при прерывании ждущего потока, в той же ситуации что и {@code ExecutionException}
     */
    public static long editingCountMatcher(String inString, String templateStr)
            throws
            IllegalArgumentException,
            NullPointerException,
            InterruptedException,
            ExecutionException
    {
        Objects.requireNonNull(inString);
        //как я понял, нельзя менять Matcher.match(String, String) и считать вхождения каким-либо другим образом,
        //про запрет на изменения входной строки не услышал.
        String finalInString = inString.replaceAll("[^" + templateStr + "]", "");//удаляем все символы, кроме нужных для подсчета
        if (finalInString.isEmpty()) return 0;//если ничего не осталось, то и нечего считать
        List<String> literals = Arrays.asList(finalInString.split(REGEXFORSPLIT));//разделяем строку по буквам
        return new ForkJoinPool(NUMOFTHREADS).//определяем кол-во потоков для распараллеливания задачи
                submit(() -> literals.parallelStream().//указываем, что последующие действия - параллельны
                map(s -> Matcher.match(s, templateStr)).count()//проверяем символ на соответствие и подсчитываем кол-во соответствий(на самом деле всегда будет true)
        ).get();//получаем значение, вычисления, для которого закончились
    }
    /* Ифнормация по поводу методов подсчета, где использовался ForkJoinPool<T>.
     * Хотел использовать RecursiveTask<T>, однако распараллеливание таким образом давало практически такую же скорость работы,
     * что и через ForkJoinPool<T>.submit(Callable<T>), хотя, мне казалось,
     * что самостоятельное рекурсивное деление последовательности покажет результат лучше.
     * Хотелось бы понять, причину этого
     */

    /**
     * Ускореение для супер поисковика
     *
     * @param inString    строка для поиска
     * @param templateStr символ для подсчета количества вхождений
     * @return количество вхождений {@code templateStr} в {@code inString}
     * @throws IllegalArgumentException при  {@code templateStr.length()!=1}
     * @throws NullPointerException     при {@code templateStr = null} либо при {@code inString = null}
     * @throws ExecutionException       при возникщей исключительной ситуации при параллельном вычислении, используя {@link ForkJoinPool#submit(Runnable)}
     * @throws InterruptedException     при прерывании ждущего потока, в той же ситуации что и {@code ExecutionException}
     */
    public static long countMatcher(String inString, String templateStr)
            throws
            IllegalArgumentException,
            NullPointerException,
            ExecutionException,
            InterruptedException
    {
        Objects.requireNonNull(inString);
        List<String> literals = Arrays.asList(inString.split(REGEXFORSPLIT));//разделяем строку по буквам
        return new ForkJoinPool(NUMOFTHREADS).//определяем кол-во потоков для распараллеливания задачи
                submit(() -> literals.parallelStream().//указываем, что последующие действия - параллельны
                filter(s -> Matcher.match(s, templateStr)).count()//проверяем символ на соответствие и подсчитываем кол-во соответствий
        ).get();//получаем значение, вычисления, для которого закончились
    }

    /**
     * Выбирает метод для подсчета количества вхождений {@code templateStr} в {@code inStr} и считает затраченное на вычисления время в секундах и миллисекундах
     *
     * @param inStr           строка для поиска
     * @param templateStr     шаблон поиска. Должен состоять из одного символа
     * @param isEditingMethod при {@code true} используется метод {@link WrapMatcher#editingCountMatcher(String, String)},
     *                        {@code false} - {@link WrapMatcher#countMatcher(String, String)}
     * @return кортеж с результатами типа {@code Tuple<Integer, String[]>},
     * * где левое значение - кол-во входов {@code templateStr} в {@code inStr},
     * * а правое - затраченное время, где первый элемент массива - секунды, а второй - миллисекунды
     * @throws IllegalArgumentException при  {@code templateStr.length()!=1}
     * @throws NullPointerException     при {@code templateStr = null}
     * @throws ExecutionException       при возникщей исключительной ситуации при параллельном вычислении, используя {@link ForkJoinPool#submit(Runnable)}
     *                                  в {@link WrapMatcher#editingCountMatcher(String, String)}
     *                                  или {@link WrapMatcher#editingCountMatcher(String, String)}
     * @throws InterruptedException     при прерывании ждущего потока, в той же ситуации что и {@code ExecutionException}
     */
    public static Tuple<Integer, String[]> selectCountMatcher(String inStr,
                                                              String templateStr,
                                                              boolean isEditingMethod)
            throws
            IllegalArgumentException,
            NullPointerException,
            ExecutionException,
            InterruptedException
    {
        long currTime = System.currentTimeMillis();
        int result = (int) (isEditingMethod ? editingCountMatcher(inStr, templateStr) : countMatcher(inStr, templateStr));
        String[] secAndMils = String.valueOf((double) (System.currentTimeMillis() - currTime) / 1000).split("\\.");
        return new Tuple<>(result, secAndMils);
    }

}
