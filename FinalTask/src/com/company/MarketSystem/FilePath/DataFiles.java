package com.company.MarketSystem.FilePath;

public enum DataFiles {
    XML_FILES_PATH("src\\com\\company\\MarketSystem\\DataFile\\Data\\"),
    PRODUCTS(XML_FILES_PATH.path + "Products.xml"),
    SELLERS(XML_FILES_PATH.path + "Sellers.xml"),
    SELLS(XML_FILES_PATH.path + "Sells.xml"),
    SELLER_RANGE(XML_FILES_PATH.path + "SellerRange.xml");

    private final String path;

    DataFiles(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}