package com.example.tarekbaz.watch_up.Adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.tarekbaz.watch_up.Config
import com.example.tarekbaz.watch_up.Models.Season
import com.example.tarekbaz.watch_up.R
import com.example.tarekbaz.watch_up.SeasonDetailActivity


class SeasonRecyclerViewAdapter(private val mContext: Context, val seasons: List<Season>,val indexSerie:Int) : RecyclerView.Adapter<SeasonRecyclerViewAdapter.ViewHolder>() {

    var lastPosition=-1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_home_film, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(mContext)
                .load(Config.IMG_BASE_URL + seasons.get(position).poster_path)
                .into(holder.image)

//        holder.name.setText("Saison " + (position + 1) )
        holder.name.text = seasons.get(position).name

        holder.image.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
//                //TODO change it
                val intent = Intent(mContext, SeasonDetailActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("indexSerie", indexSerie)
                startActivity(mContext, intent, null)
            }
        })

        // Animation
        val animation = AnimationUtils.loadAnimation(mContext,
                if (position > lastPosition)
                    R.anim.right_from_left
                else
                    R.anim.left_from_right)
        holder.itemView.startAnimation(animation)
        lastPosition = position
    }

    override fun getItemCount(): Int {
        return seasons.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var image: ImageView
        internal var name: TextView

        init {
            image = itemView.findViewById(R.id.image_card_film)
            name = itemView.findViewById(R.id.name_card_film)
        }
    }

}
