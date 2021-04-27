package com.company.MarketSystem.Model;

import com.company.MarketSystem.Entities.SellerEntity;
import com.company.Void.Notify.*;
import com.company.Void.InputErrorHandling;
import com.company.Void.Notify.Interfaces.ListenedSet;

import java.util.*;

public class Seller extends BaseNotifyPropertyChanged implements SellerModel {
    private final ListenedSet<SellerRangeItem> products;
    private final long sellerID;
    private String sellerLastName;
    private String sellerName;

    protected Seller(long sellerID, String sellerLastName, String sellerName,
                     ListenedSet<SellerRangeItem> products) throws IllegalArgumentException {
        this.sellerID = InputErrorHandling.requireUnsignedLong(sellerID);
        this.sellerLastName = InputErrorHandling.requireNotEmptyString(sellerLastName, "");
        this.sellerName = InputErrorHandling.requireNotEmptyString(sellerName, "");
        this.products = Objects.requireNonNull(products);
    }

    @Override
    public long getID() {
        return this.sellerID;
    }

    @Override
    public String getSellerName() {
        return this.sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
        super.onPropChanged();
    }

    @Override
    public String getSellerLastName() {
        return this.sellerLastName;
    }

    public void setSellerLastName(String sellerLastName) {
        this.sellerLastName = sellerLastName;
        super.onPropChanged();
    }

    public ListenedSet<SellerRangeItem> getSellerProducts() {
        return this.products;
    }

    public static Seller ToModel(SellerEntity entity, ListenedSet<SellerRangeItem> products) {
        if (!products.stream().allMatch(p -> p.getSellerID() == entity.getID())) {
            throw new IllegalArgumentException();
        }
        return new Seller(entity.getID(), entity.getSellerLastName(), entity.getSellerName(), products);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seller seller = (Seller) o;
        return getID() == seller.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

}
