package com.company.MarketSystem.Entities;

import com.company.MarketSystem.Model.Seller;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "seller")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"sellerID", "sellerLastName", "sellerName"})
public class SellerEntity implements ISeller {
    @XmlElement(name = "sellerLastName")
    private String sellerLastName;
    @XmlElement(name = "sellerName")
    private String sellerName;
    @XmlElement(name = "id")
    private long sellerID;

    @Override
    public String getSellerName() {
        return this.sellerName;
    }

    @Override
    public String getSellerLastName() {
        return this.sellerLastName;
    }

    @Override
    public long getID() {
        return this.sellerID;
    }

    public static SellerEntity ToEntity(Seller model) {
        SellerEntity entity = new SellerEntity();
        entity.sellerID = model.getID();
        entity.sellerLastName = model.getSellerLastName();
        entity.sellerName = model.getSellerName();
        return entity;
    }

}
