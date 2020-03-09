package com.example.bigspenderapk;

import java.io.Serializable;

public class LatestExchangeRate implements Serializable {
    String from;
    String to;
    float rate;

    public LatestExchangeRate(String from, String to, float rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }
}
