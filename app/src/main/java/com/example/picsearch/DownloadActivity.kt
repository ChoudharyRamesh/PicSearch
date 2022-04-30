package com.example.picsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DownloadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        title = "Downloads"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        setDownloadAdapterToRecycleView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)finish()
        else if(item.itemId==R.id.clear_downlods){
            MainActivity.downloadManager.clear()
            MainActivity.downloadAdapter.notifyDataSetChanged()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_download_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setDownloadAdapterToRecycleView()
    {
        findViewById<RecyclerView>(R.id.download_activity_recyclerView).apply {
            adapter = MainActivity.downloadAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}