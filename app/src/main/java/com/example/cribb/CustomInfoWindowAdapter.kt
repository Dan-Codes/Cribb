package com.example.cribb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(val context: Context):GoogleMap.InfoWindowAdapter{
    private lateinit var mWindow:View
    private lateinit var mContext: Context

    fun CustomInfoWindowAdapter(context: Context){
        mContext = context
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)
    }

    private fun renderWindowText(marker: Marker, view: View){
        val address:TextView = view.findViewById(R.id.infoAddress)
        val getAdd:String? = marker.title
        if (getAdd!!.isNotEmpty()) address.text = getAdd

        val ratingBar:RatingBar = view.findViewById(R.id.infoRatingBar)
        val getSnipp:Float = marker.snippet.toFloat()
        ratingBar.rating = getSnipp
        val snipp:TextView = view.findViewById(R.id.infoSnippet)

    }


    override fun getInfoContents(p0: Marker?): View {
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)
        renderWindowText(p0!!,mWindow)
        return mWindow
    }

    override fun getInfoWindow(p0: Marker?): View {
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)
        renderWindowText(p0!!, mWindow)
        return mWindow
    }

}
