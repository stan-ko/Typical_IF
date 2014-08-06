package typical_if.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPost;

import typical_if.android.model.Wall.Wall;

/**
 * Created by admin on 06.08.2014.
 */
public class Dialogs {

    public static void spamDialog(final Wall wall, final VKApiPost post) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VKUIHelper.getTopActivity());
        final String[] items = {Constants.POST_REPORT, Constants.POST_COPY_LINK};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        final AlertDialog.Builder builderIn = new AlertDialog.Builder(VKUIHelper.getTopActivity());
                        builderIn.setTitle(Constants.POST_REPORT);
                        final String[] items = {Constants.POST_REPORT_SPAM, Constants.POST_REPORT_OFFENSE, Constants.POST_REPORT_ADULT, Constants.POST_REPORT_DRUGS, Constants.POST_REPORT_PORNO, Constants.POST_REPORT_VIOLENCE};

                        builderIn.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int reason = 0;
                                switch (which) {
                                    case 0:
                                        reason = 0;
                                        break;
                                    case 1:
                                        reason = 6;
                                        break;
                                    case 2:
                                        reason = 5;
                                        break;
                                    case 3:
                                        reason = 4;
                                        break;
                                    case 4:
                                        reason = 1;
                                        break;
                                    case 5:
                                        reason = 3;
                                        break;
                                }
                                VKHelper.doReportPost(wall.group.id, post.id, reason, new VKRequest.VKRequestListener() {
                                    @Override
                                    public void onComplete(VKResponse response) {
                                        super.onComplete(response);
                                        int isSuccessed = response.json.optInt("response");

                                        if (isSuccessed == 1) {
                                            Toast.makeText(VKUIHelper.getApplicationContext(), "Reported", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                        builderIn.show();
                        break;

                    case 1:
                        ClipboardManager clipboard = (ClipboardManager) VKUIHelper.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText("http://vk.com/wall-" + wall.group.id + "_" + post.id);
                        break;
                }
            }
        });

        builder.show();
    }
}
