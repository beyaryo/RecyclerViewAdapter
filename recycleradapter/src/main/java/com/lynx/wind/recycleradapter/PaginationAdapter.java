package com.lynx.wind.recycleradapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

abstract public class PaginationAdapter<Holder extends RecyclerView.ViewHolder, DataClass>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private Class<Holder> holder;
    private int itemView;
    private int loadingView;
    private int headerView = -1;
    private List<Object> data;

    public enum LoadState {
        LOAD_NEXT, LOADING, ERROR, END
    }

    private static int TAG_LOAD = 0;
    private static int TAG_DATA = 1;
    private static int TAG_HEADER = 2;
    private LoadState state = LoadState.LOAD_NEXT;

    public PaginationAdapter(Class<Holder> holder, List<DataClass> data, int itemView, int loadingView) {
        this.holder = holder;
        this.data = ((List<Object>) data);
        this.itemView = itemView;
        this.loadingView = loadingView;

        this.data.add(state);
    }

    public PaginationAdapter(Class<Holder> holder, List<DataClass> data, int itemView, int loadingView, int headerView) {
        this.holder = holder;
        this.data = ((List<Object>) data);
        this.itemView = itemView;
        this.loadingView = loadingView;
        this.headerView = headerView;

        this.data.add(state);
    }

    public void setRecyclerView(Activity activity, RecyclerView recyclerView, final int threshold) {
        this.activity = activity;
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int count = layoutManager.getItemCount();
                int lastItem = layoutManager.findLastVisibleItemPosition();

                if (state == LoadState.LOAD_NEXT && count <= lastItem + threshold) {
                    state = LoadState.LOADING;
                    if (headerView == -1) loadMore(count - 1);
                    else loadMore(count - 2);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView == -1) {
            if (position >= data.size() - 1) return TAG_LOAD;
            else return TAG_DATA;
        } else {
            if (position == 0) return TAG_HEADER;
            if (position >= data.size()) return TAG_LOAD;
            else return TAG_DATA;
        }
    }

    @Override
    public int getItemCount() {
        int headerSize = headerView == -1 ? 0 : 1;

        if (state != LoadState.END) return headerSize + data.size();
        else return headerSize + data.size() - 1;
    }

    @Override
    @Nullable
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TAG_HEADER) {
            return new DefaultHolder(LayoutInflater.from(parent.getContext()).inflate(headerView, parent, false));
        } else if (viewType == TAG_LOAD) {
            return new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(loadingView, parent, false));
        }

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
        if (holder != null) {
            try {
                if (headerView == -1) {
                    if (position < data.size() - 1)
                        onBind(((Holder) holder), (DataClass) data.get(position), position);
                    else onLoadingBind(((LoadMoreHolder) holder).itemView, state);
                } else {
                    if (position == 0) onHeaderBind(holder.itemView);
                    else if (position < data.size())
                        onBind(((Holder) holder), (DataClass) data.get(position - 1), position - 1);
                    else onLoadingBind(((LoadMoreHolder) holder).itemView, state);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        setState(LoadState.LOAD_NEXT);
    }

    public void loadError() {
        setState(LoadState.ERROR);
    }

    public void loadEnd() {
        setState(LoadState.END);
    }

    public void refresh(List<DataClass> newData) {
        refresh(newData, false);
    }

    public void refresh(List<DataClass> newData, Boolean isClearing) {
        if(isClearing) this.data.clear();
        else this.data.remove(data.size() - 1);
        this.state = LoadState.LOAD_NEXT;
        this.data.addAll(newData);
        this.data.add(this.state);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private void setState(final LoadState state) {
        this.data.remove(data.size() - 1);
        this.state = state;
        this.data.add(this.state);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public LoadState getState() {
        return state;
    }

    public void onHeaderBind(View itemView) {
    }

    public abstract void onBind(Holder holder, DataClass data, int Index);

    public abstract void onLoadingBind(View itemView, LoadState state);

    public abstract void loadMore(int offset);

    private class DefaultHolder extends RecyclerView.ViewHolder {
        private DefaultHolder(View itemView) {
            super(itemView);
        }
    }

    private class LoadMoreHolder extends RecyclerView.ViewHolder {
        private LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }
}
