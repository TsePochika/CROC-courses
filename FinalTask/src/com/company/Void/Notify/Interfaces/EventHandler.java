package com.company.Void.Notify.Interfaces;

public interface EventHandler extends Subscribable<ObjectSubscriber> {
    void invokeAll();
    boolean isAnySubscribe();
}
