package com.company.MarketSystem.Model;

import com.company.MarketSystem.Entities.SellEntity;
import com.company.Void.InputErrorHandling;
import com.company.Void.Notify.BaseNotifyPropertyChanged;

import java.time.LocalDateTime;
import java.util.Objects;

public class Sell extends BaseNotifyPropertyChanged implements SellModel {
    private final Product product;
    private final Seller seller;

    private final long sellID;
    private final int countSellProducts;
    private final LocalDateTime sellDate;

    protected Sell(long sellID, int countSellProducts, Product product, Seller seller, LocalDateTime sellDate) {
        this.sellID = InputErrorHandling.requireUnsignedLong(sellID);
        this.product = Objects.requireNonNull(product);
        this.seller = Objects.requireNonNull(seller);
        this.countSellProducts = InputErrorHandling.requireUnsignedInt(countSellProducts);
        this.sellDate = Objects.requireNonNull(sellDate);
    }

    public Product getProduct() {
        return this.product;
    }

    public Seller getSeller() {
        return this.seller;
    }

    @Override
    public long getProductID() {
        return this.product.getID();
    }

    @Override
    public long getSellerID() {
        return this.seller.getID();
    }

    @Override
    public int getCountSellProducts() {
        return this.countSellProducts;
    }

    @Override
    public LocalDateTime getSellDate() {
        return this.sellDate;
    }

    @Override
    public long getID() {
        return this.sellID;
    }

    public static Sell ToModel(SellEntity entity, Product product, Seller seller) {
        return new Sell(entity.getID(), entity.getCountSellProducts(), product, seller, entity.getSellDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sell sell = (Sell) o;
        return getID() == sell.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

}
