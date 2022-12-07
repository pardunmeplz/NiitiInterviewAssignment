package com.example.interviewassignment_niiti


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        title = "Details"
        val data = JSONObject(intent.getStringExtra("data")!!)

        findViewById<TextView>(R.id.detailsTitleTextView).text = data.getString("title")
        findViewById<TextView>(R.id.ratingTextView).text = getString(R.string.ratingTitle,data.getString("imdbRating"))
        findViewById<TextView>(R.id.detailsDateTextView).text = getString(R.string.dateTitle,data.getString("releaseDate"))
        findViewById<TextView>(R.id.storyTextView).text = data.getString("storyline")

        val posterHandler = ImageFromURL(findViewById(R.id.detailsImageView))
        posterHandler.setURL(data.getString("posterurl"),false)

    }
}