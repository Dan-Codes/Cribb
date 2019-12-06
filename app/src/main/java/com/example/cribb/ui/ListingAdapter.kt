package com.example.cribb.ui

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cribb.R
import com.example.cribb.db
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.listing_item.view.*
import java.lang.Float.parseFloat

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
        try {
            val getRating:String = items.get(position).rating.toString()
            val num = parseFloat(getRating)
            holder?.ratingBar.rating = num
        } catch (e: NumberFormatException) {
            holder?.ratingBar.rating = parseFloat("0.0")
        }


        holder?.listingPrice?.text= "Price: " + items.get(position).price.toString()
    }

    interface OnItemLongClickListener{
        fun onItemLongClicked(position: Int, view: View)
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun removeItem(position: Int){
        val address = items[position].name

        db.collection("listings").document(address)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                Toast.makeText(context!!, "This listing is DELETED!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

        items.removeAt(position)
        notifyItemRemoved(position)
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

fun RecyclerView.addOnItemLongClickListener(onLongClickListener: ListingAdapter.OnItemLongClickListener){
    this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
            view?.setOnClickListener(null)
        }

        override fun onChildViewAttachedToWindow(view: View) {
            view.setOnClickListener {
                val holder = getChildViewHolder(view)
                onLongClickListener.onItemLongClicked(holder.adapterPosition, view)
            }
        }
    })

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val listingAddress = view.listing_address
    val listingRating = view.listing_rating
    val ratingBar = view.ratingBar
    val listingPrice = view.listing_price

}