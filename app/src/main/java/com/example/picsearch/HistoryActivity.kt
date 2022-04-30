package com.example.picsearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    private lateinit var activityHistoryAdapter: ActivityHistoryAdapter
    private lateinit var mRecyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        title = "History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.history_activity_recyclerView)
        setActivityHistoryAdapterToRecycleView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)finish()
        else if(item.itemId==R.id.clear_history){
            MainActivity.history.clearHistory()
        }
        return true
    }

    private fun setActivityHistoryAdapterToRecycleView()
    {
        activityHistoryAdapter = ActivityHistoryAdapter(MainActivity.history)
        mRecyclerView.apply {
            adapter = activityHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        activityHistoryAdapter.setOnItemClickListener {
            finish()
            searchWithHistory(text.toString())
        }
    }

    private fun searchWithHistory(history:String)
    {
        val mQuery = history.replaceFirstChar { it.uppercase() }
        Intent(this,SearchActivity::class.java).apply {
            putExtra("title", mQuery)
            startActivity(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }


}