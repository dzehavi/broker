package com.dz.broker;

public class Quote {
    final String name;
    final Double price;

    public Quote(String stockName, Double price) {
        this.name = stockName;
        this.price = price;
    }
}
