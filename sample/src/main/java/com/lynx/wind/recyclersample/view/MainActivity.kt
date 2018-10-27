package com.lynx.wind.recyclersample.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lynx.wind.recyclersample.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_simple.setOnClickListener {
            startActivity(Intent(this@MainActivity, SimpleActivity::class.java))
        }

        btn_filter.setOnClickListener {
            startActivity(Intent(this@MainActivity, FilterActivity::class.java))
        }

        btn_load.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoadMoreActivity::class.java))
        }
    }
}
