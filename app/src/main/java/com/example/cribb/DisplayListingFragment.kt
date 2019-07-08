package com.example.cribb


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_create_listing.*
import kotlinx.android.synthetic.main.fragment_display_listing.*
import kotlinx.android.synthetic.main.fragment_display_listing.view.*


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
                    val overallRating:Double = document.get("avgOverallRating") as Double
                    val amenitiesRating:Double = document.get("avgAmenities") as Double
                    val locationRating:Double = document.get("avgLocation") as Double
                    val manageRating:Double = document.get("avgManage") as Double
                    val review = document.get("reviews") as HashMap<String, *>
                    landlord.text = landlordName
                    price.text = "$$getRent"
                    overallRatingBar.rating = overallRating.toFloat()
                    overall_avg_num.setText(String.format("%.1f", overallRating))
                    amenities_avg_num.setText(String.format("%.1f", amenitiesRating))
                    location_avg_num.setText(String.format("%.1f", locationRating))
                    manage_avg_num.setText(String.format("%.1f", manageRating))

                    for (reviewer in review){
                          println(reviewer)
                        val reviewer = reviewer
                        val textView = TextView(activity)
                        val reviewMap = review
                        val ratingBar = RatingBar(activity, null, android.R.attr.ratingBarStyleSmall)
                        val reviewInfo:HashMap<String,String>  = reviewMap.getValue(reviewer.key) as HashMap<String, String>
                        val comments:String = reviewInfo.getValue("comments")
                        var overall_rating: String = (String.format("%.1f", reviewInfo.getValue("rating")))
                        //overall_rating = (String.format("%.1f", overall_rating))
                        val isAnonymous:Boolean = reviewInfo.getValue("isAnonymous") as Boolean
                        val isEdited:Boolean = reviewInfo.getValue("isEdited") as Boolean
                        var willLiveAgain:String = if (reviewInfo.getValue("willLiveAgain") as Boolean) "Yes" else "No"
                        val timestamp = reviewInfo.getValue("timeStamp") as Timestamp

                        //val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        textView.text = "Overall Rating: $overall_rating \n$comments  \n" +
                                " Will live again: $willLiveAgain\nTimestamp: ${timestamp.toDate()}\n"
                        textView.gravity = Gravity.LEFT
                        ratingBar.max = 5
                        ratingBar.stepSize = 0.1.toFloat()
                        ratingBar.rating = overall_rating.toFloat()
                        LinearLayout.addView(ratingBar,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
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

