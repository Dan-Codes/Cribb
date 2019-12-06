package com.example.cribb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ipadmin_item.view.*
import android.util.Log

class IPAddressAdminAdapter(val items : ArrayList<IPAddressFragment.IPAddress>, val context: Context) : RecyclerView.Adapter<IPAdminViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IPAdminViewHolder {
        return IPAdminViewHolder(LayoutInflater.from(context).inflate(R.layout.ipadmin_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: IPAdminViewHolder, position: Int) {
        var EmailArray:ArrayList<String> = items.get(position).ipEamils
        var EmailStringTotal = ""
        holder?.ipAddressForViewHolder?.text = items.get(position).ipAddress
        holder?.ipOccurrenceForViewHolder?.text = "There are " + items.get(position).occurrence.toString() + " users registered on this IP address!"

        for(i in EmailArray){
            EmailStringTotal += i + "\n"
        }
        holder?.ipEmailForViewHolder?.text = EmailStringTotal
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }
}

class IPAdminViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val ipAddressForViewHolder = view.ipAddress
    val ipOccurrenceForViewHolder = view.ipOccurrence
    val ipEmailForViewHolder = view.ipEmail

}