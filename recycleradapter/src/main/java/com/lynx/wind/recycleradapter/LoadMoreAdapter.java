package com.lynx.wind.recycleradapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract public class LoadMoreAdapter<Holder extends RecyclerView.ViewHolder, DataClass>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Class<Holder> holder;
    private int itemView;
    private int loadingView;
    private List<Object> data;

    public enum LoadState{
        LOAD_NEXT, LOADING, ERROR, END
    }

    private static int TAG_LOAD = 0;
    private static int TAG_DATA = 1;
    private LoadState state = LoadState.LOAD_NEXT;

    public LoadMoreAdapter(Class<Holder> holder, int itemView, int loadingView, List<DataClass> data) {
        this.holder = holder;
        this.itemView = itemView;
        this.data = ((List<Object>)data);
        this.loadingView = loadingView;

        this.data.add(state);
    }

    public void setRecyclerView(RecyclerView recyclerView, final int threshold){
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

                if (state == LoadState.LOAD_NEXT && count <= lastItem + threshold ){
                    state = LoadState.LOADING;
                    loadMore(count-1);
                }
            }
        });
    }

    @Override
    @Nullable
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if(viewType == TAG_LOAD){
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
        if(holder != null){
            try{
                if(position < data.size()-1) onBind(((Holder)holder), (DataClass) data.get(position), position);
                else onLoadingBind(((LoadMoreHolder)holder).itemView, state);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if(state != LoadState.END) return data.size();
        else return data.size()-1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position >= data.size()-1) return TAG_LOAD;
        else return TAG_DATA;
    }

    public void setState(Activity activity, final LoadState state) {
        this.data.remove(data.size()-1);
        this.state = state;
        this.data.add(this.state);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void refresh(Activity activity, List<DataClass> newData){
        this.data.remove(data.size()-1);
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

    public LoadState getState() {
        return state;
    }

    public abstract void onBind(Holder holder, DataClass data, int Index);
    public abstract void onLoadingBind(View itemView, LoadState state);
    public abstract void loadMore(int offset);

    private class LoadMoreHolder extends RecyclerView.ViewHolder{
        private LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }
}
