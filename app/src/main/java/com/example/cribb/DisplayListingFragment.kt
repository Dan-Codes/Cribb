package com.example.cribb


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_display_listing.*
import kotlinx.android.synthetic.main.fragment_display_listing.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import java.sql.Timestamp


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DisplayListingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_listing, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val safeArgs = DisplayListingFragmentArgs.fromBundle(it)
            val address = "${safeArgs.dynamicAddress}"
            address_passed.text = address
            showReviews(address)
            (activity as MainActivity).supportActionBar?.title = address
        }
    }

    private fun showReviews(add: String){
        val docRef = db.collection("listings").document(add)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val landlordName:String = document.get("landlordName") as String
                    val getRent:String = document.get("rent") as String
                    val review = document.get("reviews") as HashMap<String, *>
                    landlord.text = landlordName
                    price.text = getRent

                    println(review)
                    for (reviewer in review){
                          println(reviewer)
                        val reviewer = reviewer
                        val textView = TextView(activity)
                        val reviewMap = review

                        val reviewInfo:HashMap<String,String>  = reviewMap.getValue(reviewer.key) as HashMap<String, String>
                        val comments:String = reviewInfo.getValue("comments")
                        val overall_rating:Double = reviewInfo.getValue("rating") as Double
                        val isAnonymous:Boolean = reviewInfo.getValue("isAnonymous") as Boolean
                        val isEdited:Boolean = reviewInfo.getValue("isEdited") as Boolean
                        val willLiveAgain:Boolean = reviewInfo.getValue("willLiveAgain") as Boolean
                        val timestamp = reviewInfo.getValue("timeStamp") as com.google.firebase.Timestamp

                        textView.text = "Overall Rating: $overall_rating\n $willLiveAgain \n$comments  \nTimestamp: $timestamp"
                        textView.gravity = Gravity.CENTER
                        reviews_scrollView.LinearLayout.addView(textView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    }


                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

}
