package com.example.navigationcomponent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_info.*


class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

val button1 = findViewById<Button>(R.id.button_review)
        button1.setOnClickListener {
            val intent = Intent(this, Rating::class.java)
            startActivity(intent)
        }

        buttonWeb.setOnClickListener {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://fantaseatours.com/")
            startActivity(openURL)
        }

        val image = intent.getParcelableExtra<TourDetails>(TourRecycler.INTENT_PARCELABLE)

        val imgSrc = findViewById<ImageView>(R.id._image)
        val imgTitle = findViewById<TextView>(R.id.tour_name)
        val imgDescription = findViewById<TextView>(R.id.tour_desc)


        if (image != null) {
            imgSrc.setImageResource(image.imageSrc)
        }
        if (image != null) {
            imgTitle.text = image.imageTitle
        }
        if (image != null) {
            imgDescription.text = image.imageDesc
        }

    }
    fun Call (view: View) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + "1-784-457-4477 ")
        startActivity(dialIntent)
    }


}








