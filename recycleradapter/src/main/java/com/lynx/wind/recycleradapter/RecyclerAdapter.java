package com.lynx.wind.recycleradapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

abstract public class RecyclerAdapter<Holder extends RecyclerView.ViewHolder, DataClass>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Class<Holder> holder;
    private int headerView = -1;
    private int itemView;
    private List<DataClass> data;

    private static int TAG_HEADER = 0;
    private static int TAG_DATA = 1;

    public RecyclerAdapter(Class<Holder> holder, List<DataClass> data, int itemView) {
        this.holder = holder;
        this.itemView = itemView;
        this.data = data;
    }

    public RecyclerAdapter(Class<Holder> holder, List<DataClass> data, int itemView, int headerView) {
        this.holder = holder;
        this.itemView = itemView;
        this.data = data;
        this.headerView = headerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != -1 && position == 0) return TAG_HEADER;
        else return TAG_DATA;
    }

    @Override
    public int getItemCount() {
        if (headerView == -1) return data.size();
        else return data.size() + 1;
    }

    @Override
    @Nullable
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TAG_HEADER)
            return new DefaultHolder(LayoutInflater.from(parent.getContext()).inflate(headerView, parent, false));

        try {
            return holder
                    .getConstructor(View.class)
                    .newInstance(LayoutInflater.from(parent.getContext()).inflate(itemView, parent, false));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@Nullable RecyclerView.ViewHolder holder, int position) {
        try {
            if (headerView == -1)
                onBind((Holder) holder, data.get(position), position);
            else if (headerView != -1 && position != 0)
                onBind((Holder) holder, data.get(position - 1), position - 1);
            else
                onHeaderBind(holder.itemView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onHeaderBind(View itemView) {
    }

    public abstract void onBind(Holder holder, DataClass data, int Index);

    private class DefaultHolder extends RecyclerView.ViewHolder {
        private DefaultHolder(View itemView) {
            super(itemView);
        }
    }
}