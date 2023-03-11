package com.skymapglobal.vnstock.workspace.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skymapglobal.vnstock.workspace.blank.BlankFragment;
import com.skymapglobal.vnstock.workspace.chart.ChartFragment;

import java.util.List;

public class DetailPagerAdapter extends FragmentStateAdapter {
    private List<String> tabs;
    private String code;

    public DetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> tabs, String code) {
        super(fragmentActivity);
        this.tabs = tabs;
        this.code = code;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return ChartFragment.newInstance(code);
            default:
                return new BlankFragment();
        }

    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }
}
