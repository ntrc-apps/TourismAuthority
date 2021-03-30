package com.example.navigationcomponent


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TourAdapter (
        private val context: Context,
        private val images: List<TourDetails>,
        val listener: (TourDetails) -> Unit
) : RecyclerView.Adapter<TourAdapter.ImageViewHolder>() {
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageSrc = view.findViewById<ImageView>(R.id.tourguide_image)
        val title = view.findViewById<TextView>(R.id.tourguide_name)
        val desc = view.findViewById<TextView>(R.id.tourguide_desc)
        fun bindView(image: TourDetails, listener: (TourDetails) -> Unit) {
            imageSrc.setImageResource(image.imageSrc)
            title.text = image.imageTitle
            desc.text = image.imageDesc
            itemView.setOnClickListener { listener(image) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
            ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.tourguide_cardview, parent, false))

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindView(images[position], listener)
    }
}