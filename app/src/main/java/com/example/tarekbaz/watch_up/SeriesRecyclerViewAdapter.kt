package com.example.tarekbaz.watch_up

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.tarekbaz.watch_up.Models.Serie

class SeriesRecyclerViewAdapter(private val mContext: Context, var series : List<Serie>) : RecyclerView.Adapter<SeriesRecyclerViewAdapter.ViewHolder>() {

    val fullSerie = series


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.serie_image.setImageResource(series.get(position).image)
        holder.serie_name.text = series.get(position).title
        holder.serie_realisator.text = ""//todo

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val intent = Intent(mContext, SerieDetailActivity::class.java)
                intent.putExtra("index",position+1)
                startActivity(mContext, intent, null)
            }
        })
    }

    override fun getItemCount(): Int {
        return series.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var serie_name: TextView
        internal var serie_realisator: TextView
        internal var serie_image: ImageView

        init {
            serie_name = itemView.findViewById(R.id.film_name)
            serie_realisator = itemView.findViewById(R.id.film_realisator)
            serie_image = itemView.findViewById(R.id.film_image)
        }
    }

    fun filter(keyWords: String) {
        series = fullSerie.filter { serie -> serie.title.contains(keyWords, true) }
        notifyDataSetChanged()
    }
}