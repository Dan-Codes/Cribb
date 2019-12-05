package com.example.cribb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cribb.ui.ListingAdapter
import kotlinx.android.synthetic.main.report_item.view.*

class ReportAdminAdapter(val items : ArrayList<ReportAdminFragment.report>, val context: Context) : RecyclerView.Adapter<ReportViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        return ReportViewHolder(LayoutInflater.from(context).inflate(R.layout.report_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder?.reportAdd?.text= items.get(position).reportedAddress
        holder?.reportDetail?.text = items.get(position).detail
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }
}


class ReportViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val reportAdd = view.report_address
    val reportDetail = view.report_detail

}