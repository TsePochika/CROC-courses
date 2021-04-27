package com.company.MarketSystem.Entities;

import com.company.MarketSystem.Model.Product;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "product")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(propOrder = {"productID", "productName"})
public class ProductEntity implements IProduct {
    @XmlElement(name = "id")
    private long productID;
    @XmlElement(name = "productName")
    private String productName;

    @Override
    public String getProductName() {
        return this.productName;
    }

    @Override
    public long getID() {
        return this.productID;
    }

    public static ProductEntity ToEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.productID = product.getID();
        entity.productName = product.getProductName();
        return entity;
    }
}
