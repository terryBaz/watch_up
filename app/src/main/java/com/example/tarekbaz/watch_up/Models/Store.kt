package com.example.tarekbaz.watch_up.Models

class Store {
    companion object {
        var homeFilms : ArrayList<Movie> = ArrayList()
        var homeSeries : ArrayList<Serie> = ArrayList()
        var acteurs : ArrayList<Person> = ArrayList()

        var preferedGenres : HashSet<Int> = HashSet()
        var favFilms : ArrayList<Movie> = ArrayList()
    }
}