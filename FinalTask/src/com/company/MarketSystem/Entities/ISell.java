package com.company.MarketSystem.Entities;

import java.time.LocalDateTime;

public interface ISell extends Identity {
    long getProductID();

    long getSellerID();

    int getCountSellProducts();

    LocalDateTime getSellDate();

}
