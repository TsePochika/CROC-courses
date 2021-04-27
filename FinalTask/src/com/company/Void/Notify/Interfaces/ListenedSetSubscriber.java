package com.company.Void.Notify.Interfaces;

import com.company.Void.Notify.NotifyElementsChangedArgs;

public interface ListenedSetSubscriber extends SubscriberMarker {
     void onListChanged(Object sender, NotifyElementsChangedArgs args);
}
