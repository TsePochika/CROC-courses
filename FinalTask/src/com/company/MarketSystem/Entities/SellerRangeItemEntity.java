package com.company.MarketSystem.Entities;

import com.company.MarketSystem.Model.SellerRangeItem;


import javax.xml.bind.annotation.*;

@XmlRootElement(name = "sellerRangeItem")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"productID", "sellerID", "productPrice", "productCount"})
public class SellerRangeItemEntity implements ISellerRangeItem {

    @XmlElement(name = "productID")
    private long productID;
    @XmlElement(name = "sellerID")
    private long sellerID;
    @XmlElement(name = "productPrice")
    private int productPrice;
    @XmlElement(name = "productCount")
    private int productCount;

    public long getProductID() {
        return productID;
    }

    public long getSellerID() {
        return sellerID;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getProductCount() {
        return productCount;
    }

    public static SellerRangeItemEntity ToEntity(SellerRangeItem model) {
        SellerRangeItemEntity entity = new SellerRangeItemEntity();
        entity.sellerID = model.getSellerID();
        entity.productID = model.getProductID();
        entity.productCount = model.getProductCount();
        entity.productPrice = model.getProductPrice();
        return entity;
    }
}
