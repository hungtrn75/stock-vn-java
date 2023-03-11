package com.skymapglobal.vnstock.workspace.home;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skymapglobal.vnstock.R;

public class StocmarketAdapter extends RecyclerView.Adapter<StocmarketAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView index;
        private TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            index = itemView.findViewById(R.id.index);
            text = itemView.findViewById(R.id.change_percent);

        }
    }
}
