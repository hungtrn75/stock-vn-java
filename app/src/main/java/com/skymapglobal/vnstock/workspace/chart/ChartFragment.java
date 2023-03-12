package com.skymapglobal.vnstock.workspace.chart;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.module.NestedScrollDelegate;
import com.skymapglobal.vnstock.workspace.home.HomeViewModel;
import com.tradingview.lightweightcharts.api.interfaces.ChartApi;
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.ChartOptions;
import com.tradingview.lightweightcharts.api.options.models.CrosshairOptions;
import com.tradingview.lightweightcharts.api.options.models.HistogramSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.LayoutOptions;
import com.tradingview.lightweightcharts.api.options.models.PriceScaleMargins;
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode;
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
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ChartFragment extends Fragment {
    private String code;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final CandlestickSeriesOptions candlestickSeriesOptions = new CandlestickSeriesOptions();
    private final HistogramSeriesOptions histogramSeriesOptions = new HistogramSeriesOptions();
    private List<String> resolutions = new ArrayList<>();
    private ChartsView chartsView;
    private LinearLayout tooltip;
    private TextView tTime;
    private TextView tOpen;
    private TextView tHigh;
    private TextView tLow;
    private TextView tClose;
    private TextView tChange;
    private TextView tVolume;
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

        setupViews();
        applyChartOptions();
        createResolutions();
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
                    String time = String.format("%d-%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    BarPrice price = barPrices.get(0).getPrices();

                    tTime.setText(time);
                    tOpen.setText(price.getOpen() != null ? price.getOpen().toString() : "0");
                    tHigh.setText(price.getHigh() != null ? price.getHigh().toString() : "0");
                    tLow.setText(price.getLow() != null ? price.getLow().toString() : "0");
                    tClose.setText(price.getClose() != null ? price.getClose().toString() : "0");
                    if (price.getOpen() != null && price.getClose() != null) {
                        float chg = price.getClose() - price.getOpen();
                        Float ratio = 100 * (price.getClose() - price.getOpen()) / price.getOpen();
                        tChange.setText(String.format("%s%.2f(%s%.2f%s)", chg > 0 ? "+" : "", chg, chg > 0 ? "+" : "", ratio, "%"));
                    } else {
                        tChange.setVisibility(View.GONE);
                    }
                    if (viewModel.getVolumes().containsKey(time) && viewModel.getVolumes().get(time) != null) {
                        tVolume.setText(viewModel.getVolumes().get(time).toString());
                    } else {
                        tVolume.setVisibility(View.GONE);
                    }
                } else {
                    tooltip.setVisibility(View.GONE);
                }
            } else {
                tooltip.setVisibility(View.GONE);
            }

            return null;
        }));

        Disposable l1 = viewModel.getCandlestickDatas().subscribeOn(AndroidSchedulers.mainThread()).subscribe(data -> {
            chartsView.getApi().addCandlestickSeries(candlestickSeriesOptions, (seriesApi -> {
                seriesApi.setData(data.getCandlestickDataList());
                return null;
            }));
            chartsView.getApi().addHistogramSeries(histogramSeriesOptions, (seriesApi -> {
                seriesApi.setData(data.getHistogramDataList());
                return null;
            }));
        });
        mCompositeDisposable.add(l1);
    }


    private void subscribeOnChartReady(ChartsView view) {
        view.subscribeOnChartStateChange((state -> {
            if (state instanceof ChartsView.State.Ready) {
                Log.e("OnChartReady", state.toString());
                viewModel.getDatas(code);
            } else if (state instanceof ChartsView.State.Error) {
                Log.e("OnChartError", ((ChartsView.State.Error) state).getException().getLocalizedMessage());
            }
            return null;
        }));

    }

    private void applyChartOptions() {
        CrosshairOptions crosshairOptions = new CrosshairOptions();
        crosshairOptions.setMode(CrosshairMode.NORMAL);
        ChartOptions chartOptions = new ChartOptions();
        chartOptions.setCrosshair(crosshairOptions);
        chartsView.getApi().applyOptions(chartOptions);

        histogramSeriesOptions.setPriceFormat(PriceFormat.Companion.priceFormatBuiltIn(PriceFormat.Type.VOLUME, 1, 1f));
        histogramSeriesOptions.setPriceScaleId(new PriceScaleId(""));
        histogramSeriesOptions.setScaleMargins(new PriceScaleMargins(0.8f, 0f));
    }

    private void createResolutions() {
        resolutions.add("1phút");
        resolutions.add("5phút");
        resolutions.add("15phút");
        resolutions.add("1ngày");
    }

    @Override
    public void onDestroyView() {
        mCompositeDisposable.clear();
        super.onDestroyView();
    }

}