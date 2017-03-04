package com.example.chris.stocker;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Stock> stockList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            addStock();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addStock(){
        //check if network is available
        if (isNetworkAvailable() == true) {
            //show dialog to get stock info
            
            //Download stock details
            new DownloadStocks(this).execute();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.IDE_title))
                    .setMessage(getString(R.string.IDE_text))
                    .show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}


class DownloadStocks extends AsyncTask<ArrayList<Stock>, Void, ArrayList<Stock>> {

    Activity activity;
    Context context;

    public DownloadStocks(Activity activity) {
    this.activity = activity;
    this.context = activity.getApplicationContext();
    }

    @Override
    protected ArrayList<Stock> doInBackground(ArrayList<Stock>... params) {
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Stock> stocks) {
        super.onPostExecute(stocks);
    }
}