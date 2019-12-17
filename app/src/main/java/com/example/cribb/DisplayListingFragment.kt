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
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_display_listing.*
import kotlinx.android.synthetic.main.fragment_display_listing.view.*
import kotlinx.android.synthetic.main.fragment_profile.*


class DisplayListingFragment : androidx.fragment.app.Fragment() {
    private lateinit var address:String
    private lateinit var address1:String
    private lateinit var nameOfReviwer:String

    class Listing {

        var address: String = ""
        var rating: Float = 0F
        var isAnonymous: String = ""
        var comment: String = ""
        var timeStamp: String = ""
        var willLiveAgain: String = ""
        var isEdited: String = ""
        var locationRating = ""
        var amenititesRating = ""
        var managementRating = ""
        var name = ""
    }
    private var arrayList:ArrayList<Listing> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_listing, container, false)
    }

    override fun onResume() {
        super.onResume()
        updateRating()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val safeArgs = DisplayListingFragmentArgs.fromBundle(it)
            address = "${safeArgs.dynamicAddress}"

            showReviews(address)
            address1=""
            val splitAddress: List<String> = address.split(" ")

            for (len in splitAddress){
                if (len.last() == ',') break
                address1 = "$address1$len "

            }
            address_passed.text = address
            (activity as Main2Activity).supportActionBar?.title = address1
        }

        writeReview.setOnClickListener {

            val nextAction = DisplayListingFragmentDirections.actionDisplayListingFragment2ToWriteReviewFragment2()
            nextAction.dynamicAddress = address
            Navigation.findNavController(it).navigate(nextAction)
        }
        reportButton.setOnClickListener {
            val nextAction = DisplayListingFragmentDirections.actionDisplayListingFragment2ToReportFragment()
            nextAction.reportAddress = address
            Navigation.findNavController(it).navigate(nextAction)
        }

        displayCardView.adapter = displayListingAdapter(arrayList, this.requireContext())
        displayCardView.layoutManager = LinearLayoutManager(this.requireContext())
    }

    @SuppressLint("ResourceType")
    private fun showReviews(add: String){
        val docRef = db.collection("listings").document(add)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val landlordName:String? = document.get("landlordName") as String
                    var getRent:String? = document.get("rent") as String
                    val overallRating:Double? = document.getDouble("avgOverallRating")
                    val amenitiesRating:Double? = document.getDouble("avgAmenities")
                    val locationRating:Double? = document.getDouble("avgLocation")
                    val manageRating:Double? = document.getDouble("avgManage")
                    val phoneNumber:String? = document.get("phoneNumber").toString()
                    val review = document.get("reviews") as HashMap<String, *>
                    landlord.text = landlordName ?: "No Landlord Info"
                    getRent = getRent?.removePrefix("$")
                    price.text = "$$getRent"
                    if (overallRating != null) overallRatingBar.rating = overallRating.toFloat()
                    overall_avg_num.setText(String.format("%.1f", overallRating))
                    amenities_avg_num.setText(String.format("%.1f", amenitiesRating))
                    location_avg_num.setText(String.format("%.1f", locationRating))
                    manage_avg_num.setText(String.format("%.1f", manageRating))
                    if (phoneNumber != "null") phoneNumberDisplay.text = phoneNumber.toString()

                    for (reviewer in review){
                          println(reviewer)
                        var property = Listing()
                        val reviewer = reviewer
                        val reviewMap = review
                        val ratingBar = RatingBar(activity, null, android.R.attr.ratingBarStyleSmall)
                        val reviewInfo:HashMap<String,String>  = reviewMap.getValue(reviewer.key) as HashMap<String, String>
                        val comments:String = reviewInfo.getValue("comments")
                        var overall_rating: String = (String.format("%.1f", reviewInfo.getValue("rating")))
                        //overall_rating = (String.format("%.1f", overall_rating))
                        val isAnonymous:Boolean = reviewInfo.getValue("isAnonymous") as Boolean
                        if (isAnonymous)
                            nameOfReviwer = "Anonymous"
                        else
                            nameOfReviwer = reviewer.key
                        val isEdited:Boolean = reviewInfo.getValue("isEdited") as Boolean
                        var willLiveAgain:String = if (reviewInfo.getValue("willLiveAgain") as Boolean) "Yes" else "No"
                        val timestamp = reviewInfo.getValue("timeStamp") as Timestamp

                        property.name = nameOfReviwer
                        property.comment = comments
                        property.rating = overall_rating.toFloat()
                        property.timeStamp = timestamp.toDate().toString()
                        arrayList.add(property)

//                        val linearLayout = (activity)!!.findViewById<LinearLayout>(R.id.LinearLayout_in_scroll)
//                        val textView = TextView(activity)
//                        textView.text = "Overall Rating: $overall_rating \n$comments  \n" +
//                                "Will live again: $willLiveAgain\n$nameOfReviwer\nTimestamp: ${timestamp.toDate()}\n"
//                        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                        params.setMargins(100,10,48,10)
//                        textView.layoutParams = params
//                        var typeface = ResourcesCompat.getFont(context!!, R.font.raleway)
//                        textView.typeface = typeface
//
//                        ratingBar.max = 5
//                        ratingBar.stepSize = 0.1.toFloat()
//                        ratingBar.rating = overall_rating.toFloat()
//                        ratingBar.layoutParams = params
//                        LinearLayout_in_scroll.addView(ratingBar)
//
//                        reviews_scrollView.LinearLayout_in_scroll.addView(textView)
                    }
                    displayCardView.adapter = displayListingAdapter(arrayList, this.requireContext())

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun updateRating(){
        val addressRef = db.collection("listings").document(address)
        var propertyFields = hashMapOf<Any, Any>()

        var avgAmR = 0.0
        var avgMnR = 0.0
        var avgLcR = 0.0
        var avgOAR = 0.0

        addressRef.get().addOnSuccessListener { document ->
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

            Log.d(TAG,avgAmR.toString())
            propertyFields = hashMapOf(
                "avgAmenities" to avgAmR,
                "avgLocation" to avgLcR,
                "avgManage" to avgMnR,
                "avgOverallRating" to avgOAR
            )

            addressRef.set(propertyFields, SetOptions.merge())
        }
    }

}

