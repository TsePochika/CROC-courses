package com.company.MarketSystem.Model;

import com.company.MarketSystem.FilePath.DataFiles;
import com.company.MarketSystem.Entities.Identity;
import com.company.MarketSystem.Entities.ProductEntity;
import com.company.MarketSystem.Entities.SellEntity;
import com.company.MarketSystem.Entities.SellerEntity;
import com.company.MarketSystem.Entities.SellerRangeItemEntity;
import com.company.MarketSystem.ReadWrite.ReadTask;
import com.company.MarketSystem.FilePath.SchemaFiles;
import com.company.MarketSystem.ReadWrite.WriteTask;
import com.company.Void.Notify.DetectedOperation;
import com.company.Void.Notify.Interfaces.ListenedSet;
import com.company.Void.Notify.Interfaces.ListenedSetSubscriber;
import com.company.Void.Notify.ListenedHashSet;
import com.company.Void.Notify.NotifyElementsChangedArgs;

import com.company.Void.Tuple;
import com.company.Void.UniqueIdGenerator;
import org.xml.sax.SAXException;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

//Хотелость сделать базовый класс контекста и регулировать процессы
//инициализации и сохранния через аннотации, но немного времени не хватило.
//По этой же причине класс толком не используется.
class AppContext {
    private final SystemData data;

    //вложенный класс с данными и буферами к спискам данных
    //основная идея - не преобразовывать больший списки с данными, а лишь те элементы, которые были добавлены
    //если же элемент удален, то он пропускается по индексу в WriteTask
    public static class SystemData implements ListenedSetSubscriber {
        //region constants
        public static final String PRODUCT_ITEM = "product";
        public static final String SELLER_ITEM = "seller";
        public static final String SELL_ITEM = "sell";
        public static final String SELLER_RANGE_ITEM = "sellerRangeItem";
        public static final String PRODUCT_LIST = "products";
        public static final String SELLER_LIST = "sellers";
        public static final String SELL_LIST = "sells";
        public static final String SELLER_RANGE_ITEM_LIST = "sellerRange";
        //endregion
        //region dataLists
        private final ListenedSet<Product> products;
        private final ListenedSet<Seller> sellers;
        private final ListenedSet<Sell> sells;
        //endregion
        //region buffers
        private final Map<Long, DetectedOperation> productsBuffer = new HashMap<>();
        private final Map<Long, DetectedOperation> sellersBuffer = new HashMap<>();
        private final Map<Long, DetectedOperation> sellsBuffer = new HashMap<>();
        private final Map<Long, DetectedOperation> allSellersRangeBuffer = new HashMap<>();
        //endregion

        public SystemData(ListenedSet<Product> products, ListenedSet<Seller> sellers, ListenedSet<Sell> sells) {
            this.products = products;
            this.sellers = sellers;
            this.sells = sells;

            this.products.subscribe(this);
            this.sellers.subscribe(this);
            this.sellers.forEach(s -> s.getSellerProducts().subscribe(this));
            this.sells.subscribe(this);
        }

        /**
         * Метод реагирования на изменения в подписанных списках
         *
         * @param sender отправитель изменений
         * @param args   аргументы события
         */
        @Override
        public void onListChanged(Object sender, NotifyElementsChangedArgs args) {
            switch (args.setName) {
                case SystemData.PRODUCT_LIST -> fillBuffer(productsBuffer,
                        mapToBuffer(args.detectedOperation, args.items));
                case SystemData.SELLER_LIST -> fillBuffer(sellersBuffer,
                        mapToBuffer(args.detectedOperation, args.items));
                case SystemData.SELL_LIST -> fillBuffer(sellsBuffer,
                        mapToBuffer(args.detectedOperation, args.items));
                case SystemData.SELLER_RANGE_ITEM_LIST -> fillBuffer(allSellersRangeBuffer,
                        mapToBuffer(args.detectedOperation, args.items));
            }
        }

        /**
         * Преобразование изменненых элементов в буфер
         *
         * @param operation обнаруженная операция
         * @param items     элементы, затронутые изменением
         * @return буфер
         */
        private Map<Long, DetectedOperation> mapToBuffer(DetectedOperation operation, Object[] items) {
            return Arrays.stream(items).collect(Collectors.toMap(k -> ((Identity) k).getID(), v -> operation));
        }

