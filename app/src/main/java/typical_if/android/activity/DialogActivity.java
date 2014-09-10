package typical_if.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPost;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.event.EventShowPhotoAttachDialog;
import typical_if.android.event.EventShowReportDialog;
import typical_if.android.event.EventShowSuggestPostDialog;
import typical_if.android.fragment.FragmentAlbumsList;
import typical_if.android.fragment.FragmentMakePost;
import typical_if.android.fragment.FragmentUploadAlbumList;
import typical_if.android.fragment.FragmentWall;

/**
 * Created by admin on 10.09.2014.
 */
public class DialogActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }


    public void reportListDialog(final long gid, final long id) {
        final AlertDialog.Builder builderIn = new AlertDialog.Builder(Constants.mainActivity);
        builderIn.setTitle(R.string.post_report);
        final Resources resources = getResources();

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
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        final int isSucceed = response.json.optInt("response");

                        if (isSucceed == 1) {
                            Toast.makeText(getApplicationContext(), resources.getString(R.string.post_reported), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(final VKError error) {
                        super.onError(error);
                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                    }
                });
            }
        });
        builderIn.show();
    }

    public void reportDialog(final long gid, final long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Constants.mainActivity);
        final Resources resources = getResources();

        final String[] items = {resources.getString(R.string.post_report), resources.getString(R.string.post_copy_link)};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        reportListDialog(gid, id);
                        break;
                    case 1:
                        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText("http://vk.com/wall-" + gid + "_" + id);
                        break;
                }
            }
        });

        builder.show();
    }

    public void suggestPostDialog(final long gid, final VKApiPost post) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Constants.mainActivity);
        final Resources resources = getResources();

        final String[] items = {resources.getString(R.string.post_edit), resources.getString(R.string.post_delete)};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ItemDataSetter.setSuggestAttachments(post.attachments);
                        Constants.tempTextSuggestPost = post.text;
                        addFragment(FragmentMakePost.newInstance(gid, post.id, 1));
                        break;
                    case 1:
                        VKHelper.deleteSuggestedPost(gid, post.id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);
                                getSupportFragmentManager().popBackStack();
                                addFragment(FragmentWall.newInstance(true));
                                Toast.makeText(getApplicationContext(), resources.getString(R.string.post_deleted), Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });
                        break;
                }
            }
        });

        builder.show();
    }

    public void photoAttachDialog(final long gid, final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Constants.mainActivity);
        final Resources resources = getResources();

        final String[] items = {resources.getString(R.string.photo_from_own), resources.getString(R.string.photo_from_sd)};

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addFragment(FragmentAlbumsList.newInstance(type));
                        break;
                    case 1:
                        addFragment(FragmentUploadAlbumList.newInstance(gid, type));
                        break;
                }
            }
        });

        builder.show();
    }

    public void videoResolutionDialog(JSONObject jsonObject) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Constants.mainActivity);
        final Resources resources = getResources();

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
                        Toast.makeText(getApplicationContext(), links.get(which), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    public Dialog addPhotoFrom() {
        final String[] items =  Constants.mainActivity.getResources().getStringArray(R.array.add_photo_from);
        AlertDialog.Builder builder = new AlertDialog.Builder(Constants.mainActivity);
        builder.setTitle(Constants.mainActivity.getResources().getString(R.string.add_photo_from_title));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addFragment(FragmentUploadAlbumList.newInstance(Constants.GROUP_ID * (-1), 1));
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

    public void takePhotoFromCamera() {
        final int PICK_FROM_CAMERA = 1;
        File file = new File(Environment.getExternalStorageDirectory(),
                "pic_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        if (file == null)
            return;
        Constants.tempCameraPhotoFile = file.getAbsolutePath();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputFileUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        Constants.mainActivity.startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
    }

    //-----------------------------------EVENTS---------------------------------------

    public void onEventMainThread(EventShowPhotoAttachDialog event) {
        photoAttachDialog(event.gid,event.which);
    }

    public void onEventMainThread(EventShowReportDialog event) {
        reportDialog(event.gid,event.which);
    }

    public void onEventMainThread(EventShowSuggestPostDialog event) {
        suggestPostDialog(event.gid,event.post);
    }
}