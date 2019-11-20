package com.example.cribb

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.cribb.data.App
import com.example.cribb.ui.searchTable
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*
import com.example.cribb.ui.login.LoginActivity
import android.content.Intent
import android.content.pm.PackageManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import androidx.annotation.NonNull
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity




val db = FirebaseFirestore.getInstance()
lateinit var navController: NavController
class Main2Activity : AppCompatActivity(), searchTable.OnFragmentInteractionListener {
    var mAuthListenr : FirebaseAuth.AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        setSupportActionBar(toolbar2)
        navController = findNavController(R.id.nav_host_fragment)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mapFrag, R.id.profileFragment, R.id.createListingFragment
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController)
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        (application as App).preferenceRepository
            .nightModeLive.observe(this, Observer { nightMode ->
            nightMode?.let { delegate.localNightMode = it }
        }
        )

        mAuthListenr = FirebaseAuth.AuthStateListener(object : FirebaseAuth.AuthStateListener, (FirebaseAuth) -> Unit {
            override fun invoke(p1: FirebaseAuth) {
            }

            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = p0.getCurrentUser()
                if (user != null){
                    //do something
                }else{
                    Log.d("FragmentActivity", "Logout")
                    val intent = Intent(this@Main2Activity, LoginActivity::class.java)
                    //startActivity(intent)
                }
            }

        })
    }


    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.search -> {
                val navController = findNavController(R.id.nav_host_fragment)

                navController.navigate(R.id.action_mapFrag_to_searchTable)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>, grantResults: IntArray) {
//        Log.d("mapmap", "when")
//
//        when (requestCode){
//            MapFragment.LOCATION_PERMISSION_REQUEST_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(this,"Permission Granted!", Toast.LENGTH_SHORT).show()
//                    getFragmentManager().beginTransaction().detach(MapFragment).attach(this).commit()
//                }
//                else{
//                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//            else -> {
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//            }
//
//        }
//    }
}
