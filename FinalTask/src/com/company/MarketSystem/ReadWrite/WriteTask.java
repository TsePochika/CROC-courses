package com.company.MarketSystem.ReadWrite;

import com.company.MarketSystem.FilePath.DataFiles;
import com.company.Void.InputErrorHandling;
import com.company.Void.Tuple;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс для перезаписи файлов с данными на основе произведенных действий
 * Пропускает элементы в файле, идентификаторы которых находятся в {@code elementForRemoveByIds}
 *
 * @param <T>
 */
public class WriteTask<T> implements Callable<Void> {
    private static final String SEPARATOR = System.lineSeparator();
    private final String closeRootTag;
    private final String itemTag;
    private final String filePath;
    private final Schema schema;
    private final Class<T> type;
    private final List<T> elementsForAdd;
    //вложенный список,так как сущность может иметь составной идентификатор
    private final List<List<Long>> elementForRemoveByIds;
    //названия свойств, образующих идентификатор
    private final List<String> props;

    public WriteTask(String rootName, String itemName, List<String> idProps,
                     String filePath, String schemaPath, Class<T> type,
                     Tuple<List<T>, List<List<Long>>> elementsForAddAndDel) throws SAXException {

        this.closeRootTag = "</" + InputErrorHandling.requireNotEmptyString(rootName, "Пустой закрывающий тег корневого элемента") + ">";
        this.itemTag = "<" + InputErrorHandling.requireNotEmptyString(itemName, "Пустой тег элемента списка") + ">";
        this.filePath = InputErrorHandling.requireNotEmptyString(filePath, "Пустой путь к файлу с данными");
        this.type = Objects.requireNonNull(type, "Пустой тип!");
        this.elementsForAdd = Objects.requireNonNull(elementsForAddAndDel.getLeft());
        this.props = idProps;
        this.elementForRemoveByIds = Objects.requireNonNull(elementsForAddAndDel.getRight());
        String sPath = InputErrorHandling.requireNotEmptyString(schemaPath, "Пустой путь к файлу с xsd схемой");
        this.schema = SchemaFactory.newDefaultInstance().newSchema(new File(sPath));
    }

    public WriteTask(String rootName, String itemName, String idPropName,
                     String filePath, String schemaPath, Class<T> type,
                     Tuple<List<T>, List<Long>> elementsForAddAndDel) throws SAXException {
        this.closeRootTag = "</" + InputErrorHandling.requireNotEmptyString(rootName, "Пустой закрывающий тег корневого элемента") + ">";
        this.itemTag = "<" + InputErrorHandling.requireNotEmptyString(itemName, "Пустой тег элемента списка") + ">";
        this.props = List.of(idPropName);
        this.filePath = InputErrorHandling.requireNotEmptyString(filePath, "Пустой путь к файлу с данными");
        this.type = Objects.requireNonNull(type, "Пустой тип!");
        this.elementsForAdd = Objects.requireNonNull(elementsForAddAndDel.getLeft());
        this.elementForRemoveByIds = Objects.requireNonNull(elementsForAddAndDel.getRight()).stream().
                map(s -> new ArrayList<>(List.of(s))).collect(Collectors.toList());
        String sPath = InputErrorHandling.requireNotEmptyString(schemaPath, "Пустой путь к файлу с xsd схемой");
        this.schema = SchemaFactory.newDefaultInstance().newSchema(new File(sPath));
    }

    @Override
    public Void call() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(type);
        Marshaller serializer = context.createMarshaller();
        serializer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        serializer.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        serializer.setProperty(Marshaller.JAXB_FRAGMENT, true);
        serializer.setSchema(schema);
        if (0 != elementsForAdd.size() || 0 != elementForRemoveByIds.size()) {
            File forRead = new File(filePath);
            File forWrite = Files.createFile(
                    Path.of(DataFiles.XML_FILES_PATH.getPath(),
                            "temp" + Thread.currentThread().getId() + ".xml")).toFile();
            //region serializing elements for add
            StringWriter elementsForAddXML = new StringWriter();
            for (var product : this.elementsForAdd) {
                serializer.marshal(product, elementsForAddXML);
                elementsForAddXML.getBuffer().append(SEPARATOR);
            }
            //endregion
            //region rewrite data file
            try (var bufferedReader = new BufferedReader(new FileReader(forRead, StandardCharsets.UTF_8))) {
                try (var writer = new FileWriter(forWrite, StandardCharsets.UTF_8, false)) {
                    //кол-во свойств в сериализуемом типе
                    //понадобится для пропуска строк, которые чсляться удаленными
                    int propsReadied = 0, propsCount = type.getDeclaredFields().length;
                    String nextLine;
                    Pattern idFinder = null;
                    boolean shouldWrite, isElmsForDelNotEmpty = elementForRemoveByIds.size() != 0;
                    if (isElmsForDelNotEmpty) {
                        idFinder = prepareRegexp();
                    }
                    while (Objects.nonNull(nextLine = bufferedReader.readLine()) && !nextLine.contains(closeRootTag)) {
                        shouldWrite = true;
                        nextLine += SEPARATOR;
                        //попадаем в xml представление объекта
                        if (isElmsForDelNotEmpty && nextLine.contains(itemTag)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(nextLine);
                            //собираем все свойства, отвечающие за идентификатор
                            while (!props.stream().allMatch(stringBuilder.toString()::contains)) {
                                stringBuilder.append(nextLine = bufferedReader.readLine()).append(SEPARATOR);
                                propsReadied++;
                            }
                            String strSB = stringBuilder.toString();
                            Matcher matcher = idFinder.matcher(strSB);
                            if (matcher.find()) {
                                List<Long> ids = getMatchesId(matcher);
                                if (elementForRemoveByIds.stream().anyMatch(a -> a.equals(ids))) {
                                    shouldWrite = false;
                                    //так как было установлено, что читаемый объект нужно удалить(пропустить),
                                    //то пропускаем оставшиеся свойства и закрывающий тэг объекта
                                    for (int i = propsReadied; i < propsCount + 1; i++) {
                                        nextLine = bufferedReader.readLine();
                                    }
                                } else {
                                    nextLine = strSB;
                                }
                            }
                            propsReadied = 0;
                        }
                        if (shouldWrite) {
                            writer.write(nextLine);
                        }
                    }
                    writer.append(elementsForAddXML.toString());
                    writer.append(closeRootTag);
                }
            }
            //endregion
            if (!forRead.delete()) {
                throw new RuntimeException("Не удалось заменить файл: удаление");
            }
            if (!forWrite.renameTo(forRead)) {
                throw new RuntimeException("Не удалось заменить файл: переименование");
            }
        }
        return null;
    }

    /**
     * Подготовка регулярного выражения для поиска свойств-идентификаторов
     *
     * @return regexp для поиска
     */
    private Pattern prepareRegexp() {
        StringBuilder stringBuilder = new StringBuilder();
        String regexp = "\\s*([0-9]+)\\s*";
        for (var u : props) {
            stringBuilder.
                    append("\\s*").append("<").append(u).append(">").
                    append(regexp).
                    append("</").append(u).append(">").append("\\s*");
        }

        return Pattern.compile(stringBuilder.toString());
    }

    /**
     * Вычленение из результата матчинга идентификатора
     *
     * @param matcher матчер
     * @return список идентификаторов
     */
    private List<Long> getMatchesId(Matcher matcher) {
        List<Long> list = new LinkedList<>();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            list.add(Long.parseLong(matcher.group(i)));
        }
        return list;
    }

}

