package com.skymapglobal.vnstock;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnCloseListener;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.skymapglobal.vnstock.models.SortBy;
import com.skymapglobal.vnstock.models.StockItem;
import com.skymapglobal.vnstock.workspace.detail.DetailActivity;
import com.skymapglobal.vnstock.workspace.home.HomeViewModel;
import com.skymapglobal.vnstock.workspace.home.StockAdapter;
import com.skymapglobal.vnstock.workspace.home.StockClickListener;
import com.skymapglobal.vnstock.workspace.home.StockPagerAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements StockClickListener {

  private HomeViewModel viewModel;
  private TabLayout tabLayout;
  private ViewPager2 viewPager2;
  private SwipeRefreshLayout refreshLayout;
  private StockPagerAdapter stockPagerAdapter;
  private TabLayoutMediator tabLayoutMediator;
  private RecyclerView searchRv;
  private StockAdapter mStockAdapter;
  SearchView searchView;
  MenuItem searchItem;
  MenuItem sortItem;
  ProgressBar progressBar;
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

    progressBar = findViewById(R.id.progressBar);

    searchRv = findViewById(R.id.searchRV);
    mStockAdapter = new StockAdapter(this, this);
    searchRv.setAdapter(mStockAdapter);
    searchRv.setLayoutManager(new LinearLayoutManager(this));

    viewModel = new HomeViewModel(getApplication());
    viewModel.init();

    stockPagerAdapter = new StockPagerAdapter(this);
    viewPager2.setAdapter(stockPagerAdapter);

    Disposable disposable = viewModel.getObsStocks().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((tabs -> {
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
    Disposable disposable2 = viewModel.getFilterStocks().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((stockItemList -> {
          mStockAdapter.updateDataSource(stockItemList);
        }));

    Disposable disposable3 = viewModel.getObsSortBy().observeOn(AndroidSchedulers.mainThread())
        .subscribe((sortBy -> {
          if (sortItem != null) {
            if (sortBy == SortBy.CHG_DE) {
              sortItem.setIcon(ContextCompat.getDrawable(this, R.drawable.list_de));
            } else if (sortBy == SortBy.CHG_IN) {
              sortItem.setIcon(ContextCompat.getDrawable(this, R.drawable.list_in));
            } else {
              sortItem.setIcon(ContextCompat.getDrawable(this, R.drawable.sort_az));
            }
            Drawable drawable = sortItem.getIcon();
            if (drawable != null) {
              drawable.mutate();
              drawable.setColorFilter(ContextCompat.getColor(this, R.color.white),
                  PorterDuff.Mode.SRC_ATOP);
            }
          }
        }));
    Disposable disposable4 = viewModel.getObsLoading().observeOn(AndroidSchedulers.mainThread())
        .subscribe((loading -> {
          progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }));
    mCompositeDisposable.add(disposable);
    mCompositeDisposable.add(disposable2);
    mCompositeDisposable.add(disposable3);
    mCompositeDisposable.add(disposable4);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    searchItem = menu.findItem(R.id.action_search);
    sortItem = menu.findItem(R.id.action_sort);
    Drawable drawable = searchItem.getIcon();
    if (drawable != null) {
      drawable.mutate();
      drawable.setColorFilter(ContextCompat.getColor(this, R.color.white),
          PorterDuff.Mode.SRC_ATOP);
    }
    Drawable drawable1 = sortItem.getIcon();

    if (drawable1 != null) {
      drawable1.mutate();
      drawable1.setColorFilter(ContextCompat.getColor(this, R.color.white),
          PorterDuff.Mode.SRC_ATOP);
    }
    searchView = (SearchView) searchItem.getActionView();
    searchView.setIconified(true);
    searchView.setMaxWidth(Integer.MAX_VALUE);
    searchView.setQueryHint("Nhập mã chứng khoán cần tìm...");
    searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
      @Override
      public boolean onMenuItemActionExpand(MenuItem item) {
        sortItem.setVisible(false);
        searchRv.setVisibility(View.VISIBLE);
        return true;
      }

      @Override
      public boolean onMenuItemActionCollapse(MenuItem item) {
        sortItem.setVisible(true);
        searchRv.setVisibility(View.INVISIBLE);
        invalidateOptionsMenu();
        return true;
      }
    });

    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        viewModel.setSearchTerm(newText);
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

  @Override
  public void onItemClickListener(StockItem stockItem, Integer position) {
    Log.e("onItemClickListener", "" + stockItem.getCode());
    searchItem.collapseActionView();
    Intent intent = new Intent(ListActivity.this, DetailActivity.class);
    intent.putExtra("stockItem", stockItem);
    startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_sort:
        viewModel.setSortType();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}