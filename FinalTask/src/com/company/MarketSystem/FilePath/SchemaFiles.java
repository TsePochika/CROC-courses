package com.company.MarketSystem.FilePath;

public enum SchemaFiles {
    XSD_FILEPATH("src\\com\\company\\MarketSystem\\DataFile\\FileSchemes\\"),
    PRODUCT_SCHEMA(XSD_FILEPATH.path + "Product.xsd"),
    SELL_SCHEMA(XSD_FILEPATH.path + "Sell.xsd"),
    SELLER_SCHEMA(XSD_FILEPATH.path + "Seller.xsd"),
    SELLER_RANGE_ITEM_SCHEMA(XSD_FILEPATH.path + "SellerRangeItem.xsd");

    private final String path;

    SchemaFiles(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}