        private void fillBuffer(Map<Long, DetectedOperation> bufferTo, Map<Long, DetectedOperation> bufferFrom) {
            for (Long fromKey : bufferFrom.keySet()) {
                if (bufferTo.containsKey(fromKey)) {
                    bufferTo.replace(fromKey, bufferFrom.get(fromKey));
                } else {
                    bufferTo.put(fromKey, bufferFrom.get(fromKey));
                }
            }
        }

        private Object ConvertToEntity(Object model) {
            if (model instanceof Product)
                return ProductEntity.ToEntity((Product) model);
            if (model instanceof Seller)
                return SellerEntity.ToEntity((Seller) model);
            if (model instanceof Sell)
                return SellEntity.ToEntity((Sell) model);
            if (model instanceof SellerRangeItem)
                return SellerRangeItemEntity.ToEntity((SellerRangeItem) model);
            return null;
        }

        private <E, M extends Identity> Tuple<List<E>, List<Long>> mergeToTuple(Set<M> dataSet, Map<Long, DetectedOperation> dataSetBuffer) {
            return new Tuple<>(getElementsForAdd(dataSet, dataSetBuffer), getIDsForDelete(dataSetBuffer));
        }

        private <E, M extends Identity> Tuple<List<E>, List<List<Long>>> mergeToTuple(Set<M> dataSet, Map<Long, DetectedOperation> dataSetBuffer, int countPropsById) {
            return new Tuple<>(getElementsForAdd(dataSet, dataSetBuffer), getIDsForDelete(dataSetBuffer, countPropsById));
        }

        private List<Long> getIDsForDelete(Map<Long, DetectedOperation> dataSetBuffer) {
            return dataSetBuffer.keySet().stream().
                    filter(k -> dataSetBuffer.get(k).equals(DetectedOperation.REMOVE) ||
                            dataSetBuffer.get(k).equals(DetectedOperation.CHANGE)).
                    collect(Collectors.toList());
        }

        //получение составных идентификаторов
        private List<List<Long>> getIDsForDelete(Map<Long, DetectedOperation> dataSetBuffer, int countPropsAsId) {
            return dataSetBuffer.keySet().stream().
                    filter(k -> dataSetBuffer.get(k).equals(DetectedOperation.REMOVE) ||
                            dataSetBuffer.get(k).equals(DetectedOperation.CHANGE)).
                    map(i -> new ArrayList<>(UniqueIdGenerator.unPairingMulti(i, countPropsAsId))).collect(Collectors.toList());
        }

        @SuppressWarnings(value = "unchecked")
        private <E, M extends Identity> List<E> getElementsForAdd(Set<M> dataSet, Map<Long, DetectedOperation> dataSetBuffer) {
            return dataSetBuffer.keySet().stream().
                    filter(k -> (dataSetBuffer.get(k).equals(DetectedOperation.ADD) ||
                            dataSetBuffer.get(k).equals(DetectedOperation.CHANGE)) &&
                            (dataSet.stream().anyMatch(i -> i.getID() == k))).
                    map(k -> (E) this.ConvertToEntity(dataSet.stream().filter(i -> i.getID() == k).findFirst().orElseThrow())).
                    collect(Collectors.toList());
        }
    }

    AppContext() throws Exception {
        var dataList = initializeData();
        //region set deps
        ListenedSet<Product> products = dataList.get(0).stream().
                map(pe -> Product.ToModel((ProductEntity) pe)).
                collect(Collectors.toCollection(() -> new ListenedHashSet<>(SystemData.PRODUCT_LIST)));
        List<SellerRangeItem> allSellersRange = dataList.get(1).stream().
                map(srie -> (SellerRangeItemEntity) srie).
                map(srie -> SellerRangeItem.ToModel(srie,
                        products.stream().
                                filter(p -> p.getID() == srie.getProductID()).
                                findFirst().orElseThrow())).
                collect(Collectors.toList());
        ListenedSet<Seller> sellers = dataList.get(2).stream().
                map(se -> (SellerEntity) se).
                map(se -> Seller.ToModel(se,
                        allSellersRange.stream().
                                filter(sri -> sri.getSellerID() == se.getID()).
                                collect(Collectors.toCollection(() -> new ListenedHashSet<>(SystemData.SELLER_RANGE_ITEM_LIST))))).
                collect(Collectors.toCollection(() -> new ListenedHashSet<>(SystemData.SELLER_LIST)));
        ListenedSet<Sell> sells = dataList.get(3).stream().
                map(se -> (SellEntity) se).
                map(se -> Sell.ToModel(se,
                        products.stream().
                                filter(p -> p.getID() == se.getProductID()).
                                findFirst().orElseThrow(),
                        sellers.stream().
                                filter(s -> s.getID() == se.getSellerID()).
                                findFirst().orElseThrow())).
                collect(Collectors.toCollection(() -> new ListenedHashSet<>(SystemData.SELL_LIST)));

        //endregion
        this.data = new SystemData(products, sellers, sells);
    }

