package com.example.chris.stocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Chris on 3/16/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchtable";

    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STOCKS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
             + SYMBOL + " TEXT not null unique, " + COMPANY + " TEXT not null)";
        db.execSQL(CREATE_STOCKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void addStock(Stock stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("addStock: Adding " + stock.getTicker());
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getTicker());
        values.put(COMPANY, stock.getName());
        db.insert(TABLE_NAME, null, values);
        db.close();
        System.out.println("addStock: Add Complete");
    }

    public void deleteStock(String symbol) {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("Deleting stock: "+symbol);
        db.delete(TABLE_NAME, SYMBOL + " = ?", new String[] {symbol});
        db.close();
        System.out.println("Stock deleted: "+symbol);
    }

    public ArrayList<String[]> loadStocks() {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("load all Symbol-Company entries from DB");
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, new String[] {SYMBOL, COMPANY},
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);

                stocks.add(new String[] {symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
        }
        return stocks;
    }
}
