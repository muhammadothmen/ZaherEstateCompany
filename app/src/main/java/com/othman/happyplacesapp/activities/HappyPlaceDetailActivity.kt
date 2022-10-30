package com.othman.happyplacesapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.othman.happyplacesapp.R
import com.othman.happyplacesapp.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_happy_place_detail.*
import kotlinx.android.synthetic.main.activity_main.*

class HappyPlaceDetailActivity : AppCompatActivity() {
    var model:HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)

        setSupportActionBar(tbPlaceDetail)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        tbPlaceDetail.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            model = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }
        if (model != null) {
            supportActionBar?.title = model?.title
            iv_place_image.setImageURI(Uri.parse(model?.image))
            tv_description.text = model?.description
            tv_location.text = model?.location
        }
        btn_view_on_map.setOnClickListener {
            val intent = Intent(this@HappyPlaceDetailActivity,MapActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,model)
            startActivity(intent)
        }
    }
}