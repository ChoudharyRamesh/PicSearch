package com.example.picsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val history: History):
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(){

    inner class HistoryViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    private lateinit var mRecyclerView: RecyclerView
    private var filteredStringSet:MutableSet<String> = mutableSetOf()
    private var lastFilteredWith:String = ""
    var searchView:SearchView? = null

    init {
        history.getHistory()?.let { filteredStringSet.addAll(it) }
        history.setOnHistoryChangedListenerAdapter {
            filter(lastFilteredWith)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_adapter,parent,false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.itemView.apply {
            val tv = findViewById<TextView>(R.id.textView_item_adapter)
            tv.text = filteredStringSet.elementAt(position)
            tv.setOnClickListener{
                onItemClick(this)
            }
            findViewById<ImageButton>(R.id.cancel_btn_item_adapter).setOnClickListener{
                    removeItem(this)
            }
        }
    }

    private fun onItemClick(item: View) {

        preventTwoClick(item)
        searchView?.let{
            val text = item.findViewById<TextView>(R.id.textView_item_adapter).text.toString()
            it.setQuery(text,true)
        }
    }

    fun addItem(text: String){
        filteredStringSet.add(text.lowercase())
        history.addHistory(text.lowercase())
    }

    private fun removeItem(view: View)
    {
        preventTwoClick(view)
        val itemIndex = mRecyclerView.getChildLayoutPosition(view)
        val text = filteredStringSet.elementAt(itemIndex)
        filteredStringSet.remove(text)
        history.removeHistory(text)
        notifyItemRemoved(itemIndex)
    }

    private fun preventTwoClick(view: View) {
        view.isEnabled = false
        view.postDelayed({ view.isEnabled = true }, 500)
    }

    override fun getItemCount(): Int {
        return filteredStringSet.size
    }


    fun filter(text: String) {
        filteredStringSet.clear()
        lastFilteredWith = text
        history.getHistory()?.let {
            for (item in it) {
                if (item.contains(text,true)) filteredStringSet.add(item)
            }
            notifyDataSetChanged()
        }
    }
}