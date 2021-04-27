package com.company.Void.Notify.Interfaces;

import java.util.Set;

public interface ModifiedSet<T> extends Set<T> {
    T get(int index);

    T remove(int index);

}
