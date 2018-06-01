package edu.swe.healthcareapplication.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import edu.swe.healthcareapplication.BaseApplication;
import edu.swe.healthcareapplication.BuildConfig;
import edu.swe.healthcareapplication.R;
import edu.swe.healthcareapplication.util.BundleConstants;
import edu.swe.healthcareapplication.util.NotificationConstants;
import edu.swe.healthcareapplication.view.ChatRoomActivity;
import java.util.HashMap;
import java.util.Map;

public class ChatNotificationService extends FirebaseMessagingService {

  private static final String TAG = ChatNotificationService.class.getSimpleName();

  private Map<String, Integer> userNotifyIdMap;
  private int userNotifyId;

  public ChatNotificationService() {
    userNotifyId = 1;
    userNotifyIdMap = new HashMap<>();
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
    Log.d(TAG, "FCM Notification Message: " +
        remoteMessage.getNotification());
    Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

    NotificationManager notificationManager = (NotificationManager) getSystemService(
        Context.NOTIFICATION_SERVICE);

    if (notificationManager != null && remoteMessage.getData().size() > 0 && checkAppBackground()) {
      Map<String, String> data = remoteMessage.getData();
      String senderId = data.get("senderId");
      String title = data.get("title");
      String body = data.get("body");
      String roomKey = data.get("roomKey");

      if (!userNotifyIdMap.containsKey(senderId)) {
        userNotifyIdMap.put(senderId, userNotifyId);
        userNotifyId++;
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(notificationManager);
      }
      sendNotification(notificationManager, roomKey, senderId, title, body);
    }
  }

  private boolean checkAppBackground() {
    return ((BaseApplication) getApplication()).isBackground();
  }

  @TargetApi(Build.VERSION_CODES.O)
  private void createNotificationChannel(NotificationManager notificationManager) {
    if (notificationManager.getNotificationChannel(NotificationConstants.CHANNEL_CHATS) == null
        || BuildConfig.DEBUG) {
      NotificationChannel channel = new NotificationChannel(
          NotificationConstants.CHANNEL_CHATS, getString(R.string.notification_channel_chats),
          NotificationManager.IMPORTANCE_HIGH);

      channel.setShowBadge(true);
      channel.enableVibration(true);
      channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private void sendNotification(NotificationManager notificationManager, String roomKey,
      String senderId, String title,
      String body) {
    Intent intent = new Intent(this, ChatRoomActivity.class);
    intent.putExtra(BundleConstants.BUNDLE_CHAT_NAME, title);
    intent.putExtra(BundleConstants.BUNDLE_CHAT_ROOM_KEY, roomKey);

    PendingIntent pendingIntent = PendingIntent
        .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
        NotificationConstants.CHANNEL_CHATS)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(Notification.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_chat_white_24dp)
        .setDefaults(Notification.DEFAULT_ALL)
        .setSound(defaultSoundUri)
        .setShowWhen(true)
        .setAutoCancel(true)
        .setContentTitle(title)
        .setContentText(body)
        .setContentIntent(pendingIntent);

    notificationManager.notify(userNotifyIdMap.get(senderId), notificationBuilder.build());
  }
}
