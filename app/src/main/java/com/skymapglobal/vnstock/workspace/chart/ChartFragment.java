package com.skymapglobal.vnstock.workspace.chart;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.Resolution;
import com.skymapglobal.vnstock.module.NestedScrollDelegate;
import com.skymapglobal.vnstock.module.NestedScrollListener;
import com.skymapglobal.vnstock.utils.Transform;
import com.tradingview.lightweightcharts.api.chart.models.color.IntColorKt;
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi;
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.ChartOptions;
import com.tradingview.lightweightcharts.api.options.models.CrosshairOptions;
import com.tradingview.lightweightcharts.api.options.models.HistogramSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.LineSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.PriceScaleMargins;
import com.tradingview.lightweightcharts.api.options.models.PriceScaleOptions;
import com.tradingview.lightweightcharts.api.series.common.SeriesData;
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode;
import com.tradingview.lightweightcharts.api.series.enums.LineWidth;
import com.tradingview.lightweightcharts.api.series.models.BarPrice;
import com.tradingview.lightweightcharts.api.series.models.BarPrices;
import com.tradingview.lightweightcharts.api.series.models.PriceFormat;
import com.tradingview.lightweightcharts.api.series.models.PriceScaleId;
import com.tradingview.lightweightcharts.api.series.models.Time;
import com.tradingview.lightweightcharts.view.ChartsView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChartFragment extends Fragment implements ResolutionClickListener,
    NestedScrollListener {

  private String code;
  private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
  private final CandlestickSeriesOptions candlestickSeriesOptions = new CandlestickSeriesOptions();
  private final HistogramSeriesOptions histogramSeriesOptions = new HistogramSeriesOptions();
  private final LineSeriesOptions ema20Options = new LineSeriesOptions();
  private final LineSeriesOptions ema25Options = new LineSeriesOptions();

  private final LineSeriesOptions rsi6Options = new LineSeriesOptions();
  private final LineSeriesOptions rsi14Options = new LineSeriesOptions();
  private List<Resolution> resolutions = new ArrayList<>();

  private SeriesApi candlestickSeriesApi;
  private SeriesApi histogramSeriesApi;
  private SeriesApi ema20SeriesApi;
  private SeriesApi ema25SeriesApi;
  private SeriesApi rsi6SeriesApi;
  private SeriesApi rsi14SeriesApi;
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
  private String memoLastEma20;
  private String memoLastEma25;

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
    chartsView.addTouchDelegate(new NestedScrollDelegate(getContext(), this));

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
          String time;
          if (viewModel.getMemoResolution() == null || viewModel.getMemoResolution().getId() > 5) {
            time = String.format("%d-%d-%d", calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
          } else {
            time = String.format("%d-%d-%d %d:%d", calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
          }
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
            ema20.setText(String.format("EMA(20): %.2f", ema20Value));
            ema25.setText(String.format("EMA(25): %.2f", ema25Value));
          }
        } else {
          tooltip.setVisibility(View.GONE);
          if (memoLastEma25 != null && memoLastEma20 != null) {
            emaContainer.setVisibility(View.VISIBLE);
            ema20.setText(memoLastEma20);
            ema25.setText(memoLastEma25);
          } else {
            emaContainer.setVisibility(View.INVISIBLE);
          }
        }
      } else {
        tooltip.setVisibility(View.GONE);
      }
      return null;
    }));

    chartsView.getApi().getTimeScale().subscribeVisibleTimeRangeChange((logicalRange) -> {
      if (logicalRange != null) {
        Object ema20Value = viewModel.getEma20WithLogicalTime(logicalRange.getTo());
        Object ema25Value = viewModel.getEma25WithLogicalTime(logicalRange.getTo());
        if (ema20Value != null || ema25Value != null) {
          emaContainer.setVisibility(View.VISIBLE);
          if (ema20Value != null) {
            ema20.setVisibility(View.VISIBLE);
            memoLastEma20 = String.format("EMA(20): %.2f", ema20Value);
            ema20.setText(memoLastEma20);
          } else {
            ema20.setVisibility(View.GONE);
          }
          if (ema25Value != null) {
            ema25.setVisibility(View.VISIBLE);
            memoLastEma25 = String.format("EMA(25): %.2f", ema25Value);
            ema25.setText(memoLastEma25);
          } else {
            ema25.setVisibility(View.GONE);
          }
        } else {
          emaContainer.setVisibility(View.INVISIBLE);
        }
      }
      return null;
    });

    Disposable l1 = viewModel.getCandlestickDatas(code).subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          if (candlestickSeriesApi == null) {
            chartsView.getApi().addCandlestickSeries(candlestickSeriesOptions, (seriesApi -> {
              candlestickSeriesApi = seriesApi;
              seriesApi.setData(data.getCandlestickDataList());
              return null;
            }));
          } else {
            candlestickSeriesApi.setData(data.getCandlestickDataList());
          }
          if (histogramSeriesApi == null) {
            chartsView.getApi().addHistogramSeries(histogramSeriesOptions, (seriesApi -> {
              histogramSeriesApi = seriesApi;
              seriesApi.setData(data.getHistogramDataList());
              return null;
            }));
          } else {
            histogramSeriesApi.setData(data.getHistogramDataList());
          }

          if (ema20SeriesApi == null) {
            chartsView.getApi().addLineSeries(ema20Options, (seriesApi -> {
              ema20SeriesApi = seriesApi;
              seriesApi.setData(data.getEma20());
              return null;
            }));
          } else {
            ema20SeriesApi.setData(data.getEma20());
          }
          if (ema25SeriesApi == null) {
            chartsView.getApi().addLineSeries(ema25Options, (seriesApi -> {
              ema25SeriesApi = seriesApi;
              seriesApi.setData(data.getEma25());
              return null;
            }));
          } else {
            ema25SeriesApi.setData(data.getEma25());
          }
          if (rsi6SeriesApi == null) {
            chartsView.getApi().addLineSeries(rsi6Options, (seriesApi -> {
              rsi6SeriesApi = seriesApi;
              seriesApi.setData(data.getRsi6());
              return null;
            }));
          } else {
            rsi6SeriesApi.setData(data.getRsi6());
          }
          if (rsi14SeriesApi == null) {
            chartsView.getApi().addLineSeries(rsi14Options, (seriesApi -> {
              rsi14SeriesApi = seriesApi;
              seriesApi.setData(data.getRsi14());
              return null;
            }));
          } else {
            rsi14SeriesApi.setData(data.getRsi14());
          }
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
    priceScaleMargins.setBottom(0.4f);
    priceScaleMargins.setTop(0.1f);
    priceScaleOptions.setScaleMargins(priceScaleMargins);
    chartOptions.setRightPriceScale(priceScaleOptions);

    chartsView.getApi().applyOptions(chartOptions);

    histogramSeriesOptions.setPriceFormat(
        PriceFormat.Companion.priceFormatBuiltIn(PriceFormat.Type.VOLUME, 1, 1f));
    histogramSeriesOptions.setPriceScaleId(new PriceScaleId("histogram"));
    PriceScaleMargins histogramMargins = new PriceScaleMargins();
    histogramMargins.setBottom(0.2f);
    histogramMargins.setTop(0.6f);
    histogramSeriesOptions.setScaleMargins(histogramMargins);

    ema20Options.setColor(IntColorKt.toIntColor(Color.argb(204, 39, 204, 83)));
    ema20Options.setLineWidth(LineWidth.ONE);
    ema20Options.setPriceLineVisible(false);
    ema20Options.setLastValueVisible(false);
    ema20Options.setCrosshairMarkerVisible(false);

    ema25Options.setColor(IntColorKt.toIntColor(Color.argb(204, 178, 33, 118)));
    ema25Options.setLineWidth(LineWidth.ONE);
    ema25Options.setPriceLineVisible(false);
    ema25Options.setLastValueVisible(false);
    ema25Options.setCrosshairMarkerVisible(false);

    rsi6Options.setPriceScaleId(new PriceScaleId("rsi"));
    rsi6Options.setColor(IntColorKt.toIntColor(Color.argb(204, 255, 233, 90)));
    rsi6Options.setLineWidth(LineWidth.ONE);
    rsi6Options.setPriceLineVisible(false);
    rsi6Options.setLastValueVisible(false);
    rsi6Options.setCrosshairMarkerVisible(false);
    PriceScaleMargins rsiMargins = new PriceScaleMargins();
    rsiMargins.setBottom(0f);
    rsiMargins.setTop(0.8f);
    rsi6Options.setScaleMargins(rsiMargins);

    rsi14Options.setPriceScaleId(new PriceScaleId("rsi"));
    rsi14Options.setColor(IntColorKt.toIntColor(Color.argb(204, 178, 33, 118)));
    rsi14Options.setLineWidth(LineWidth.ONE);
    rsi14Options.setPriceLineVisible(false);
    rsi14Options.setLastValueVisible(false);
    rsi14Options.setCrosshairMarkerVisible(false);
    rsi14Options.setScaleMargins(rsiMargins);
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

  @Override
  public void onHorizontalScroll(float x) {
    Log.e("onHorizontalScroll", "scroll:" + x);
  }
}