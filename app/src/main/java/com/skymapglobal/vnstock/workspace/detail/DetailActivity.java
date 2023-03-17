package com.skymapglobal.vnstock.workspace.detail;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.StockItem;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private List<String> tabs = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private DetailPagerAdapter detailPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        createTabs();
        setupViews();
        setupListener();
    }

    @SuppressLint("DefaultLocale")
    private void setupViews(){
        Intent intent = getIntent();
        StockItem stockItem = (StockItem) intent.getSerializableExtra("stockItem");

        viewPager2 = findViewById(R.id.detailViewPager);
        tabLayout = findViewById(R.id.detailTabLayout);

        detailPagerAdapter = new DetailPagerAdapter(this, tabs, stockItem.getCode(), stockItem);
        viewPager2.setAdapter(detailPagerAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    tab.setText(tabs.get(position));
                });
        tabLayoutMediator.attach();

        ((TextView)findViewById(R.id.code)).setText(stockItem.getCode());
        ((TextView)findViewById(R.id.price)).setText(String.format("%.2f",stockItem.getPrice()));
        ((TextView)findViewById(R.id.name)).setText(stockItem.getName());
        ((TextView)findViewById(R.id.vol)).setText(String.format("Vol: %.2f",stockItem.getVol()));
        ((TextView)findViewById(R.id.chg)).setText(String.format("Chg: %.2f",stockItem.getChg()));
    }

    private void setupListener() {
        findViewById(R.id.closeBtn).setOnClickListener(v -> {
            onBackPressed();
        });
    }

    protected void createTabs() {
        tabs.add("Biểu đồ");
        tabs.add("Chi tiết");
        tabs.add("Lịch sử");
        tabs.add("Tin tức nội bộ");
        tabs.add("Thị trường");
    }
}