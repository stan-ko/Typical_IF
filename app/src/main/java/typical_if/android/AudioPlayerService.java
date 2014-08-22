package typical_if.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.vk.sdk.VKUIHelper;

/**
 * Created by LJ on 21.08.2014.
 */
public class AudioPlayerService extends Service {


    public static final String playMusic = "com.action.PLAY";
    public static final String pauseMusic = "com.action.PAUSE";
    public static final String PreviousMusic = "Previous";
    public static final String NextMusic = "Next";

    int id = 1;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;


    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(VKUIHelper.getTopActivity().getApplicationContext(), 0, new Intent(pauseMusic), 0);
        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Singer")
                .setContentText("Title")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .addAction(android.R.drawable.ic_media_previous, PreviousMusic, pendingIntent)
                .addAction(android.R.drawable.ic_media_pause, pauseMusic, pendingIntent)
                .addAction(android.R.drawable.ic_media_next, NextMusic, pendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotifyManager.notify(id, mBuilder.build());
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals("com.action.PAUSE")) {
                    Constants.mediaPlayer.pause();
                }
            }
        }
            return super.onStartCommand(intent, flags, startId);
        }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
