package com.example.cribb

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cribb.ui.ListingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.display_review_cell.view.*
import kotlinx.android.synthetic.main.reviews_cell.view.*
import kotlinx.android.synthetic.main.reviews_cell.view.cell_address
import kotlinx.android.synthetic.main.reviews_cell.view.cell_description
import kotlinx.android.synthetic.main.reviews_cell.view.ratingBar
import kotlinx.android.synthetic.main.reviews_cell.view.timeStamp

class displayListingAdapter(val items : ArrayList<DisplayListingFragment.Listing>, val context: Context) : RecyclerView.Adapter<ViewHolder1>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder1, position: Int) {
        holder?.listingAddress?.text= items[position].address
        holder?.listingRating?.rating= items[position].rating
        holder?.listingDescription?.text= "Review: " + items[position].comment
        holder?.listingAuthor?.text = items[position].name
        holder?.listingTimeStamp.text = items[position].timeStamp
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder1 {
        return ViewHolder1(LayoutInflater.from(context).inflate(R.layout.display_review_cell, parent, false))
    }


    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }


}

class ViewHolder1 (view: View) : RecyclerView.ViewHolder(view) {
    val listingAuthor = view.reviewer
    val listingAddress = view.cell_address
    val listingRating = view.ratingBar
    val listingDescription = view.cell_description
    val listingTimeStamp = view.timeStamp

}