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
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import typical_if.android.AudioPlayer;
import typical_if.android.AudioPlayerService;
import typical_if.android.Constants;
import typical_if.android.FloatingToolbar_ButtonHelper;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.VKRequestListener;
import typical_if.android.event.MainActivityAddFragmentEvent;
import typical_if.android.fragment.FragmentComments;
import typical_if.android.fragment.FragmentFullScreenViewer;
import typical_if.android.fragment.FragmentPhotoFromCamera;
import typical_if.android.fragment.FragmentWall;
import typical_if.android.fragment.NavigationDrawerFragment;
import typical_if.android.fragment.PollFragment;


public class MainActivity extends DialogActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentFullScreenViewer.OnFragmentInteractionListener,
        FragmentComments.OnFragmentInteractionListener, PollFragment.OnFragmentInteractionListener {

    private static final int PICK_FROM_CAMERA = 1;
    private static String sTokenKey = "VK_ACCESS_TOKEN";
    public NavigationDrawerFragment mNavigationDrawerFragment;
   public static Toolbar toolbar;
    void showAlertChanges() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_of_alert_main)
                .setCancelable(false)
                .setMessage(R.string.сhanges_of_new_version)
                .setPositiveButton(R.string.positive_button_alert_of_main, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        builder.setCancelable(true);
                    }
                });

        builder.create().show();
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);


        if (OfflineMode.getIsFirstRunMainActivity()) {
            OfflineMode.setNotFirstRunMainActivity();
            showAlertChanges();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

//
        // ensure that the view is available if we add the fragment
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.
        final ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        final View child = decor.getChildAt(0);
        decor.removeView(child);
        final ViewGroup container = (ViewGroup) drawer.findViewById(R.id.drawer_content); // This is the container we defined just now.
        container.addView(child, 0);
        drawer.findViewById(R.id.navigation_drawer).setPadding(0, getStatusBarHeight(), 0, 0);
        decor.addView(drawer);
        Constants.myIntent = new Intent(this, AudioPlayerService.class);
        Constants.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        try {
            if (getIntent().getExtras() != null) {
                onNotificationClick(mNavigationDrawerFragment, getIntent());
            }
        } catch (NullPointerException npe) {
        }

        // VK init
        VKUIHelper.onCreate(this);
        VKSdk.initialize(sdkListener, Constants.APP_ID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
        VKSdk.wakeUpSession(this);

        ItemDataSetter.fragmentManager = getSupportFragmentManager();
        ItemDataSetter.fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    try {
                        if (!Constants.isPollFragmentLoaded) {
                            if (Constants.makePostMenu.size() == 3) {
                            }
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
    protected void onPause() {
        super.onPause();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn = powerManager.isScreenOn();

        if (!isScreenOn) {


            if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("isClickable")) {
                onNavigationDrawerItemSelected(5, false);
            } else
                onNavigationDrawerItemSelected((int) (long) OfflineMode.loadLong(Constants.VK_GROUP_ID), false);

            // The screen has been locked
            // do stuff...
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onNewIntent(Intent intent) {
        onNotificationClick(mNavigationDrawerFragment, intent);
    }

    private void onNotificationClick(NavigationDrawerFragment mNavigationDrawerFragment, Intent notifIntent) {
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



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == PICK_FROM_CAMERA) {
            FragmentPhotoFromCamera fragmentPhotoFromCamera = FragmentPhotoFromCamera.newInstance(Constants.tempCameraPhotoFile);
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
            VKHelper.getMyselfInfo(new VKRequestListener() {
                @Override
                public void onSuccess() {
                    VKHelper.UserObject user = VKHelper.getUserFromResponse(vkResponse);
                    if (user.id == 0) {
                        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
                        user.id = sPref.getLong("uid", 0); //TODO че делать если нулл?
                        Constants.USER_ID = user.id;
                        return;
                    }
                    Constants.USER_ID = user.id;
                    OfflineMode.saveUserId(Constants.USER_ID);

                    mNavigationDrawerFragment.refreshNavigationHeader(user);
//                    ((FragmentWall) getSupportFragmentManager().getFragments().get(1)).checkFabSuggest();
                }

//                @Override
//                public void onError() {
//                    showErrorToast();
//                }
            });
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            VKHelper.getMyselfInfo(new VKRequestListener() {
                @Override
                public void onSuccess() {
                    VKHelper.UserObject user = VKHelper.getUserFromResponse(vkResponse);
                    if (user.id == 0) {
                        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
                        user.id = sPref.getLong("uid", 0); //TODO че делать если нулл?
                        Constants.USER_ID = user.id;
                        return;
                    }
                    Constants.USER_ID = user.id;
                    OfflineMode.saveUserId(Constants.USER_ID);

                    mNavigationDrawerFragment.refreshNavigationHeader(user);

//                    ((FragmentWall) getSupportFragmentManager().getFragments().get(1)).checkFabSuggest();
                }

//                @Override
//                public void onError() {
//                    showErrorToast();
//                }
            });
        }
    };
    Fragment fragment = null;

    @Override
    public void onNavigationDrawerItemSelected(int groupPosition, boolean isResume) {


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
                OfflineMode.saveLong(vkGroupId, Constants.VK_GROUP_ID);
                FloatingToolbar_ButtonHelper.setToolbarAttachments(groupPosition);
                fragment = FragmentWall.newInstance(false);


                break;
            case 6:
                finish();
                //   Log.d("finish"," - ---- -- - - -- - --------------------------  -- - " );
                break;
            case 7:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
        }


//        Log.d("OnNavigationItemSelected", " status: position = " + groupPosition);
        if (groupPosition != 6) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
            replaceFragment(fragment);
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
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
            AudioPlayer.stop();
        }

    }


    @SuppressWarnings("unused") // used via EventBus but is Lint undetectable
    public void onEventMainThread(MainActivityAddFragmentEvent event) {
        addFragment(event.fragmentVideoView);
    }

}