    private List<List<?>> initializeData() throws Exception {
        List<ReadTask<?>> fileData = Arrays.asList(
                new ReadTask<>(
                        SystemData.SELLER_RANGE_ITEM, DataFiles.SELLER_RANGE.getPath(),
                        SchemaFiles.SELLER_RANGE_ITEM_SCHEMA.getPath(), SellerRangeItemEntity.class),
                new ReadTask<>(
                        SystemData.SELLER_ITEM, DataFiles.SELLERS.getPath(),
                        SchemaFiles.SELLER_SCHEMA.getPath(), SellerEntity.class),
                new ReadTask<>(
                        SystemData.SELL_ITEM, DataFiles.SELLS.getPath(),
                        SchemaFiles.SELL_SCHEMA.getPath(), SellEntity.class),
                new ReadTask<>(
                        SystemData.PRODUCT_ITEM, DataFiles.PRODUCTS.getPath(),
                        SchemaFiles.PRODUCT_SCHEMA.getPath(), ProductEntity.class));

        ExecutorService service = Executors.newWorkStealingPool(fileData.size());
        List<List<?>> dataList = service.invokeAll(fileData).stream().
                map(s -> {
                    try {
                        return s.get();
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                }).collect(Collectors.toList());
        service.shutdown();

        return List.of(dataList.get(3), dataList.get(0), dataList.get(1), dataList.get(2));
    }

    public void save() throws SAXException, InterruptedException {
        List<WriteTask<?>> list = Arrays.asList(
                new WriteTask<>(
                        SystemData.PRODUCT_LIST, SystemData.PRODUCT_ITEM, "id",
                        DataFiles.PRODUCTS.getPath(), SchemaFiles.PRODUCT_SCHEMA.getPath(),
                        ProductEntity.class, data.mergeToTuple(data.products, data.productsBuffer)),
                new WriteTask<>(
                        SystemData.SELLER_ITEM, SystemData.SELLER_LIST, "id",
                        DataFiles.SELLERS.getPath(), SchemaFiles.SELLER_SCHEMA.getPath(),
                        SellerEntity.class, data.mergeToTuple(data.sellers, data.sellersBuffer)),
                new WriteTask<>(
                        SystemData.SELL_LIST, SystemData.SELL_ITEM, "id",
                        DataFiles.SELLS.getPath(), SchemaFiles.SELL_SCHEMA.getPath(),
                        SellEntity.class, data.mergeToTuple(data.sells, data.sellsBuffer)),
                new WriteTask<>(
                        SystemData.SELLER_RANGE_ITEM_LIST, SystemData.SELLER_RANGE_ITEM, List.of("productID", "sellerID"),
                        DataFiles.SELLER_RANGE.getPath(), SchemaFiles.SELLER_RANGE_ITEM_SCHEMA.getPath(),
                        SellerRangeItemEntity.class, data.mergeToTuple(this.getAllSellersRange(), data.allSellersRangeBuffer, 2)));

        ExecutorService service = Executors.newFixedThreadPool(list.size());
        service.invokeAll(list);
        service.shutdown();
    }

    public ListenedSet<Product> getProducts() {
        return this.data.products;
    }

    public ListenedSet<Seller> getSellers() {
        return this.data.sellers;
    }

    public ListenedSet<Sell> getSells() {
        return this.data.sells;
    }

    public ListenedSet<SellerRangeItem> getAllSellersRange() {
        return data.sellers.stream().flatMap(s -> s.getSellerProducts().stream()).collect(Collectors.toCollection(() -> new ListenedHashSet<>(SystemData.SELLER_RANGE_ITEM_LIST)));
    }
}