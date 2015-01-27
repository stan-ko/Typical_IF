package typical_if.android.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.ExpandableListAdapter;
import typical_if.android.view.AnimatedExpandableListView;

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
    private ExpandableListAdapter mExpandapleListAdapter;

    public static DrawerLayout mDrawerLayout;
    public static AnimatedExpandableListView mDrawerListView;
    public static View mFragmentContainerView;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    View v;
    TextView navDrawTitle;
    List<String> listDataHeader;
    SparseArray<List<String>> listDataChild;

    RelativeLayout aboutUs;

    public NavigationDrawerFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);


        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
        // Select either the default item (0) or the last selected item.
        selectItem(0, 0);
    }

    public void refreshNavigationDrawer() {
        mExpandapleListAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        setRetainInstance(true);
        mDrawerListView = (AnimatedExpandableListView) v.findViewById(R.id.navigation_drawer_exp_list);
        mDrawerListView.setGroupIndicator(null);

        navDrawTitle = (TextView) v.findViewById(R.id.navigation_drawer_title);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoescb.ttf");
        navDrawTitle.setTypeface(font);

        prepareListData();

        aboutUs = (RelativeLayout) v.findViewById(R.id.aboutUsLayout);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = ((MainActivity) getActivity());
                mainActivity.addFragment(FragmentAboutUs.newInstance());
                closeDrawer();
            }
        });

//        ImageView imageView = (ImageView) v.findViewById(R.id.photo1);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((MainActivity)getActivity()).addFragment(FragmentAboutUs.newInstance());
//            }
//        });

        mExpandapleListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        mDrawerListView.setAdapter(mExpandapleListAdapter);
        mDrawerListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listDataChild.get(groupPosition) == null) {
                    if (mDrawerLayout != null) {
                        mCallbacks.onNavigationDrawerItemSelected(groupPosition, 0);
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                } else {
                    if (mDrawerListView.isGroupExpanded(groupPosition)) {
                        mDrawerListView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        mDrawerListView.expandGroupWithAnimation(groupPosition);
                    }
                }

                return true;
            }

        });

        mDrawerListView.setOnGroupExpandListener(new AnimatedExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    mDrawerListView.collapseGroupWithAnimation(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                List<String> list = listDataChild.get(groupPosition);
                if (mDrawerLayout != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDrawerLayout.closeDrawer(mFragmentContainerView);
                        }
                    });

                    mCallbacks.onNavigationDrawerItemSelected(groupPosition, childPosition);
                }
                return false;
            }
        });

        return v;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new SparseArray<List<String>>();

        // Adding child data
        List<String> tf = new ArrayList<String>();
        setChildItems(tf, 0);

        List<String> tz = new ArrayList<String>();
        setChildItems(tz, 1);

        List<String> fb = new ArrayList<String>();
        setChildItems(fb, 2);

        List<String> fn = new ArrayList<String>();
        setChildItems(fn, 3);
       // int y=0 ;


        listDataHeader.add(getString(R.string.menu_group_title_tf));
        listDataHeader.add(getString(R.string.menu_group_title_tz));
        listDataHeader.add(getString(R.string.menu_group_title_fb));
        listDataHeader.add(getString(R.string.menu_group_title_fn));
        listDataHeader.add(getString(R.string.menu_group_title_events));
        listDataHeader.add("");
        listDataHeader.add(getString(R.string.settings));
        listDataHeader.add(getString(R.string.menu_group_about_us));
        listDataHeader.add(getString(R.string.menu_group_exit));
        if (Constants.refresherDrawerCounter>0){
            refreshDrawer();
            Constants.refresherDrawerCounter=0;
        }

 }

    public void refreshDrawer (){
        listDataHeader.clear();
        listDataHeader.add(getString(R.string.menu_group_title_tf));
        listDataHeader.add(getString(R.string.menu_group_title_tz));
        listDataHeader.add(getString(R.string.menu_group_title_fb));
        listDataHeader.add(getString(R.string.menu_group_title_fn));
        listDataHeader.add(getString(R.string.menu_group_title_events));
        listDataHeader.add("");
        listDataHeader.add(getString(R.string.settings));
        listDataHeader.add(getString(R.string.menu_group_about_us));
        listDataHeader.add(getString(R.string.menu_group_exit));
    }



    public void setChildItems(List<String> list, Integer groupIndex) {
        list.add(getString(R.string.look_wall));
        list.add(getString(R.string.look_albums));
        listDataChild.put(groupIndex, list);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

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
                    showGlobalContextActionBar();
                    ((MainActivity) getActivity()).getSupportActionBar().show();
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

    private void selectItem(int groupPosition, int childPosition) {

        if (mDrawerListView != null) {
            //mDrawerListView.setSelectedChild(groupPosition, childPosition, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(groupPosition, childPosition);
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
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.main, menu);
            showGlobalContextActionBar();
        } else {

        }

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
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        } else {

        }
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.menu_group_title_tf);
        actionBar.setIcon(R.drawable.tf_logo);
    }

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
        void onNavigationDrawerItemSelected(int groupPosition, int childPosition);

        ;
    }
}