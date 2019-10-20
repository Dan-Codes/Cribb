package com.example.cribb


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_display_listing.*
import kotlinx.android.synthetic.main.fragment_display_listing.view.*


class DisplayListingFragment : androidx.fragment.app.Fragment() {
    private lateinit var address:String
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
            address = "${safeArgs.dynamicAddress}"
            address_passed.text = address
            showReviews(address)
            (activity as Main2Activity).supportActionBar?.title = address
        }

        writeReview.setOnClickListener {

            val nextAction = DisplayListingFragmentDirections.actionDisplayListingFragment2ToWriteReviewFragment2()
            nextAction.dynamicAddress = address
            Navigation.findNavController(it).navigate(nextAction)
        }
    }

    @SuppressLint("ResourceType")
    private fun showReviews(add: String){
        val docRef = db.collection("listings").document(add)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val landlordName:String? = document.get("landlordName") as String
                    val getRent:String? = document.get("rent") as String
                    val overallRating:Double? = document.getDouble("avgOverallRating")
                    val amenitiesRating:Double? = document.getDouble("avgAmenities")
                    val locationRating:Double? = document.getDouble("avgLocation")
                    val manageRating:Double? = document.getDouble("avgManage")
                    val review = document.get("reviews") as HashMap<String, *>
                    landlord.text = landlordName
                    price.text = "$$getRent"

                    if (overallRating != null) overallRatingBar.rating = overallRating.toFloat()
                    overall_avg_num.setText(String.format("%.1f", overallRating))
                    amenities_avg_num.setText(String.format("%.1f", amenitiesRating))
                    location_avg_num.setText(String.format("%.1f", locationRating))
                    manage_avg_num.setText(String.format("%.1f", manageRating))

                    for (reviewer in review){
                          println(reviewer)
                        val reviewer = reviewer
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

                        val linearLayout = (activity)!!.findViewById<LinearLayout>(R.id.LinearLayout_in_scroll)
                        val textView = TextView(activity)
                        textView.text = "Overall Rating: $overall_rating \n$comments  \n" +
                                " Will live again: $willLiveAgain\nTimestamp: ${timestamp.toDate()}\n"
                        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        params.setMargins(100,10,48,10)
                        textView.layoutParams = params

//                        var params = textView.layoutParams as? LinearLayout.LayoutParams
//                        val params = LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT)

//
//                        textView.id=1




                        ratingBar.max = 5
                        ratingBar.stepSize = 0.1.toFloat()
                        ratingBar.rating = overall_rating.toFloat()
                        ratingBar.layoutParams = params
                        LinearLayout_in_scroll.addView(ratingBar)

                        reviews_scrollView.LinearLayout_in_scroll.addView(textView)
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

