package com.example.tarekbaz.watch_up.Models

import java.util.*

data class Season(val discription: String,
                  val image: Int,
                  var episodes: List<Episode>,
                  val evaluation: Double,
                  val trailer: Int,
                  var comments: List<Comment>,
                  var linkedActors: List<Person>,
                  val poster_path: String = "",
                  val name: String = "",
                  var air_date: Date = Date()
//                  var _id : Int = 0
){
}