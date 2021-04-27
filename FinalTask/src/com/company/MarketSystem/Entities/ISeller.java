package com.company.MarketSystem.Entities;

public interface ISeller extends Identity {
    String getSellerName();

    String getSellerLastName();

    boolean equals(Object d);

}
