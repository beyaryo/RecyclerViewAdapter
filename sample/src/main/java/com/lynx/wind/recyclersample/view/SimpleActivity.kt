package com.lynx.wind.recyclersample.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.lynx.wind.recycleradapter.RecyclerAdapter
import com.lynx.wind.recyclersample.R
import com.lynx.wind.recyclersample.holder.SimpleHolder
import com.lynx.wind.recyclersample.model.User
import kotlinx.android.synthetic.main.activity_simple.*

class SimpleActivity: AppCompatActivity() {

    val data = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupView()
    }

    override fun onSupportNavigateUp(): Boolean {
        return true.also { onBackPressed() }
    }

    private fun setupView(){
        dummyData()

        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        list.adapter = object: RecyclerAdapter<SimpleHolder, User>(SimpleHolder::class.java, R.layout.item_user, data){
            override fun onBind(holder: SimpleHolder, data: User, index: Int) {
                holder.bind(data)
            }
        }
    }

    private fun dummyData(){
        for(i in 0 until 10){
            data.add(User(i, "User Name-$i"))
        }
    }
}