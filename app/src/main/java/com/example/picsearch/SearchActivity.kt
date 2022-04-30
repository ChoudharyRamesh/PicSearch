package com.example.picsearch

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.picsearch.R.id.toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.media.Image
import android.provider.MediaStore
import android.util.Log
import java.io.IOException
import java.math.BigInteger
import java.util.*

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}


class SearchActivity : AppCompatActivity() {

    private val mImageUrlDataList = ArrayList<ImageUrlData>()
    private lateinit var rvForImageSearch: RecyclerView
    private lateinit var adapter: ImageSearchAdapter
    private lateinit var searchText:String
    private var currentPage = 1;
    lateinit var cl_fullImageView : ConstraintLayout
    lateinit var fullImageView: ImageView
    private var writePermissionGranted = false
    var currentFullViewImageId:BigInteger = BigInteger("0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        cl_fullImageView = findViewById(R.id.cl_fullImageView)
        fullImageView = findViewById(R.id.iv_fullImageView)
        val title = intent.getStringExtra("title")
        searchText = title!!
        setToolBarTitle(title!!)

        val toolbar: Toolbar = findViewById(toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setImageSearchAdapter()

        findViewById<BottomNavigationView>(R.id.navViewBottom).setOnItemSelectedListener {
            when(it.itemId) {
                R.id.menu_btn_back -> showPrevPage()
                R.id.menu_btn_next -> showNextPage()
            }
            return@setOnItemSelectedListener(true)
        }

        showImage(currentPage)

        findViewById<Button>(R.id.btn_download).setOnClickListener {
            preventTwoClick(it)
            updateOrRequestPermission()
            val bitmap = fullImageView.drawable.toBitmap()
            val saved = savePhotoToExternalStorage(currentFullViewImageId.toString(),bitmap)
            if(saved){
                Toast.makeText(this,"Image Downloaded", Toast.LENGTH_SHORT).show()
                val data = ImageDownloadData(searchText+"_"+currentFullViewImageId.toString()+".jpg")
                MainActivity.downloadManager.add(data)
                MainActivity.downloadAdapter.notifyDataSetChanged()
            }
            else Toast.makeText(this,"Download failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun preventTwoClick(view: View) {
        view.isEnabled = false
        view.postDelayed({ view.isEnabled = true }, 500)
    }

    private fun savePhotoToExternalStorage(displayName: String, bmp: Bitmap): Boolean {
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }

        return try {
            contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch(e: IOException) {
           Log.d("nishi","error occured ${e.message}")
            false
        }
    }


    private fun updateOrRequestPermission()
    {
        val hasWritePermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
       val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        writePermissionGranted = hasWritePermission || minSdk29
        if(!writePermissionGranted){
            var permissionToRequest = mutableListOf<String>()
             permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissionToRequest.toTypedArray(), 0)
        }
    }


    override fun onBackPressed() {
        if(cl_fullImageView.visibility==View.VISIBLE){
            cl_fullImageView.visibility = View.INVISIBLE
            return
        }
        super.onBackPressed()
    }

    private fun showImage(pageNumber:Int){

        GlobalScope.launch {

            val imageApiFetcher = ImageApiFetcher(searchText)
            val _imageUrlDataList = imageApiFetcher.getUrls(pageNumber)

            runOnUiThread {
                if (_imageUrlDataList.isNullOrEmpty()) {
                    Toast.makeText(applicationContext, "No image found", Toast.LENGTH_SHORT).show()
                    if(currentPage>1) currentPage--;
                    return@runOnUiThread
                }

                mImageUrlDataList.clear()
                adapter.notifyDataSetChanged()
                mImageUrlDataList.addAll(_imageUrlDataList)
                adapter.notifyDataSetChanged()
            }
        }
    }

   private fun showNextPage(){
        currentPage++;
        showImage(currentPage)
    }

    private fun showPrevPage(){
        if(currentPage>1){
            currentPage--;
            showImage(currentPage)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)finish()
        return true
    }

    private fun setToolBarTitle(text:String){
        findViewById<Toolbar>(toolbar).title = text
    }

    private fun setImageSearchAdapter(){
        rvForImageSearch = findViewById(R.id.rvforSearchedImages)
        rvForImageSearch.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = ImageSearchAdapter(this, mImageUrlDataList)
        rvForImageSearch.adapter = adapter
    }

}