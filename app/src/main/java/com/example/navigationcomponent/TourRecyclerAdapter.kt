package com.example.navigationcomponent

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tourguide_cardview.view.*


class TourRecyclerAdapter  (private var tourList: ArrayList <TourDetails>, private val context: Activity): RecyclerView.Adapter<TourRecyclerAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tourguide_cardview,
                parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount() = tourList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = tourList[position]


        holder.tourName.text = currentItem.tourName


        holder.layout.setOnClickListener {
            val intent = Intent(context, TourActivity::class.java).apply {
                putExtra("tourguide_id", currentItem.tourId)
            }
            context.startActivity(intent)
        }
        Picasso.get().load(currentItem.tourImage).placeholder(R.drawable.cruise).into(holder.tourImage)
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tourImage: ImageView = itemView.image
        val tourName: TextView = itemView.tour_name
        val layout: LinearLayout = itemView.layout
    }
}
