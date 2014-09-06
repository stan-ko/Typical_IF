package typical_if.android;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by LJ on 08.08.2014.
 */
public class UploadPhotoService extends Service{

    int id = 1;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(Constants.RESOURCES.getString(R.string.upload_photo_server))
                .setContentText(Constants.RESOURCES.getString(R.string.upload_photo_progress))
                .setSmallIcon(android.R.drawable.ic_menu_upload);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do the "lengthy" operation 20 times
                // Sets the progress indicator to a max value, the
                // current completion percentage, and "determinate"
                // state
                mBuilder.setProgress(0, 0, true);
                mNotifyManager.notify(id, mBuilder.build());
                // Sleeps the thread, simulating an operation
                // that takes time
                try {
                    // Sleep for 5 seconds
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    Log.d("MYTAG", "sleep failure");
                }
                // When the loop is finished, updates the notification
                mBuilder.setContentText(Constants.RESOURCES.getString(R.string.upload_photo_finish))
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

