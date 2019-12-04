package com.example.cribb

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.cribb.data.App
import kotlinx.android.synthetic.main.activity_admin_view.*

class AdminView : AppCompatActivity(), ListingAdminFragment.OnFragmentInteractionListener,
    ReportAdminFragment.OnFragmentInteractionListener, IPAddressFragment.OnFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view)

        (application as App).preferenceRepository
            .nightModeLive.observe(this, Observer { nightMode ->
            nightMode?.let { delegate.localNightMode = it }
        }
        )

        viewP3.adapter = SimplePagerAdapter(supportFragmentManager)

        viewP3.currentItem = 0
        viewP3.setPageTransformer(true, ZoomOutPageTransformer())
    }

    override fun onFragmentInteraction(uri: Uri) {
        // save some data from the fragment...
        // other business logic...
    }

    class SimplePagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager)
        : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
        override fun getItem(p0: Int): Fragment {
            Log.i("SimplePagerAdapter"
                , "item index $p0" )
            when (p0){
                0 -> {
                    return ListingAdminFragment()
                }
                1 -> {
                    return ReportAdminFragment()
                }
                2 -> {
                    return IPAddressFragment()
                }
            }
            return ListingAdminFragment()
        }
        override fun getCount(): Int {
            return 3
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }
}
