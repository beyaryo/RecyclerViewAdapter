package com.lynx.wind.recyclersample.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lynx.wind.recycleradapter.RecyclerAdapter;
import com.lynx.wind.recyclersample.R;
import com.lynx.wind.recyclersample.holder.SimpleHolder;
import com.lynx.wind.recyclersample.model.User;

import java.util.ArrayList;

public class SimpleActivity extends AppCompatActivity {

    private ArrayList<User> data = new ArrayList<>();

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

    private void setupView() {
        dummyData();

        RecyclerView recyclerView = findViewById(R.id.list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerAdapter<SimpleHolder, User>(SimpleHolder.class, data, R.layout.item_user, R.layout.item_header) {
            @Override
            public void onBind(SimpleHolder holder, User data, int Index) {
                holder.bind(data);
            }

            @Override
            public void onHeaderBind(View itemView) {
                // Do something here when header binded
            }
        });
    }

    private void dummyData() {
        for (int i = 0; i < 10; i++)
            data.add(new User(i, "User Name-$i"));
    }
}
