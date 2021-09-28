package com.example.navigationcomponent

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.tourguide_cardview.view.*


class TourRecyclerAdapter(private var toursList: ArrayList<TourDetails>, private val context: Activity) : RecyclerView.Adapter<TourRecyclerAdapter.MyViewHolder>(), Filterable {
    var tourFilterList = ArrayList<TourDetails>()

    init {
        tourFilterList = toursList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.tourguide_cardview,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int{
        return tourFilterList.size
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = tourFilterList[position]


        holder.tourName.text = currentItem.tourName


        holder.layout.setOnClickListener {
            val intent = Intent(context, TourActivity::class.java).apply {
                putExtra("tourguide_id", currentItem.tourId)
            }
            context.startActivity(intent)
        }
        Picasso.get().load(currentItem.tourImage).placeholder(R.drawable.cruise)
            .into(holder.tourImage)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tourImage: ImageView = itemView.findViewById(R.id.image)
        val tourName: TextView = itemView.findViewById(R.id.tour_name)
        val layout: LinearLayout = itemView.findViewById(R.id.layout)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    tourFilterList = toursList
                } else {
                    val resultList = ArrayList<TourDetails>()
                    for (row in toursList) {
                        if (row.tourName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    tourFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = tourFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                tourFilterList = results?.values as ArrayList<TourDetails>
                notifyDataSetChanged()
            }
        }
    }
}
