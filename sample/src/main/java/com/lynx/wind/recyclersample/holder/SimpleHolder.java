package com.lynx.wind.recyclersample.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lynx.wind.recyclersample.R;
import com.lynx.wind.recyclersample.model.User;

public class SimpleHolder extends RecyclerView.ViewHolder {
    public SimpleHolder(View itemView) {
        super(itemView);
    }

    public void bind(User data){
        ((TextView)itemView.findViewById(R.id.txt)).setText(data.getName());
    }
}
