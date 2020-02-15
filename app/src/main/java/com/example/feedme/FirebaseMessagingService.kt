package com.example.feedme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Override
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    @Override
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val notification = p0.notification
        if (notification != null) {
            Log.d(TAG, "HEJ")
            Log.d(TAG, notification.body)
        }
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle("textTitle")
            .setContentText("textContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_home_black_24dp, "Snooze",
                pendingIntent)

        with(NotificationManagerCompat.from(this)){
            notify(123, builder.build())
        }

        val from = p0.from
        val volume = p0.data["volume"]
        if (from != null)
            Log.d(TAG, from)
        if (volume != null)
            Log.d(TAG, volume)

        Log.d(TAG, p0.toString())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val TAG = "MyFirebaseMessagingService"
        const val CHANNEL_ID = "CHANNEL1"
    }
}