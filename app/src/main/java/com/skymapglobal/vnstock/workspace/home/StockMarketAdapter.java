package com.skymapglobal.vnstock.workspace.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.StockMarket;

import java.util.List;

public class StockMarketAdapter extends RecyclerView.Adapter<StockMarketAdapter.ViewHolder> {

    private List<StockMarket> mStockMarket;
    private Context mContext;

    public StockMarketAdapter(List<StockMarket> mStockMarket, Context mContext) {
        this.mStockMarket = mStockMarket;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View stockView = inflater.inflate(R.layout.item_market, parent, false);
        StockMarketAdapter.ViewHolder viewHolder = new StockMarketAdapter.ViewHolder(stockView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockMarket item = mStockMarket.get(position);
        holder.index.setText(item.getIndex());
        if(item.getName().equals("HNXINDEX")){
            holder.name.setText("HNX");
        }else if(item.getName().equals("VNINDEX")){
            holder.name.setText("VN INDEX");
        }else if(item.getName().equals("HNXUPCOMINDEX")){
            holder.name.setText("UPCOM");
        }else{
            holder.name.setText(item.getName());
        }


        if(Double.parseDouble(item.getChange()) < 0){
            holder.text.setBackground(mContext.getDrawable(R.color.red));
            holder.text.setText(item.getChange()+"("+ item.getPercent() +"%)");
            holder.index.setTextColor(mContext.getColor(R.color.red));
        }else if(Double.parseDouble(item.getChange()) == 0){
            holder.text.setBackground(mContext.getDrawable(R.color.orange));
            holder.text.setText(item.getChange()+"("+ item.getPercent() +"%)");
            holder.index.setTextColor(mContext.getColor(R.color.orange));
        }else{
            holder.text.setBackground(mContext.getDrawable(R.color.green));
            holder.text.setText('+'+item.getChange()+"("+ item.getPercent() +"%)");
            holder.index.setTextColor(mContext.getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return mStockMarket.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView index;
        private TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            index = itemView.findViewById(R.id.index);
            text = itemView.findViewById(R.id.change_percent);
        }
    }
}
