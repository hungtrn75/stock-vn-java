package com.skymapglobal.vnstock.workspace.chart;

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

import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.workspace.home.HomeViewModel;
import com.tradingview.lightweightcharts.api.interfaces.ChartApi;
import com.tradingview.lightweightcharts.api.options.models.CandlestickSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.ChartOptions;
import com.tradingview.lightweightcharts.api.options.models.CrosshairOptions;
import com.tradingview.lightweightcharts.api.options.models.HistogramSeriesOptions;
import com.tradingview.lightweightcharts.api.options.models.LayoutOptions;
import com.tradingview.lightweightcharts.api.options.models.PriceScaleMargins;
import com.tradingview.lightweightcharts.api.series.enums.CrosshairMode;
import com.tradingview.lightweightcharts.api.series.models.PriceFormat;
import com.tradingview.lightweightcharts.api.series.models.PriceScaleId;
import com.tradingview.lightweightcharts.view.ChartsView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ChartFragment extends Fragment {
    private String code;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final CandlestickSeriesOptions candlestickSeriesOptions = new CandlestickSeriesOptions();
    private final HistogramSeriesOptions histogramSeriesOptions = new HistogramSeriesOptions();
    private ChartsView chartsView;
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
    }

    private void setupListeners() {
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

    @Override
    public void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }
}