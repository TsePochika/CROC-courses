package com.company.MarketSystem.ReadWrite;

import com.company.Void.InputErrorHandling;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class ReadTask<E> implements Callable<List<E>> {
    private final String filePath;
    private final Schema schema;
    private final Class<E> entityType;
    private final String nodeName;

    public ReadTask(String nodeName, String filePath, String schemaPath, Class<E> entityType) throws SAXException {
        this.nodeName = InputErrorHandling.requireNotEmptyString(nodeName, "Пустое имя узла!");
        this.filePath = InputErrorHandling.requireNotEmptyString(filePath, "Пустой путь к файлу с данными!");
        String sPath = InputErrorHandling.requireNotEmptyString(schemaPath, "Пустой путь к файлу с xsd схемой!");
        this.schema = SchemaFactory.newDefaultInstance().newSchema(new File(sPath));
        this.entityType = Objects.requireNonNull(entityType, "Пустой тип сущности!");
    }

    @Override
    public List<E> call() throws Exception {
        JAXBContext context = JAXBContext.newInstance(entityType);
        Unmarshaller deserializer = context.createUnmarshaller();
        deserializer.setSchema(schema);

        List<E> items = new LinkedList<>();
        try (var inputStream = new FileInputStream(filePath)) {
            XMLStreamReader streamReader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
            streamReader.nextTag();
            while (!streamReader.isEndElement()) {
                if (streamReader.getLocalName().equals(nodeName)) {
                    items.add(deserializer.unmarshal(streamReader, entityType).getValue());
                }
                streamReader.nextTag();
            }
            streamReader.close();
        }
        return items;
    }
}
