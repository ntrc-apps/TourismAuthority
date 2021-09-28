package com.example.navigationcomponent.com.example.navigationcomponent

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationcomponent.R
import com.example.navigationcomponent.TourActivity
import com.example.navigationcomponent.TourDetails
import com.squareup.picasso.Picasso



class TaxiRecyclerAdapter  (private var taxiList: ArrayList <TaxiDetails>, private val context: Activity): RecyclerView.Adapter<TaxiRecyclerAdapter.MyViewHolder>(), Filterable {

    var taxiFilterList = ArrayList<TaxiDetails>()

    init {
        taxiFilterList = taxiList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.taxi_cardview,
                parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int{
        return taxiFilterList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = taxiFilterList[position]


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

        val taxiImage: ImageView = itemView.findViewById(R.id.taxis_image)
        val taxiName: TextView = itemView.findViewById(R.id.taxis_name)
        val layout: LinearLayout = itemView.findViewById(R.id.layout)
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    taxiFilterList = taxiList
                } else {
                    val resultList = ArrayList<TaxiDetails>()
                    for (row in taxiList) {
                        if (row.taxiName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    taxiFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = taxiFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                taxiFilterList = results?.values as ArrayList<TaxiDetails>
                notifyDataSetChanged()
            }
        }
    }
}
