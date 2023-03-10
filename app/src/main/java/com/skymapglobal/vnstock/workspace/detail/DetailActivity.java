package com.skymapglobal.vnstock.workspace.detail;

import android.content.Intent;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.StockItem;

public class DetailActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    Intent intent=getIntent();
    StockItem stockItem = (StockItem) intent.getSerializableExtra("stockItem");
    Log.e("DetailActivity",stockItem.getCode());
  }
}