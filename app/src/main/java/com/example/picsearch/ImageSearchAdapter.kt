package com.example.picsearch

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageSearchAdapter(private val activity : SearchActivity, private val imageUrlDataList: List<ImageUrlData>):
    RecyclerView.Adapter<ImageSearchAdapter.ImageViewHolder>() {
    
    private lateinit var mRecyclerView:RecyclerView

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.ivSearchImage)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_image_holder, parent, false)
        view.setOnClickListener {
           activity.cl_fullImageView.visibility = View.VISIBLE
            val itemIndex = mRecyclerView.getChildLayoutPosition(view)
            Glide.with(activity)
                .load(imageUrlDataList[itemIndex].largeImageURL)
                .placeholder(com.denzcoskun.imageslider.R.drawable.loading)
                .fitCenter()
                .into(activity.fullImageView)
            activity.currentFullViewImageId = imageUrlDataList[itemIndex].id
        }
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(activity)
            .load(imageUrlDataList[position].webformatURL)
            .placeholder(com.denzcoskun.imageslider.R.drawable.loading)
            .fitCenter()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrlDataList.size
    }
}