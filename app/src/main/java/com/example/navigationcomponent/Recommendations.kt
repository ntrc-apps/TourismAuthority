package com.example.navigationcomponent

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.navigationcomponent.custom_classes.TourismSite
import kotlinx.android.synthetic.main.activity_recommendations.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class Recommendations : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendations)


        aquatic.setOnClickListener {
            val intent = Intent(this, Aquatic::class.java)
            startActivity(intent)
        }

        culture.setOnClickListener {
            val intent = Intent(this, Culture::class.java)
            startActivity(intent)
        }

        hikes.setOnClickListener {
            val intent = Intent(this, Hikes::class.java)
            startActivity(intent)
        }

        leisure.setOnClickListener {
            val intent = Intent(this, Leisure::class.java)
            startActivity(intent)
        }

    }
}