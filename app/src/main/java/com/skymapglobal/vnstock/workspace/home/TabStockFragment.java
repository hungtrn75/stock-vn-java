package com.skymapglobal.vnstock.workspace.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.StockItem;
import com.skymapglobal.vnstock.workspace.detail.DetailActivity;
import java.util.List;

public class TabStockFragment extends Fragment implements StockClickListener {

  private List<StockItem> stockItemList;
  private RecyclerView mRvStock ;
  private StockAdapter mStockAdapter;

  public TabStockFragment(List<StockItem> stocks) {
    stockItemList = stocks;
  }

  public void setStockItemList(List<StockItem> stockItemList) {
    this.stockItemList = stockItemList;
    if (mStockAdapter != null) {
      Log.e("setStockItemList", "update");
      mStockAdapter.updateDataSource(stockItemList);
    }
  }

  public static TabStockFragment newInstance(List<StockItem> stocks) {
    TabStockFragment fragment = new TabStockFragment(stocks);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRvStock = getView().findViewById(R.id.rvStock);

    mStockAdapter = new StockAdapter(getContext(), this);
    mRvStock.setAdapter(mStockAdapter);
    mRvStock.setLayoutManager(new LinearLayoutManager(getContext()));
    if (stockItemList != null) {
      mStockAdapter.updateDataSource(stockItemList);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_tab_stock, container, false);
  }

  @Override
  public void onItemClickListener(StockItem stockItem, Integer position) {
    Intent intent = new Intent(getActivity(), DetailActivity.class);
    intent.putExtra("stockItem", stockItem);
    startActivity(intent);
  }
}