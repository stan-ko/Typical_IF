package typical_if.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.fragment.FragmentUploadAlbumList;

/**
 * Created by admin on 06.08.2014.
 */
public class Dialogs {

    public static void reportListDialog(final Context context, final long gid, final long id) {
        final AlertDialog.Builder builderIn = new AlertDialog.Builder(VKUIHelper.getTopActivity());
        builderIn.setTitle(R.string.post_report);
        final Resources resources = context.getResources();

        final String[] items = resources.getStringArray(R.array.post_report_types);

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
                VKHelper.doReportPost(gid, id, reason, new VKRequest.VKRequestListener() {
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
    }

    public static void addPhotoToCommentDialog(final FragmentManager fragmentManager) {
        final String[] items = {"З карти памяті", "З своїх альбомів"};
        AlertDialog.Builder builder = new AlertDialog.Builder(VKUIHelper.getTopActivity());
        builder.setTitle("Завантажити фото ?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        fragmentManager.beginTransaction().add(R.id.container, new FragmentUploadAlbumList()).addToBackStack(null).commit();
                        Log.d("222222222222222222222222222222222222222", "333333333333333333333333333");
                        //dialog.cancel();
                        break;
                    case 1:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    public static void reportDialog(final Context context, final long gid, final long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VKUIHelper.getTopActivity());
        final Resources resources = context.getResources();

        final String[] items = {resources.getString(R.string.post_report), resources.getString(R.string.post_copy_link)};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        reportListDialog(context, gid, id);
                        break;
                    case 1:
                        ClipboardManager clipboard = (ClipboardManager) MyApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText("http://vk.com/wall-" + gid + "_" + id);
                        break;
                }
            }
        });

        builder.show();
    }

    public static void videoResolutionDialog(final Context context, JSONObject jsonObject) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VKUIHelper.getTopActivity());
        final Resources resources = context.getResources();

        ArrayList<String> items = new ArrayList<String>();
        final ArrayList<String> links = new ArrayList<String>();

        if (jsonObject.has("mp_240")) {
            items.add("240");
            links.add(jsonObject.optString("mp_240"));
        }
        if (jsonObject.has("mp_360")) {
            items.add("360");
            links.add(jsonObject.optString("mp_360"));
        }
        if (jsonObject.has("mp_480")) {
            items.add("480");
            links.add(jsonObject.optString("mp_480"));
        }
        if (jsonObject.has("mp_720")) {
            items.add("720");
            links.add(jsonObject.optString("mp_720"));
        }

        builder.setTitle("Choose resolution");
        builder.setItems((String[])items.toArray(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        Toast.makeText(context, links.get(which), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }
}
