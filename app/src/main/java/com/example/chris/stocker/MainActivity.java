package com.example.chris.stocker;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ArrayList<Stock> stockList = new ArrayList<>();
    DatabaseHandler db = new DatabaseHandler(this);
    private RVAdapter rvAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadStocks DS = new DownloadStocks(this);
        DownloadStockData DSD = new DownloadStockData(this);

        //set background color
        //getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        rvAdapter = new RVAdapter(this, stockList);
        mRecyclerView.setAdapter(rvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_stock) {
            retrieveStock();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void processNewStock(String symbol, String name) {
        new DownloadStockData(this).execute(symbol, name);
    }

    public void addStockToList(JSONObject j) {
        //show alert for duplicate object
        if (checkDuplicateFound(j) == true ) {
            new AlertDialog.Builder(this)
                    .setTitle("Stock Already Added")
                    .setMessage("This stock is already added to the list.")
                    .show();
        } else {
            //remap object to stock object
            String ticker, name, changeAmount, changePercentage, lastTradePrice;
            ticker = name = changeAmount = changePercentage = lastTradePrice = null;
            boolean direction = false;
            try {
                ticker = j.getString("t");
                name = j.getString("stockName");
                lastTradePrice = j.getString("l");
                changeAmount = j.getString("c");
                changePercentage = j.getString("cp");
                if  (changeAmount.substring(0,1).equals("+")) {
                    direction = true; //true == "+"
                } else {
                    direction = false; //false == "-"
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //add stock to list
            Stock sto = new Stock(ticker,name,lastTradePrice,direction,changeAmount,changePercentage);
            stockList.add(sto);
            //sort stockList
            Collections.sort(stockList, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            System.out.println("Sorted stockList is: ");
            for (Stock s : stockList) {
                System.out.println(s.toString());
            }
            //add stock to db
            db.addStock(sto);
            //notify adapter changed
            rvAdapter.notifyDataSetChanged();

            //print stocks
            ArrayList<String[]> ASR = db.loadStocks();
            ASR.toString();
        }
    }

    public void deleteStock(final String symbol) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Stock")
                .setMessage("Are you sure you wish to delete this stock?")
                .setPositiveButton("Yes", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Deleting Stock");
                        db.deleteStock(symbol);
                        System.out.println("Stock removed from db");
                        for (Stock s : stockList ) { //loop through stocklist
                            if (s.getTicker() == symbol) { //if symbol name is equal to ticker name
                                stockList.remove(s); //delete the stock
                                System.out.println("Stock removed: "+s.getTicker());
                            }
                        }
                        //notify adapter changed
                        rvAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", null)
                .show();
        //print stocks
        ArrayList<String[]> ASR = db.loadStocks();
        ASR.toString();
    }

    public boolean checkDuplicateFound(JSONObject j) {
        if (stockList.contains(j)) {
            return true;
        } else {
            return false;
        }
    }

    public void retrieveStock(){
        //check if network is available
        if (isNetworkAvailable() == true) {
            //show dialog to get stock info
           final EditText search = new EditText(this);
            search.setId(R.id.search);
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.SSD_title))
                    .setView(search)
                    .setPositiveButton(getString(R.string.find), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Search Text is "+search.getText().toString());
                            //should be where it goes to download stock
                            //or parses string and then searches
                            /*TODO
                            * Check for duplicate*/
                            //Download stock details
                              new DownloadStocks(MainActivity.this).execute(search.getText().toString());

                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Cancel clicked");
                        }
                    })
                    .show();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.IDE_title))
                    .setMessage(getString(R.string.IDE_text))
                    .show();
        }
    }
}


class DownloadStocks extends AsyncTask<String, Void, String> {
    MainActivity activity;
    Context context;
    final String APIKey = "94e3a83d4a1c7c5159661a5834bad8d8b306e054";
    ProgressDialog pd;
    int statusCode;

