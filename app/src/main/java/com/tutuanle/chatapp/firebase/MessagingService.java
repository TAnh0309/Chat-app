package com.tutuanle.chatapp.firebase;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.ChatScreenActivity;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        User user = new User();
        user.setUid(message.getData().get(Constants.KEY_USER_ID));
        user.setName(message.getData().get(Constants.KEY_NAME));
        user.setToken(message.getData().get(Constants.KEY_FCM_TOKEN));

        int notificationID = new Random().nextInt();
        String channelId = "chat_message";
        Intent intent = new Intent(this, ChatScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        // custom notification
        @SuppressLint("RemoteViewLayout")
        RemoteViews notificationLayout  = new RemoteViews(getPackageName(), R.layout.custom_notification);
//        notificationLayout.setTextViewText(R.id.username, user.getName());
//        notificationLayout.setTextViewText(R.id.message, message.getData().get(Constants.KEY_MESSAGE));
//        notificationLayout.setImageViewBitmap();



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_baseline_toys_24);
        builder.setContentTitle(user.getName());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                message.getData().get(Constants.KEY_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);


//        builder.setSmallIcon(R.drawable.ic_baseline_message_24);
//        builder.setCustomContentView(notificationLayout);
//        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        builder.setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "Chat Message ";
            String channelDescription = "This notification channel is used for chat message notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationID, builder.build());
    }
}
