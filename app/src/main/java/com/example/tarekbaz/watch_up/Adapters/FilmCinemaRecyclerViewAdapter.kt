package com.example.tarekbaz.watch_up.Adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.tarekbaz.watch_up.FilmDetailActivity
import com.example.tarekbaz.watch_up.Models.Movie
import com.example.tarekbaz.watch_up.R

class FilmCinemaRecyclerViewAdapter(private val mContext: Context, var films : List<Movie>) : RecyclerView.Adapter<FilmCinemaRecyclerViewAdapter.ViewHolder>() {

    var fullFilms = films

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.film_image.setImageResource(films.get(position).image)
            holder.film_name.text = films.get(position).title
//            holder.film_realisator.setText(films.get(position))//todo
//            holder.film_salle.setText(films.get(position))//todo
//            var formatter: DateFormat = SimpleDateFormat("HH:mm")
//            holder.film_date.setText(formatter.format(times!!.get(position)))

            holder.itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {

                    val intent = Intent(mContext, FilmDetailActivity::class.java)
                    intent.putExtra("index", position)
                    ContextCompat.startActivity(mContext, intent, null)

                }
            })
        }

        override fun getItemCount(): Int {
            return this.films.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var film_name: TextView
            internal var film_realisator: TextView
            internal var film_salle: TextView
            internal var film_date: TextView
            internal var film_image: ImageView

            init {
                film_name = itemView.findViewById(R.id.film_name)
                film_realisator = itemView.findViewById(R.id.film_realisator)
                film_salle = itemView.findViewById(R.id.film_salle)
                film_date = itemView.findViewById(R.id.film_date)
                film_image = itemView.findViewById(R.id.film_image)
            }
        }

    fun filter(keyWords: String) {
        films = fullFilms.filter { film -> film.title.contains(keyWords, true) }
        notifyDataSetChanged()
    }
}