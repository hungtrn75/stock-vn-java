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
    final StockItem stock = mStocks.get(position);
    holder.mName.setText(stock.getName());
    holder.itemView.setOnClickListener(v->{
      listener.onItemClickListener(stock,position);
    });
  }

  @Override
  public int getItemCount() {
    return mStocks.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder{
    private TextView mName;
    public ViewHolder(@NonNull View itemView){
      super(itemView);
      mName = itemView.findViewById(R.id.name);
    }
  }
}
