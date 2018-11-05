package com.lynx.wind.recyclersample.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lynx.wind.recycleradapter.PaginationAdapter;
import com.lynx.wind.recyclersample.R;
import com.lynx.wind.recyclersample.holder.SimpleHolder;
import com.lynx.wind.recyclersample.model.User;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PaginationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<User> users = new ArrayList<>();
    private SwipeRefreshLayout swiper;
    private boolean tempError = true;

    private PaginationAdapter<SimpleHolder, User> adapter = new PaginationAdapter<SimpleHolder, User>(
            SimpleHolder.class, users, R.layout.item_user, R.layout.item_loading, R.layout.item_header) {
        @Override
        public void onBind(SimpleHolder holder, User data, int Index) {
            holder.bind(data);
        }

        @Override
        public void onLoadingBind(View itemView, LoadState state) {
            if (state == LoadState.ERROR) {
                itemView.findViewById(R.id.loading).setVisibility(View.GONE);
                itemView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            } else {
                itemView.findViewById(R.id.loading).setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.error_layout).setVisibility(View.GONE);
            }

            itemView.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.loadNext();
                }
            });
        }

        @Override
        public void loadMore(int offset) {
            loadData(offset);
        }

        @Override
        public void onHeaderBind(View itemView) { }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRefresh() {
        adapter.pause();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                users.clear();

                for (int i = 0; i < 20; i++)
                    users.add(new User(i, "User Name-" + i));

                adapter.refresh(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       swiper.setRefreshing(false);
                    }
                });
            }
        }, 2500);
    }

    private void setupView() {
        swiper = findViewById(R.id.swipe);
        RecyclerView recyclerView = findViewById(R.id.list);
        swiper.setOnRefreshListener(this);
        adapter.setRecyclerView(this, recyclerView, 10);
    }

    private void loadData(final int offset) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (tempError && offset > 50) {
                    adapter.error();
                    tempError = false;
                } else if (offset > 100) {
                    adapter.end();
                } else {
                    for (int i = offset; i < offset + 20; i++)
                        users.add(new User(i, "User Name-" + i));

                    adapter.refresh();
                }
            }
        }, 2500);
    }
}
