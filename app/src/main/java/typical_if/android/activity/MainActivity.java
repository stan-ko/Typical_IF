package typical_if.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import typical_if.android.AudioPlayer;
import typical_if.android.AudioPlayerService;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.adapter.ActionBarArrayAdapter;
import typical_if.android.fragment.FragmentComments;
import typical_if.android.fragment.FragmentFullScreenViewer;
import typical_if.android.fragment.FragmentPhotoFromCamera;
import typical_if.android.fragment.FragmentWall;
import typical_if.android.fragment.NavigationDrawerFragment;


public class MainActivity extends DialogActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentFullScreenViewer.OnFragmentInteractionListener,
        FragmentComments.OnFragmentInteractionListener {


    private Drawable mIcon;
    private CharSequence mTitle;
    private static final int PICK_FROM_CAMERA = 1;
    private static String sTokenKey = "VK_ACCESS_TOKEN";
    public NavigationDrawerFragment mNavigationDrawerFragment;
    ActionBarArrayAdapter list;

    void showAlertChanges() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_of_alert_main))
                .setCancelable(false)
                .setMessage(getString(R.string.сhanges_of_new_version))
                .setPositiveButton(getString(R.string.pisitive_button_alert_of_main), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder.setCancelable(true);
                    }
                });

        builder.create().show();
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            if (OfflineMode.loadInt("surprise") < 15) {
                OfflineMode.saveInt(0, "surprise");
            }
        } catch (Exception e) {
        }

      if (OfflineMode.isFirstRun("mainFirstRun")){
            showAlertChanges();
       }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer.findViewById(R.id.drawer_content); // This is the container we defined just now.
        container.addView(child, 0);
        drawer.findViewById(R.id.navigation_drawer).setPadding(0, getStatusBarHeight(), 0, 0);
        decor.addView(drawer);

        ActionBar actionBar = getSupportActionBar();

        Constants.mainActivity = this;
        Constants.myIntent = new Intent(this, AudioPlayerService.class);
        Constants.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            actionBar.setDisplayShowTitleEnabled(true);
        } else {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        try {
            if (getIntent().getExtras() != null) {
                notifClick(mNavigationDrawerFragment, getIntent());
            }
        } catch (NullPointerException npe) {
        }


        VKUIHelper.onCreate(this);


        VKSdk.initialize(sdkListener, Constants.APP_ID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
        VKSdk.wakeUpSession(this);

        ItemDataSetter.fragmentManager = getSupportFragmentManager();
        ItemDataSetter.fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    try {
                        if (Constants.makePostMenu.size() == 3) {
                            FragmentWall.setEnabledMenu();
                            getSupportActionBar().show();
                        }
                    } catch (NullPointerException e) {
                    }
                }
            }
        });

        mNavigationDrawerFragment.refreshNavigationHeader(VKHelper.UserObject.getUserFromShared());
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        notifClick(mNavigationDrawerFragment, intent);
    }

    private void notifClick(NavigationDrawerFragment mNavigationDrawerFragment, Intent notifIntent) {
        if (notifIntent.getExtras().getBoolean("isClickable")) {
            mNavigationDrawerFragment.closeDrawer();
        }
    }

    public long setGroupId(final int clickedPosition) {
        switch (clickedPosition) {
            case 0:
                return Constants.TF_ID;
            case 1:
                return Constants.TZ_ID;
            case 2:
                return Constants.FB_ID;
            case 3:
                return Constants.FN_ID;
            case 4:
                return Constants.ST_ID;
            case 5:
                return Constants.ZF_ID;
            default:
                return Constants.TF_ID;
        }

    }

    public void onSectionAttached(final long groupIndex) {
        switch ((int) groupIndex) {
            case 0:
                mTitle = getString(R.string.menu_group_title_tf);
                mIcon = getResources().getDrawable(R.drawable.ic_ab_tf);
                Constants.Mtitle = mTitle.toString();
                break;
            case 1:
                mTitle = getString(R.string.menu_group_title_tz);
                mIcon = getResources().getDrawable(R.drawable.ic_ab_tz);
                Constants.Mtitle = mTitle.toString();
                break;
            case 2:
                mTitle = getString(R.string.menu_group_title_fb);
                mIcon = getResources().getDrawable(R.drawable.ic_ab_fb);
                Constants.Mtitle = mTitle.toString();

                break;
            case 3:
                mTitle = getString(R.string.menu_group_title_fn);
                mIcon = getResources().getDrawable(R.drawable.ic_ab_fn);
                Constants.Mtitle = mTitle.toString();
                break;
            case 4:
                mTitle = getString(R.string.menu_group_title_stantsiya);
                mIcon = getResources().getDrawable(R.drawable.ic_ab_st);
                Constants.Mtitle = mTitle.toString();
                break;
            case 5:
                mTitle = getString(R.string.menu_group_title_events);
                mIcon = getResources().getDrawable(R.drawable.ic_ab_a);
                Constants.Mtitle = mTitle.toString();
                break;
        }
    }

 //   String[] items;

