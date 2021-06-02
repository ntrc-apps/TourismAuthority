package com.example.navigationcomponent.com.example.navigationcomponent

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationcomponent.R
import com.example.navigationcomponent.TourActivity
import com.example.navigationcomponent.TourDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tourguide_cardview.view.*


class TaxiRecyclerAdapter  (private var taxiList: ArrayList <TaxiDetails>, private val context: Activity): RecyclerView.Adapter<TaxiRecyclerAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.taxi_cardview,
                parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount() = taxiList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = taxiList[position]


        holder.taxiName.text = currentItem.taxiName


        holder.layout.setOnClickListener {
            val intent = Intent(context, TaxiActivity::class.java).apply {
                putExtra("taxi_id", currentItem.taxiId)
            }
            context.startActivity(intent)
        }
        Picasso.get().load(currentItem.taxiImage).placeholder(R.drawable.cruise).into(holder.taxiImage)
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taxiImage: ImageView = itemView.image
        val taxiName: TextView = itemView.tour_name
        val layout: LinearLayout = itemView.layout
    }
}
