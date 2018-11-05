package com.lynx.wind.recycleradapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

abstract public class PaginationAdapter<Holder extends RecyclerView.ViewHolder, DataClass>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private RecyclerView recyclerView;
    private Class<Holder> holder;
    private int itemView;
    private int loadingView;
    private int headerView = -1;
    private List<DataClass> data;
    private List<Object> paginationData = new ArrayList<>();

    public enum LoadState {
        LOAD_NEXT, PAUSE, LOADING, ERROR, END
    }

    private static int TAG_LOAD = 0;
    private static int TAG_DATA = 1;
    private static int TAG_HEADER = 2;
    private LoadState state = LoadState.LOAD_NEXT;

    public PaginationAdapter(Class<Holder> holder, List<DataClass> data, int itemView, int loadingView) {
        this.holder = holder;
        this.itemView = itemView;
        this.loadingView = loadingView;
        this.data = data;
        this.paginationData.addAll(this.data);
        this.paginationData.add(state);
    }

    public PaginationAdapter(Class<Holder> holder, List<DataClass> data, int itemView, int loadingView, int headerView) {
        this.holder = holder;
        this.itemView = itemView;
        this.loadingView = loadingView;
        this.headerView = headerView;
        this.data = data;
        this.paginationData.addAll(this.data);
        this.paginationData.add(state);
    }

    public void setRecyclerView(Activity activity, RecyclerView recyclerView, final int threshold) {
        this.activity = activity;
        this.recyclerView = recyclerView;
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
            if (position >= paginationData.size() - 1) return TAG_LOAD;
            else return TAG_DATA;
        } else {
            if (position == 0) return TAG_HEADER;
            if (position >= paginationData.size()) return TAG_LOAD;
            else return TAG_DATA;
        }
    }

    @Override
    public int getItemCount() {
        int headerSize = headerView == -1 ? 0 : 1;

        if (state != LoadState.END && state != LoadState.PAUSE) return headerSize + paginationData.size();
        else return headerSize + paginationData.size() - 1;
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
                    if (position < paginationData.size() - 1)
                        onBind(((Holder) holder), (DataClass) paginationData.get(position), position);
                    else onLoadingBind(((LoadMoreHolder) holder).itemView, state);
                } else {
                    if (position == 0) onHeaderBind(holder.itemView);
                    else if (position < paginationData.size())
                        onBind(((Holder) holder), (DataClass) paginationData.get(position - 1), position - 1);
                    else onLoadingBind(((LoadMoreHolder) holder).itemView, state);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadNext() {
        setState(LoadState.LOAD_NEXT);
    }

    public void pause(){
        setState(LoadState.PAUSE);
    }

    public void error() {
        setState(LoadState.ERROR);
    }

    public void end() {
        setState(LoadState.END);
    }

    public void refresh() {
        refresh(false);
    }

    public void refresh(boolean clearAll){
        this.state = LoadState.LOAD_NEXT;

        if(clearAll) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(0);
                }
            });
            paginationData.clear();
        }
        else paginationData.remove(paginationData.size() - 1);

        paginationData.addAll(data.subList(paginationData.size(), data.size()));
        paginationData.add(this.state);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private void setState(final LoadState state) {
        this.state = state;

        paginationData.remove(paginationData.size() - 1);
        paginationData.add(this.state);

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

    public void onHeaderBind(View itemView) {}

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
