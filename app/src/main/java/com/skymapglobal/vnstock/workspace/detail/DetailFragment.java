package com.skymapglobal.vnstock.workspace.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.StockItem;


public class DetailFragment extends Fragment {

    private StockItem stockItem;

    public DetailFragment(StockItem stockItem) {
        this.stockItem = stockItem;
        Log.e("TAG", stockItem.getCode());
    }

    public static DetailFragment newInstance(StockItem stockItem) {
        DetailFragment fragment = new DetailFragment(stockItem);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        TextView tc = getView().findViewById(R.id.tc);
        TextView tran = getView().findViewById(R.id.tran);
        TextView san = getView().findViewById(R.id.san);
        TextView gia = getView().findViewById(R.id.gia);
        TextView kl = getView().findViewById(R.id.kl);
        TextView rate = getView().findViewById(R.id.rate);
        tc.setText(String.valueOf(stockItem.getTc()));
        tc.setTextColor(getContext().getColor(R.color.orange));
        tran.setText(String.valueOf(stockItem.getTran()));
        tran.setTextColor(getContext().getColor(R.color.purple_500));
        san.setText(String.valueOf(stockItem.getSan()));
        san.setTextColor(getContext().getColor(R.color.blue));
        gia.setText(String.valueOf(stockItem.getPrice()));
        gia.setTextColor(getContext().getColor(R.color.orange));
        kl.setText(String.format("%.0f",stockItem.getKl()));
        kl.setTextColor(getContext().getColor(R.color.purple_500));
        rate.setText("+"+String.valueOf(stockItem.getChg())+"(0.0%)");
        if(stockItem.getChg()< 0){
            rate.setTextColor(getContext().getColor(R.color.red));
        }else if(stockItem.getChg() == 0){
            rate.setTextColor(getContext().getColor(R.color.orange));
        }else{
            rate.setTextColor(getContext().getColor(R.color.green));
        }

        TextView a1 = getView().findViewById(R.id.a1);
        TextView a2 = getView().findViewById(R.id.a2);
        TextView a3 = getView().findViewById(R.id.a3);
        TextView b1 = getView().findViewById(R.id.b1);
        TextView b2 = getView().findViewById(R.id.b2);
        TextView b3 = getView().findViewById(R.id.b3);
        TextView c1 = getView().findViewById(R.id.c1);
        TextView c2 = getView().findViewById(R.id.c2);
        TextView c3 = getView().findViewById(R.id.c3);
        TextView d1 = getView().findViewById(R.id.d1);
        TextView d2 = getView().findViewById(R.id.d2);
        TextView d3 = getView().findViewById(R.id.d3);

        a1.setText(String.format("%.0f",stockItem.getA1()));
        a2.setText(String.format("%.0f",stockItem.getA2()));
        a3.setText(String.format("%.0f",stockItem.getA3()));
        b1.setText(String.valueOf(stockItem.getB1()));
        b2.setText(String.valueOf(stockItem.getB2()));
        b3.setText(String.valueOf(stockItem.getB3()));
        c1.setText(String.valueOf(stockItem.getC1()));
        c2.setText(String.valueOf(stockItem.getC2()));
        c3.setText(String.valueOf(stockItem.getC3()));
        d1.setText(String.format("%.0f",stockItem.getD1()));
        d2.setText(String.format("%.0f",stockItem.getD2()));
        d3.setText(String.format("%.0f",stockItem.getD3()));

        TextView cao = getView().findViewById(R.id.cao);
        TextView thap = getView().findViewById(R.id.thap);
       cao.setText(String.valueOf(stockItem.getCao()));
        thap.setText(String.valueOf(stockItem.getThap()));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }
}