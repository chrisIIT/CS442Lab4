package com.example.chris.stocker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

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

public class MainActivity extends AppCompatActivity {
    ArrayList<Stock> stockList = new ArrayList<>();
    DownloadStocks DS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set background color
        //getWindow().getDecorView().setBackgroundColor(Color.BLACK);
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
//    public boolean checkDuplicates(ArrayList<>) {
//
//    }
}


class DownloadStocks extends AsyncTask<String, Void, String> {
    Activity activity;
    Context context;
    final String APIKey = "94e3a83d4a1c7c5159661a5834bad8d8b306e054";
    ProgressDialog pd;
    int statusCode;

    public DownloadStocks(Activity activity) {
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
                    System.out.println("Response: "+line);
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
            System.out.println("Stocks are "+stocks);
            JSONArray JA = null;
            try {
                JA = new JSONArray(stocks);
                if (JA.length() == 1) { //only one stock found
                    System.out.println("One stock found");
                    System.out.println("number of stocks are "+JA.length());
                    MainActivity ma = new MainActivity();
                    JSONObject j = (JSONObject)JA.get(0);
                    ma.processNewStock(j.getString("company_symbol"), j.getString("company_name")); //pass symbol string and company nameto processNewStock
                } else { //more than one stock found
                    System.out.println("More than one stock found");
                    System.out.println("number of stocks are "+JA.length());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

class DownloadStockData extends AsyncTask <String, Void, String> {
    Activity activity;
    Context context;
    int statusCode;
    String stockName;

    public DownloadStockData(Activity activity) {
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

                while ((line = reader.readLine()) != null)) {
                    buffer.append(line + "\n");
                    System.out.println("Response is: " + line);
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
            String t = s.substring(2); //remove // from the string
            try {
                JSONArray JA = new JSONArray(t);
                JSONObject j = (JSONObject)JA.get(0);
                JSONObject newJ = new JSONObject();
                newJ.put("stockName", stockName);
                newJ.put("t",j.getString("t"));
                newJ.put("l",j.getString("t"));
                newJ.put("c",j.getString("c"));
                newJ.put("cp",j.getString("cp"));

                MainActivity ma = new MainActivity();
                ma.addStockToList(newJ); //pass object to the stocklist
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