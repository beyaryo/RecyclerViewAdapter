package com.lynx.wind.recycleradapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract public class RecyclerAdapter<Holder extends RecyclerView.ViewHolder, DataClass>
        extends RecyclerView.Adapter<Holder> {

    private Class<Holder> holder;
    private int itemView;
    private List<DataClass> data;

    public RecyclerAdapter(Class<Holder> holder, int itemView, List<DataClass> data) {
        this.holder = holder;
        this.itemView = itemView;
        this.data = data;
    }

    @Override
    @Nullable
    public Holder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        try {
            return holder
                    .getConstructor(View.class)
                    .newInstance(LayoutInflater.from(parent.getContext()).inflate(itemView, parent, false));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@Nullable Holder holder, int position) {
        if (holder != null) onBind(holder, data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public abstract void onBind(Holder holder, DataClass data, int Index);
}