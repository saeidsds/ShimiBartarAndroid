package com.multiplatform.shimibartar;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.multiplatform.model.GeneralObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FireBaseReceiveMessageService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (false) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                try {
                    if(remoteMessage.getData().get("message")!=null)
                        parse_json(remoteMessage.getData().get("message"));
                }catch (Exception e){
                    int i=0;
                    i++ ;
                }
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(GeneralObject obj) {

        Intent intent = new Intent(this, NotificationListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("service_id",obj.category_id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                        //.setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(obj.title)
                        .setContentText(obj.content)
                        .setOngoing(false)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            notificationBuilder.setSmallIcon(R.drawable.notification_icon2);
        }
        else
        {
            notificationBuilder.setSmallIcon(R.drawable.notification_icon);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                    "MultiPlatform Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }


        if(sp.getBoolean("login",false)==true )
        {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            updateMyActivityMessage(this);
        }

    }







    SharedPreferences sp ;
    SharedPreferences.Editor spe;

    private void parse_json(String json_str) {

        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        JSONArray jArray = null ;
        JSONObject json_obj=null;
        if(sp.getBoolean("login",false))
        {

            GeneralObject obj = new GeneralObject();

            try {
                json_obj = new JSONObject(json_str);
                obj.title=json_obj.getString("title");
                obj.content=json_obj.getString("content");
                sendNotification(obj);
            }
            catch (JSONException e) {
                return ;
            }
        }

    }











//    private  void generateNotification(Context context, String msg, String type, Intent  notificationIntent) {
//
//
//        int notification_id=0;
//        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification;
//        String title = context.getString(R.string.app_name);
//
//        Gson gson = new Gson();
//        notification_id=3;
//        notificationIntent.putExtra("show_notification",1);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent =PendingIntent.getActivity(context, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//
//        builder.setAutoCancel(false);
//        builder.setContentTitle(title);
//        builder.setContentText(msg);
//
//
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentIntent(intent);
//        builder.setOngoing(false);
//        notification=builder.build();
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        long[] vibrate = {0,100,200,300};
//        notification.vibrate = vibrate;
//        notification.defaults |= Notification.DEFAULT_LIGHTS;
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        SharedPreferences sp1= context.getSharedPreferences("init", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor spe1 = sp1.edit();
//        notificationManager.notify(notification_id, notification);
//        updateMyActivityMessage(context,type, msg);
//
//
//    }
//
//
//
//
//
    static void updateMyActivityMessage(Context context) {


        Intent intent=new Intent("notification_message");
        //intent.putExtra("msg", msg);
        context.sendBroadcast(intent);

    }
}
