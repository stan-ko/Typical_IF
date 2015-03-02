package typical_if.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (intent.getAction() == Constants.SCHEDULE_FOR_EIGHT_HOUR) {
                context.startService(new Intent(context, NotificationService.class).setAction(Constants.REPEAT_ACTION));
//                Log.d("SCHEDULE_FOR_EIGHT_HOUR", " --------------------------- ");
            }
            if (intent.getAction() == Constants.SCHEDULE_FOR_ONE_HOUR) {
                context.startService(new Intent(context, NotificationService.class).setAction(Constants.REPEAT_ACTION));
//                Log.d("SCHEDULE_FOR_ONE_HOUR", " --------------------------- ");

            }
        }
    }
}
