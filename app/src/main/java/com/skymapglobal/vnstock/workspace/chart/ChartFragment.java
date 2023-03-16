package com.skymapglobal.vnstock.workspace.chart;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import android.widget.ProgressBar;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.Resolution;
import com.skymapglobal.vnstock.module.NestedScrollDelegate;
import com.skymapglobal.vnstock.utils.Transform;
import com.skymapglobal.vnstock.workspace.home.HomeViewModel;
import com.tradingview.lightweightcharts.api.chart.models.color.Colorable.ColorAdapter;
import com.tradingview.lightweightcharts.api.chart.models.color.ColorableKt;
import com.tradingview.lightweightcharts.api.chart.models.color.IntColorKt;
import com.tradingview.lightweightcharts.api.interfaces.ChartApi;
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi;
import com.tradingview.lightweightcharts.api.options.models.AreaSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.ChartOptions;
import com.tradingview.lightweightcharts.api.options.models.CrosshairOptions;
import com.tradingview.lightweightcharts.api.options.models.HistogramSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.LayoutOptions;
import com.tradingview.lightweightcharts.api.options.models.LineSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.PriceScaleMargins;
import com.tradingview.lightweightcharts.api.options.models.PriceScaleOptions;
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode;
import com.tradingview.lightweightcharts.api.series.enums.LineWidth;
import com.tradingview.lightweightcharts.api.series.models.BarPrice;
import com.tradingview.lightweightcharts.api.series.models.BarPrices;
import com.tradingview.lightweightcharts.api.series.models.MouseEventParams;
import com.tradingview.lightweightcharts.api.series.models.PriceFormat;
import com.tradingview.lightweightcharts.api.series.models.PriceScaleId;
import com.tradingview.lightweightcharts.api.series.models.Time;
import com.tradingview.lightweightcharts.view.ChartsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChartFragment extends Fragment implements ResolutionClickListener {

  private String code;
  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private final CandlestickSeriesOptions candlestickSeriesOptions = new CandlestickSeriesOptions();
  private final HistogramSeriesOptions histogramSeriesOptions = new HistogramSeriesOptions();
  private final LineSeriesOptions ema20Options = new LineSeriesOptions();
  private final LineSeriesOptions ema25Options = new LineSeriesOptions();
  private List<Resolution> resolutions = new ArrayList<>();
  private Set<SeriesApi> seriesApis = new HashSet<>();
  private ChartsView chartsView;
  private LinearLayout tooltip;
  private TextView tTime;
  private TextView tOpen;
  private TextView tHigh;
  private TextView tLow;
  private TextView tClose;
  private TextView tChange;
  private TextView tVolume;
  private ProgressBar progressBar;
  private LinearLayout emaContainer;
  private TextView ema20;
  private TextView ema25;

  private RecyclerView resolutionList;
  private ResolutionAdapter resolutionAdapter;
  private ChartViewModel viewModel;

  public ChartFragment() {
    // Required empty public constructor
  }

  public static ChartFragment newInstance(String code) {
    ChartFragment fragment = new ChartFragment();
    Bundle args = new Bundle();
    args.putString("code", code);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      code = getArguments().getString("code");
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ViewModelProvider viewModelProvider = new ViewModelProvider(this);
    viewModel = viewModelProvider.get(ChartViewModel.class);

    createResolutions();
    setupViews();
    applyChartOptions();
    subscribeOnChartReady(chartsView);
    setupListeners();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_chart, container, false);
  }

  private void setupViews() {
    chartsView = requireView().findViewById(R.id.charts_view);
    chartsView.addTouchDelegate(new NestedScrollDelegate(getContext()));

    tooltip = requireView().findViewById(R.id.tooltip);
    tTime = requireView().findViewById(R.id.tTime);
    tOpen = requireView().findViewById(R.id.tOpen);
    tHigh = requireView().findViewById(R.id.tHigh);
    tLow = requireView().findViewById(R.id.tLow);
    tClose = requireView().findViewById(R.id.tClose);
    tChange = requireView().findViewById(R.id.tChange);
    tVolume = requireView().findViewById(R.id.tVolume);
    progressBar = requireView().findViewById(R.id.progressBar);
    emaContainer = requireView().findViewById(R.id.emaInfo);
    ema20 = requireView().findViewById(R.id.ema20);
    ema25 = requireView().findViewById(R.id.ema25);

    resolutionList = requireView().findViewById(R.id.resolutionList);
    resolutionAdapter = new ResolutionAdapter(getContext(), resolutions, this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(RecyclerView.HORIZONTAL);
    resolutionList.setLayoutManager(layoutManager);
    resolutionList.setAdapter(resolutionAdapter);
  }

  @SuppressLint({"DefaultLocale", "SetTextI18n"})
  private void setupListeners() {
    chartsView.getApi().subscribeCrosshairMove((mouseEventParams -> {
      if (mouseEventParams.getSeriesPrices() != null) {
        tooltip.setVisibility(View.VISIBLE);
        List<BarPrices> barPrices = Arrays.asList(mouseEventParams.getSeriesPrices());
        if (barPrices.size() > 0) {
          Time.Utc businessDay = (Time.Utc) mouseEventParams.getTime();
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(businessDay.getDate());
          String time = String.format("%d-%d-%d %d-%d-%d", calendar.get(Calendar.YEAR),
              calendar.get(Calendar.MONTH) + 1,
              calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
              calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
          BarPrice price = barPrices.get(0).getPrices();

          tTime.setText(time);
          tOpen.setText(price.getOpen() != null ? price.getOpen().toString() : "0");
          tHigh.setText(price.getHigh() != null ? price.getHigh().toString() : "0");
          tLow.setText(price.getLow() != null ? price.getLow().toString() : "0");
          tClose.setText(price.getClose() != null ? price.getClose().toString() : "0");
          if (price.getOpen() != null && price.getClose() != null) {
            tChange.setVisibility(View.VISIBLE);
            float chg = price.getClose() - price.getOpen();
            Float ratio = 100 * (price.getClose() - price.getOpen()) / price.getOpen();
            tChange.setText(
                String.format("%s%.2f(%s%.2f%s)", chg > 0 ? "+" : "", chg, chg > 0 ? "+" : "",
                    ratio, "%"));
          } else {
            tChange.setVisibility(View.GONE);
          }

          if (barPrices.size() > 1) {
            tVolume.setVisibility(View.VISIBLE);
            float vol = barPrices.get(1).getPrices().getValue();
            tVolume.setText(Transform.getHumanReadablePriceFromNumber(vol));
          } else {
            tVolume.setVisibility(View.GONE);
          }

          if (barPrices.size() > 3) {
            float ema20Value = barPrices.get(2).getPrices().getValue();
            float ema25Value = barPrices.get(3).getPrices().getValue();
            ema20.setText(String.format("%.2f", ema20Value));
            ema25.setText(String.format("%.2f", ema25Value));
          }
        } else {
          tooltip.setVisibility(View.GONE);
        }
      } else {
        tooltip.setVisibility(View.GONE);
      }

      return null;
    }));

    Disposable l1 = viewModel.getCandlestickDatas(code).subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          if (data.getEma20().size() > 0 && data.getEma25().size() > 0) {
            emaContainer.setVisibility(View.VISIBLE);
            ema20.setText(
                String.format("%.2f", data.getEma20().get(data.getEma20().size() - 1).getValue()));
            ema25.setText(
                String.format("%.2f", data.getEma25().get(data.getEma25().size() - 1).getValue()));
          } else {
            emaContainer.setVisibility(View.INVISIBLE);
          }
          seriesApis.forEach(s -> chartsView.getApi().removeSeries(s, () -> {
            return null;
          }));
          seriesApis.clear();
          chartsView.getApi().addCandlestickSeries(candlestickSeriesOptions, (seriesApi -> {
            seriesApi.setData(data.getCandlestickDataList());
            seriesApis.add(seriesApi);
            return null;
          }));
          chartsView.getApi().addHistogramSeries(histogramSeriesOptions, (seriesApi -> {
            seriesApis.add(seriesApi);
            seriesApi.setData(data.getHistogramDataList());
            return null;
          }));
          chartsView.getApi().addLineSeries(ema20Options, (seriesApi -> {
            seriesApis.add(seriesApi);
            seriesApi.setData(data.getEma20());
            return null;
          }));
          chartsView.getApi().addLineSeries(ema25Options, (seriesApi -> {
            seriesApis.add(seriesApi);
            seriesApi.setData(data.getEma25());
            return null;
          }));
        }, Throwable::printStackTrace);

    Disposable l2 = viewModel.getSelectedResolution().subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(resolution -> {
          resolutionAdapter.setSelected(resolution);
        });

    Disposable l3 = viewModel.getLoadingObs().subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(loading -> {
          progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    mCompositeDisposable.add(l1);
    mCompositeDisposable.add(l2);
    mCompositeDisposable.add(l3);
  }


  private void subscribeOnChartReady(ChartsView view) {
    view.subscribeOnChartStateChange((state -> {
      if (state instanceof ChartsView.State.Ready) {
        Log.e("OnChartReady", state.toString());
        viewModel.setResolution(resolutions.get(5));
      } else if (state instanceof ChartsView.State.Error) {
        Log.e("OnChartError",
            ((ChartsView.State.Error) state).getException().getLocalizedMessage());
      }
      return null;
    }));

  }

  private void applyChartOptions() {
    CrosshairOptions crosshairOptions = new CrosshairOptions();
    crosshairOptions.setMode(CrosshairMode.NORMAL);
    ChartOptions chartOptions = new ChartOptions();

    chartOptions.setCrosshair(crosshairOptions);
    PriceScaleOptions priceScaleOptions = new PriceScaleOptions();
    PriceScaleMargins priceScaleMargins = new PriceScaleMargins();
    priceScaleMargins.setBottom(0.3f);
    priceScaleMargins.setTop(0.1f);
    priceScaleOptions.setScaleMargins(priceScaleMargins);
    chartOptions.setRightPriceScale(priceScaleOptions);

    PriceScaleOptions leftPriceScaleOptions = new PriceScaleOptions();
    PriceScaleMargins leftPriceScaleMargins = new PriceScaleMargins();
    leftPriceScaleMargins.setBottom(0f);
    leftPriceScaleMargins.setTop(0.8f);
    leftPriceScaleOptions.setVisible(true);
    leftPriceScaleOptions.setScaleMargins(leftPriceScaleMargins);

    chartOptions.setLeftPriceScale(leftPriceScaleOptions);
    chartsView.getApi().applyOptions(chartOptions);

    histogramSeriesOptions.setPriceFormat(
        PriceFormat.Companion.priceFormatBuiltIn(PriceFormat.Type.VOLUME, 1, 1f));
    histogramSeriesOptions.setPriceScaleId(new PriceScaleId("left"));

    ema20Options.setColor(IntColorKt.toIntColor(Color.argb(204, 178, 33, 118)));
    ema20Options.setLineWidth(LineWidth.ONE);

    ema25Options.setColor(IntColorKt.toIntColor(Color.argb(204, 39, 204, 83)));
    ema25Options.setLineWidth(LineWidth.ONE);
  }

  private void createResolutions() {
    resolutions.add(new Resolution(1, "1m", "1"));
    resolutions.add(new Resolution(2, "5m", "5"));
    resolutions.add(new Resolution(3, "15m", "15"));
    resolutions.add(new Resolution(4, "30m", "30"));
    resolutions.add(new Resolution(5, "1H", "60"));
    resolutions.add(new Resolution(6, "1D", "1D"));
    resolutions.add(new Resolution(7, "1W", "D"));
    resolutions.add(new Resolution(8, "1M", "D"));
  }

  @Override
  public void onDestroyView() {
    mCompositeDisposable.clear();
    super.onDestroyView();
  }

  @Override
  public void onResolutionClickListener(Resolution resolution, Integer position) {
    viewModel.setResolution(resolution);
  }
}