package com.example.cribb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cribb.ui.ListingAdapter
import kotlinx.android.synthetic.main.reviews_cell.view.*

class ReviewListAdapter(val items : ArrayList<ProfileFragment.Listing>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.reviews_cell, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.listingAddress?.text= items.get(position).address
        holder?.listingRating?.rating= items.get(position).rating.toFloat()
        holder?.listingDescription?.text= "Price: " + items.get(position).comment
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }
}

fun RecyclerView.addOnItemClickListener(onClickListener: ListingAdapter.OnItemClickListener) {
    this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
            view?.setOnClickListener(null)
        }

        override fun onChildViewAttachedToWindow(view: View) {
            view.setOnClickListener {
                val holder = getChildViewHolder(view)
                onClickListener.onItemClicked(holder.adapterPosition, view)
            }
        }
    })
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val listingAddress = view.cell_address
    val listingRating = view.ratingBar
    val listingDescription = view.cell_description

}