    public DownloadStocks(MainActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(activity);
        pd.setMessage("Retrieving Stocks...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("http://stocksearchapi.com/api/?search_text="+params[0]+"&api_key="+APIKey);
            connection = (HttpURLConnection) url.openConnection();

            connection.connect();
            statusCode = connection.getResponseCode();
            //handle other status codes
            if (statusCode  == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+'\n');
                    System.out.println("DownloadStocks Response: "+line);
                }
                return buffer.toString();
            } else {
                System.out.println("Status Code is "+statusCode);
            }
        } catch (MalformedURLException e ){
            e.printStackTrace();
        } catch (IOException e ) {
            e.printStackTrace();
        } finally {
            if (connection != null) {  //connection opened
                connection.disconnect(); //close connection
            }
            try {
                if (reader != null) { //reader opened
                    reader.close(); //close reader
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String stocks) {
        super.onPostExecute(stocks);
        if (pd.isShowing()) {
            pd.dismiss();
        }
        if (statusCode != HttpURLConnection.HTTP_OK) {
            new AlertDialog.Builder(activity)
                    .setTitle("Error")
                    .setMessage("Could not find matching stock.")
                    .show();
        }
        else if (statusCode == 400) {
            new AlertDialog.Builder(activity)
                    .setTitle("No Stock Entered")
                    .setMessage("Cannot obtain stock.")
                    .show();
        }
        else if (statusCode == 429) {
            new AlertDialog.Builder(activity)
                    .setTitle("API Calls Exceeded")
                    .setMessage("Call amount was exceeded for the month.")
                    .show();
        }
        else if ((statusCode == 500) || (statusCode == 503)) {
            new AlertDialog.Builder(activity)
                    .setTitle("Server Error")
                    .setMessage("Servers temporarily unavailable. Please try again later.")
                    .show();
        }
        else { //stocks found
            JSONArray JA = null;
            try {
                JA = new JSONArray(stocks);
                if (JA.length() == 1) { //only one stock found
                    //System.out.println("One stock found");
                    //System.out.println("number of stocks are "+JA.length());
                   // MainActivity ma = new MainActivity();
                    JSONObject j = (JSONObject)JA.get(0);
                    activity.processNewStock(j.getString("company_symbol"), j.getString("company_name")); //pass symbol string and company nameto processNewStock
                } else { //more than one stock found
                    //System.out.println("More than one stock found");
                    //System.out.println("number of stocks are "+JA.length());


                    TextView[] tArray = new TextView[JA.length()]; //create textview array
                    JSONObject[] k = new JSONObject[JA.length()];
                    LinearLayout linear = new LinearLayout(activity);
                    linear.setOrientation(LinearLayout.VERTICAL);
                    for (int i = 0; i<JA.length(); i++) {
                        //create textview and jsonobject for each stock
                        k[i] = (JSONObject)JA.get(i);
                        tArray[i] = new TextView(activity);
                        tArray[i].setText(k[i].getString("company_symbol")+" - "+k[i].getString("company_name"));
                        //styling
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        lp.setMargins(0,30,0,30);
                        tArray[i].setLayoutParams(lp);
                        //styling

                        //set each textview's onclick listener and addview
                        linear.addView(tArray[i]);
                        tArray[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               // activity.processNewStock(k[i].getString("company_symbol"),k[i].getString("company_name"));
                                TextView tv = (TextView)v;
                                System.out.println("clicked text is "+tv.getText().toString());
                                String[] data = tv.getText().toString().split(" - ");
                                String symbol = data[0];
                                String company_name = data[1];
                                System.out.println("Symbol is: "+symbol);
                                System.out.println("Company name is: "+company_name);
                                //TODO uncomment this for the rest
                                activity.processNewStock(symbol, company_name);
                            }
                        });
                    }
                    new AlertDialog.Builder(activity)
                            .setTitle("Select A Stock")
                            .setView(linear)
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

class DownloadStockData extends AsyncTask <String, Void, String> {
    MainActivity activity;
    Context context;
    int statusCode;
    String stockName;

    public DownloadStockData(MainActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("http://finance.google.com/finance/info?client=ig&q="+params[0]); //get data from google
            connection = (HttpURLConnection)url.openConnection();
            connection.connect();

            statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    System.out.println("DownloadStockData Response is: " + line);
                }
                stockName = params[1];
                System.out.println("Stock Name is: "+stockName);
                return buffer.toString();
            } else {
                System.out.println("Status Code is "+statusCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e){
                    e.printStackTrace();
                }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (statusCode == HttpURLConnection.HTTP_OK) {
            //response retrieved successfully
            System.out.println("inPostExecute DownloadStockData");
            String t = s.substring(4); //remove // from the string
            try {
                JSONArray JA = new JSONArray(t);
                JSONObject j = (JSONObject)JA.get(0);
                JSONObject newJ = new JSONObject();
                //add elements to the new object
                newJ.put("stockName", stockName); //company name
                newJ.put("t",j.getString("t")); //ticker
                newJ.put("l",j.getString("l")); //last trade price
                newJ.put("c",j.getString("c")); //price change amount
                newJ.put("cp",j.getString("cp")); //price change percentage

                //MainActivity ma = new MainActivity();
                activity.addStockToList(newJ); //pass object to the stocklist
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle("Error")
                    .setMessage("No stock found.")
                    .show();
        }
    }
}