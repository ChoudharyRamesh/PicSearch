package com.example.picsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ActivityHistoryAdapter(private val history: History):
    RecyclerView.Adapter<ActivityHistoryAdapter.ActivityHistoryViewHolder>(){

    private val stringSet = history.getHistory()!!
    inner class ActivityHistoryViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    init {
        history.setOnHistoryChangedListenerActivityAdapter {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity_history_adapter,parent,false)
        view.setOnClickListener{
            val tv = view.findViewById<TextView>(R.id.textView_item_activity_adapter)
            onItemClickListener?.let { it2-> it2(tv) }
        }
        return ActivityHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityHistoryViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.textView_item_activity_adapter).text = stringSet.elementAt(position)
        }
    }

    private var onItemClickListener:(TextView.()->Unit)?=null
    fun setOnItemClickListener(block:TextView.()->Unit){
        onItemClickListener = block
    }

    private fun preventTwoClick(view: View) {
        view.isEnabled = false
        view.postDelayed({ view.isEnabled = true }, 500)
    }

    override fun getItemCount(): Int {
        return stringSet.size
    }


}