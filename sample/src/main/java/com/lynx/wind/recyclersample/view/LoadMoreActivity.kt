package com.lynx.wind.recyclersample.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lynx.wind.recycleradapter.LoadMoreAdapter
import com.lynx.wind.recyclersample.R
import com.lynx.wind.recyclersample.holder.SimpleHolder
import com.lynx.wind.recyclersample.model.User
import kotlinx.android.synthetic.main.activity_simple.*
import kotlinx.android.synthetic.main.item_loading.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class LoadMoreActivity : AppCompatActivity() {

    private val adapter by lazy { object: LoadMoreAdapter<SimpleHolder, User>(
            SimpleHolder::class.java, R.layout.item_user, R.layout.item_loading, ArrayList<User>()) {

        override fun onBind(holder: SimpleHolder, data: User, index: Int) {
            holder.bind(data)
        }

        override fun onLoadingBind(itemView: View, state: LoadState) {
            if(state == LoadState.ERROR){
                itemView.loading.visibility = View.GONE
                itemView.error_layout.visibility = View.VISIBLE
            }else{
                itemView.loading.visibility = View.VISIBLE
                itemView.error_layout.visibility = View.GONE
            }

            itemView.btn_retry.setOnClickListener {
                setState(this@LoadMoreActivity, LoadMoreAdapter.LoadState.LOAD_NEXT)
            }
        }

        override fun loadMore(offset: Int) {
            loadData(offset)
        }
    }}

    private var tempError = true

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

    private fun setupView() {
        adapter.setRecyclerView(list, 5)
    }

    private fun loadData(offset: Int) {
        Timer().schedule(2500){
            if(tempError && offset > 50){
                adapter.setState(this@LoadMoreActivity, LoadMoreAdapter.LoadState.ERROR)
                tempError = false
            }else if(offset > 100){
                adapter.setState(this@LoadMoreActivity, LoadMoreAdapter.LoadState.END)
            }else{
                val newData = ArrayList<User>()

                for (i in offset until offset+20)
                    newData.add(User(i, "User Name-$i"))

                adapter.refresh(this@LoadMoreActivity, newData)
            }
        }
    }
}