//    ActionBar.OnNavigationListener callback = new ActionBar.OnNavigationListener() {
//
//        //  String[] items = getResources().getStringArray(R.array.actions); // List items from res
//
//        @Override
//        public boolean onNavigationItemSelected(int position, long id) {
//
//            // Do stuff when navigation item is selected
//
//            Log.d("NavigationItemSelected", items[position]); // Debug
//
//            switch (position) {
//                case 0: {
//                    addFragment(FragmentMakePost.newInstance(Constants.GROUP_ID, 0, 0));
//                }
//                break;
//                case 1: {
//                    addFragment(FragmentWall.newInstance(true));
//                }
//                break;
//
//                case 2: {
//                    if (Constants.isMember == 0) {
//                        VKHelper.groupJoin(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
//                            @Override
//                            public void onComplete(final VKResponse response) {
//                                super.onComplete(response);
//                                Toast.makeText(getApplicationContext(), getString(R.string.group_joined), Toast.LENGTH_SHORT).show();
//                                replaceFragment(FragmentWall.newInstance(false));
//                            }
//
//                            @Override
//                            public void onError(final VKError error) {
//                                super.onError(error);
//                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
//                            }
//                        });
//                    } else {
//                        VKHelper.groupLeave(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
//                            @Override
//                            public void onComplete(final VKResponse response) {
//                                super.onComplete(response);
//                                Toast.makeText(getApplicationContext(), getString(R.string.group_leaved), Toast.LENGTH_SHORT).show();
//                                replaceFragment(FragmentWall.newInstance(false));
//                            }
//
//                            @Override
//                            public void onError(final VKError error) {
//                                super.onError(error);
//                                OfflineMode.onErrorToast(getApplicationContext());
//                            }
//                        });
//                    }
//                }
//                break;
//
//            }
//            Toast.makeText(getApplicationContext(), "SelectedItem is: " + getSupportActionBar().getSelectedNavigationIndex(), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        ;
//
//
//    };

    public void restoreActionBar() {
//        final ActionBar actionBar = getSupportActionBar();
//
//        actionBar.setIcon(mIcon);
//
//        if (Constants.isMember == 0) {
//            items = getResources().getStringArray(R.array.menu_join_group);
//
//        } else {
//            items = getResources().getStringArray(R.array.menu_leave_group);
//        }
//        if (VKSdk.isLoggedIn()) {
//            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setTitle(mTitle);
//            list = new ActionBarArrayAdapter(getApplicationContext(), items, mTitle);
//            list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//
//
//
//
//            actionBar.setListNavigationCallbacks(list, callback);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(mTitle);
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_shape_background));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setLogo(mIcon);

    }

//            actionBar.setListNavigationCallbacks(list, new ActionBar.OnNavigationListener() {
//                @Override
//                public boolean onNavigationItemSelected(int position, long id) {
//                    getSupportActionBar().setSelectedNavigationItem(position);

