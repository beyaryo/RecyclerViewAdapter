package com.lynx.wind.recyclersample.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.lynx.wind.recycleradapter.FilterAdapter
import com.lynx.wind.recyclersample.R
import com.lynx.wind.recyclersample.holder.SimpleHolder
import com.lynx.wind.recyclersample.model.User
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : AppCompatActivity() {

    val data = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupView()
    }

    override fun onSupportNavigateUp(): Boolean {
        return true.also { onBackPressed() }
    }

    private fun setupView() {
        dummyData()

        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        list.adapter = object : FilterAdapter<SimpleHolder, User>(SimpleHolder::class.java, R.layout.item_user, data) {
            override fun onBind(holder: SimpleHolder, data: User, index: Int) {
                holder.bind(data)
            }

            override fun onFiltering(comparator: String, comparedBy: User): Boolean {
                return comparedBy.name.toLowerCase().contains(comparator)
            }

            override fun onFilterResult(count: Int) {
                Toast.makeText(this@FilterActivity, "Found $count user's", Toast.LENGTH_SHORT).show()
            }
        }

        query.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                (list.adapter as FilterAdapter<*, *>).filter.filter(query.text.toString())
            }
            return@setOnEditorActionListener false
        }
    }

    private fun dummyData() {
        data.add(User(1, "Alex"))
        data.add(User(2, "Giant"))
        data.add(User(3, "Josh"))
        data.add(User(4, "Sam"))
        data.add(User(5, "Rich"))
        data.add(User(6, "Tremor"))
        data.add(User(7, "Proton"))
        data.add(User(8, "Sidiq"))
        data.add(User(9, "Michael"))
        data.add(User(10, "Leonardo"))
    }
}