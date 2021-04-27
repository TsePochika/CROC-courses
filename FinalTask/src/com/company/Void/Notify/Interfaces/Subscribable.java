package com.company.Void.Notify.Interfaces;

public interface Subscribable<T extends SubscriberMarker> {
    void subscribe(T subscriber);

    void unsubscribe(T subscriber);
}
