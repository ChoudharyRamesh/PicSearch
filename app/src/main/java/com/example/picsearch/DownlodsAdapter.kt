package com.example.picsearch

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DownloadAdapter(private val imageDownloadDataList:ArrayList<ImageDownloadData>):
    RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>(){
    inner class DownloadViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download_adapter,parent,false)
        return DownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.itemView.apply {
             val tv = findViewById<TextView>(R.id.tv_downloaded_file_name)
             tv.text = imageDownloadDataList[position].fileName
        }
    }

    override fun getItemCount(): Int {
        return imageDownloadDataList.size
    }

}