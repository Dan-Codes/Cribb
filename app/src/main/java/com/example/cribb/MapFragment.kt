package com.example.cribb

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.mancj.materialsearchbar.MaterialSearchBar
import androidx.navigation.Navigation
import androidx.transition.FragmentTransitionSupport
import com.google.android.gms.maps.MapFragment
import com.google.android.libraries.places.internal.it
import com.google.firebase.firestore.Transaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_display_listing.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MapFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
   // private var listener: OnFragmentInteractionListener? = null

    //private val db = FirebaseFirestore.getInstance()
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    private lateinit var predictionList: List<AutocompletePrediction>
    private lateinit var mLastKnownLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var mapView: View
    private lateinit var mMapView: MapView
    private lateinit var materialSearchBar: MaterialSearchBar
    private lateinit var transaction : FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mapView = inflater.inflate(R.layout.fragment_map, container, false)

//        mMapView = ((mapView.findViewById(R.id.maps)))
//        mMapView.onCreate(null)
//        mMapView.onResume()
//        mMapView.getMapAsync(this)
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return mapView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).bottom_nav.menu.getItem(0).isChecked = true
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        db.collection("listings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val location: GeoPoint? = document.getGeoPoint("geopoint")
                    val address: String? = document.get("address").toString()

//                    val review = document.get("reviews") as HashMap<String, *>
//                    println(review)
//
//                    var countReviews = 0
//                    var averageLocation:Double = 0.0
//                    var averageManagement:Double = 0.0
//                    var averageAmenities:Double = 0.0
//                    for (reviewer in review) {
//                        println(reviewer)
//                        val reviewer = reviewer
//                        val reviewMap = review
//
//                        val reviewInfo:HashMap<String,String>  = reviewMap.getValue(reviewer.key) as HashMap<String, String>
//                        val lRating:Double = reviewInfo.getValue("locationRating") as Double
//                        val mRating:Double = reviewInfo.getValue("managementRating") as Double
//                        val aRating:Double = reviewInfo.getValue("amenitiesRating") as Double
//                        if (lRating != null && mRating != null && aRating != null){
//                            countReviews++
//                            averageLocation += lRating
//                            averageManagement += mRating
//                            averageAmenities += aRating
//                        }
//
//                    }
//                    if (averageLocation != 0.0){
//                        averageLocation /= countReviews
//                        averageManagement /= countReviews
//                        averageAmenities /= countReviews
//
//                    }
//                    val avg = db.collection("listings").document(document.id)
//
//                    avg
//                        .update("avgLocation", averageLocation)
//                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
//                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
//
//                    avg
//                        .update("avgManage", averageManagement)
//                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
//                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
//
//                    avg
//                        .update("avgAmenities", averageAmenities)
//                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
//                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

                    mMap.addMarker(
                        MarkerOptions().position(LatLng(location!!.latitude, location.longitude)).title(
                            address
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }


//        // Add a marker in Sydney and move the camera
        val syracuse = LatLng(43.038710, -76.134265)
//        //mMap.addMarker(MarkerOptions().position(syracuse).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(syracuse, 12.0f))
        setUpMap() //checks if location is enabled and requests users permission
        mMap.isMyLocationEnabled = true //creates a blue location dot and location button
//
//        //moves location button to bottom right
        val locationButton =
            (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 150)
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
//            // Got last known location. In some rare situations this can be null.
//            // 3
//            if (location != null) {
//                mLastKnownLocation = location
//                val currentLatLng = LatLng(mLastKnownLocation.latitude, mLastKnownLocation.longitude)
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//            }
//        }

        mMap.setOnInfoWindowClickListener(this)

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )

            return
        }
    }

    @Override
    override fun onInfoWindowClick(marker: Marker) {
//        Toast.makeText(
//            this, "Info window clicked",
//            Toast.LENGTH_SHORT
//        ).show()
        var address: String = marker.title
//        val newFragment = DisplayListingFragment()
//        transaction
//            .beginTransaction()
//            .replace(R.id.container, newFragment)
//            .addToBackStack(null)
//            .commit()
        val nextaction = MapFragmentDirections.actionMapFragmentToDisplayListingFragment()
        nextaction.dynamicAddress = address
        Navigation.findNavController(mapView).navigate(nextaction)
        println("finished clicking")
    }

    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }

//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MapFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MapFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
