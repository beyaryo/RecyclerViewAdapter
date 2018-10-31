package com.lynx.wind.recyclersample.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lynx.wind.recycleradapter.FilterAdapter;
import com.lynx.wind.recyclersample.R;
import com.lynx.wind.recyclersample.holder.SimpleHolder;
import com.lynx.wind.recyclersample.model.User;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;

    private ArrayList<User> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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

        recyclerView = findViewById(R.id.list);
        editText = findViewById(R.id.query);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new FilterAdapter<SimpleHolder, User>(SimpleHolder.class, data, R.layout.item_user, R.layout.item_header) {
            @Override
            public void onBind(SimpleHolder holder, User data, int Index) {
                holder.bind(data);
            }

            @Override
            public boolean onFiltering(String comparator, User comparedBy) {
                return comparedBy.getName().toLowerCase().contains(comparator);
            }

            @Override
            public void onFilterResult(int count) {
                Toast.makeText(FilterActivity.this, "Found " +count+ " user's", Toast.LENGTH_SHORT).show();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                ((FilterAdapter)recyclerView.getAdapter())
                        .getFilter()
                        .filter(s.toString());
            }
        });
    }

    private void dummyData() {
        data.add(new User(1, "Alex"));
        data.add(new User(2, "Giant"));
        data.add(new User(3, "Josh"));
        data.add(new User(4, "Sam"));
        data.add(new User(5, "Rich"));
        data.add(new User(6, "Tremor"));
        data.add(new User(7, "Proton"));
        data.add(new User(8, "Sidiq"));
        data.add(new User(9, "Michael"));
        data.add(new User(10, "Leonardo"));
    }
}
