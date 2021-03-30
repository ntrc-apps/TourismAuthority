package com.example.navigationcomponent


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TaxiRecycler : AppCompatActivity() {
    companion object {
        val INTENT_PARCELABLE = "OBJECT_INTENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val imageList = listOf<TourDetails>(
                TourDetails(
                        R.drawable.bb,
                        "Sea Breeze Nature Tours","   We offer a comprehensive coastal tour, paying attention to the history ,flora and fauna of the coast. We frequently see turtles, flying fish and scores of birds , both resident and migratory."

                ),
                TourDetails(
                        R.drawable.scaramouche,
                        "Scaramouche","Have fun"

                ),
                TourDetails(R.drawable.beach,
                        "Marine Tech Services","Have fun"

                ),
                TourDetails(
                        R.drawable.fantasea_tours,
                        "Pirate Tours","Have fun"
                ),
                TourDetails(
                        R.drawable.fantasea_tours,
                        "Volcano Trips","Have fun"
                )
        )

        val recyclerView = findViewById<RecyclerView>(R.id._taxiRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = TourAdapter(this, imageList) {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra(INTENT_PARCELABLE, it)
            startActivity(intent)
        }
    }
}
