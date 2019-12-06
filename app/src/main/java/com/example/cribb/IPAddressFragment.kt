package com.example.cribb

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_ipaddress.*
import kotlinx.android.synthetic.main.fragment_search_table.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [IPAddressFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [IPAddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IPAddressFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    class IPAddress{
        var occurrence:Int = 0
        var ipAddress:String = ""
        var ipEamils:ArrayList<String> = ArrayList()
    }

    private var ipAdds:ArrayList<IPAddress> = ArrayList()

    private fun addIPAddress(){

        db.collection("IP")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    var flagedIp = IPAddress()
                    val occurrenceOfThisIp:Int = document.get("occurrences").toString().toInt()
                    if(occurrenceOfThisIp > 1){
                        flagedIp.occurrence = occurrenceOfThisIp
                        flagedIp.ipAddress = document.id
                        val email = document.get("emails") as ArrayList<String>
                        flagedIp.ipEamils = email
                        ipAdds.add(flagedIp)
                    }
                    Log.d("**********", flagedIp.ipAddress)
                }


                IPAddressListing_list.layoutManager = LinearLayoutManager(this.requireContext())
                IPAddressListing_list.adapter = IPAddressAdminAdapter(ipAdds, this.requireContext())
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

//        IPAddressListing_list.addOnItemClickListener(object: IPAddressAdminAdapter.OnItemClickListener {
//            override fun onItemClicked(position: Int, view: View) {
////                var nextAction = searchTableDirections.actionSearchTableToDisplayListingFragment2()
////
////                Navigation.findNavController(searchView).navigate(nextAction)
////                var nextAction = IPAddressFragmentDirections.actionIPAddressFragmentToIPAddressItemFragment()
////
////                nextAction.dynamicIPAddress = ipAdds.get(position).ipAddress
////
////                val navController = findNavController()
////                navController.navigate(nextAction)
//
//                val transaction =
//                transaction.replace(R.id.fragment_layout_id, fragment)
//                transaction.commit()
//            }
//        })
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ipaddress, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addIPAddress()
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
         * @return A new instance of fragment IPAddressFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IPAddressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
