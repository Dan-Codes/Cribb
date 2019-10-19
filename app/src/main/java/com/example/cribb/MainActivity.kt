package com.example.cribb

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.internal.it
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
val db = FirebaseFirestore.getInstance()
lateinit var navController: NavController
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        //setupBottomNavMenu(navController)
        //setupSideNavigationMenu(navController)
        //setupActionBar(navController)
        //actionBar.setDisplayHomeAsUpEnabled(true)
        NavigationUI.setupActionBarWithNavController(this,navController)

        bottom_nav.setOnNavigationItemSelectedListener{ item ->
            var fragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home -> {fragment = CreateListingFragment()}
                R.id.nav_loc -> {fragment = MapFragment()}
                R.id.nav_person -> fragment = ProfileFragment()
                R.id.home -> {
                    onBackPressed()
                }

            }
            item.isEnabled = true
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                .replace(R.id.nav_host_fragment, fragment!!)
                .addToBackStack(fragment.tag)
                .commit()
            return@setOnNavigationItemSelectedListener true
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        Log.d("test", "pressed")
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
    private fun setupBottomNavMenu(navController: NavController) {
        bottom_nav?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupSideNavigationMenu(navController: NavController) {
        nav_view?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupActionBar(navController: NavController) {
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId)
//        {
//            R.id.nav_home -> {
//                item.isEnabled = true
//                println("pressed!")
//            }
//            R.id.nav_person ->{
//                item.isEnabled = true
//            }
//            R.id.nav_loc -> {
//                item.isEnabled = true
//            }
//        }
//        return true
//    }

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }

    }
}
