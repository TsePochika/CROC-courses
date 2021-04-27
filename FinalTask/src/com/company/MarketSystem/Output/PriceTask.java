package com.company.MarketSystem.Output;

import com.company.MarketSystem.Entities.ProductEntity;
import com.company.MarketSystem.Entities.SellerEntity;
import com.company.MarketSystem.Model.Product;
import com.company.MarketSystem.Model.Seller;

public class PriceTask {
    private final ProductEntity product;
    private final SellerEntity seller;
    private final Integer price;

    public PriceTask(Product product, Seller seller, Integer integer) {
        this.product = ProductEntity.ToEntity(product);
        this.seller = SellerEntity.ToEntity(seller);
        this.price = integer;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public SellerEntity getSeller() {
        return seller;
    }

    public Integer getPrice() {
        return price;
    }
}
