package com.example.cribb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cribb.data.App
import com.example.cribb.ui.ListingAdapter
import com.example.cribb.ui.login.LoginActivity
import com.example.cribb.ui.searchTable
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_search_table.*


class ProfileFragment: Fragment() {
    class Listing {

        var address: String = ""
        var rating: String = ""
        var isAnonymous: String = ""
        var comment: String = ""
        var timeStamp: String = ""
        var willLiveAgain: String = ""
        var isEdited: String = ""
        var locationRating = ""
        var amenititesRating = ""
        var managementRating = ""

    }

//    init {
//        pullReviews()
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val darkThemeSwitch: SwitchMaterial = view.findViewById(R.id.dark_theme_switch)
        val preferenceRepository = (requireActivity().application as App).preferenceRepository

        preferenceRepository.isDarkThemeLive.observe(this, Observer { isDarkTheme ->
            isDarkTheme?.let { darkThemeSwitch.isChecked = it }
        })

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            preferenceRepository.isDarkTheme = checked
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //Navigation.findNavController(it).navigate()

        }
        pullReviews()
    }

    private fun pullReviews() {
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()

        var arrayList: ArrayList<Listing> = ArrayList()
        var property = Listing()
        property.address = "404 University Ave."
        property.comment = "this is a test"
//        val docRef = db.collection("listings").document(user)
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    //Log.d(TAG, "${document.id} => ${document.data}")
//
//                    var property = Listing()
//
//
//                    val review = document.get("Review History") as HashMap<String, *>
//
//                    for (reviewer in review){
//                        val reviewMap = review
//                        val reviewInfo:HashMap<String,String>  = reviewMap.getValue(reviewer.key) as HashMap<String, String>
//                        property.address = reviewer.toString()
//                        property.comment = reviewInfo.getValue("comments").toString()
//                        property.rating = reviewInfo.getValue("avgOverallRating").toString()
//                        property.amenititesRating = reviewInfo.getValue("avgAmenities").toString()
//                        property.locationRating = reviewInfo.getValue("avgLocation").toString()
//                        property.managementRating = reviewInfo.getValue("avgManage").toString()
//                        property.timeStamp = reviewInfo.getValue("timeStamp").toString()
//
//                    }
//
//                    var totalRating = 0.0
//                    var reviewCount = 0.0
//
//                    review.forEach { k, v ->
//                        var eachReview = v
//
//                        for ((k, v) in eachReview) {
//                            if (k == "rating")
//                                totalRating += v as Double
//                            reviewCount++
//                        }
//                    }
//                    if(document.get("avgOverallRating") == 0.0)
//                        property.rating = "Not rated!"
//                    else
//                        property.rating = (document.get("avgOverallRating")).toString()

        arrayList.add(property)

    //sharedProp = arrayList
    //listing_list.layoutManager = LinearLayoutManager(this.requireContext()
        cardView.layoutManager = LinearLayoutManager(this.requireContext())
        cardView.adapter = ReviewListAdapter(arrayList, this.requireContext())
    }
}