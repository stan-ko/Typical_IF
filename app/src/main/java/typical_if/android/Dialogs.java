package typical_if.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.io.File;

import typical_if.android.fragment.FragmentUploadAlbumList;

/**
 * Created by admin on 06.08.2014.
 */
public class Dialogs {

    public static android.support.v4.app.FragmentManager fragmentManager = null;

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

    public static Dialog addPhotoFrom() {
        final String[] items =  VKUIHelper.getTopActivity().getResources().getStringArray(R.array.add_photo_from);
        AlertDialog.Builder builder = new AlertDialog.Builder(VKUIHelper.getTopActivity());
        builder.setTitle(VKUIHelper.getTopActivity().getResources().getString(R.string.add_photo_from_title));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        FragmentUploadAlbumList fragmentUploadPhotoList = new FragmentUploadAlbumList();
                        fragmentManager.beginTransaction().replace(R.id.container, fragmentUploadPhotoList).addToBackStack(null).commit();
                        dialog.cancel();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.setCancelable(true);

        return builder.create();
    }

    public static void takePhotoFromCamera() {
        final int PICK_FROM_CAMERA = 1;
        File file = new File(Environment.getExternalStorageDirectory(),
                "pic_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        if (file == null)
            return;
        Constants.tempCameraPhotoFile = file.getAbsolutePath();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputFileUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        VKUIHelper.getTopActivity().startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
    }

}
