package com.example.tarekbaz.watch_up

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.tarekbaz.watch_up.API.MovieService
import com.example.tarekbaz.watch_up.API.Responses.ListPaginatedResponse
import com.example.tarekbaz.watch_up.Models.Genre
import com.example.tarekbaz.watch_up.Models.Movie
import com.example.tarekbaz.watch_up.Models.Store
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class NewMoviesNotification {

    companion object {

        val ALREADY_SEEN = "ALREADY_SEEN"

        fun create(context: Context) {
            if (Store.preferedGenres.isEmpty()) return

            val gson = GsonBuilder().create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(Config.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            val service = retrofit.create<MovieService>(MovieService::class.java!!)

            val prefGenres = context.getSharedPreferences(Genre.KEY, Context.MODE_PRIVATE)
            val filmAlreadySeen = prefGenres.getStringSet(ALREADY_SEEN, HashSet<String>())

            Log.i("notyLog film alr0 ", filmAlreadySeen.toString())

            val keys = ArrayList(Store.preferedGenres)
            var genresList = keys[0].toString()
            for (i in 1 until keys.size) {
                genresList += "|" + keys[i]
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val cal = Calendar.getInstance()
            val date_bonr_inf = sdf.format(cal.time)
            cal.add(Calendar.DATE, 7)
            val date_bonr_sup = sdf.format(cal.time)

            service.latestMovies(date_bonr_inf.toString(), date_bonr_sup.toString(), genresList)
                    .enqueue(object : Callback<ListPaginatedResponse<Movie>> {
                        override fun onResponse(call: Call<ListPaginatedResponse<Movie>>, response: retrofit2.Response<ListPaginatedResponse<Movie>>?) {
                            if ((response != null) && (response.code() == 200
                                            && !response.body()!!.results.isEmpty())) {


                                val latestMovies = response.body()!!.results

                                Log.i("myLogiii", "from service ")

                                var latestMovie : Movie? = null

                                for (i in 0 until latestMovies.size){
                                    if (!filmAlreadySeen.contains(latestMovies[i].id.toString())){
                                        latestMovie = latestMovies[i]
                                        break
                                    }
                                }
                                Log.i("notyLog film alr ", latestMovie.toString())

                                if (latestMovie == null) {
                                    Toast.makeText(context, "No up movie " + response.toString()
                                            , Toast.LENGTH_LONG).show()
                                    return
                                }

                                Store.homeFilms.add(latestMovie)
                                initNotificatioData(latestMovie, context)

                                val editor = prefGenres.edit()

                                filmAlreadySeen.add(latestMovie.id.toString())

                                //todo
                                editor.putStringSet(ALREADY_SEEN, null)
                                editor.commit()

                                editor.putStringSet(ALREADY_SEEN, filmAlreadySeen)
                                editor.commit()

                            } else if (response != null) {
                                Toast.makeText(context, "No up movie " + response.toString()
                                        , Toast.LENGTH_LONG).show()
                                Log.i("myLogi333", "" + response.toString())

                            }
                        }

                        override fun onFailure(call: Call<ListPaginatedResponse<Movie>>?, t: Throwable?) {
                            Toast.makeText(context, "Echec Noty", Toast.LENGTH_LONG).show()
                        }
                    })

        }


        fun createFromService(context: Context) {
            val gson = GsonBuilder().create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(Config.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            val service = retrofit.create<MovieService>(MovieService::class.java)

            val prefGenres = context.getSharedPreferences(Genre.KEY, Context.MODE_PRIVATE)
            val genres = prefGenres.getStringSet(Genre.KEY, HashSet<String>())
            if (genres.isEmpty()) return
            val filmAlreadySeen = prefGenres.getStringSet(ALREADY_SEEN, HashSet<String>())

            Log.i("myLogiii1122", genres.toString())
            Log.i("notyLog film alr ", filmAlreadySeen.toString())

            val keys = ArrayList(genres)
            var genresList = keys[0].toString()
            for (i in 1 until keys.size) {
                genresList += "|" + keys[i]
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val cal = Calendar.getInstance()
            val date_bonr_inf = sdf.format(cal.time)
            cal.add(Calendar.DATE, 7)
            val date_bonr_sup = sdf.format(cal.time)

            service.latestMovies(date_bonr_inf.toString(), date_bonr_sup.toString(), genresList)
                    .enqueue(object : Callback<ListPaginatedResponse<Movie>> {
                        override fun onResponse(call: Call<ListPaginatedResponse<Movie>>, response: retrofit2.Response<ListPaginatedResponse<Movie>>?) {
                            if ((response != null) && (response.code() == 200
                                            && !response.body()!!.results.isEmpty())) {
                                val latestMovies = response.body()!!.results

                                Log.i("myLogiii", "from service ")

                                var latestMovie : Movie? = null

                                //todo test if the film is alredy seen
                                for (i in 0 until latestMovies.size){
                                    if (!filmAlreadySeen.contains(latestMovies[i].id.toString())){
                                        latestMovie = latestMovies[i]
                                        break
                                    }
                                }

                                if (latestMovie == null) {
                                    Toast.makeText(context, "No up movie " + response.toString()
                                            , Toast.LENGTH_LONG).show()
                                    return
                                }

                                val editor = prefGenres.edit()

                                filmAlreadySeen.add(latestMovie.id.toString())

                                //todo
                                editor.putStringSet(ALREADY_SEEN, null)
                                editor.commit()

                                editor.putStringSet(ALREADY_SEEN, filmAlreadySeen)
                                editor.commit()

                                initNotificatioData(latestMovie, context)
                            } else if (response != null) {
                                Toast.makeText(context, "No up movie " + response.toString()
                                        , Toast.LENGTH_LONG).show()
                                Log.i("myLogi333", "" + response.toString())

                            }
                        }

                        override fun onFailure(call: Call<ListPaginatedResponse<Movie>>?, t: Throwable?) {
                            Toast.makeText(context, "Echec Noty", Toast.LENGTH_LONG).show()
                        }
                    })
        }

        fun initNotificatioData(film: Movie, context: Context) {
            Glide.with(context)
                    .asBitmap()
                    .load(Config.IMG_BASE_URL + film.poster_path)
                    .into(object : SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            createNotification(resource, film, context)
                        }
                    })
        }

        @SuppressLint("NewApi")
        private fun createNotification(bitmap: Bitmap, film: Movie, context: Context) {
            // Create an explicit intent for an Activity in your app
            val intent = Intent(context, FilmDetailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("index", film.id)
            intent.putExtra("fromNotification", true)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//        Store.homeFilms.add(films.get(position))

            val CHANNEL_ID = "CHANNEL_WU_01"
            val notificationId = 1

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        CharSequence name = getString(CHANNEL_ID);
//        String description = getString(R.string.channel_description);
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, "CHANNEL_WU_NAME", importance)
//        channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = context.getSystemService(NotificationManager::class.java!!)
                notificationManager.createNotificationChannel(channel)
            }

            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.star)
                    .setLargeIcon(bitmap)
                    .setContentTitle(film.title)
                    .setContentText("Premium show: ${SimpleDateFormat("E dd MMM yyyy").format(film.release_date)}")
                    .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .setBigContentTitle(film.title)
                            .setSummaryText("Premium show: ${SimpleDateFormat("E dd MMM yyyy").format(film.release_date)}")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)



            //run
            val notificationManager = NotificationManagerCompat.from(context)
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, mBuilder.build())

        }

        val SERVICE_ID = 9

        val KEY = "ALARMSHP"
        val DAY = "DAY"

        fun startAlarmService(context: Context) {

            val sp = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val cal = Calendar.getInstance()
            val todayDate = sdf.format(cal.time)

            val lastDaySharedSet = sp.getString(DAY, "2000-02-20")

//            if (todayDate.toString() != lastDaySharedSet) {//todo
//            val editor = sp.edit()
//            editor.putString(DAY, todayDate.toString())
//            editor.commit()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR))
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND))

            Log.i("myLogiii", "service started " + Build.VERSION.SDK_INT)
            val intent = Intent(context, AlarmReceiver::class.java)

//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val pintent = PendingIntent.getBroadcast(
                    context, SERVICE_ID, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            //todo PendingIntent.FLAG_CANCEL_CURRENT
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


            alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES/*todo*/,
                    pintent)

            Log.i("myLogiii", "already service started ")

//            AlarmManager.INTERVAL_DAY
//            System.currentTimeMillis()

//            }

        }
    }
}