//                    switch (getSupportActionBar().getSelectedNavigationIndex()) {
//                        case 0:
//                            addFragment(FragmentMakePost.newInstance(Constants.GROUP_ID, 0, 0));
//                            break;
//                        case 1:
//                            addFragment(FragmentWall.newInstance(true));
//                            break;
//
//                        case 2:
//                            if (Constants.isMember==0){
//                            VKHelper.groupJoin(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
//                                @Override
//                                public void onComplete(final VKResponse response) {
//                                    super.onComplete(response);
//                                    Toast.makeText(getApplicationContext(), getString(R.string.group_joined), Toast.LENGTH_SHORT).show();
//                                     replaceFragment(FragmentWall.newInstance(false));
//                                }
//
//                                @Override
//                                public void onError(final VKError error) {
//                                    super.onError(error);
//                                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
//                                }
//                            });
//                    } else {
//                        VKHelper.groupLeave(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
//                            @Override
//                            public void onComplete(final VKResponse response) {
//                                super.onComplete(response);
//                                Toast.makeText(getApplicationContext(), getString(R.string.group_leaved), Toast.LENGTH_SHORT).show();
//                                  replaceFragment(FragmentWall.newInstance(false));
//                            }
//
//                            @Override
//                            public void onError(final VKError error) {
//                                super.onError(error);
//                                OfflineMode.onErrorToast(getApplicationContext());
//                            }
//                        });
//                    }
//
//                            break;
//
//                    }

        //   Toast.makeText(getApplicationContext(), "SelectedItem is: " + getSupportActionBar().getSelectedNavigationIndex(), Toast.LENGTH_SHORT).show();
        //  return true;
        // }
        //   });
//        else {
//            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//            actionBar.setDisplayShowTitleEnabled(true);
//        }
 //   }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mNavigationDrawerFragment.onOptionsItemSelected(item)) {
            return true;
        }
//        switch (item.getItemId()) {
//            case R.id.action_go_home:
//
//                return true;
//
//            default:
        return super.onOptionsItemSelected(item);

        //  }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == PICK_FROM_CAMERA) {
            FragmentPhotoFromCamera fragmentPhotoFromCamera = new FragmentPhotoFromCamera().newInstance(Constants.tempCameraPhotoFile);
            addFragment(fragmentPhotoFromCamera);
        }
    }

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(Constants.S_MY_SCOPE);
        }

        @Override
        public void onAccessDenied(final VKError authorizationError) {
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            VKHelper.getMyselfInfo(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);
                    VKHelper.UserObject user = VKHelper.getUserFromResponse(response);
                    if (user.id == 0) {
                        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
                        user.id = sPref.getLong("uid", 0); //TODO че делать если нулл?
                        Constants.USER_ID = user.id;
                        return;
                    }
                    Constants.USER_ID = user.id;
                    ItemDataSetter.saveUserId(Constants.USER_ID);

                    mNavigationDrawerFragment.refreshNavigationHeader(user);
                    ((FragmentWall) getSupportFragmentManager().getFragments().get(1)).checkFabSuggest();
                }

                @Override
                public void onError(final VKError error) {
                    super.onError(error);
                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                }
            });
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            VKHelper.getMyselfInfo(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);
                    VKHelper.UserObject user = VKHelper.getUserFromResponse(response);
                    if (user.id == 0) {
                        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
                        user.id = sPref.getLong("uid", 0); //TODO че делать если нулл?
                        Constants.USER_ID = user.id;
                        return;
                    }
                    Constants.USER_ID = user.id;
                    ItemDataSetter.saveUserId(Constants.USER_ID);

                    mNavigationDrawerFragment.refreshNavigationHeader(user);

                    ((FragmentWall) getSupportFragmentManager().getFragments().get(1)).checkFabSuggest();
                }

                @Override
                public void onError(final VKError error) {
                    super.onError(error);
                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                }
            });
        }
    };
    Fragment fragment = null;

    @Override
    public void onNavigationDrawerItemSelected(int groupPosition) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        long vkGroupId;
        switch (groupPosition) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                vkGroupId = setGroupId(groupPosition);
                Constants.GROUP_ID = vkGroupId;

                onSectionAttached(groupPosition);
                fragment = FragmentWall.newInstance(false);

                break;
            case 6:
                finish();
                break;
        }

        if (groupPosition != 6) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
            replaceFragment(fragment);
        }
        restoreActionBar();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportActionBar().show();
            mNavigationDrawerFragment.toggle();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this.isFinishing()) {
            stopService(new Intent(this, AudioPlayerService.class));
            AudioPlayerService.cancelNotification(this, Constants.notifID);
            if (Constants.mediaPlayer != null) {
                Constants.mediaPlayer.stop();
                Constants.playedPausedRecord.isPaused = true;
                Constants.playedPausedRecord.isPlayed = false;
                Constants.previousSeekBarState.setVisibility(View.INVISIBLE);
                try {
                    Constants.tempThread.interrupt();
                } catch (NullPointerException e) {
                }
                AudioPlayer.progressBar(Constants.previousSeekBarState).interrupt();
            }
        }
    }


}

