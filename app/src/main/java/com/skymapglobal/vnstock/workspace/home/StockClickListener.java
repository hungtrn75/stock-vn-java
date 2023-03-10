package com.skymapglobal.vnstock.workspace.home;

import com.skymapglobal.vnstock.models.StockItem;

public interface StockClickListener {
  void onItemClickListener(StockItem stockItem, Integer position);
}
