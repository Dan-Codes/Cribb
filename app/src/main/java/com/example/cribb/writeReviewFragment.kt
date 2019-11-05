package com.example.cribb


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cribb.ui.login.LoginResult
import com.example.cribb.ui.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.security.AuthProvider
import com.google.firebase.auth.FirebaseUser
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
                                val reviewMap = review
                                val reviewInfo:HashMap<String,*>  = reviewMap.getValue(reviewer.key) as HashMap<String, *>
                                Toast.makeText(context!!, "You have already reviewed this property.", Toast.LENGTH_SHORT).show()
                                val comment:String = reviewInfo.getValue("comments").toString()
                                review_comments.setText(comment)

                                val ratingInt = (reviewInfo.getValue("rating"))
                                seekBar.progress = ratingInt.toString().toDouble().roundToInt()

                                overall_rating.text = (reviewInfo.getValue("rating")).toString()
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
