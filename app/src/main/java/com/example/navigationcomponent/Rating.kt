package com.example.navigationcomponent

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Rating : AppCompatActivity() {
    lateinit var rateCount: TextView
    lateinit var showRating: TextView
    lateinit var review: EditText
    lateinit var submit: Button
    private lateinit var ratingBar: RatingBar
    var rateValue = 0f
    var temp: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        rateCount = findViewById(R.id.rateCount)
        ratingBar = findViewById(R.id.ratingBar)
        review = findViewById(R.id.review)
        submit = findViewById(R.id.submitBtn)
        showRating = findViewById(R.id.showRating)
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            rateValue = ratingBar.rating
            if (rateValue <= 1 && rateValue > 0) rateCount.text = "Bad $rateValue/5" else if (rateValue <= 2 && rateValue > 1) rateCount.text =
                    "Okay $rateValue/5" else if (rateValue <= 3 && rateValue > 2) rateCount.text = "Good $rateValue/5" else if (rateValue <= 4 && rateValue > 3) rateCount.text =
                    "Very Good $rateValue/5" else if (rateValue <= 5 && rateValue > 4) rateCount.text =
                    "Best $rateValue/5"
        }
        submit.setOnClickListener {
            temp = rateCount.text.toString()
            showRating.text = """ Your Rating:
            $temp
            ${review.getText()}
            """.trimIndent()
            review.setText("")
            ratingBar.rating = 0f
            rateCount.text = ""
        }
    }
}