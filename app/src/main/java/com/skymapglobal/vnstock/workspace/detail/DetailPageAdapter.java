package com.skymapglobal.vnstock.workspace.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skymapglobal.vnstock.models.StockItem;

public class DetailPageAdapter extends FragmentStateAdapter {
    private StockItem stockItem;
    public DetailPageAdapter(@NonNull FragmentActivity fragmentActivity,StockItem stockItem) {
        super(fragmentActivity);
        this.stockItem = stockItem;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 1){
            return DetailFragment.newInstance(stockItem);
        }
        return BlankFragment.newInstance("12","23");


    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
