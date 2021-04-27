package com.company;

import com.company.MarketSystem.Entities.ProductEntity;
import com.company.MarketSystem.Entities.SellerEntity;
import com.company.MarketSystem.Model.*;
import com.company.MarketSystem.Output.PriceTask;
import com.company.MarketSystem.Output.TopTask;
import com.company.Void.UnsignedIntCorrecter;
import com.google.gson.Gson;
import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>1. Для каждого товара вывести в файл продавца, у которого наименьшая цена на этот товар, и саму цену на этот товар у этого продавца</p>
 * <p>2. Вывести в файл топ 5 дат, в которые было продано наибольшее количество товаров</p>
 * <p>3. Входной - XML. Выходной - JSON</p>
 */
public class Main {
    public static final String OUTPUTS = "src\\com\\company\\output.json";
    public static final String SEP = System.lineSeparator();
    public static final Scanner in = new Scanner(System.in);
    public static Market market;

    public static void main(String[] args) {
        try {
            market = Market.newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        final String ioError = "Не удалось записать" + SEP;
        final String[] menuM = new String[]{SEP + "1.Наименьшие цены на товар" + SEP, "2.Топ дат" + SEP, "3.Выход" + SEP};
        final String[] menuS = new String[]{SEP + "1.Наименьшая цена" + SEP, "2.Наибольшая цена" + SEP};

        final String inputMsgForMenuItem = "Пункт: ";
        final String inputMsgForTop = "Кол-во продавцов: ";

        final String condErrorMsgForTop = "Нет столько дат" + SEP;
        final String condErrorMsgForMenuItem = "Нет такого пункта" + SEP;

        final String parseErrorMsg = "Это не число" + SEP;

        final Predicate<Integer> menuMCond = i -> i >= 1 && i <= menuM.length;
        final Predicate<Integer> menuSCond = i -> i >= 1 && i <= menuS.length;

        long dateCount = market.getSells().stream().map(Sell::getSellDate).distinct().count();
        final Predicate<Integer> topCond = i -> i >= 1 && i <= dateCount;

        UnsignedIntCorrecter correcter = new UnsignedIntCorrecter(in);
        boolean isContinue = true;
        Object writeObj = null;
        while (isContinue) {
            Arrays.stream(menuM).forEach(System.out::print);
            switch (correcter.inputUnsignedInt(inputMsgForMenuItem, parseErrorMsg, condErrorMsgForMenuItem, menuMCond)) {
                case 1 -> {
                    Arrays.stream(menuS).forEach(System.out::print);
                    int num = correcter.inputUnsignedInt(inputMsgForMenuItem, parseErrorMsg, condErrorMsgForMenuItem, menuSCond);
                    boolean isMin = 1 == num;
                    List<PriceTask> priceTaskList = selectStream(num).
                            map(sri -> new PriceTask(
                                    sri.getProduct(),
                                    market.getSellers().
                                            stream().
                                            filter(s -> s.getID() == sri.getSellerID()).
                                            findFirst().orElseThrow(),
                                    sri.getProductPrice())).
                            collect(Collectors.toList());

                    if (isMin) {
                        priceTaskList.sort(Comparator.comparing(PriceTask::getPrice));
                    } else {
                        priceTaskList.sort(Comparator.comparingInt(PriceTask::getPrice).reversed());
                    }

                    String minOrMax = isMin ? "Наименьшая" : "Наибольшая";
                    displayFirstTask(priceTaskList, minOrMax + " цена");
                    writeObj = priceTaskList;
                }
                case 2 -> {
                    if (dateCount != 0) {
                        System.out.printf("(Максимум: %d)" + SEP, dateCount);
                        int topCount = correcter.inputUnsignedInt(inputMsgForTop, parseErrorMsg, condErrorMsgForTop, topCond);
                        List<TopTask> topTasks = market.getSells().stream().
                                collect(Collectors.groupingBy(Sell::getSellDate, Collectors.summingInt(Sell::getCountSellProducts))).
                                entrySet().stream().
                                map(s -> new TopTask(s.getKey(), s.getValue())).
                                sorted(Comparator.comparing(TopTask::getCount).reversed()).
                                limit(topCount).
                                collect(Collectors.toList());

                        displaySecondTask(topTasks);
                        writeObj = topTasks;
                    }
                }
                case 3 -> {
                    isContinue = false;
                    if (Objects.nonNull(writeObj)) {
                        try {
                            writeOutput(writeObj);
                        } catch (IOException e) {
                            System.out.print(ioError);
                        }
                    }
                }
            }
        }
    }

    public static void writeOutput(Object o) throws IOException {
        try (var fileWriter = new FileWriter(OUTPUTS, StandardCharsets.UTF_8)) {
            fileWriter.write(new Gson().toJson(o));
        }
    }

    public static Stream<SellerRangeItem> selectStream(int n) {
        return switch (n) {
            case 1 -> getMin();
            case 2 -> getMax();
            default -> throw new IllegalStateException("Unexpected value: " + n);
        };
    }

    public static Stream<SellerRangeItem> getMin() {
        return market.getProducts().stream().
                map(pr -> market.getAllSellersRange().stream().
                        filter(sri -> sri.getProductID() == pr.getID()).
                        min(Comparator.comparingInt(SellerRangeItem::getProductPrice)).orElseThrow());
    }

    public static Stream<SellerRangeItem> getMax() {
        return market.getProducts().stream().
                map(pr -> market.getAllSellersRange().stream().
                        filter(sri -> sri.getProductID() == pr.getID()).
                        max(Comparator.comparingInt(SellerRangeItem::getProductPrice)).orElseThrow());
    }

    public static void displayFirstTask(List<PriceTask> priceTaskList, String minOrMax) {
        AT_Row row;
        AsciiTable table = new AsciiTable();
        table.addRule();
        row = table.addRow(null, "Продукт", null, null, "Продавец", minOrMax);
        table.addRule();
        row.setTextAlignment(TextAlignment.CENTER);
        row = table.addRow("id", "Название", "id", "Имя", "Фамилия", "");
        row.setTextAlignment(TextAlignment.CENTER);
        table.addRule();
        ProductEntity product;
        SellerEntity seller;

        for (var task : priceTaskList) {
            product = task.getProduct();
            seller = task.getSeller();
            row = table.addRow(
                    product.getID(), product.getProductName(),
                    seller.getID(), seller.getSellerName(), seller.getSellerLastName(),
                    task.getPrice());
            table.addRule();
            row.setTextAlignment(TextAlignment.LEFT);
        }
        System.out.println(table.render(90));
    }

    public static void displaySecondTask(List<TopTask> topTasks) {
        AT_Row row;
        AsciiTable table = new AsciiTable();
        table.addRule();
        row = table.addRow(null, null, null, "Дата", "");
        table.addRule();
        row.setTextAlignment(TextAlignment.CENTER);
        row = table.addRow("Год", "Месяц", "День", "Время", "Количество");
        row.setTextAlignment(TextAlignment.CENTER);
        table.addRule();

        LocalDateTime dateTime;
        for (var top : topTasks) {
            dateTime = top.getDateTime();
            row = table.addRow(
                    dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(),
                    dateTime.getHour() + ":" + dateTime.getMinute(),
                    top.getCount());
            table.addRule();
            row.setTextAlignment(TextAlignment.LEFT);
        }
        System.out.println(table.render(90));
    }

}

