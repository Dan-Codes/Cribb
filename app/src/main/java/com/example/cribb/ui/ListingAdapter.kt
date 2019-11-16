package com.example.cribb.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cribb.R
import kotlinx.android.synthetic.main.listing_item.view.*

class ListingAdapter(val items : ArrayList<searchTable.Listing>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.listing_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.listingAddress?.text= items.get(position).name
        holder?.listingRating?.text= "Rating: " + items.get(position).rating.toString()
        holder?.listingPrice?.text= "Price: " + items.get(position).price.toString()
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val listingAddress = view.listing_address
    val listingRating = view.listing_rating
    val listingPrice = view.listing_price

}