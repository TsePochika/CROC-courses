package com.company.Void.Notify;

import com.company.Void.InputErrorHandling;
import com.company.Void.Notify.Interfaces.ListenedSet;
import com.company.Void.Notify.Interfaces.ListenedSetSubscriber;
import com.company.Void.Notify.Interfaces.ObjectSubscriber;
import com.company.Void.Notify.Interfaces.SubscriableOnObject;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListenedHashSet<T extends BaseNotifyPropertyChanged> extends LinkedHashSet<T> implements ListenedSet<T>, ObjectSubscriber {
    private final List<ListenedSetSubscriber> subscribers;
    private final String setName;

    public ListenedHashSet(String setName) {
        this.setName = InputErrorHandling.requireNotEmptyString(setName, "Пустое имя множества");
        this.subscribers = new LinkedList<>();
    }

    //region add operations
    @Override
    public boolean add(T element) {
        boolean isSuccesses;
        if (isSuccesses = super.add(element)) {
            subscribeOnElement(element);
            actionWithElements(DetectedOperation.ADD, element);
        }
        return isSuccesses;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean isSuccesses;
        if (isSuccesses = super.addAll(collection)) {
            collection.forEach(this::subscribeOnElement);
            actionWithElements(DetectedOperation.ADD, collection.toArray());
        }
        return isSuccesses;
    }
    //endregion

    //region remove operations
    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean remove(Object element) {
        boolean isSuccesses;
        if (isSuccesses = super.remove(element)) {
            unsubscribeFromElement((T) element);
            actionWithElements(DetectedOperation.REMOVE, element);
        }
        return isSuccesses;
    }

    @Override
    public T remove(int index) throws IndexOutOfBoundsException {
        T element = baseGet(index);
        super.remove(element);
        unsubscribeFromElement(element);
        actionWithElements(DetectedOperation.REMOVE, element);
        return element;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean removeAll(Collection<?> collection) {
        boolean isSuccesses;
        if (isSuccesses = super.removeAll(collection)) {
            collection.stream().map(s -> (T) s).forEach(this::unsubscribeFromElement);
            actionWithElements(DetectedOperation.REMOVE, collection.toArray());
        }
        return isSuccesses;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return this.removeAll(super.stream().filter(filter).collect(Collectors.toList()));
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return removeAll(super.stream().filter(s -> !collection.contains(s)).collect(Collectors.toList()));
    }

    @Override
    public void clear() {
        super.forEach(this::unsubscribeFromElement);
        super.clear();
        actionWithElements(DetectedOperation.REMOVE, super.toArray());
    }

    //endregion
    private void subscribeOnElement(T element) {
        ((SubscriableOnObject) element).subscribe(this);
    }

    private void unsubscribeFromElement(T element) {
        ((SubscriableOnObject) element).unsubscribe(this);
    }

    //region get operations
    private T baseGet(int index) throws IndexOutOfBoundsException {
        if (InputErrorHandling.requireUnsignedInt(index) >= super.size()) {
            throw new IndexOutOfBoundsException();
        }
        int i = 0;
        T element = null;
        for (T val : this) {
            if (i == index) {
                element = val;
                break;
            }
            i++;
        }
        return element;
    }

    @Override
    public T get(int index) {
        return baseGet(index);
    }

    //endregion
    private void actionWithElements(DetectedOperation detectedOperation, Object... items) {
        NotifyElementsChangedArgs args = new NotifyElementsChangedArgs(detectedOperation, items, this.setName);
        subscribers.forEach(s -> s.onListChanged(this, args));
    }

    @Override
    public void onObjectChanged(Object sender) {
        NotifyElementsChangedArgs args = new NotifyElementsChangedArgs(DetectedOperation.CHANGE, new Object[]{sender}, this.setName);
        subscribers.forEach(s -> s.onListChanged(this, args));
    }

    @Override
    public void subscribe(ListenedSetSubscriber listener) {
        subscribers.add(Objects.requireNonNull(listener));
    }

    @Override
    public void unsubscribe(ListenedSetSubscriber listener) {
        subscribers.remove(listener);
    }
}
