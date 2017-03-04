package com.example.chris.stocker;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chris on 3/3/2017.
 */

public class Stock {
    String ticker;
    String name;
    double lastTradePrice;
    boolean direction;
    double changeAmount;
    double changePercentage;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(double lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }

    public JSONObject createStockObject(Context context, String ticker, String name, double lastTradePrice, boolean direction, double changeAmount, double changePercentage) {
        JSONObject stockObj = new JSONObject();
        try {
            stockObj.put(context.getString(R.string.ticker), ticker);
            stockObj.put(context.getString(R.string.name), name);
            stockObj.put(context.getString(R.string.lastTradePrice), lastTradePrice);
            stockObj.put(context.getString(R.string.direction), direction);
            stockObj.put(context.getString(R.string.changeAmount), changeAmount);
            stockObj.put(context.getString(R.string.changePercentage), changePercentage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stockObj;
    }
}
