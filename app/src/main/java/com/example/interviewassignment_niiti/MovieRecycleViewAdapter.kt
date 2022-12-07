package com.example.interviewassignment_niiti

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class MovieRecycleViewAdapter(private val context:Context, private val dataList:JSONArray) : RecyclerView.Adapter<MovieRecycleViewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater:LayoutInflater = LayoutInflater.from(context)
        val view:View = inflater.inflate(R.layout.list_item,parent,false)
        return MyViewHolder(view,context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data: JSONObject = dataList[position] as JSONObject
            holder.cardData = data
            holder.dateView.text = data.getString("releaseDate")
            holder.titleView.text = data.getString("title")
            holder.posterHandler.setURL(data.getString("posterurl"))
    }

    override fun getItemCount(): Int {
        return dataList.length()
    }

    class MyViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var cardData: JSONObject? = null
        val posterHandler : ImageFromURL = ImageFromURL(itemView.findViewById(R.id.posterImageView))
        val dateView : TextView = itemView.findViewById(R.id.dateTextView)
        val titleView : TextView = itemView.findViewById(R.id.titleTextView)
        init {
            val cardView : CardView = itemView.findViewById(R.id.itemCardView)
            cardView.setOnClickListener {
                if (cardData == null ){return@setOnClickListener}
                val displayIntent : Intent = Intent(context,DetailsActivity().javaClass)
                displayIntent.putExtra("data", cardData.toString())
                context.startActivity(displayIntent)
            }
        }
    }
}