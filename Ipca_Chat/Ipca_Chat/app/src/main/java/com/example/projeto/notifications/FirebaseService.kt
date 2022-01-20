package com.example.projeto.notifications

import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.projeto.R
import com.example.projeto.SalaChat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my channel"

class FirebaseService : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, SalaChat::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val remoteView = RemoteViews(packageName, R.layout.notification_layout)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)

        remoteView.setTextViewText(R.id.notification_title, message.data["title"])
        remoteView.setTextViewText(R.id.notification_message, message.data["message"])

        Glide.with(applicationContext)
            .asBitmap()
            .load(message.data["userPhotoUrl"])
            .circleCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    remoteView.setImageViewBitmap(R.id.notification_photo, resource)
                    notification.setSmallIcon(R.drawable.ipca_logo_rgb_v2)
                    notification.setCustomContentView(remoteView)
                    notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())

                    val notification = notification.build()
                    notificationManager.notify(notificationID, notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}