package com.skymapglobal.vnstock;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.skymapglobal.vnstock.workspace.home.HomeViewModel;
import com.skymapglobal.vnstock.workspace.home.StockAdapter;
import com.skymapglobal.vnstock.workspace.home.StockPagerAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

  private HomeViewModel viewModel;
  private TabLayout tabLayout;
  private ViewPager2 viewPager2;
  private SwipeRefreshLayout refreshLayout;
  private StockPagerAdapter stockPagerAdapter;
  private TabLayoutMediator tabLayoutMediator;
  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    viewPager2 = findViewById(R.id.pager);
    viewPager2.setOffscreenPageLimit(1);

    tabLayout = findViewById(R.id.tabLayout);
    refreshLayout = findViewById(R.id.refreshLayout);
    refreshLayout.setOnRefreshListener(() -> viewModel.getStocks());

    viewModel = new HomeViewModel(getApplication());
    viewModel.init();

    stockPagerAdapter = new StockPagerAdapter(this);
    viewPager2.setAdapter(stockPagerAdapter);

    Disposable disposable = viewModel.getTabs().observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(AndroidSchedulers.mainThread()).subscribe((tabs -> {
          if (tabLayoutMediator == null) {
            tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                  tab.setText(tabs.get(position).getTitle());
                });
            tabLayoutMediator.attach();
          }
          stockPagerAdapter.setTabList(tabs);
          refreshLayout.setRefreshing(false);
        }));
    mCompositeDisposable.add(disposable);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    Drawable drawable = searchItem.getIcon();
    if (drawable != null) {
      drawable.mutate();
      drawable.setColorFilter(ContextCompat.getColor(this, R.color.white),
          PorterDuff.Mode.SRC_ATOP);
    }
    SearchView searchView = (SearchView) searchItem.getActionView();
    searchView.setQueryHint("Nhập mã chứng khoán cần tìm...");

    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected void onDestroy() {
    viewModel.clearObservables();
    mCompositeDisposable.clear();
    stockPagerAdapter.dispose();
    super.onDestroy();
  }
}