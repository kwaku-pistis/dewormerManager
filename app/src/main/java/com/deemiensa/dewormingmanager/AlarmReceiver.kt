package com.deemiensa.dewormingmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deemiensa.dewormingmanager.viewpager.Dewormer

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        if (intent.action == "FOO_ACTION") {
            val fooString = intent.getStringExtra("KEY_FOO_STRING")
            //Toast.makeText(context, fooString, Toast.LENGTH_LONG).show()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                val name = "Dewormer"
                val descriptionText = "Dewormer taking" //getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel("Dewormer", name, importance)
                mChannel.description = descriptionText
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }


            (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(
                intent.getIntExtra("notificationId", 0),
                NotificationCompat.Builder(context, "Dewormer").apply {
                    setSmallIcon(android.R.drawable.ic_dialog_info)
                    setContentTitle("REMINDER")
                    setContentText(intent.getCharSequenceExtra(fooString))
                    setWhen(System.currentTimeMillis())
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setAutoCancel(true)
                    setDefaults(Notification.DEFAULT_SOUND)
                    setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, Dewormer::class.java), 0))
                }.build()
            )
        }

    }
}