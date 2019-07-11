package com.example.cribb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_listing.*
import com.google.android.libraries.places.api.Places
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import kotlin.math.abs


class CreateListingFragment : Fragment() {

    private lateinit var geocoder: Geocoder
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Places.
        Places.initialize((activity as MainActivity).applicationContext, getString(R.string.google_api_key))

// Create a new Places client instance.
        val placesClient = Places.createClient(context!!)
//        val autocompleteFragment = ((activity as MainActivity).supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as? AutocompleteSupportFragment?)
//// Specify the types of place data to return.
//        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
//
//// Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onError(p0: Status) {
//
//            }
//
//            override fun onPlaceSelected(place: Place) {
//                Log.i(TAG, "Place: " + place.name + ", " + place.id)
//            }
//
//        })
        submit.setOnClickListener {
            uploadProperty()
        }
    }

    private fun uploadProperty() {
        if (address1.text.isNullOrBlank() || city.text.isNullOrBlank() || state.text.isNullOrBlank() || zipcode.text.isNullOrBlank() || rent.text.isNullOrBlank() || landlord.text.isNullOrBlank()) {
            Toast.makeText(context, "You must include all required address fields", Toast.LENGTH_SHORT).show()
            return
        }
        var listAddress: List<Address>
        val full_address = "${address1.text} ${city.text}, ${state.text} ${zipcode.text}"
        geocoder = Geocoder(context!!)


        listAddress = geocoder.getFromLocationName(full_address, 3)
        if (listAddress == null) {
            Toast.makeText(context!!, "This place does not exist.", Toast.LENGTH_SHORT).show()
            return
        }
        val geoPoint: GeoPoint = GeoPoint(listAddress[0].latitude, listAddress[0].longitude)
        Log.d("GeoPoint", "$geoPoint")
        checkDidAdd(geoPoint)
        //Log.d("added", "$check")
    }

    private fun checkDidAdd(geoPoint: GeoPoint):Boolean = runBlocking {
        var added = false

        val wait = async {
            db.collection("listings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val currentGeoPoint = document.get("geopoint") as GeoPoint
                    println("test point")
                    if (abs(geoPoint.latitude - currentGeoPoint.latitude) < 0.0001 && abs(geoPoint.longitude - currentGeoPoint.longitude) < 0.0001) {
                        added = true
                        Log.d("caught", "already in database")
                        break
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        }.join()

        println("done!!")
        return@runBlocking added
    }
}