package com.company.MarketSystem.Entities;

import com.company.MarketSystem.LocalDateTimeAdapter;
import com.company.MarketSystem.Model.Sell;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

@XmlRootElement(name = "sell")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"sellID", "productID", "sellerID", "countSellProducts", "sellDate"})

public class SellEntity implements ISell {
    @XmlElement(name = "id")
    private long sellID;
    @XmlElement(name = "productID")
    private long productID;
    @XmlElement(name = "sellerID")
    private long sellerID;
    @XmlElement(name = "countSellProducts")
    private int countSellProducts;
    @XmlElement(name = "sellDate")
    @XmlJavaTypeAdapter(value = LocalDateTimeAdapter.class)
    private LocalDateTime sellDate;

    @Override
    public long getProductID() {
        return this.productID;
    }

    @Override
    public long getSellerID() {
        return this.sellerID;
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

    public static SellEntity ToEntity(Sell model) {
        SellEntity entity = new SellEntity();
        entity.sellID = model.getID();
        entity.sellerID = model.getSellerID();
        entity.productID = model.getProductID();
        entity.countSellProducts = model.getCountSellProducts();
        entity.sellDate = model.getSellDate();
        return entity;
    }
}
