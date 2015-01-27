package typical_if.android.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

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
import typical_if.android.fragment.FragmentAlbumsList;
import typical_if.android.fragment.FragmentComments;
import typical_if.android.fragment.FragmentFullScreenViewer;
import typical_if.android.fragment.FragmentPhotoFromCamera;
import typical_if.android.fragment.FragmentWall;
import typical_if.android.fragment.NavigationDrawerFragment;


public class MainActivity extends DialogActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentFullScreenViewer.OnFragmentInteractionListener,
        FragmentComments.OnFragmentInteractionListener,ActionBar.OnNavigationListener {



    private Drawable mIcon;
    private CharSequence mTitle;
    private static final int PICK_FROM_CAMERA = 1;
    private static String sTokenKey = "VK_ACCESS_TOKEN";
    public NavigationDrawerFragment mNavigationDrawerFragment;
   // String[] data = new String[] { "one", "two", "three" };
    ActionBarArrayAdapter list ;
    ActionBar actionBar ;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         try {if (OfflineMode.loadInt("surprise")<15){OfflineMode.saveInt(0, "surprise");}} catch (Exception e){}

         requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
         setContentView(R.layout.activity_main);
         ActionBar actionBar = getSupportActionBar();

        Constants.mainActivity = this;
        Constants.myIntent = new Intent(this, AudioPlayerService.class);
        Constants.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        if(mNavigationDrawerFragment.isDrawerOpen()){
            actionBar.setDisplayShowTitleEnabled(true);
        }
        else {
            actionBar.setDisplayShowTitleEnabled(false);
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
                    } catch (NullPointerException e) {}
                }
            }
        });



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
            default:
                return Constants.ZF_ID;
        }

    }
    public void onSectionAttached(final long groupIndex) {
        switch ((int) groupIndex) {
            case 0:
                mTitle = getString(R.string.menu_group_title_tf);
                mIcon = getResources().getDrawable(R.drawable.tf_logo);
                Constants.Mtitle=mTitle.toString();
                break;
            case 1:
                mTitle = getString(R.string.menu_group_title_tz);
                mIcon = getResources().getDrawable(R.drawable.tz_logo);
                Constants.Mtitle=mTitle.toString();
                break;
            case 2:
                mTitle = getString(R.string.menu_group_title_fb);
                mIcon = getResources().getDrawable(R.drawable.fb_logo);
                Constants.Mtitle=mTitle.toString();

                break;
            case 3:
                mTitle = getString(R.string.menu_group_title_fn);
                mIcon = getResources().getDrawable(R.drawable.fn_logo);
                Constants.Mtitle=mTitle.toString();
                break;
            case 4:
                mTitle = getString(R.string.menu_group_title_events);
                mIcon = getResources().getDrawable(R.drawable.abc_ic_cab_done_holo_dark);
                Constants.Mtitle=mTitle.toString();
                break;
        }
    }
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(mTitle);
        actionBar.setIcon(mIcon);
        list = new ActionBarArrayAdapter(getApplicationContext(),getResources().getStringArray(R.array.menu_join_group),mTitle);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(list, this);
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
            mNavigationDrawerFragment.refreshNavigationDrawer();
            VKHelper.getMyselfInfo(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);
                    long userId = VKHelper.getUserIdFromResponse(response);
                    if (userId == 0) {
                        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
                        userId = sPref.getLong("uid",0); //TODO че делать если нулл?
                        Constants.USER_ID = userId;
                        return;
                    }
                    Constants.USER_ID = userId;
                    ItemDataSetter.saveUserId(Constants.USER_ID);
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
            mNavigationDrawerFragment.refreshNavigationDrawer();
            VKHelper.getMyselfInfo(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);
                    long userId = VKHelper.getUserIdFromResponse(response);
                    if (userId == 0) {
                        final SharedPreferences sPref = TIFApp.getAppContext().getSharedPreferences("uid", Activity.MODE_PRIVATE);
                        userId = sPref.getLong("uid",0); //TODO че делать если нулл?
                        Constants.USER_ID = userId;
                        return;
                    }
                    Constants.USER_ID = userId;
                    ItemDataSetter.saveUserId(Constants.USER_ID);
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
    public void onNavigationDrawerItemSelected(int groupPosition, int childPosition) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        long vkGroupId;
        switch (groupPosition) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                vkGroupId = setGroupId(groupPosition);///////////////////////////////////////////////////////////////////////////////////////////////////
                Constants.GROUP_ID = vkGroupId;


                onSectionAttached(groupPosition);

                if (childPosition == 0) {

                    fragment = FragmentWall.newInstance(false);


                } else if (childPosition == 1) {

                    fragment = FragmentAlbumsList.newInstance(1);

                }

                break;
            case 5:
                if (VKSdk.isLoggedIn()) {
                    VKSdk.logout();
                    mNavigationDrawerFragment.refreshNavigationDrawer();
                } else {
                    VKSdk.authorize(Constants.S_MY_SCOPE, true, true);
                }
                break;
            case 6:
                changeLanguage();
                break;
            case 7:
                finish();
                break;
        }

        if (groupPosition != 6 && groupPosition != 5) {
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
    protected void onDestroy(){
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

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    return true;
    }
}

