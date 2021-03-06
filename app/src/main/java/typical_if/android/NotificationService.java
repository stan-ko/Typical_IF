package typical_if.android;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKApiPost;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import typical_if.android.activity.MainActivity;
import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;

public class NotificationService extends Service {

    private long mInterval;
    private  int offsetDefault = 0;
    private  int countOfPosts = 1;
    private  int extended = 1;
    AlarmManager alarmManager;
    private final static int ONE_HOUR_MILLISECONDS = 60 * 60 * 1000;
    private Wall wall;
    private final static int ONE_DAY_MILLISECONDS = 24 * 60 * 60 * 1000;



    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("onCreateService", "----------------------");
        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        VKSdk.initialize(sdkListener, Constants.APP_ID, VKAccessToken.tokenFromSharedPreferences(this, Constants.TIF_VK_API_KEY_TOKEN));
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (OfflineMode.getIsFirstRunStartCommand()){
            OfflineMode.setNotFirstRunStartCommand();
            offsetDefault = 0;
            countOfPosts = 100;
            extended = 1;
            makeRequestsForAllPosts(extended, offsetDefault, countOfPosts);
        }
//        Log.d("onStartCommand", "----------------------");
        try {
            if (intent.getAction().equals(Constants.ACTION_BOOT_COMPLETED)) {
                makeRequests(extended, offsetDefault, countOfPosts);

//                Log.d("ACTION_BOOT_COMPLETED", "-----------");
            }
            if (intent.getAction().equals(Constants.ACTION_START_FROM_SPLASH_ACTIVITY)) {
                makeRequests(extended, offsetDefault, countOfPosts);

//                Log.d("ACTION_START_FROM_SPLASH_ACTIVITY", "-----------");
            }
            if (intent.getAction().equals(Constants.ACTION_FIRST_RUN)) {
                makeRequests(extended, offsetDefault, countOfPosts);

//                Log.d("ACTION_FIRST_RUN", "-----------");
            }
            if (intent.getAction().equals(Constants.REPEAT_ACTION)) {
                makeRequests(extended, offsetDefault, countOfPosts);
//                Log.d("REPEAT_ACTION", "-----------");
            }

        } catch (NullPointerException NPE) {
            //TODO make action 3 (restart Service)
//            Log.d("restart", "-----------");
        }
        return START_STICKY;
    }
    private void makeRequests(int extended, int offsetDefault, int countOfPosts) {
        VKHelper.doGroupWallRequest(extended, offsetDefault, countOfPosts, Constants.ZF_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                handleRequestComplete(vkJson);
//                Log.d("Make", "Request");
            }

//            @Override
//            public void onError(final VKError error) {
//                super.onError(error);
//            }
        });
    }

    private void makeRequestsForAllPosts(int extended, int offsetDefault, int countOfPosts) {
        VKHelper.doGroupWallRequest(extended, offsetDefault, countOfPosts, Constants.ZF_ID, new VKRequestListener() {
            @Override
            public void onSuccess() {
                OfflineMode.saveJSON(Constants.ZF_ID, vkJson);
            }
//
//            @Override
//            public void onError(final VKError error) {
//                super.onError(error);
//            }
        });
    }
    private void handleRequestComplete(final JSONObject json) {
        parseJson(json);
    }
    private void parseJson(JSONObject newPostJson) {
        wall = VKHelper.getGroupWallFromJSON(newPostJson);
        ArrayList<VKWallPostWrapper> posts;
        posts = wall.posts;
        VKWallPostWrapper postW = posts.get(0);
        VKApiPost post = postW.post;

        if (ItemDataSetter.checkNewPostResult(post.date)) {
            setAlarm(true);
        } else {
            setAlarm(false);
        }
    }
    Intent intentForSchedule;
    PendingIntent pendingIntent;
    private void setAlarm(boolean isNewPost) {
        if (isNewPost) {
            OfflineMode.saveInt(Calendar.getInstance().get(Calendar.DATE), Constants.DATE_OF_NOTIF_SEND);
            final long currentTime = System.currentTimeMillis();
            final long tomorrowTime = currentTime - (currentTime % ONE_DAY_MILLISECONDS) + ONE_DAY_MILLISECONDS + ONE_HOUR_MILLISECONDS * 8;
            mInterval = tomorrowTime - currentTime;
            sendNotif();
            intentForSchedule = createIntent(Constants.SCHEDULE_FOR_EIGHT_HOUR);
            pendingIntent = PendingIntent.getBroadcast(TIFApp.getAppContext(), 0, intentForSchedule, 0);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + mInterval, pendingIntent);
            Log.d("intervalForNextServiceStart ", " to 8 hours " + mInterval);
            stopSelf();
        } else {
            mInterval = 60 * 60 * 1000;
            intentForSchedule = createIntent(Constants.SCHEDULE_FOR_ONE_HOUR);
            pendingIntent = PendingIntent.getBroadcast(TIFApp.getAppContext(), 0, intentForSchedule, 0);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + mInterval, pendingIntent);
            Log.d("intervalForNextServiceStart ", " for 1 hour " + mInterval);
            stopSelf();
        }
    }
    Intent createIntent( String action) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction(action);
        return intent;
    }
    private void sendNotif() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_zf_mdpi)
                        .setContentTitle("Події Франківська")
                        .setContentText("Додай найцікавіше в календар")
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("isClickable", true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(Constants.S_MY_SCOPE);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
            new AlertDialog.Builder(NotificationService.this)
                    .setMessage(authorizationError.toString())
                    .show();
        }
        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {

        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


}
