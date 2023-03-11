package com.skymapglobal.vnstock.workspace.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.StockItem;
import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {
  private Context mContext;
  private StockClickListener listener;
  private List<StockItem> mStocks;

  public StockAdapter(Context context, StockClickListener listener){
    this.mContext = context;
    this.listener = listener;
    this.mStocks = new ArrayList<>();
  }

  @SuppressLint("NotifyDataSetChanged")
  public void updateDataSource( List<StockItem> stocks){
    this.mStocks = stocks;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View stockView = inflater.inflate(R.layout.item_stock, parent, false);
    ViewHolder viewHolder = new ViewHolder(stockView);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    StockItem item = mStocks.get(position);
    holder.nameBank.setText(item.getCode());
    holder.price.setText(String.valueOf(item.getPrice()));
    if(item.getRatio() < 0){
      holder.rate.setBackground(mContext.getDrawable(R.drawable.ic_border_red));
      holder.price.setTextColor(mContext.getColor(R.color.red));
      holder.rate.setText(String.valueOf(String.format("%.2f",item.getRatio()))+"%");
    }else if(item.getRatio() == 0){
      holder.rate.setBackground(mContext.getDrawable(R.drawable.ic_border_yellow));
      holder.price.setTextColor(mContext.getColor(R.color.yellow));
      holder.rate.setText(String.valueOf(String.format("%.2f",item.getRatio()))+"%");
    }else{
      holder.rate.setBackground(mContext.getDrawable(R.drawable.ic_border));
      holder.price.setTextColor(mContext.getColor(R.color.green));
      holder.rate.setText("+"+String.valueOf(String.format("%.2f",item.getRatio()))+"%");
    }

    holder.des.setText(item.getName());
    holder.vol.setText("Vol:     "+ String.valueOf(item.getVol()));
    holder.chg.setText("Chg:     "+String.valueOf(item.getChg()));
    holder.itemView.setOnClickListener(v->{
      listener.onItemClickListener(item,position);
    });
  }

  @Override
  public int getItemCount() {
    return mStocks.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder{
    private TextView nameBank;
    private TextView price;
    private TextView rate;
    private TextView des;
    private  TextView vol;
    private  TextView chg;
    public ViewHolder(@NonNull View itemView){
      super(itemView);
      nameBank = itemView.findViewById(R.id.nameBank);
      price = itemView.findViewById(R.id.price);
      rate = itemView.findViewById(R.id.rate);
      des = itemView.findViewById(R.id.des);
      vol = itemView.findViewById(R.id.vol);
      chg = itemView.findViewById(R.id.chg);
    }
  }
}
