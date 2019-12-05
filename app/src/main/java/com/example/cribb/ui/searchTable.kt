package com.example.cribb.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Property
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.Navigation
import com.example.cribb.R
import com.example.cribb.db
import kotlinx.android.synthetic.main.fragment_search_table.*
import kotlinx.android.synthetic.main.listing_item.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [searchTable.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [searchTable.newInstance] factory method to
 * create an instance of this fragment.
 */
class searchTable : Fragment(), SearchView.OnQueryTextListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    class Listing{
        var name:String = ""
        var rating:String = ""
        var price:String = ""
    }

    private var sharedProp:ArrayList<Listing> = ArrayList()
    private var filteredProp:ArrayList<Listing> = ArrayList()

    init {
        addProperty()
    }

    private fun addProperty(){
        var arrayList:ArrayList<Listing> = ArrayList()
        db.collection("listings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //Log.d(TAG, "${document.id} => ${document.data}")

                    var property = Listing()

                    val review = document.get("reviews") as HashMap<String, HashMap<String, Any>>

                    property.name = document.get("address") as String
                    property.price = (document.get("rent") as String)

                    var totalRating = 0.0
                    var reviewCount = 0.0

                    review.forEach { k, v ->
                        var eachReview = v

                        for ((k, v) in eachReview) {
                            if (k == "rating")
                                totalRating += v as Double
                            reviewCount++
                        }
                    }
                    if(document.get("avgOverallRating") == 0.0)
                        property.rating = "Not rated!"
                    else
                        property.rating = (document.get("avgOverallRating")).toString()

                    arrayList.add(property)
                }
                sharedProp = arrayList
                listing_list.layoutManager = LinearLayoutManager(this.requireContext())
                listing_list.adapter = ListingAdapter(sharedProp, this.requireContext())
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_table, container, false)
    }

    override fun onQueryTextSubmit(query: String): Boolean {

        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.d(TAG, newText)
        updateProperty(newText)
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDefault.setOnClickListener {
            if (filteredProp.isEmpty()) {
                sharedProp = ArrayList(sharedProp.sortedWith(compareBy({ it.name })))
                listing_list.adapter = ListingAdapter(sharedProp, this.requireContext())
            }
            else {
                filteredProp = ArrayList(filteredProp.sortedWith(compareBy({ it.name })))
                listing_list.adapter = ListingAdapter(filteredProp, this.requireContext())
            }
        }

        btnRating.setOnClickListener {
            if (filteredProp.isEmpty()) {
                sharedProp = ArrayList(sharedProp.sortedWith(compareBy({ it.rating })))
                listing_list.adapter = ListingAdapter(sharedProp, this.requireContext())
            }
            else {
                filteredProp = ArrayList(filteredProp.sortedWith(compareBy({ it.rating })))
                listing_list.adapter = ListingAdapter(filteredProp, this.requireContext())
            }
        }

        btnPrice.setOnClickListener {
            if (filteredProp.isEmpty()) {
                sharedProp = ArrayList(sharedProp.sortedWith(compareBy({ it.price })))
                listing_list.adapter = ListingAdapter(sharedProp, this.requireContext())
            }
            else {
                filteredProp = ArrayList(filteredProp.sortedWith(compareBy({ it.price })))
                listing_list.adapter = ListingAdapter(filteredProp, this.requireContext())
            }
        }

        val searchView = getView()!!.findViewById<SearchView>(R.id.searchView)
        searchView!!.setOnQueryTextListener(this)

        listing_list.addOnItemClickListener(object: ListingAdapter.OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                var nextAction = searchTableDirections.actionSearchTableToDisplayListingFragment2()
                if (filteredProp.isEmpty()){
                    nextAction.dynamicAddress = sharedProp.get(position).name
                }

                else{
                    nextAction.dynamicAddress = filteredProp.get(position).name
                }
                Navigation.findNavController(searchView).navigate(nextAction)
            }
        })
    }

    private fun updateProperty(searchString: String){
        var searchString = searchString
        searchString = searchString.toLowerCase(Locale.getDefault())

        filteredProp.clear()

        if (searchString.isEmpty()) {
            filteredProp.addAll(sharedProp)
            listing_list.adapter = ListingAdapter(filteredProp, this.requireContext())
        }
        else {
            for (wp in sharedProp){
                if (wp.name.toLowerCase(Locale.getDefault()).contains(searchString)){
                    filteredProp.add(wp)
                }
            }
            listing_list.adapter = ListingAdapter(filteredProp, this.requireContext())
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment searchTable.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            searchTable().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
