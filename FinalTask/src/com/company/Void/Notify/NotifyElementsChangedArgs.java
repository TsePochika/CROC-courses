package com.company.Void.Notify;

import com.company.Void.InputErrorHandling;

import java.util.Objects;

public class NotifyElementsChangedArgs {
    public final DetectedOperation detectedOperation;
    public final Object[] items;
    public final String setName;

    public NotifyElementsChangedArgs(DetectedOperation detectedOperation, Object[] items, String setName) {
        this.detectedOperation = Objects.requireNonNull(detectedOperation);
        this.items = InputErrorHandling.requireNotEmptyArray(items, "Пустой масссив значений");
        this.setName = InputErrorHandling.requireNotEmptyString(setName,"Пустое имя множества");
        if (DetectedOperation.CHANGE == this.detectedOperation) {
            if (items.length > 1) {
                throw new IllegalArgumentException("Не может быть несколько изменненых элементов за раз");
            }
        }
    }
}
