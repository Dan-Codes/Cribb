package com.example.cribb


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import android.R.attr.apiKey
import android.R.attr.installLocation
import com.google.android.libraries.places.internal.e
import com.google.android.gms.common.api.ApiException
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import androidx.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.android.gms.tasks.Task
import android.content.pm.PackageManager
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.Arrays.asList




class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    val db = FirebaseFirestore.getInstance()
    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize Places.
        Places.initialize(applicationContext, "AIzaSyDoTyyUjp3Xtdll-X3-zTnORb66-fkCUNE")

// Create a new Places client instance.


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        db.collection("listings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val location :GeoPoint? = document.getGeoPoint("geopoint")
                    val address : String? = document.get("address").toString()
                    mMap.addMarker(MarkerOptions().position(LatLng(location!!.latitude,location!!.longitude)).title(address))
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }


        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun findLocation(){
    }


}
