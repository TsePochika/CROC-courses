package com.company.MarketSystem.ds;

import com.company.MarketSystem.Model.Product;
import com.company.MarketSystem.Model.Sell;
import com.company.MarketSystem.Model.Seller;
import com.company.Void.Notify.Interfaces.ListenedSet;
import com.company.Void.Notify.ListenedHashSet;

@Deprecated
public class ContextR extends BaseContext {
    @DataList(dataPath ="ps",schemaPath = "pe")
    private ListenedSet<Product> products = new ListenedHashSet<>("");
    @DataList(dataPath ="ss",schemaPath = "se")
    private ListenedSet<Seller> sellers = new ListenedHashSet<>("");
    @DataList(dataPath ="as",schemaPath = "ae")
    private ListenedSet<Sell> sells = new ListenedHashSet<>("");
}
