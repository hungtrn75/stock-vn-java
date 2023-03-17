package com.skymapglobal.vnstock.workspace.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skymapglobal.vnstock.models.StockItem;
import com.skymapglobal.vnstock.workspace.blank.BlankFragment;
import com.skymapglobal.vnstock.workspace.chart.ChartFragment;

import java.util.List;

public class DetailPagerAdapter extends FragmentStateAdapter {
    private List<String> tabs;
    private String code;
    private StockItem stockItem;

    public DetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> tabs, String code,StockItem stockItem) {
        super(fragmentActivity);
        this.tabs = tabs;
        this.code = code;
        this.stockItem = stockItem;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return ChartFragment.newInstance(code);
            case 1:
                return DetailFragment.newInstance(stockItem);
            default:
                return new BlankFragment();
        }

    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }
}
