package com.example.chris.stocker;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Chris on 3/16/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.StockViewHolder>{
    MainActivity activity;

    public class StockViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView ticker, name, lastTradePrice,changeAmount,changePercentage,direction;

        StockViewHolder (View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            ticker = (TextView) itemView.findViewById(R.id.ticker);
            name = (TextView) itemView.findViewById(R.id.name);
            lastTradePrice = (TextView) itemView.findViewById(R.id.lastTradePrice);
            changeAmount = (TextView) itemView.findViewById(R.id.changeAmount);
            changePercentage = (TextView) itemView.findViewById(R.id.changePercentage);
            direction = (TextView) itemView.findViewById(R.id.direction);
            cv.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    activity.deleteStock(ticker.getText().toString());
                    return true;
                }
            });
        }
    }

    ArrayList<Stock> stockList;

    public RVAdapter(MainActivity activity, ArrayList<Stock> stockList) {
        this.activity = activity;
        this.stockList = stockList;
    }

    @Override
    public RVAdapter.StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout,parent,false);
        StockViewHolder svh = new StockViewHolder(v);
        return svh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(RVAdapter.StockViewHolder holder, int position) {
        holder.ticker.setText(stockList.get(position).getTicker());
        holder.name.setText(stockList.get(position).getName());
        holder.lastTradePrice.setText(stockList.get(position).getLastTradePrice());
        holder.changeAmount.setText(stockList.get(position).getChangeAmount());
        holder.changePercentage.setText(stockList.get(position).getChangePercentage());
        holder.direction.setText("+");
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
