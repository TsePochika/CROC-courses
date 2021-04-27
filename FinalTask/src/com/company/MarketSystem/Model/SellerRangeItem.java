package com.company.MarketSystem.Model;

import com.company.MarketSystem.Entities.SellerRangeItemEntity;
import com.company.Void.InputErrorHandling;
import com.company.Void.Notify.BaseNotifyPropertyChanged;
import com.company.Void.UniqueIdGenerator;

import java.util.Objects;

public class SellerRangeItem extends BaseNotifyPropertyChanged implements SellerRangeItemModel {
    private final Product product;
    private final long sellerID;
    private int productPrice;
    private int countProduct;

    protected SellerRangeItem(Product product, long sellerID, int productPrice, int countProduct) {
        this.product = Objects.requireNonNull(product);
        this.sellerID = InputErrorHandling.requireUnsignedLong(sellerID);
        this.productPrice = InputErrorHandling.requireUnsignedInt(productPrice);
        this.countProduct = InputErrorHandling.requireUnsignedInt(countProduct);
    }

    public Product getProduct() {
        return this.product;
    }

    @Override
    public long getProductID() {
        return this.product.getID();
    }

    @Override
    public long getSellerID() {
        return this.sellerID;
    }

    @Override
    public int getProductPrice() {
        return this.productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        super.onPropChanged();
    }

    @Override
    public int getProductCount() {
        return this.countProduct;
    }

    public void setProductCount(int productCount) {
        this.countProduct = productCount;
        super.onPropChanged();
    }

    @Override
    public long getID() {
        return UniqueIdGenerator.pairing(this.product.getID(), this.sellerID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SellerRangeItem seller = (SellerRangeItem) o;
        return getID() == seller.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getID(), sellerID);
    }

    public static SellerRangeItem ToModel(SellerRangeItemEntity entity, Product product) {
        return new SellerRangeItem(product, entity.getSellerID(), entity.getProductPrice(), entity.getProductCount());
    }
}
