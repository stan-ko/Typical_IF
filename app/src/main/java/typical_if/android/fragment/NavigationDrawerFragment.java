package typical_if.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.List;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.DrawerListViewAdapter;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerListViewAdapter drawerListViewAdapter;

    public static DrawerLayout mDrawerLayout;
    public static ListView mDrawerListView;
    public static View mFragmentContainerView;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    View v;
    HeaderViewHolder headerViewHolder;
    List<DrawerListViewAdapter.GroupObject> listDataHeader;

    private MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        activity = ((MainActivity) getActivity());

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
        // Select either the default item (0) or the last selected item.

        if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().getExtras().getBoolean("isClickable")) {
            selectItem(5);
        } else {
            selectItem(0);
        }
    }

    private void startLoadingWall(int groupPosition) {
        final int groupPositionH = groupPosition;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallbacks.onNavigationDrawerItemSelected(groupPositionH);
                    }
                });
            }
        }, 360);
    }

    public void refreshNavigationHeader(VKHelper.UserObject user) {
        if (VKSdk.isLoggedIn()) {
            headerViewHolder.btLogin.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_lock_open_white_24dp));

            ImageLoader.getInstance().displayImage(user.photo, headerViewHolder.imgAvatar, TIFApp.additionalOptions);
            headerViewHolder.txtTitle.setText(user.fullName);

            headerViewHolder.imgAvatar.setTag("http://vk.com/id" + String.valueOf(user.id));
            headerViewHolder.imgAvatar.setOnTouchListener(touchListener);
        } else {
            headerViewHolder.btLogin.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_lock_white_24dp));

            headerViewHolder.txtTitle.setText(getString(R.string.tif_title_header));
            headerViewHolder.imgAvatar.setImageResource(R.drawable.mobile_tf_logo);
            headerViewHolder.imgAvatar.setTag("");
            headerViewHolder.imgAvatar.setOnTouchListener(null);
        }

        drawerListViewAdapter.notifyDataSetChanged();
    }

    public static class HeaderViewHolder {
        public final RoundedImageView imgAvatar;
        public final RobotoTextView txtTitle;
        public final ImageButton btExit;
        public final ImageButton btAbout;
        public final ImageButton btSwitch;
        public final ImageButton btLogin;

        HeaderViewHolder(View view) {
            this.imgAvatar = (RoundedImageView) view.findViewById(R.id.img_nav_draw);
            this.txtTitle = (RobotoTextView) view.findViewById(R.id.txt_title_nav_draw);
            this.btExit = (ImageButton) view.findViewById(R.id.ibt_exit_nav_draw);
            this.btAbout = (ImageButton) view.findViewById(R.id.ibt_about_us_nav_draw);
            this.btSwitch = (ImageButton) view.findViewById(R.id.ibt_change_language_nav_draw);
            this.btLogin = (ImageButton) view.findViewById(R.id.ibt_login_nav_draw);
        }
    }

    public final View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RoundedImageView img = (RoundedImageView) v;
            img.setBorderColor(getResources().getColor(R.color.music_progress));

            Uri uri = Uri.parse((String) v.getTag());

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    img.setBorderWidth(4f);
                    img.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                    img.getContext().startActivity(new Intent(android.content.Intent.ACTION_VIEW, uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


                case MotionEvent.ACTION_CANCEL: {
                    img.setBorderWidth(0f);
                    img.invalidate();
                    break;
                }
            }

            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        setRetainInstance(true);

        mDrawerListView = (ListView) v.findViewById(R.id.navigation_drawer_exp_list);
        mDrawerListView.setLayoutParams(
                new ViewGroup.LayoutParams(
                        (int) (TIFApp.getDisplayWidth() - getResources().getDimension(R.dimen.navigation_drawer_width_negative)),
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        View header = inflater.inflate(R.layout.navigation_drawer_header, null);
        headerViewHolder = new HeaderViewHolder(header);

        headerViewHolder.btAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.addFragment(FragmentAboutUs.newInstance());
                closeDrawer();
            }
        });

        headerViewHolder.btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        headerViewHolder.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VKSdk.isLoggedIn()) {
                    VKSdk.logout();
                    refreshNavigationHeader(null);
                } else {
                    VKSdk.authorize(Constants.S_MY_SCOPE, true, true);
                }
            }
        });

        headerViewHolder.btSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeLanguage();
            }
        });

        mDrawerListView.addHeaderView(header);

        prepareListData();

        drawerListViewAdapter = new DrawerListViewAdapter(getActivity(), listDataHeader);
        mDrawerListView.setAdapter(drawerListViewAdapter);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                   @Override
                                                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                       if (mDrawerLayout != null) {
                                                           closeDrawer();
                                                           startLoadingWall(--position);
                                                       }
                                                   }
                                               }
        );

        return v;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<DrawerListViewAdapter.GroupObject>();

        listDataHeader.add(new DrawerListViewAdapter.GroupObject(getString(R.string.menu_group_title_tf), R.drawable.ic_tf));
        listDataHeader.add(new DrawerListViewAdapter.GroupObject(getString(R.string.menu_group_title_tz), R.drawable.ic_tz));
        listDataHeader.add(new DrawerListViewAdapter.GroupObject(getString(R.string.menu_group_title_fb), R.drawable.ic_fb));
        listDataHeader.add(new DrawerListViewAdapter.GroupObject(getString(R.string.menu_group_title_fn), R.drawable.ic_fn));
        listDataHeader.add(new DrawerListViewAdapter.GroupObject(getString(R.string.menu_group_title_stantsiya), R.drawable.ic_st));
        listDataHeader.add(new DrawerListViewAdapter.GroupObject(getString(R.string.menu_group_title_events), R.drawable.ic_a));
        listDataHeader.add(new DrawerListViewAdapter.GroupObject(getString(R.string.menu_group_exit), R.drawable.ic_exit_to_app_white_24dp));

    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    boolean isDrawerOpen = false;

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > .55 && !isDrawerOpen) {
                    onDrawerOpened(drawerView);
                    isDrawerOpen = true;
                } else if (slideOffset < .45 && isDrawerOpen) {
                    onDrawerClosed(drawerView);
                    isDrawerOpen = false;
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!isAdded()) {
                    return;
                } else {
//                    showGlobalContextActionBar();
//                    ((MainActivity) getActivity()).getSupportActionBar().show();
                    FragmentWall.setDisabledMenu();
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int groupPosition) {
        if (mDrawerLayout != null) {
            closeDrawer();
        }
        if (mCallbacks != null) {
            startLoadingWall(groupPosition);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            ((MainActivity) getActivity()).getSupportActionBar().show();
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
//        if (mDrawerLayout != null && isDrawerOpen()) {
//            inflater.inflate(R.menu.main, menu);
//            showGlobalContextActionBar();
//        } else {
//
//        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        FragmentWall.setDisabledMenu();
        super.onPrepareOptionsMenu(menu);
//        if (mDrawerLayout != null && isDrawerOpen()) {
//            showGlobalContextActionBar();
//        } else {
//
//        }
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
//    private void showGlobalContextActionBar() {
//        ActionBar actionBar = getActionBar();
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setTitle(R.string.menu_group_title_tf);
//        actionBar.setIcon(R.drawable.mobile_tf_logo);
//    }
    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public void toggle() {
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }


    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int groupPosition);

    }
}