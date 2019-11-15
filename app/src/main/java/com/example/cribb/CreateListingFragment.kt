package com.example.cribb

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.type.Date
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*
import java.text.Normalizer
import kotlin.math.abs



class CreateListingFragment : Fragment() {

    private lateinit var geocoder: Geocoder
    private lateinit var geoPoint: GeoPoint
    private lateinit var full_address:String
    private lateinit var formView: View
    private var job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
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
        formView = view
        Log.d("view", "$formView")
        Places.initialize((activity as Main2Activity).applicationContext, getString(R.string.google_api_key))

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

    private fun uploadProperty(): Boolean {
        if (address1.text.isNullOrBlank() || city.text.isNullOrBlank() || state.text.isNullOrBlank() || zipcode.text.isNullOrBlank() || rent.text.isNullOrBlank() || landlord.text.isNullOrBlank()) {
            Toast.makeText(context, "You must include all required address fields", Toast.LENGTH_SHORT).show()
            return false
        }
        var listAddress: List<Address>
        full_address = "${address1.text} ${city.text}, ${state.text} ${zipcode.text}"
        geocoder = Geocoder(context!!)


        listAddress = geocoder.getFromLocationName(full_address, 3)
        if (listAddress == null) {
            Toast.makeText(context!!, "This place does not exist.", Toast.LENGTH_SHORT).show()
            return false
        }
        geoPoint = GeoPoint(listAddress[0].latitude, listAddress[0].longitude)
        Log.d("GeoPoint", "$geoPoint")
        //Log.d("added", "$check")

        val nestedData = HashMap<String,Any?>()
        val docData = hashMapOf(
            "address" to full_address,
            "geopoint" to geoPoint,
            "property" to true,
            "landlordName" to landlord.text.toString(),
            "rent" to rent.text.toString(),
            "addedby" to "dli123@syr.edu",
            "reviews" to nestedData,
            "avgAmenities" to 0.0,
            "avgLocation" to 0.0,
            "avgManage" to 0.0,
            "avgOverallRating" to 0.0
        )

        val docRef = db.collection("listings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val currentGeoPoint = document.get("geopoint") as GeoPoint
                    if (abs(geoPoint.latitude - currentGeoPoint.latitude) < 0.0001 && abs(geoPoint.longitude - currentGeoPoint.longitude) < 0.0001) {
                        Log.d("caught", "already in database")
                        Toast.makeText(
                            context!!,
                            "This place already exists on Cribb",
                            Toast.LENGTH_SHORT
                        ).show()
                        //resultListener.onResult(true)
                        //myCallback(false)
                        return@addOnSuccessListener
                    }
                }
                //myCallback(true)
                Log.d("tag", "address doesn't exist in database, initializing data entry")
                db.collection("listings").document(full_address)
                    .set(docData)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        //val fragment :com.example.cribb.MapFragment = com.example.cribb.MapFragment.newInstance(geoPoint.latitude,geoPoint.longitude)
                        //com.example.cribb.MapFragment.newInstance(geoPoint.latitude,geoPoint.longitude)
                        val navController = findNavController()
                        //navController.
                        var nextAction = CreateListingFragmentDirections.actionCreateListingFragmentToMapFrag()
                        nextAction.lat = geoPoint.latitude.toFloat()
                        nextAction.longitude = geoPoint.longitude.toFloat()
                        navController.navigate(nextAction)
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                    }
            }

        return true
    }
//, myCallback: (Boolean) -> Unit
    private fun checkDidAdd(geoPoint: GeoPoint, fullAddress: String){

        scope.launch {
            val docRef = db.collection("listings")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val currentGeoPoint = document.get("geopoint") as GeoPoint
                        if (abs(geoPoint.latitude - currentGeoPoint.latitude) < 0.0001 && abs(geoPoint.longitude - currentGeoPoint.longitude) < 0.0001) {
                            Log.d("caught", "already in database")
                            Toast.makeText(context!!, "This place already exists on Cribb", Toast.LENGTH_SHORT).show()
                            //resultListener.onResult(true)
                            //myCallback(false)
                            return@addOnSuccessListener
                        }
                    }
                    //myCallback(true)
                    Log.d("tag", "address doesn't exist in database, initializing data entry")



//                var nextAction = CreateListingFragmentDirections.addedProperty(geoPoint.latitude.toString(),geoPoint.longitude.toString())
//                Navigation.findNavController(formView).navigate(nextAction)
                val fragment = com.example.cribb.MapFragment.newInstance(geoPoint.latitude,geoPoint.longitude)
                fragmentManager
                    ?.beginTransaction()
                    ?.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                    ?.replace(view!!.id, fragment)
                    ?.commit()


                }.addOnFailureListener { exception ->
                    Log.d("TAG", "Error getting documents: ", exception)
                }
            println("done!!")
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(latitude: Double, longitude: Double) = CreateListingFragment().apply {
            arguments = Bundle().apply {
                putDouble("passing lat", latitude)
                putDouble("passing lng", longitude)
            }
        }
    }
    interface ResultListener {
        fun onResult(isAdded: Boolean)
        fun onError(error: Throwable)
    }
}