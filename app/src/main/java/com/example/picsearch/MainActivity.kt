package com.example.picsearch


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var historyAdapter: HistoryAdapter
    lateinit var searchRecyclerView:RecyclerView
    companion object{
        lateinit var history: History
        lateinit var downloadManager:DownloadManager
        lateinit var downloadAdapter:DownloadAdapter
    }

    override fun onBackPressed() {
        when {
            searchRecyclerView.visibility == View.VISIBLE -> searchRecyclerView.visibility = View.INVISIBLE
            drawerLayout.isDrawerOpen(Gravity.LEFT) -> drawerLayout.closeDrawer(Gravity.LEFT)
            else -> super.onBackPressed()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        history = History(this)

        downloadManager = DownloadManager(this)
        downloadAdapter = DownloadAdapter(downloadManager.imageDownloadDataList)

        searchRecyclerView = findViewById(R.id.search_recyclerView)
        drawerLayout = findViewById(R.id.drawer)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        setActionBarDrawerToggle()
        populateImageSlider()
        setHistoryAdapterToRecycleView()
        setMenuItemClickListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        history.saveHistory()
        downloadManager.saveDownloads()
        super.onSaveInstanceState(outState)
    }

    private fun setHistoryAdapterToRecycleView()
    {
        historyAdapter = HistoryAdapter(history)
        findViewById<RecyclerView>(R.id.search_recyclerView).apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setActionBarDrawerToggle() {
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        val searchActionItem = menu?.findItem(R.id.search)
        val searchView: SearchView = searchActionItem?.actionView as SearchView
        historyAdapter.searchView = searchView
        searchView.queryHint = "Search picture here"

        searchView.setOnSearchClickListener {
           searchRecyclerView.visibility = View.VISIBLE
        }

        searchView.setOnCloseListener {
            searchRecyclerView.visibility = View.INVISIBLE
            false
        }

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRecyclerView.visibility = View.INVISIBLE
                onSearchSubmitted(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRecyclerView.visibility = View.VISIBLE
                newText?.let { historyAdapter.filter(newText) }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun onSearchSubmitted(query:String?)
    {
        // FIXME :- this method is called twice if we press enter in emulator
        query?.let{
            val mQuery = query.lowercase().replaceFirstChar { it.uppercase() }
            historyAdapter.addItem(mQuery)
            Intent(this,SearchActivity::class.java).apply {
                putExtra("title", mQuery)
                startActivity(this)
            }
        }
    }

    private fun populateImageSlider()
    {
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.animal0, "Elephants and tigers may become extinct."))
        imageList.add(SlideModel(R.drawable.animal1, "The animal population decreased by 58 percent in 42 years"))
        imageList.add(SlideModel(R.drawable.animal5, "The panther chameleon"))
        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList,ScaleTypes.CENTER_CROP)
    }

    private fun setMenuItemClickListener() {
        var navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            onNavMenuItemClicked(it)
            drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }
    }

    private fun onNavMenuItemClicked(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_history -> openHistoryActivity()
            R.id.menu_downloads -> openDownloadActivity()
        }
    }

    private fun openHistoryActivity()
    {
        Intent(this,HistoryActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun openDownloadActivity()
    {
        Intent(this,DownloadActivity::class.java).apply {
            startActivity(this)
        }
    }
}