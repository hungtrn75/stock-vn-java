package com.skymapglobal.vnstock.workspace.detail;


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
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private DetailPageAdapter detailPageAdapter;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    Intent intent=getIntent();
    StockItem item = (StockItem) intent.getSerializableExtra("stockItem");
    Log.e("DetailActivity",item.getCode());

    TextView nameBank = findViewById(R.id.nameBank);
    nameBank.setText(item.getCode());
    TextView price = findViewById(R.id.price);
    price.setText(String.valueOf(item.getPrice()));
    TextView rate = findViewById(R.id.rate);
    if(item.getRatio() < 0){
      rate.setBackground(getDrawable(R.drawable.ic_border_red));
      price.setTextColor(getColor(R.color.red));
      rate.setText(String.valueOf(String.format("%.2f",item.getRatio()))+"%");
    }else if(item.getRatio() == 0){
     rate.setBackground(getDrawable(R.drawable.ic_border_orange));
      price.setTextColor(getColor(R.color.yellow));
      rate.setText(String.valueOf(String.format("%.2f",item.getRatio()))+"%");
    }else{
    rate.setBackground(getDrawable(R.drawable.ic_border));
      price.setTextColor(getColor(R.color.green));
      rate.setText("+"+String.valueOf(String.format("%.2f",item.getRatio()))+"%");
    }
    TextView des = findViewById(R.id.des);
    des.setText(item.getName());
    TextView vol = findViewById(R.id.vol);
    vol.setText("Vol:     "+ String.valueOf(item.getVol()));
    TextView chg = findViewById(R.id.chg);
   chg.setText("Chg:     "+String.valueOf(item.getChg()));

    List<String> tabs = new ArrayList<>();
    tabs.add("Bieu do");
    tabs.add("Chi tiet");
    tabs.add("Lich su");

      viewPager2 = findViewById(R.id.pager2);
      viewPager2.setOffscreenPageLimit(1);

      tabLayout = findViewById(R.id.tabLayout2);
      detailPageAdapter = new DetailPageAdapter(this,item);
      viewPager2.setAdapter(detailPageAdapter);
      TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
              (tab, position) -> {
                  tab.setText(tabs.get(position));
              });
      tabLayoutMediator.attach();


  }
}