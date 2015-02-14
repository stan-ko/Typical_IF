package typical_if.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;

import typical_if.android.fragment.FragmentWall;

/**
 * Created by LJ on 21.08.2014.
 */
public class AudioPlayerService extends Service {


    public static final String playMusic = "com.action.PLAY";
    public static final String pauseMusic = "com.action.PAUSE";

    int icon = R.drawable.ic_notif_play_small;
    long when = System.currentTimeMillis();
    Notification notification = new Notification(icon, Constants.title, when);
    PendingIntent pendingIntent;
    public RemoteViews contentView;

    @Override
    public void onCreate() {
        super.onCreate();

        contentView = new RemoteViews(getPackageName(), R.layout.custom_notif);
        if (Constants.playedPausedRecord.isPlayed == true && Constants.playedPausedRecord.isPaused == false){
            contentView.setImageViewResource(R.id.notification_image, R.drawable.ic_notif_pause);
            pendingIntent = PendingIntent.getService(Constants.mainActivity.getApplicationContext(), 0, new Intent(pauseMusic), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else if (Constants.playedPausedRecord.isPaused == true && Constants.playedPausedRecord.isPlayed == false){
            contentView.setImageViewResource(R.id.notification_image, R.drawable.ic_notif_play);
            pendingIntent = PendingIntent.getService(Constants.mainActivity.getApplicationContext(), 0, new Intent(playMusic), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        contentView.setImageViewResource(R.id.logo_image_while_playing, FragmentWall.playableLogoRes);
        contentView.setTextViewText(R.id.notification_title, Constants.title);
        contentView.setTextViewText(R.id.notification_text, Constants.artist);
        notification.contentView = contentView;
        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.contentView.setOnClickPendingIntent(R.id.notification_image, pendingIntent);
    }




    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Constants.notificationManager.notify(Constants.notifID, notification);
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals("com.action.PAUSE")) {
                    Constants.mediaPlayer.pause();
                    Constants.playedPausedRecord.isPlayed = false;
                    Constants.playedPausedRecord.isPaused = true;
                    //FragmentWall.refresh();
                    Constants.previousCheckBoxState.setChecked(false);
                    contentView.setImageViewResource(R.id.notification_image, R.drawable.ic_notif_play);
                    pendingIntent = PendingIntent.getService(Constants.mainActivity.getApplicationContext(), 0, new Intent(playMusic), 0);
                    onCreate();
                    Constants.notificationManager.notify(Constants.notifID, notification);
                }
                else if (action.equals("com.action.PLAY")) {
                    Constants.mediaPlayer.start();
                    Constants.playedPausedRecord.isPlayed = true;
                    Constants.playedPausedRecord.isPaused = false;
                    //FragmentWall.refresh();
                    Constants.previousCheckBoxState.setChecked(true);
                    contentView.setImageViewResource(R.id.notification_image, R.drawable.ic_notif_pause);
                    pendingIntent = PendingIntent.getService(Constants.mainActivity.getApplicationContext(), 0, new Intent(pauseMusic), 0);
                    onCreate();
                    Constants.notificationManager.notify(Constants.notifID, notification);
                }


            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
