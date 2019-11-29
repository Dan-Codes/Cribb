package com.example.cribb

import android.content.ContentValues.TAG
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
import kotlin.math.round
import kotlin.math.roundToLong


class ProfileFragment: Fragment() {
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

        preferenceRepository.isDarkThemeLive.observe(activity!!, Observer { isDarkTheme ->
            isDarkTheme?.let { darkThemeSwitch.isChecked = it }
        })

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            preferenceRepository.isDarkTheme = checked
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val nextAction = ProfileFragmentDirections.actionProfileFragmentToLoginActivity()
            Navigation.findNavController(it).navigate(nextAction)
        }
        adminButton.setOnClickListener {
            val nextAction = ProfileFragmentDirections.actionProfileFragmentToAdminView()
            Navigation.findNavController(it).navigate(nextAction)
        }
        pullReviews()
    }

    private fun pullReviews() {
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()

        var arrayList: ArrayList<Listing> = ArrayList()
//        var property = Listing()
//        property.address = "404 University Ave."
//        property.comment = "this is a test"
//        property.rating = 5.0F
        val docRef = db.collection("Users").document(user)
        docRef.get()
            .addOnSuccessListener { document ->

                    Log.d(TAG, "${document.id} => ${document.data}")
                if (document.data != null) {

                    val review = document.get("Review History") as HashMap<String, String>

                    for (address in review) {
                        var property = Listing()
                        val reviewMap = review
                        Log.d("address", "$address")
                        val reviewInfo: HashMap<String, Any> =
                            reviewMap.getValue(address.key) as HashMap<String, Any>
                        property.address = address.key
                        property.comment = reviewInfo.getValue("comments").toString()
                        val rating = reviewInfo.getValue("rating").toString()
                        property.rating = rating.toFloat()
                        property.amenititesRating =
                            reviewInfo.getValue("amenitiesRating").toString()
                        property.locationRating = reviewInfo.getValue("locationRating").toString()
                        property.managementRating =
                            reviewInfo.getValue("managementRating").toString()
                        property.timeStamp = reviewInfo.getValue("timeStamp").toString()

                        arrayList.add(property)
                    }
                        //sharedProp = arrayList
                        //listing_list.layoutManager = LinearLayoutManager(this.requireContext()
                        cardView.layoutManager = LinearLayoutManager(this.requireContext())
                        cardView.adapter = ReviewListAdapter(arrayList, this.requireContext())
                    }

            }
    }
    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}