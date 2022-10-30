package com.othman.happyplacesapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.othman.happyplacesapp.R
import com.othman.happyplacesapp.activities.AddHappyPlaceActivity
import com.othman.happyplacesapp.activities.MainActivity
import com.othman.happyplacesapp.database.DatabaseHandler
import com.othman.happyplacesapp.models.HappyPlaceModel
import kotlinx.android.synthetic.main.item_happy_place.view.*
import java.util.*
import kotlin.collections.ArrayList

open class HappyPlaceAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var onClickListener: OnClickListener? = null
    var placesFilterList = ArrayList<HappyPlaceModel>()
    init {
        placesFilterList = list
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)





    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_place,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = placesFilterList[position]

        if (holder is MyViewHolder) {
            holder.itemView.iv_place_image.setImageURI(Uri.parse(model.image))
            holder.itemView.tvTitle.text = model.title
            holder.itemView.tvDescription.text = model.description

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return placesFilterList.size
    }

    /**
     * A function to edit the added happy place detail and pass the existing details through intent.
     */
    fun notifyEditItem(activityLauncher: ActivityResultLauncher<Intent>, position: Int) {
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, placesFilterList[position])
        //activity.startActivityForResult(intent, requestCod) // Activity is started with requestCode
        activityLauncher.launch(intent)
        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }

    /**
     * A function to delete the added happy place detail from the local storage.
     */
    fun removeAt(position: Int) {

        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deleteHappyPlace(placesFilterList[position])

        if (isDeleted > 0) {
            Log.e("mph","isdeleted")
            val model = placesFilterList[position]
            list.remove(model)
            placesFilterList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * A function to bind the onclickListener.
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    placesFilterList = list
                } else {
                    val resultList = ArrayList<HappyPlaceModel>()
                    for (row in list){
                        if (row.title?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.description?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.location?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                        ){
                            resultList.add(row)
                        }
                    }
                    placesFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = placesFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                placesFilterList = results?.values as ArrayList<HappyPlaceModel>
                Log.e("mph","puplish")
                notifyDataSetChanged()
            }

        }
    }
}