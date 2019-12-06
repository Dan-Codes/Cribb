package com.example.cribb

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cribb.data.App
import com.example.cribb.ui.ListingAdapter
import com.example.cribb.ui.login.LoginActivity
import com.example.cribb.ui.searchTable
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_search_table.*
import java.sql.Timestamp
import java.text.FieldPosition
import kotlin.math.round
import kotlin.math.roundToLong


class ProfileFragment: Fragment() {
    class Listing {

        var address: String = ""
        var rating: Float = 0F
        var isAnonymous: String = ""
        var comment: String = ""
        var timeStamp: String = ""
        var willLiveAgain: String = ""
        var isEdited: String = ""
        var locationRating = ""
        var amenititesRating = ""
        var managementRating = ""

    }
    private var arrayList:ArrayList<Listing> = ArrayList()

    private var swipeBackground:ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
    private lateinit var deleteIcon:Drawable
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser!!
        user?.let {
            // Name, email address, and profile photo Url
            val fullName = user.displayName
            name.text = fullName
            Log.d("checkfor", "$fullName")
        }

        val circularImageView = view.findViewById<CircularImageView>(R.id.circularImageView)
        circularImageView.setImageResource(R.mipmap.ic_launcher)
        val darkThemeSwitch: SwitchMaterial = view.findViewById(R.id.dark_theme_switch)
        val preferenceRepository = (requireActivity().application as App).preferenceRepository


        preferenceRepository.isDarkThemeLive.observe(activity!!, Observer { isDarkTheme ->
            isDarkTheme?.let { darkThemeSwitch.isChecked = it }
        })

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            preferenceRepository.isDarkTheme = checked
        }

        checkAdmin()

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val nextAction = ProfileFragmentDirections.actionProfileFragmentToLoginActivity()
            Navigation.findNavController(it).navigate(nextAction)
            activity!!.finish()
        }
        adminButton.setOnClickListener {
            val nextAction = ProfileFragmentDirections.actionProfileFragmentToAdminView()
            Navigation.findNavController(it).navigate(nextAction)
        }

        cardView.adapter = ReviewListAdapter(arrayList, this.requireContext())
        cardView.layoutManager = LinearLayoutManager(this.requireContext())

        deleteIcon = ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_delete)!!

        pullReviews()

        val itemTouchHelperCallback  = object:ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                 return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position : Int) {
                (cardView.adapter as ReviewListAdapter).removeItem(viewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                Toast.makeText(context!!, "Swipe to the end will delete this review!", Toast.LENGTH_SHORT).show()
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2


                swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                deleteIcon.setBounds(itemView.right - iconMargin - deleteIcon.intrinsicWidth, itemView.top + iconMargin,
                    itemView.right - iconMargin, itemView.bottom - iconMargin)

                swipeBackground.draw(c)
                c.save()
                c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom )

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(cardView)
    }

    private fun pullReviews() {
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()

        val docRef = db.collection("Users").document(user)
        docRef.get()
            .addOnSuccessListener { document ->

                    Log.d(TAG, "${document.id} => ${document.data}")
                val firstName = document.get("First Name").toString()
                val lastName = document.get("Last Name").toString().first()
                val fullName = "$firstName $lastName."
                name.text = fullName
                val review = document.get("Review History") as HashMap<String, String>?
                if (document.data != null && review != null) {

                    for (address in review) {
                        var property = Listing()
                        val reviewMap = review
                        Log.d("address", "$address")
                        val reviewInfo: HashMap<String, Any> =
                            reviewMap.getValue(address.key) as HashMap<String, Any>
                        property.address = address.key
                        property.comment = reviewInfo.getValue("comments").toString()
                        val rating = reviewInfo.getValue("rating").toString()
                        property.rating = rating.toFloat()
                        property.amenititesRating =
                            reviewInfo.getValue("amenitiesRating").toString()
                        property.locationRating = reviewInfo.getValue("locationRating").toString()
                        property.managementRating =
                            reviewInfo.getValue("managementRating").toString()
                        var timeStamp = reviewInfo.getValue("timeStamp") as com.google.firebase.Timestamp
                        timeStamp.toDate()
                        property.timeStamp = timeStamp.toDate().toString()

                        arrayList.add(property)
                    }
                    cardView.adapter = ReviewListAdapter(arrayList, this.requireContext())
                }
                else {
                    reviews.text = "You Have No Reviews"
                }

            }
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    private fun checkAdmin(){
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()

        db.collection("Users").document(user)
            .get()
            .addOnSuccessListener { document ->
                val adminStatus = document.get("Admin") as Boolean

                if (adminStatus) adminButton.visibility = View.VISIBLE
            }
    }


}