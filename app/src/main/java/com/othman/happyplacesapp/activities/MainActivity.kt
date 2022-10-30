package com.othman.happyplacesapp.activities



import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.othman.happyplacesapp.adapters.HappyPlaceAdapter
import com.othman.happyplacesapp.adapters.HappyPlacesAdapter
import com.othman.happyplacesapp.database.DatabaseHandler
import com.othman.happyplacesapp.databinding.ActivityMainBinding
import com.othman.happyplacesapp.models.HappyPlaceModel
import com.othman.happyplacesapp.utils.SwipeToDeleteCallback
import com.othman.happyplacesapp.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var placesAdapter: HappyPlaceAdapter

    private val openAddHappyPlaceActivity: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK ) {
                getHappyPlacesListFromLocalDB()
            }else {
                Log.e("Activity","canceled or back pressed")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(tbMain)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        tbMain.setNavigationOnClickListener {
            onBackPressed()
        }

        fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            //startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            openAddHappyPlaceActivity.launch(intent)
        }

        placesSearch.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon
        ).setColorFilter(Color.WHITE)
        placesSearch.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn
        ).setColorFilter(Color.WHITE)
        placesSearch.findViewById<TextView>(androidx.appcompat.R.id.search_src_text
        ).setTextColor(Color.WHITE)

        getHappyPlacesListFromLocalDB()
    }

    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlacesList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()
        if (getHappyPlacesList.size > 0) {
            Log.e("hpm","get")
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        } else {
            rvHappyPlacesList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }
    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {


        rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)
        Log.e("hpm","set")

        placesAdapter = HappyPlaceAdapter(this, happyPlacesList)
        rvHappyPlacesList.adapter = placesAdapter

        placesSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                placesAdapter.filter.filter(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                placesAdapter.filter.filter(newText)
                return false
            }
        })
        placesSearch.setOnQueryTextFocusChangeListener { _,IsFocused ->
            if (IsFocused){
                fabAddHappyPlace.visibility = View.GONE
            } else{
                fabAddHappyPlace.visibility = View.VISIBLE
            }
        }

        placesAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{
            override fun onClick(position: Int,model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity,HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })

        val editSwipeToEdit = object: SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(openAddHappyPlaceActivity,viewHolder.adapterPosition)
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeToEdit)
        editItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)

        val deleteSwipeToDelete = object: SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as HappyPlaceAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeToDelete)
        deleteItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)
    }


    companion object {
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        internal const val EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}