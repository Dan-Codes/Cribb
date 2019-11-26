package com.example.cribb


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.cribb.ui.login.LoginResult
import com.example.cribb.ui.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.security.AuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_create_listing.view.*
import kotlinx.android.synthetic.main.fragment_display_listing.*
import kotlinx.android.synthetic.main.fragment_write_review.*
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class writeReviewFragment : Fragment() {



    private var edited:Boolean = false
    private lateinit var address:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_write_review, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val safeArgs = writeReviewFragmentArgs.fromBundle(it)
            address = "${safeArgs.dynamicAddress}"
            checkReview()
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        address_review.text = address

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                overall_rating.text = seekBar!!.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
                Toast.makeText(context!!, "Start", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
                Toast.makeText(context!!, "End", Toast.LENGTH_SHORT).show()
            }
        })

        button_post_review.setOnClickListener{
            val user = FirebaseAuth.getInstance().currentUser
            val addressRef = db.collection("listings").document(address)
            val userRef = db.collection("Users").document(user!!.email!!)

            val comment = review_comments.text ?: "No Comment!"

            if (edited) {
                var propertyFields = hashMapOf<Any, Any>()
                val userFields = hashMapOf<Any, Any>()
                val reviewer = hashMapOf<Any, Any>()
                val property = hashMapOf<Any, Any>()

                val reviewDetail = hashMapOf<Any,Any>(
                    "comments" to comment.toString(),
                    "isAnonymous" to anonymous_switch.isChecked,
                    "isEdited" to true,
                    "rating" to overall_rating.text.toString().toFloat(),
                    "willLiveAgain" to live_here_again_switch.isChecked,
                    "locationRating" to location_ratingBar.rating,
                    "managementRating" to manage_ratingBar.rating,
                    "amenitiesRating" to amenities_ratingBar.rating,
                    "timeStamp" to FieldValue.serverTimestamp()
                )

                userFields["Review History"] = property
                property[address] = reviewDetail

                propertyFields["reviews"] = reviewer
                reviewer[user!!.email!!] = reviewDetail

                addressRef.set(propertyFields, SetOptions.merge()).addOnSuccessListener {



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

                    Thread.sleep(500)

                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    var nextAction = writeReviewFragmentDirections.actionWriteReviewFragment2ToDisplayListingFragment2()
                    nextAction.dynamicAddress = address
                    Navigation.findNavController(button_post_review).navigate(nextAction)
                }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                userRef.set(userFields, SetOptions.merge())
            }
            else{
                var propertyFields = hashMapOf<Any, Any>()
                val userFields = hashMapOf<Any, Any>()
                val reviewer = hashMapOf<Any, Any>()
                val property = hashMapOf<Any, Any>()

                val reviewDetail = hashMapOf<Any,Any>(
                    "comments" to comment.toString(),
                    "isAnonymous" to anonymous_switch.isChecked,
                    "isEdited" to false,
                    "rating" to overall_rating.text.toString().toFloat(),
                    "willLiveAgain" to live_here_again_switch.isChecked,
                    "locationRating" to location_ratingBar.rating,
                    "managementRating" to manage_ratingBar.rating,
                    "amenitiesRating" to amenities_ratingBar.rating,
                    "timeStamp" to FieldValue.serverTimestamp()
                )

                userFields["Review History"] = property
                property[address] = reviewDetail

                propertyFields["reviews"] = reviewer
                reviewer[user!!.email!!] = reviewDetail

                addressRef.set(propertyFields, SetOptions.merge()).addOnSuccessListener {



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

                    Thread.sleep(500)

                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    var nextAction = writeReviewFragmentDirections.actionWriteReviewFragment2ToDisplayListingFragment2()
                    nextAction.dynamicAddress = address
                    Navigation.findNavController(button_post_review).navigate(nextAction)
                }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                userRef.set(userFields, SetOptions.merge())
            }
        }
    }

    private fun checkReview() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("username1", "${user.email}")
            Log.d("addresstest", "$address")

            val docRef = db.collection("listings").document(address)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {

                        val review = document.get("reviews") as HashMap<String, *>

                        for (reviewer in review) {
                            if (reviewer.key == user.email){
                                edited = true
                                val reviewMap = review
                                val reviewInfo:HashMap<String,*>  = reviewMap.getValue(reviewer.key) as HashMap<String, *>
                                Toast.makeText(context!!, "You have already reviewed this property.", Toast.LENGTH_SHORT).show()
                                val comment:String = reviewInfo.getValue("comments").toString()
                                review_comments.setText(comment)

                                val ratingInt = (reviewInfo.getValue("rating"))
                                seekBar.progress = ratingInt.toString().toDouble().roundToInt()

                                overall_rating.text = seekBar.progress.toString()
                                location_ratingBar.rating = (reviewInfo.getValue("locationRating")).toString().toFloat()
                                manage_ratingBar.rating = (reviewInfo.getValue("managementRating")).toString().toFloat()
                                amenities_ratingBar.rating = (reviewInfo.getValue("amenitiesRating")).toString().toFloat()
                                live_here_again_switch.isChecked = (reviewInfo.getValue("willLiveAgain")).toString().toBoolean()
                                anonymous_switch.isChecked = (reviewInfo.getValue("isAnonymous")).toString().toBoolean()
                                break
                            }

                        }

                    }


                }

        }
    }
}
