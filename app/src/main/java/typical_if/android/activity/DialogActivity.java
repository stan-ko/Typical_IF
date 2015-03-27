package typical_if.android.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiVideo;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.VKRequestListener;
import typical_if.android.event.EventShowPhotoAttachDialog;
import typical_if.android.event.EventShowReportDialog;
import typical_if.android.event.EventShowSuggestPostDialog;
import typical_if.android.event.MainActivityAddFragmentEvent;
import typical_if.android.fragment.FragmentAlbumsList;
import typical_if.android.fragment.FragmentMakePost;
import typical_if.android.fragment.FragmentUploadAlbumList;
import typical_if.android.fragment.FragmentVideoView;
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
        try {
            Log.d("getSupportFragmentManager is :" + getSupportFragmentManager(), "fragment = " + fragment);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();
            } else {
                fragment = FragmentWall.newInstance(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();

            }
        } catch (IllegalStateException ise) {
            Toast.makeText(getApplicationContext(), R.string.exception_during_run, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException npe) {
            Toast.makeText(getApplicationContext(), R.string.exception_during_run, Toast.LENGTH_SHORT).show();
        }
    }

    int index;

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack("").commit();
        index = getSupportFragmentManager().getBackStackEntryCount();
    }

    public void changeLanguage() {
        final AlertDialog.Builder builderIn = new AlertDialog.Builder(this);
        builderIn.setTitle(R.string.change_lan);

        final Resources resources = getResources();
        final String[] items = resources.getStringArray(R.array.app_languages);
        final String lang = ItemDataSetter.getUserLan();
        builderIn.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (lang != "ua") {
                            restartAfterChanges(0, "ua");
                        }
                        ++Constants.refresherDrawerCounter;
                        break;
                    case 1:
                        if (lang != "ru") {
                            restartAfterChanges(0, "ru");
                        }
                        ++Constants.refresherDrawerCounter;

                        break;

                }
            }
        });
        builderIn.show();

    }


    public void restartAfterChanges(final int key, final String lan) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.restart_app_dialog);

        dialog.setPositiveButton(R.string.okay,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ItemDataSetter.saveUserLanguage(key, lan);
                        Intent mStartActivity = new Intent(DialogActivity.this, SplashActivity.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(DialogActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                });
        dialog.setNegativeButton(R.string.cancel
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.setCancelable(true);
            }
        });
        dialog.create().show();
    }


    public void reportListDialog(final long gid, final long id) {
        final AlertDialog.Builder builderIn = new AlertDialog.Builder(this);
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
                VKHelper.doReportPost(gid, id, reason, new VKRequestListener() {
                    @Override
                    public void onSuccess() {
                        if (hasJson) {
                            final int isSucceed = vkJson.optInt(VKHelper.TIF_VK_SDK_KEY_RESPONSE);

                            if (isSucceed == 1) {
                                Toast.makeText(getApplicationContext(), R.string.post_reported, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

//                    @Override
//                    public void onError() {
//                        showErrorToast();
//                    }
                });
            }
        });
        builderIn.show();
    }

    public void reportDialog(final long gid, final long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        final Resources resources = getResources();
        final String[] items = {getString(R.string.post_edit), getString(R.string.post_delete)};

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
                        VKHelper.deleteSuggestedPost(gid, post.id, new VKRequestListener() {
                            @Override
                            public void onSuccess() {
                                getSupportFragmentManager().popBackStack();
                                Toast.makeText(getApplicationContext(), R.string.post_deleted, Toast.LENGTH_SHORT).show();
                            }

//                            @Override
//                            public void onError() {
//                                showErrorToast();
//                            }
                        });
                        break;
                }
            }
        });

        builder.show();
    }

    public void photoAttachDialog(final long gid, final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public static String isShowDialogNedeed(VKApiVideo video) {
        ArrayList<String> items = new ArrayList<String>();
        ArrayList<String> links = new ArrayList<String>();

        //  JSONObject files = jsonObject.optJSONObject("files");


        if (video.player.contains("youtube")) {
            link = video.player;
            return link;
        } else if (isOneLink(video, links, items)) {
            link = links.get(0);
            ;
            return link;
        } else {
            new DialogActivity(video, links, items);
            return link;
        }


    }

    public DialogActivity(VKApiVideo video, ArrayList<String> links, ArrayList<String> items) {
        videoResolutionDialog(video, items, links);
    }

    public DialogActivity() {
    }


    public static boolean isOneLink(VKApiVideo video, ArrayList<String> links, ArrayList<String> items) {

        if (video.mp4_240 != null) {
            items.add("240");
            links.add(video.mp4_240);
        }
        if (video.mp4_360 != null) {
            items.add("360");
            links.add(video.mp4_360);
        }
        if (video.mp4_480 != null) {
            items.add("480");
            links.add(video.mp4_480);
        }
        if (video.mp4_720 != null) {
            items.add("720");
            links.add(video.mp4_720);
        }


        if (links.size() != 1 || items.size() != 1) {
            return false;
        } else {
            return
                    true;
        }
    }


    static String link = null;

    public void videoResolutionDialog(final VKApiVideo video, final ArrayList<String> items, final ArrayList<String> links) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int count = links.size();

        builder.setTitle("Choose resolution");


        String[] dialogItems = items.toArray(new String[items.size()]);
        for (int i = 0; i < items.size(); i++) {
            dialogItems[i] = items.get(i);
        }

        builder.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentVideoView fragment;
                if (count == 4) {
                    switch (which) {
                        case 0:
                            link = links.get(0);
                            //  fragment = new FragmentVideoView(link);
                            //   new ItemDataSetter(fragment, link);
                            break;
                        case 1:
                            link = links.get(1);
                            //  fragment = new FragmentVideoView(link);
                            //  new ItemDataSetter(fragment, link);
                            break;
                        case 2:
                            link = links.get(2);
                            // fragment = new FragmentVideoView(link);
                            //  new ItemDataSetter(fragment, link);
                            break;
                        case 3:
                            link = links.get(3);
                            // fragment = new FragmentVideoView(link);
                            // new ItemDataSetter(fragment, link);
                            break;
                        default:

                    }
                }

                if (count == 3) {

                    switch (which) {
                        case 0:
                            link = links.get(0);

                            break;
                        case 1:
                            link = links.get(1);

                            break;
                        case 2:
                            link = links.get(2);

                            break;
                        default:

                    }


                }

                if (count == 2) {

                    switch (which) {
                        case 0:
                            link = links.get(0);

                            break;
                        case 1:
                            link = links.get(1);


                            break;
                        default:

                    }
                }
                EventBus.getDefault().post(new MainActivityAddFragmentEvent(FragmentVideoView.newInstance(link, video)));
                //((MainActivity) Constants.mainActivity).addFragment(FragmentVideoView.newInstance(link, video));

            }


        });

        builder.show();
    }

    public Dialog addPhotoFrom() {
        final String[] items = getResources().getStringArray(R.array.add_photo_from);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_photo_from_title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addFragment(FragmentUploadAlbumList.newInstance(OfflineMode.loadLong(Constants.VK_GROUP_ID) * (-1), 1));
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
        startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
    }

    //-----------------------------------EVENTS---------------------------------------

    public void onEventMainThread(EventShowPhotoAttachDialog event) {
        photoAttachDialog(event.gid, event.which);
    }

    public void onEventMainThread(EventShowReportDialog event) {
        reportDialog(event.gid, event.which);
    }

    public void onEventMainThread(EventShowSuggestPostDialog event) {
        suggestPostDialog(event.gid, event.post);
    }
}