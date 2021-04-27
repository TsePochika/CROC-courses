package com.company.MarketSystem.Output;

import java.time.LocalDateTime;

public class TopTask {
    private final LocalDateTime dateTime;
    private final Integer count;

    public TopTask(LocalDateTime dateTime, Integer integer) {
        this.dateTime = dateTime;
        this.count = integer;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Integer getCount() {
        return count;
    }
}
