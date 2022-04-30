package com.example.picsearch

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ImageDownloadData(val fileName:String)

class DownloadManager(context: Context) {
    private val sharedPrefId:String = "ID_SHARED_PREF@435#"
    private val saveKey:String = "download_history"
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(sharedPrefId, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    val imageDownloadDataList : ArrayList<ImageDownloadData>  = arrayListOf()

    init {
        val savedString = sharedPreferences.getString(saveKey,null)
        savedString?.let{
            val itemType = object : TypeToken<ArrayList<ImageDownloadData>>() {}.type
            val savedList:ArrayList<ImageDownloadData> = Gson().fromJson(savedString,itemType)
            imageDownloadDataList.addAll(savedList)
        }
    }

    fun saveDownloads()
    {
        editor.apply {
            val jsonString =  Gson().toJson(imageDownloadDataList)
            editor.putString(saveKey,jsonString)
            editor.commit();
        }
    }

    fun add(data:ImageDownloadData){
        imageDownloadDataList.add(data)
    }

    fun clear(){
        imageDownloadDataList.clear()
        saveDownloads()
    }
}