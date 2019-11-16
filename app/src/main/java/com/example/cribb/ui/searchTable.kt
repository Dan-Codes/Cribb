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
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.cribb.R
import com.example.cribb.db
import kotlinx.android.synthetic.main.fragment_search_table.*
import kotlinx.android.synthetic.main.listing_item.*
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
class searchTable : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    class Listing{

        var name:String = ""
        var rating:String = ""
        var price:String = ""
    }

    var emptyList = Listing()
    var sharedProp:ArrayList<Listing> = ArrayList()

    private var properties:ArrayList<Listing> = ArrayList()

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
                sharedProp.add(emptyList)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addProperty()




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
