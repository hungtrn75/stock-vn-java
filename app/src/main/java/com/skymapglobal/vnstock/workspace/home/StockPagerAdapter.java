package com.skymapglobal.vnstock.workspace.home;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.skymapglobal.vnstock.models.Tab;
import java.util.HashMap;
import java.util.List;

public class StockPagerAdapter extends FragmentStateAdapter {

  private final HashMap<String, TabStockFragment> memoFragments = new HashMap<>();
  private List<Tab> tabList;

  public StockPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
    super(fragmentActivity);
  }

  @SuppressLint("NotifyDataSetChanged")
  public void setTabList(List<Tab> tabList) {
    this.tabList = tabList;
    tabList.forEach((tab -> {
      if (memoFragments.containsKey(tab.getTitle())) {
        memoFragments.get(tab.getTitle()).setStockItemList(tab.getStocks());
      }
    }));
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Tab tab = tabList.get(position);
    TabStockFragment memo = new TabStockFragment(tabList.get(position).getStocks());
    memoFragments.put(tab.getTitle(), memo);
    return memo;
  }

  @Override
  public int getItemCount() {
    if (tabList != null) {
      return tabList.size();
    }
    return 0;
  }

  public void dispose() {
    memoFragments.values().forEach(Fragment::onDestroy);
  }
}
