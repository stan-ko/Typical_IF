package typical_if.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedEventRecevier extends BroadcastReceiver {
    final String LOG_TAG = "myLogs";

    public BootCompletedEventRecevier() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        Log.d(LOG_TAG, "onReceive " + intent.getAction());
        context.startService(new Intent(context, NotificationService.class));
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
