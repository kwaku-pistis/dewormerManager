package com.deemiensa.dewormingmanager.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deemiensa.dewormingmanager.R
import com.deemiensa.dewormingmanager.activities.Dewormer
import com.deemiensa.dewormingmanager.offline.SharedPref
import java.text.SimpleDateFormat
import java.util.*


class NotificationScheduler: JobService() {

    private lateinit var notifyManager: NotificationManager
    private lateinit var mScheduler: JobScheduler

    // Notification Channel ID
    private val PRIMARY_CHANNEL_ID: String = "primary_notification_channel"

    private lateinit var sharedPref: SharedPref

    override fun onStartJob(params: JobParameters?): Boolean {
        val daysLeft = calculateDaysLeft(this)

        if (daysLeft <= 1) {
            createNotificationChannel()

            // set up the notification content intent to launch the app when clicked
            val contentPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, Dewormer::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("REMINDER")
                .setContentText("Hi there! It is time to deworm again. Stay Healthy!")
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.vdoc)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
            notifyManager.notify(0, builder.build())
        }

        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private fun createNotificationChannel() {

        // Define notification manager object.
        notifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Job Service notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications from Job Service"
            notifyManager.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * calculate the number of days left
     * */
    private fun calculateDaysLeft(context: Context): Long {
        sharedPref = SharedPref(context)
        val currentDate = Date()

        val parseDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val date = sharedPref.unFormattedNextDate

        if (!date.isNullOrEmpty()){
            val nextDate = parseDate.parse(date)
            val diff: Long = nextDate!!.time - currentDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return days
        }

        return 100
    }
}