package com.company.MarketSystem.Model;

import com.company.MarketSystem.Entities.Identity;
import com.company.Void.Notify.Interfaces.ListenedSet;
import com.company.Void.Notify.ListenedHashSet;
import org.xml.sax.SAXException;

import java.time.LocalDateTime;
import java.util.*;

public class Market {
    private static Market instance;
    private final AppContext systemData;

    private Market() throws Exception {
        this.systemData = new AppContext();
    }

    public static Market newInstance() throws Exception {
        if (Objects.isNull(instance)) {
            return instance = new Market();
        }
        return instance;
    }

    public Product newProduct(String productName) {
        return new Product(getNewId(getProducts()), productName);
    }

    public Seller newSeller(String sellerName, String sellerLastName) {
        return new Seller(getNewId(getSellers()), sellerLastName, sellerName, new ListenedHashSet<>(AppContext.SystemData.SELLER_RANGE_ITEM_LIST));
    }

    public Sell newSell(int countSell, Product product, Seller seller, LocalDateTime sellDate) {
        Optional<SellerRangeItem> optional = seller.getSellerProducts().stream().filter(s -> s.getProductID() == product.getID()).findFirst();
        SellerRangeItem sellerRangeItem;
        if (optional.isPresent()) {
            sellerRangeItem = optional.get();
            int productCount = sellerRangeItem.getProductCount();
            if (productCount > countSell) {
                sellerRangeItem.setProductCount(productCount - countSell);
            } else if (productCount < countSell) {
                throw new IllegalArgumentException("Не хватает продуктов");
            } else {
                seller.getSellerProducts().remove(sellerRangeItem);
            }
        } else {
            throw new IllegalArgumentException();
        }
        Sell sell = new Sell(getNewId(getSellers()), countSell, product, seller, sellDate);
        getSells().add(sell);
        return sell;
    }

    public SellerRangeItem newSellerRangeItem(Product product, int sellerId, int productPrice, int productCount) {
        return new SellerRangeItem(product, sellerId, productPrice, productCount);
    }

    private <T extends Identity> int getNewId(Set<T> listenedSet) {
        return (int) listenedSet.stream().max(Comparator.comparingInt(s -> (int) s.getID())).orElseThrow().getID() + 1;
    }

    public void save() throws InterruptedException, SAXException {
        this.systemData.save();
    }

    public ListenedSet<Product> getProducts() {
        return this.systemData.getProducts();
    }

    public ListenedSet<Seller> getSellers() {
        return this.systemData.getSellers();
    }

    public ListenedSet<Sell> getSells() {
        return this.systemData.getSells();
    }

    public ListenedSet<SellerRangeItem> getAllSellersRange() {
        return this.systemData.getAllSellersRange();
    }

}
