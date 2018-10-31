package com.lynx.wind.recycleradapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

abstract public class FilterAdapter<Holder extends RecyclerView.ViewHolder, DataClass>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Class<Holder> holder;
    private int itemView;
    private int headerView = -1;
    private List<DataClass> data;
    private List<DataClass> filterData;

    private static int TAG_HEADER = 0;
    private static int TAG_DATA = 1;

    public FilterAdapter(Class<Holder> holder, List<DataClass> data, int itemView) {
        this.holder = holder;
        this.itemView = itemView;
        this.data = data;
        this.filterData = this.data;
    }

    public FilterAdapter(Class<Holder> holder, List<DataClass> data, int itemView, int headerView) {
        this.holder = holder;
        this.itemView = itemView;
        this.headerView = headerView;
        this.data = data;
        this.filterData = this.data;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != -1 && position == 0) return TAG_HEADER;
        else return TAG_DATA;
    }

    @Override
    public int getItemCount() {
        if (headerView == -1) return filterData.size();
        else return filterData.size() + 1;
    }

    @Nullable
    @Override
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
                onBind((Holder) holder, filterData.get(position), position);
            else if (headerView != -1 && position != 0)
                onBind((Holder) holder, filterData.get(position - 1), position - 1);
            else
                onHeaderBind(holder.itemView);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void onHeaderBind(View itemView) {
    }

    public abstract void onBind(Holder holder, DataClass data, int Index);

    public abstract boolean onFiltering(String comparator, DataClass comparedBy);

    public abstract void onFilterResult(int count);

    private class DefaultHolder extends RecyclerView.ViewHolder {
        private DefaultHolder(View itemView) {
            super(itemView);
        }
    }
}