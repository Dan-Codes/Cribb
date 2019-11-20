package com.example.cribb


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.example.cribb.R.*
import com.example.cribb.ui.searchTable
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
//import com.google.android.libraries.places.internal.it
import com.google.firebase.firestore.Transaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_display_listing.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
//import com.google.android.libraries.places.internal.i
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_map.*
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.common.api.ApiException as ApiException



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var predictionList: List<AutocompletePrediction>
    private lateinit var mLastKnownLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var mapView: View
    private lateinit var mMapView: MapView
    private lateinit var materialSearchBar: MaterialSearchBar
    private lateinit var transaction : FragmentManager
    private var suggestionList : ArrayList<String> = arrayListOf()
    private var lat:Double = 0.0
    private var lng:Double = 0.0
    private var geoPoint_passed :GeoPoint = GeoPoint(0.0,0.0)
    private var permission:Boolean = true
    private var param1: Double? = null
    private var param2: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val safeArgs = MapFragmentArgs.fromBundle(it)
            lat = safeArgs.lat.toDouble()
            lng = safeArgs.longitude.toDouble()
            param1 = it.getDouble(ARG_PARAM1)
            param2 = it.getDouble(ARG_PARAM2)
        }
        setHasOptionsMenu(true)


        Log.d("geoPoints", "$param1")
        setUpMap()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        var searchIt:MenuItem = menu.findItem(R.id.search)
        searchIt.setVisible(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mapView = inflater.inflate(layout.fragment_map, container, false)

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
//            arguments?.let {
//                if (!it.isEmpty) {
//                val safeArgs = MapFragmentArgs.fromBundle(it)
//                var getlat = "${safeArgs.lat}"
//                var getlng = "${safeArgs.lng}"
//                geoPoint_passed = GeoPoint(getlat.toDouble(),getlng.toDouble())
//                }
//            }
        arguments?.getDouble("passing lat")?.let {
            Log.d("debug", "$it")
            if (it != 0.0)
            lat = it
        }
        arguments?.getDouble("passing lng")?.let {
            if (it != 0.0)
                lng = it
        }

    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
          try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context, R.raw.maps_style_json))

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (e: Resources.NotFoundException ) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        mMap = googleMap

        db.collection("listings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val location: GeoPoint? = document.getGeoPoint("geopoint")
                    val address: String? = document.get("address").toString()
                    val avgLocation = document.getDouble("avgLocation")
                    var avgManage = document.getDouble("avgManage")
                    var avgAmenities = document.getDouble("avgAmenities")
                    var overallRating= 0.0
                    val review = document.get("reviews") as HashMap<String, *>
                    var countReviews = 0

                    val marker = mMap.addMarker(
                        MarkerOptions().position(LatLng(location!!.latitude, location.longitude)).title(
                            address
                        )
                    )
                    //(abs(lat - ThirdState.shared.varLat) < 0.000001 || abs(long - ThirdState.shared.varLong) < 0.000001)
                    //if ((location!!.latitude - lat) < 0.000001 && (location!!.longitude - lng) < 0.000001){
                    if (location!!.latitude.toFloat() == lat.toFloat() && location!!.longitude.toFloat() == lng.toFloat()){
                        marker.showInfoWindow()
                        Log.d("info window", "$marker")
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

        if (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    Log.d("location", "$location")
                    var current_location = LatLng(location!!.latitude,location!!.longitude)

                    val syracuse = LatLng(43.038710, -76.134265)


                    if (lat != 0.0) {
                        val location = LatLng(lat,lng)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15.0f))
                    }
                    else
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(syracuse, 15.0f))
                }

            //setUpMap() //checks if location is enabled and requests users permission
            mMap.isMyLocationEnabled = true //creates a blue location dot and location button
        }


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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode){
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context,"Permission Granted!", Toast.LENGTH_SHORT).show()
                    permission = false
                }
                else{
                    Toast.makeText(context,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }

        }
    }

    @Override
    override fun onInfoWindowClick(marker: Marker) {
        var address: String = marker.title
        val nextaction = MapFragmentDirections.actionMapFragToDisplayListingFragment2()
        nextaction.dynamicAddress = address
        val fragment = DisplayListingFragment()
        Navigation.findNavController(mapView).navigate(nextaction)
//        fragmentManager
//            ?.beginTransaction()
//            ?.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
//            ?.replace(R.id.nav_host_fragment, fragment!!)
//            ?.addToBackStack(fragment.tag)
//            ?.commit()
        println("finished clicking")

    }



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        @JvmStatic
        fun newInstance(param1: Double, param2: Double) =
            com.example.cribb.MapFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_PARAM1, param1)
                    putDouble(ARG_PARAM2, param2)
                }
            }
    }
}
