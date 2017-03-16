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
    String lastTradePrice;
    boolean direction;
    String changeAmount;
    String changePercentage;

    public Stock(String ticker, String name, String lastTradePrice, boolean direction, String changeAmount, String changePercentage) {
        this.ticker = ticker;
        this.name = name;
        this.lastTradePrice = lastTradePrice;
        this.changeAmount = changeAmount;
        this.changePercentage = changePercentage;
    }

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

    public String getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(String lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public boolean getDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public String getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(String changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(String changePercentage) {
        this.changePercentage = changePercentage;
    }

    @Override
    public String toString() {
        return "ticker: "+getTicker()+" name: "+getName()+" lastTradePrice: "+getLastTradePrice()+" direction: "+getDirection()+" changeAmount: "+getChangeAmount()+" changePercentage: "+getChangePercentage()+"\n";
    }

    public JSONObject createStockObject(Context context, String ticker, String name, String lastTradePrice, boolean direction, String changeAmount, String changePercentage) {
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
