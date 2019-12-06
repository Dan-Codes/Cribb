package com.example.cribb

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cribb.ui.ListingAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.reviews_cell.view.*
import java.lang.reflect.Field

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
        holder?.listingAddress?.text= items[position].address
        holder?.listingRating?.rating= items[position].rating
        holder?.listingDescription?.text= "Review: " + items[position].comment
        holder?.listingTimeStamp.text = items[position].timeStamp
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }


    fun removeItem(viewHolder: RecyclerView.ViewHolder){
        val user = FirebaseAuth.getInstance().currentUser
        val address = items[viewHolder.adapterPosition].address
        val userEmail = user!!.email!!


        val userRef = db.collection("Users").document(userEmail)

        val userUpdates = hashMapOf<String, Any>(
            "Review History." + address to FieldValue.delete()
        )


        val propRef = db.collection("listings").document(address)
        var propertyFields = hashMapOf<Any, Any>()
        val reviewer = hashMapOf<Any, Any>()

        propertyFields["reviews"] = reviewer
        reviewer[userEmail] = FieldValue.delete()

        propRef.set(propertyFields, SetOptions.merge())

        userRef.update(userUpdates).addOnCompleteListener { }


        var avgAmR = 0.0
        var avgMnR = 0.0
        var avgLcR = 0.0
        var avgOAR = 0.0

        propRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var totalOverallRating = 0.0
                var totalAmentRating = 0.0
                var totalLocRating = 0.0
                var totalManageRating = 0.0

                var reviewerNum = 0

                val review = document.get("reviews") as HashMap<String, *>

                for (reviewer in review) {
                    reviewerNum++
                    val reviewMap = review
                    val reviewInfo:HashMap<String,*>  = reviewMap.getValue(reviewer.key) as HashMap<String, *>
                    totalLocRating += (reviewInfo.getValue("locationRating")).toString().toFloat()
                    totalAmentRating += (reviewInfo.getValue("amenitiesRating")).toString().toFloat()
                    totalManageRating += (reviewInfo.getValue("managementRating")).toString().toFloat()

                    totalOverallRating += (reviewInfo.getValue("rating")).toString().toFloat()
                }
                avgAmR = totalAmentRating/reviewerNum
                avgMnR = totalManageRating/reviewerNum
                avgLcR = totalLocRating/reviewerNum

                avgOAR = totalOverallRating/reviewerNum
            }

            Log.d(ContentValues.TAG,avgAmR.toString())
            propertyFields = hashMapOf(
                "avgAmenities" to avgAmR,
                "avgLocation" to avgLcR,
                "avgManage" to avgMnR,
                "avgOverallRating" to avgOAR
            )

            propRef.set(propertyFields, SetOptions.merge())
        }


        items.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
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
    val listingTimeStamp = view.timeStamp

}