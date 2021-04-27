package com.company.MarketSystem.Model;

import com.company.MarketSystem.Entities.ProductEntity;
import com.company.Void.InputErrorHandling;
import com.company.Void.Notify.BaseNotifyPropertyChanged;

import java.util.Objects;

public class Product extends BaseNotifyPropertyChanged implements ProductModel {
    private final long productID;
    private String productName;

    protected Product(long productID, String productName) {
        this.productID = InputErrorHandling.requireUnsignedLong(productID);
        this.productName = InputErrorHandling.requireNotEmptyString(productName, "Пусто название продукта");
    }

    @Override
    public long getID() {
        return this.productID;
    }

    @Override
    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        super.onPropChanged();
    }

    public static Product ToModel(ProductEntity entity) {
        return new Product(entity.getID(), entity.getProductName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getID() == product.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }
}
