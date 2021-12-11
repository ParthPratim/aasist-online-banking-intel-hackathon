package intel.oneman.aassist;

/**
 * Created by wwwpp on 28/07/2018.
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FMS extends FirebaseMessagingService {
    private static final String TAG = "FCMSERVICE";

    public static String currtopic="";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log data to Log Cat
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //create notification
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences("AassistAppSettings", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS); // 0 - for private mode
        Map<String , String> rmd = remoteMessage.getData();
        String fromemail = rmd.get("fromemail");
        String toemail = rmd.get("toemail");
        String amount = rmd.get("amount");
        String em = pref.getString("email","friends@intel.com");
        if(toemail.compareTo(em) == 0){
            createNotification(Integer.parseInt(amount),fromemail);
        }
    }

    private void createNotification( int amount , String fromemail) {
        String messageBody = "Request sent by - "+fromemail;
        String title = "New Pull Transfer of INR "+amount;
        Intent intent = new Intent( this , AAssist.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("toemail", fromemail);
        intent.putExtra("amount", amount);
        intent.putExtra("PullTransfer", true);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification mNotificationBuilder = new Notification.Builder( this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel( true )
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder);
    }
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences pref = this.getApplicationContext().getSharedPreferences("AassistAppSettings", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS); // 0 - for private mode
        updateTopic(pref);
        Log.d("NEW_TOKEN",s);
    }
    public static void updateTopic(SharedPreferences pref){
        if(currtopic.compareTo("")!=0){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(currtopic);
        }
        String tok = pref.getString("access_token","fa08f366b068e7e679df10492e1337598a6f27d19aa6258f3c8a48cc8bbb1b04");
        if(currtopic.compareTo("")!=0){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(currtopic);
        }
        FirebaseMessaging.getInstance().subscribeToTopic(tok+"pulltransfer");
        currtopic = tok+"pulltransfer";
    }
}
