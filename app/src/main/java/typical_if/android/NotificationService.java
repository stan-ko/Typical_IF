package typical_if.android;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPost;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;

public class NotificationService extends Service {
    NotificationManager nm;
    private long mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    private final int offsetDefault = 0;
    private final int countOfPosts = 1;
    private final int extended = 1;
    JSONObject newPostJson;
    AtomicInteger threadsCounter;
    Wall wall;
   private final String key= "isNotificationAlreadySended";
     final static int ONE_DAY_MILLISECONDS = 24*60*60*1000;
    public NotificationService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mHandler = new Handler();
        Log.d("onCreateService", "----------------------");


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        VKSdk.initialize(sdkListener, Constants.APP_ID, VKAccessToken.tokenFromSharedPreferences(this, Constants.TIF_VK_API_KEY_TOKEN));
        Log.d("onStartCommand", "----------------------");
        if (OfflineMode.isOnline(TIFApp.getAppContext())){
            startRepeatingTask();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
           // if (!OfflineMode.loadBool(key)){
                makeRequests();
            //}
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }
    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    void updateStatus(boolean isNewPost){
        if (isNewPost == true){
            long tomorrowTime = System.currentTimeMillis();
            tomorrowTime = tomorrowTime - (tomorrowTime% ONE_DAY_MILLISECONDS)+32*60*60*1000;
            long intervalForNextServiceStart = tomorrowTime-System.currentTimeMillis();
            mInterval=intervalForNextServiceStart;
            //mInterval=10000;

            Log.d("intervalForNextServiceStart-------------------------------------------", ""+mInterval);
        } else {
           mInterval =60*60*1000;
           // mInterval = 20000;
            Log.d("intervalForNextServiceStart--------------------------------------------", ""+mInterval);
        }
    }

    private void makeRequests() {
        threadsCounter = new AtomicInteger(1);
        final AtomicInteger requestSessionThreadsCounter = threadsCounter;
       VKHelper.doGroupWallRequest(extended, offsetDefault, countOfPosts, Constants.ZF_ID, new VKRequest.VKRequestListener() {
           @Override
           public void onComplete(final VKResponse response) {
               super.onComplete(response);
               handleRequestComplete(response.json, requestSessionThreadsCounter);
               Log.d("Make", "Request");
           }

           @Override
           public void onError(final VKError error) {
               super.onError(error);
           }
       });
    }

    void handleRequestComplete(final JSONObject json, final AtomicInteger requestSessionThreadsCounter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (threadsCounter!=requestSessionThreadsCounter)
                    return;
                newPostJson = json;
                decrementThreadsCounter(requestSessionThreadsCounter);
            }
        }).start();
    }

    void decrementThreadsCounter(final AtomicInteger requestSessionThreadsCounter) {
        if (threadsCounter!=requestSessionThreadsCounter)
            return;
        if (requestSessionThreadsCounter.decrementAndGet() == 0)
            //OfflineMode.saveBool(false, key);
          wall = VKHelper.getGroupWallFromJSON(newPostJson);
        ArrayList<VKWallPostWrapper> posts;
        posts = wall.posts;
        VKWallPostWrapper postW =  posts.get(0);
        VKApiPost post = postW.post;
        if (ItemDataSetter.checkNewPostResult(post.date)){
            updateStatus(true);
            sendNotif();
        } else {
            updateStatus(false);
           // OfflineMode.saveBool(false, key);
        }
    }

    void sendNotif() {
//          OfflineMode.saveBool(true, key);
        Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar",
                System.currentTimeMillis());
        //Intent intent = new Intent(this, MainActivity.class);
//      intent.putExtra(MainActivity.FILE_NAME, "somefile");
       // PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        notif.setLatestEventInfo(this, "Події на сьогодні", "Додай найцікавіше до календаря", null);
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

        nm.notify(1, notif);
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
            new AlertDialog.Builder(Constants.mainActivity)
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
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


}
