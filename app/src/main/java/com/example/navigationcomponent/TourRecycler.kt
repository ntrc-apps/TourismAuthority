package com.example.navigationcomponent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TourRecycler : AppCompatActivity() {
    companion object {
        val INTENT_PARCELABLE = "OBJECT_INTENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour_recycler)

        val imageList = listOf<TourDetails>(
                TourDetails(
                        R.drawable.fantasea_tours,
                        "Fantasea Tours","At Fantasea Tours we have created comfortable, exciting and personalized tours. Our tours take you to the most spectacular places throughout St. Vincent and the Grenadines; our staff’s knowledge of the islands and the waters surrounding them offers the opportunity for top quality tours."

                ),
                TourDetails(
                        R.drawable.hazecotours,
                        "Hazeco Tours","Have fun"

                ),
                TourDetails(
                        R.drawable.coreasnco,
                        "Coreas and Co","Have fun"

                ),
                TourDetails(
                        R.drawable.divestvincent,
                        "Dive St.Vincent","Have fun"
                ),
                TourDetails(
                        R.drawable.treefish,
                        "Tree-Fish Consultancy Ltd.","Have fun"
                )
        )

        val recyclerView = findViewById<RecyclerView>(R.id._imageRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = TourAdapter(this, imageList) {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra(INTENT_PARCELABLE, it)
            startActivity(intent)
        }
    }
}
