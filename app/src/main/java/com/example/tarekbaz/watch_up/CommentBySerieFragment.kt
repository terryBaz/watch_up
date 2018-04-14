package com.example.tarekbaz.watch_up

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CommentBySerieFragment : Fragment() {

    //todo Models
    val userNames: List<String> = mutableListOf(
            "Cinema Paris Salle 0012", "Larousse Cinema", "Cinema des Rois -Marseille-"
    )
    val userImages: List<Int> = mutableListOf(
            R.drawable.film4, R.drawable.film5, R.drawable.serie1, R.drawable.film5
    )

    val commentFor: List<String> = mutableListOf(
            "Le throne de Fer", "LaCasa de Papel", "Breaking Bad", "LaCasa de Papel"
    )

    val comments: List<String> = mutableListOf(
            "Space excelsive", "From mars", "Live from the sea", "Directly from desert"
    )

    val commentDates: List<String> = mutableListOf(
            "7/7 de 8:00 à 23:00", "24/24 sauf samedi de 8:00 à 23:00", "Toujours 10:00 à 23:00", "de 8:00 à 20:00 sauf lundi"
    )

    //TODO USE Models
    val filmNames: List<String> = mutableListOf(
            "La Belle et La Bète", "Hunger Game", "Drone", "Hunger Game 2"
    )

    val imageFilmsUrls: List<Int> = mutableListOf(
             R.drawable.serie1, R.drawable.film4, R.drawable.film5, R.drawable.film5
    )

    val filmDirectors: List<String> = mutableListOf(
            "Le throne de Fer", "LaCasa de Papel", "Breaking Bad", "LaCasa de Papel"
    )

    fun switchToCommentView(){
        this.view!!.findViewById<View>(R.id.search_bar).visibility = View.GONE
        this.view!!.findViewById<View>(R.id.evaluation).visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context)
        val commentRecycler = this.view!!.findViewById<RecyclerView>(R.id.recycler)
        commentRecycler.setLayoutManager(layoutManager)
        val adapter_comments = CommentRecyclerViewAdapter(context, userNames, userImages, comments, commentFor, commentDates)
        commentRecycler.setAdapter(adapter_comments)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_comment_evaluation, container, false)

        val layoutManager = LinearLayoutManager(context)
        val lastRecycler = rootView.findViewById<RecyclerView>(R.id.recycler)
        lastRecycler.setLayoutManager(layoutManager)
        val adapter_series = CommentBySeriesFilterRecyclerViewAdapter(context, filmNames , imageFilmsUrls, filmDirectors)
        lastRecycler.setAdapter(adapter_series)

        return rootView
    }


    companion object {

        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(sectionNumber: Int): CommentBySerieFragment {
            val fragment = CommentBySerieFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}