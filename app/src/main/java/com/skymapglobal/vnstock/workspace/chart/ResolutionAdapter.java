package com.skymapglobal.vnstock.workspace.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skymapglobal.vnstock.R;
import com.skymapglobal.vnstock.models.Resolution;

import java.util.List;

public class ResolutionAdapter extends RecyclerView.Adapter<ResolutionAdapter.ViewHolder> {
    private Context mContext;
    private List<Resolution> resolutions;
    private ResolutionClickListener listener;
    private Resolution selected;

    public ResolutionAdapter(Context mContext, List<Resolution> resolutions, ResolutionClickListener listener) {
        this.mContext = mContext;
        this.resolutions = resolutions;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelected(Resolution selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResolutionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View stockView = inflater.inflate(R.layout.item_resolution, parent, false);
        ResolutionAdapter.ViewHolder viewHolder = new ResolutionAdapter.ViewHolder(stockView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResolutionAdapter.ViewHolder holder, int position) {
        Resolution item = resolutions.get(position);
        holder.name.setText(item.getName());
        if (selected != null && item.getId().equals(selected.getId())) {
            holder.parent.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_border_resolution_selected));
            holder.name.setTextColor(Color.rgb(255, 255, 255));
        } else {
            holder.parent.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_border_resolution));
            holder.name.setTextColor(Color.rgb(0, 0, 0));
        }
        holder.itemView.setOnClickListener(v -> {
            listener.onResolutionClickListener(item, position);
        });
    }

    @Override
    public int getItemCount() {
        return resolutions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private LinearLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.itemResolutionContainer);
            name = itemView.findViewById(R.id.rName);
        }
    }
}
