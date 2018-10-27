package com.lynx.wind.recycleradapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

abstract public class FilterAdapter<Holder extends RecyclerView.ViewHolder, DataClass>
        extends RecyclerView.Adapter<Holder> implements Filterable {

    private Class<Holder> holder;
    private int itemView;
    private List<DataClass> data;
    private List<DataClass> filterData;

    public FilterAdapter(Class<Holder> holder, int itemView, List<DataClass> data) {
        this.holder = holder;
        this.itemView = itemView;
        this.data = data;
        this.filterData = this.data;
    }

    @Nullable
    @Override
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
        if (holder != null) onBind(holder, filterData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return filterData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<DataClass> resultList = new ArrayList<>();
                String charSearch = constraint.toString().toLowerCase();

                if (charSearch.equals("")) resultList.addAll(data);
                else {
                    for (DataClass row : data) {
                        if (onFiltering(charSearch, row)) resultList.add(row);
                    }

                    filterData = resultList;
                }

                FilterResults result = new FilterResults();
                result.values = resultList;
                result.count = resultList.size();

                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null) {
                    filterData = ((ArrayList<DataClass>) results.values);
                    notifyDataSetChanged();
                    onFilterResult(results.count);
                }
            }
        };
    }

    public abstract void onBind(Holder holder, DataClass data, int Index);
    public abstract boolean onFiltering(String comparator, DataClass comparedBy);
    public abstract void onFilterResult(int count);
}