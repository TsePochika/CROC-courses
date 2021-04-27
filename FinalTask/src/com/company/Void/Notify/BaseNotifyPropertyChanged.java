package com.company.Void.Notify;

import com.company.Void.Notify.Interfaces.EventHandler;
import com.company.Void.Notify.Interfaces.ObjectSubscriber;
import com.company.Void.Notify.Interfaces.SubscriableOnObject;

/**
 * Класс для прослушивания изменений свойств
 */
public class BaseNotifyPropertyChanged implements SubscriableOnObject {
    protected final EventHandler eventHandler = new PropertyChangedEventHandler(this);

    protected void onPropChanged() {
        if (eventHandler.isAnySubscribe()){
            eventHandler.invokeAll();
        }
    }

    @Override
    public void subscribe(ObjectSubscriber subscriber) {
        eventHandler.subscribe(subscriber);
    }

    @Override
    public void unsubscribe(ObjectSubscriber subscriber) {
        eventHandler.unsubscribe(subscriber);
    }
}
