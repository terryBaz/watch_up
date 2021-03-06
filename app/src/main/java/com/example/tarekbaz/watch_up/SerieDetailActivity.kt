package com.example.tarekbaz.watch_up

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.tarekbaz.watch_up.API.Responses.CreditsResponse
import com.example.tarekbaz.watch_up.API.Responses.ListPaginatedResponse
import com.example.tarekbaz.watch_up.API.SerieService
import com.example.tarekbaz.watch_up.Adapters.CommentRecyclerViewAdapter
import com.example.tarekbaz.watch_up.Adapters.HomeSerieRecyclerViewAdapter
import com.example.tarekbaz.watch_up.Adapters.SeasonRecyclerViewAdapter
import com.example.tarekbaz.watch_up.Models.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_detail_serie.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat


class SerieDetailActivity : AppCompatActivity() {

    var is_fan = false

    private fun initSeasonsRecyclerView(seasons: List<Season>, index: Int) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        seasonsRecyclerView.setLayoutManager(layoutManager)
        val adapter_seasons = SeasonRecyclerViewAdapter(this, seasons, index)
        seasonsRecyclerView.setAdapter(adapter_seasons)
    }

    private fun initAssociatedSeriesRecyclerView(assSerie: List<Serie>) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        associatedSeriesRecyclerView.setLayoutManager(layoutManager)
        val adapter_films = HomeSerieRecyclerViewAdapter(this, assSerie)
        associatedSeriesRecyclerView.setAdapter(adapter_films)
    }

    private fun initCommentsRecyclerView(comments: List<Comment>) {
        val layoutManager = LinearLayoutManager(this)
        commentsSerieRecyclerView.setLayoutManager(layoutManager)
        val adapter_films = CommentRecyclerViewAdapter(this, comments)
        commentsSerieRecyclerView.setAdapter(adapter_films)
    }


    //Add search view
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_love, menu)

        //Set fun heart icon for the first time
        if (this.is_fan) {
            this.is_fan = false
            menu.findItem(R.id.love_item).setIcon(R.drawable.heart2)
        } else {
            this.is_fan = true
            menu.findItem(R.id.love_item).setIcon(R.drawable.heart_inactive)
        }
        return true
    }

    var serie: Serie = Store.homeSeries[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_serie)

        val serieId = intent.extras.getInt("_id", 0)

        Store.homeSeries.forEach { it ->
            if (it.id == serieId)
                serie = it
        }


        if(! serie.genre_ids.isEmpty()) {
            serie.genresList = Genre.genresList.get(serie.genre_ids[0])?.name + ""
            for (i in 1 until serie.genre_ids.size) {
                serie.genresList += " / " + Genre.genresList.get(serie.genre_ids[i])?.name
            }
        }

//        //todo fav
//        Mocker.favSerieList.forEach { it ->
//            if (it.id == serie.id)
//                is_fan = true
//        }

        val glide = Glide.with(this)

        glide.load(Config.IMG_BASE_URL + serie.poster_path)
                .into(filmCard)

        glide.load(Config.IMG_BASE_URL + serie.poster_path)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable,
                                                 transition: Transition<in Drawable>?) {
                        frameLayout.setBackground(resource)
                    }
                })

        serieTitle.text = serie.title
        descriptionText.text = serie.discription
        if (serie.first_air_date != null) {
            serieDate.text = "(${SimpleDateFormat("yyyy").format(serie.first_air_date)})"
        }
        evaluationText.text = serie.evaluation.toString()
        serieType.text = serie.genresList

        initDetailSerieDataAPI(serie.id)

        setSupportActionBar(toolbar_detail_serie)
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
            getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        }
        //Set activity title
        toolbar_detail_serie.title = serie.title

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.love_item) {
            if (this.is_fan) {
                this.is_fan = false
                item.setIcon(R.drawable.heart2)
//                Mocker.favSerieList.add(serie)
                Toast.makeText(this, "Série ajoutée à Mes Fans", Toast.LENGTH_SHORT).show()
            } else {
                this.is_fan = true
                item.setIcon(R.drawable.heart_inactive)
//                Mocker.favSerieList.remove(serie)
                Toast.makeText(this, "Série enlevée de Mes Fans", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun initDetailSerieDataAPI(serieId: Int) {

        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
                .baseUrl(Config.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        val service = retrofit.create<SerieService>(SerieService::class.java!!)


        service.serieDetails(serieId).enqueue(object : Callback<Serie> {
            override fun onResponse(call: Call<Serie>, response: retrofit2.Response<Serie>?) {
                if ((response != null) && (response.code() == 200)) {
                    serie.seasons = response.body()!!.seasons
                    serie.episode_run_time = response.body()!!.episode_run_time

                    initSeasonsRecyclerView(serie.seasons, serie.id)
                }
            }

            override fun onFailure(call: Call<Serie>?, t: Throwable?) {
                Toast.makeText(baseContext, "Echec details", Toast.LENGTH_LONG).show()
            }
        })

        service.relatedSeries(serieId).enqueue(object : Callback<ListPaginatedResponse<Serie>> {

            override fun onResponse(call: Call<ListPaginatedResponse<Serie>>, response: retrofit2.Response<ListPaginatedResponse<Serie>>?) {
                if ((response != null) && (response.code() == 200)) {
                    val relatedSeriers = response.body()!!.results
                    if(relatedSeriers.isEmpty())
                        noAssociatedSeries.visibility = TextView.VISIBLE
                    serie.linkedSeries = relatedSeriers
                    relatedSeriers.forEach { it ->
                        Store.homeSeries.add(it)
                    }
                    initAssociatedSeriesRecyclerView(relatedSeriers)
                }

            }

            override fun onFailure(call: Call<ListPaginatedResponse<Serie>>?, t: Throwable?) {
                Toast.makeText(baseContext, "Echec related", Toast.LENGTH_LONG).show()
            }
        })



        service.reviewsSerie(serieId).enqueue(object: Callback<ListPaginatedResponse<Comment>> {
            override fun onResponse(call: Call<ListPaginatedResponse<Comment>>, response: retrofit2.Response<ListPaginatedResponse<Comment>>?) {
                if ((response != null) && (response.code() == 200)) {
                    val comments = response.body()!!.results
                    if (comments.isEmpty()) noComments.visibility = TextView.VISIBLE
                    serie.comments = comments
                    initCommentsRecyclerView(comments)
                }
            }
            override fun onFailure(call: Call<ListPaginatedResponse<Comment>>?, t: Throwable?){
                Toast.makeText(baseContext, "Echec reviews", Toast.LENGTH_LONG).show()
            }
        })


        service.creditsSerie(serieId).enqueue(object : Callback<CreditsResponse> {
            override fun onResponse(call: Call<CreditsResponse>, response: retrofit2.Response<CreditsResponse>?) {
                if ((response != null) && (response.code() == 200)) {
                    val cast = response.body()!!.cast
                    actorsNames.text = "Acune actuers est specifé"
                    if (!cast.isEmpty()) {
                        var end = cast.size

                        serie.actorsList = cast[0].name + ""
                        if (cast.size > 3) end = 3
                        for (i in 1 until end) {
                            serie.actorsList += ", " + cast[i].name
                        }

                        actorsNames.text = serie.actorsList
                    }


                    val crew = response.body()!!.crew
                    crew.forEach {
                        if (it.job == CreditsResponse.DIRECTOR){
                            producerText.text = it.name
                            serie.director = it.name
                            return
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CreditsResponse>?, t: Throwable?) {
                Toast.makeText(baseContext, "Echec", Toast.LENGTH_LONG).show()
            }
        })


    }

}