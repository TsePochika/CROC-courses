package com.company.MarketSystem.ds;

import com.company.Void.Notify.Interfaces.ListenedSet;
import com.company.Void.Tuple;
import io.github.classgraph.ClassGraph;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Deprecated
public class BaseContext {
    List<String> lists;
    List<Tuple<String, String>> filesPath;
    private final String entityPath = "com.company.Void";

    public BaseContext() {
        var y = getDataLists();
        lists = y.stream().map(Field::getName).collect(Collectors.toList());
        filesPath = getFilePaths(y);
        var models = getGenericTypes(y);
        var yu = new ClassGraph().verbose().enableAllInfo().acceptClasses(entityPath).scan().getAllClasses();
    }

    private List<Field> getDataLists() {
        return Arrays.stream(this.getClass().getDeclaredFields()).
                filter(f -> f.isAnnotationPresent(DataList.class) &&
                        f.getType().equals(ListenedSet.class)).
                filter(f -> ((Class<?>)
                        ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]).
                        isAnnotationPresent(Model.class)).
                collect(Collectors.toList());
    }

    private List<Tuple<String, String>> getFilePaths(List<Field> fields) {
        return fields.stream().map(f -> f.getAnnotation(DataList.class)).map(a -> new Tuple<>(a.dataPath(), a.schemaPath())).collect(Collectors.toList());
    }

    private List<Class<?>> getGenericTypes(List<Field> fields) {
        return fields.stream().
                map(f -> ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]).
                map(c -> (Class<?>) c).
                collect(Collectors.toList());
    }
}
