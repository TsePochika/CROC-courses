package com.company.Void.Notify.Interfaces;

import com.company.Void.Notify.BaseNotifyPropertyChanged;

public interface ListenedSet<T extends BaseNotifyPropertyChanged> extends Subscribable<ListenedSetSubscriber>, ModifiedSet<T> {
}
