package com.example.picsearch

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class History(context: Context) {

    private val sharedPrefId:String = "ID_SHARED_PREF@435#"
    private val saveKey:String = "search_history"
    private val sharedPreferences:SharedPreferences =
        context.getSharedPreferences(sharedPrefId,Context.MODE_PRIVATE)
    private val editor:SharedPreferences.Editor = sharedPreferences.edit()
    private val historyStringSet : MutableSet<String>  = mutableSetOf()
    private var callBack1:(()->Unit)? = null
    private var callBack2:(()->Unit)? = null

    init {
        val savedSet = sharedPreferences.getStringSet(saveKey,null)
        savedSet?.let {
            historyStringSet.addAll(savedSet)
        }
    }

    fun saveHistory()
    {
        editor.apply {
            putStringSet(saveKey,historyStringSet)
            Log.d("nishi","history saved $historyStringSet")
            editor.commit()
        }
    }

    fun getHistory():MutableSet<String>?{ return historyStringSet }

    fun addHistory(text:String){
        historyStringSet.add(text.lowercase())
        callBack2?.let { it() }
    }

    fun removeHistory(text: String){
        Log.d("nishi","history removed $text")
        historyStringSet.remove(text)
        callBack2?.let { it() }
    }

    fun clearHistory(){
        historyStringSet.clear()
        saveHistory()
        callBack1?.let { it() }
        callBack2?.let { it() }
    }

    fun setOnHistoryChangedListenerAdapter(adapterMethod:()->Unit){
        callBack1 = adapterMethod
    }

    fun setOnHistoryChangedListenerActivityAdapter(activityAdapterMethod:()->Unit){
        callBack2 = activityAdapterMethod
    }

}