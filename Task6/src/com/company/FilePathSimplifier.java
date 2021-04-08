package com.company;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FilePathSimplifier {
    private static final String ILLEXMSG = "Путь должен соответствовать шаблону ввода";
    private static final String EMPTYEXMSG = "Путь не должен быть пустым";
    private static final String CURRDIR = ".";
    private static final String ROOTDIR = "..";
    private static final String DIRDELIMITER = "/";
    /**
     * Регулярное выражение для поиска наименования директории
     * с последующим знаком перехода к родительской.
     * <p>Ищет имя диретроии только с одним знаком перехода к родительской:
     * <blockquote><pre>
     *    ../music/metal/<font color="#ccbf2d">behemoth/../</font>../<font color="#ccbf2d">rock/../</font>../videos
     * </pre></blockquote>
     * <p>На следующей итерации будет:
     * <blockquote><pre>
     *    ../music/<font color="#ccbf2d">metal/../</font>../videos
     * </pre></blockquote>
     * <p>Описание выражения:
     * <blockquote><pre>
     * \\w+        Имя директории со знаком разделения.
     * (           Группа для символа текущей директории.
     *  \\./       Символ текущей директории.
     * )*
     * (?:         Незахватываемая группа для символа перехода.
     *    \\.\\./  Символ перехода к родительской директории.
     * )*
     * \1*         После знака перехода может быть
     *              знак текущей дериктории.
     * </pre></blockquote>
     */
    private static final String REPLACETEPLATE = "\\w+/(\\./)*(?:\\.\\./)\1*";
    /**
     * Кривое регулярное выражение для проверки пути
     * <p>Допускает подобное окончание пути:
     * <blockquote><pre>
     *     rdir/dir/sdir/
     * </pre></blockquote>
     * <p>Не допускает подобное окончание пути:
     * <blockquote><pre>
     *     rdir/dir/sdir
     * </pre></blockquote>
     * <p>Описание выражения:
     * <blockquote><pre>
     * (?:                   Незахватываемая основаная группа.
     *    (?:                Незахватываемая группа для имени
     *                        директории с знаком разделения
     *       (?:             Незахватываемая группа для.
     *                        определения допустимых символов
     *          \\.*         Какое-то количество точек
     *                        может содержаться  в начале слова.
     *          [^/?<>:"*. ] Системные символы не должны
     *                        содержаться в имени директории.
     *                       Точка стоит, чтобы избежать
     *                        её повторений
     *                        в знаках прехода по директориям.
     *          \\.{0}       В конце имени не может
     *                        содержаться точка.
     *       )+
     *       /               Знак разделения директорий.
     *    )
     *    |                  В пути могут быть как имена директорий,
     *                        так и символы перехода по ним.
     *    (?:                Незахватываемая группа для символов
     *                        перехода по директориям
     *                        с знаком разделения.
     *       \\.{1,2}        Может быть либо символ
     *                        текущей директории ".",
     *                        либо перехода в родительскую "..".
     *       /              Знак разделеиния директорий.
     *    )
     * )+
     * </pre></blockquote>
     */
    private static final String PATHTEMPLATE = "(?:(?:(?:\\.*[^/?<>:\"*. ]\\.{0})+/)|(?:\\.{1,2}/))+";

    /**
     * Упрощение пути, с использованием регулярных выражений.
     * <p>Проверяет на валидность по {@link #checkPath(String)}
     * <p>Упрощает по {@link #REPLACETEPLATE}
     *
     * @param path путь для упращения
     * @return упрощенный путь
     * @throws IllegalArgumentException при несооветствии входной строки шаблону {@link #PATHTEMPLATE}
     * @throws NullPointerException     при пустой входной строке
     */
    public static String simplifyByRegExp(String path) throws IllegalArgumentException, NullPointerException {
        if (!checkPath(path)) throw new IllegalArgumentException(ILLEXMSG);
        // как понимаю, нам не нужны повторяющиеся символы текущей директории
        path = removeDouble(path, CURRDIR + DIRDELIMITER);
        Pattern pattern = Pattern.compile(REPLACETEPLATE, Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(path);
        // на каждой итерации удаляем директорию с последующим символом перехода к родительской
        while (matcher.find()) {
            path = matcher.replaceAll("");
            matcher = pattern.matcher(path);
        }

        return path;
    }

    /**
     * Упрощение пути, с использованием {@link LinkedList}
     * <p>Проверяет на валидность по {@link #checkPath(String)}
     *
     * @param path путь для упрощения
     * @return упрощенный путь
     * @throws IllegalArgumentException при несооветствии входной строки шаблону {@link #PATHTEMPLATE}
     * @throws NullPointerException     при пустой входной строке
     */
    public static String simplifyByLinkedList(String path) throws IllegalArgumentException, NullPointerException {
        if (!checkPath(path)) throw new IllegalArgumentException(ILLEXMSG);
        // как понимаю, нам не нужны повторяющиеся символы текущей директории
        path = removeDouble(path, CURRDIR + DIRDELIMITER);
        List<String> literals = Arrays.stream(path.split(DIRDELIMITER)).collect(Collectors.toCollection(LinkedList::new));

        int dirNameId = -1;
        String currLit;
        for (int i = 0; i < literals.size(); i++) {
            currLit = literals.get(i);
            if (!currLit.equals(CURRDIR) && !currLit.equals(ROOTDIR)) {
                dirNameId = i;
            } else if (currLit.equals(ROOTDIR) && dirNameId > 0) {
                literals.subList(dirNameId, i + 1).clear();
                i = literals.get(dirNameId - 1).equals(CURRDIR) ? dirNameId -= 2 : --dirNameId;
                if (-2 == i) i = -1;
            }
        }

        String outputPath = String.join(DIRDELIMITER, literals);
        if (path.lastIndexOf(CURRDIR) == path.length() - 2) outputPath += DIRDELIMITER;
        return outputPath;
    }

    /**
     * Проверка пути на валидность
     * <p>Выступает в роли костыля для {@link #PATHTEMPLATE}
     *
     * @param path путь для проверки
     * @return {@code true}, если проверка пройдена успешно
     * {@code false}, если не пройдена.
     * @throws NullPointerException при пустой строке
     */
    private static boolean checkPath(String path) throws NullPointerException {
        if (null == path || path.isBlank()) throw new NullPointerException(EMPTYEXMSG);
        Pattern pattern = Pattern.compile(PATHTEMPLATE);
        Matcher matcher = pattern.matcher(path);
        if (!matcher.matches()) {
            /* Из-за неидеального регулярного выражения для проверки
             * путь типа "rdir/dir/sdir" не будет подходить под шаблон,
             * так как в конце строки отсутствует симовол разделения "/"
             * Поэтому нужно добавлять данный символ к входной строке,
             * избегая ситуаций подобных ситуаций: "rdir/dir/sdir/."
             */
            if (path.lastIndexOf(CURRDIR) != path.length() - 1) {
                matcher = pattern.matcher(path + DIRDELIMITER);
                return matcher.matches();
            }
            return false;
        } else {
            /* Нужно исключить ситуации, когда путь имеет следующий вид:
             * "rdir/dir/sdir/"
             */
            int lastIndex = path.length() - 1;
            return !((path.lastIndexOf(CURRDIR) != lastIndex - 1)
                    && (path.lastIndexOf(DIRDELIMITER) == lastIndex));
        }
    }

    /**
     * Удаление дубликатов указанной последовательности символов из строки
     *
     * @param str     строка для удаления
     * @param dRemove последоватеьность, дубликаты которой требуется удалить
     * @return {@code str} без дубликатов
     */
    private static String removeDouble(String str, String dRemove) {
        String repDRemove = dRemove.repeat(2);
        while (str.contains(repDRemove)) {
            str = str.replace(repDRemove, dRemove);
        }
        return str;
    }
}
