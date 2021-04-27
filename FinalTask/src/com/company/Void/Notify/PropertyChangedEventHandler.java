package com.company.Void.Notify;

import com.company.Void.Notify.Interfaces.EventHandler;
import com.company.Void.Notify.Interfaces.ObjectSubscriber;

import java.util.ArrayList;
import java.util.List;

public class PropertyChangedEventHandler implements EventHandler {
    private final List<ObjectSubscriber> subscribers = new ArrayList<>();
    private final Object context;

    public PropertyChangedEventHandler(Object context) {
        this.context = context;
    }

    @Override
    public void invokeAll() {
        subscribers.forEach(s -> s.onObjectChanged(context));
    }

    @Override
    public void subscribe(ObjectSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(ObjectSubscriber subscriber) {
        this.subscribers.remove(subscriber);
    }

    public boolean isAnySubscribe() {
        return subscribers.size() != 0;
    }
}
