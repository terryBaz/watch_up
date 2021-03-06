package com.example.tarekbaz.watch_up.Offline

import android.arch.persistence.room.*
import com.example.tarekbaz.watch_up.Models.AssotiationMovies
import com.example.tarekbaz.watch_up.Models.Comment
import com.example.tarekbaz.watch_up.Models.Movie


interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insert(vararg obj: T)

    @Delete
    fun delete(vararg obj: T)

    @Update(onConflict = OnConflictStrategy.FAIL)
    fun update(vararg obj: T)
}



@Dao
abstract class MovieDAO:BaseDao<Movie>{

//    @Transaction
//     fun insertTrans(movie: Movie){
//        insert(movie)
//    }

    /**
     * Updating only is_fav
     */
    @Query("UPDATE movie SET is_fav=:value WHERE movie.id = :id")
    abstract fun setFav(id: Int, value:Int)

    @Query("SELECT * FROM movie")
    abstract fun getMovies(): List<Movie>

    @Query("SELECT id FROM movie WHERE is_fav = 1")
    abstract fun getFavMoviesId(): List<Int>

    @Query("SELECT * FROM movie WHERE is_fav = 1")
    abstract fun getFavMovies(): List<Movie>

    @Query("SELECT * FROM movie WHERE id = :id")
    abstract fun getMovie(id: Int): List<Movie>
}

@Dao
abstract class RelatedMoviesDAO:BaseDao<AssotiationMovies> {

   @Query("SELECT * FROM movie INNER JOIN movies_related_movies "+
                "ON movie.id = movies_related_movies.related_movie_id WHERE movies_related_movies.movie_id=:id")
   abstract fun  getRelatedMovies(id: Int):List<Movie>

    // get the number of associated movies of the movie  movie
    @Query("SELECT count(*) FROM movie INNER JOIN movies_related_movies "+
            "ON movie.id = movies_related_movies.related_movie_id "+
            " WHERE movies_related_movies.movie_id= :id"+
            " group by movies_related_movies.related_movie_id ")
    abstract fun nbrAssociated(id: Int): Int

    // get the number of associations with other movies
    @Query("SELECT count(*) FROM movies_related_movies "+
            " WHERE movies_related_movies.related_movie_id= :id"+
            " group by movies_related_movies.related_movie_id ")
    abstract fun nbrAssociation(id: Int): Int

    // delete
    @Query("DELETE FROM movies_related_movies WHERE " +
            " movie_id = :id")
    abstract fun deleteAllRelated(id: Int): Int

}


@Dao
abstract class CommentDAO:BaseDao<Comment>{
    @Query("SELECT * FROM comment")
    abstract fun getComments(): List<Comment>

    @Query("SELECT * FROM comment WHERE film_id = :fid")
    abstract fun getFilmComments(fid: Int): List<Comment>
}