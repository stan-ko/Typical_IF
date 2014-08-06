package typical_if.android.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import java.util.ArrayList;
import java.util.List;

import typical_if.android.AnimatedExpandableListView;
import typical_if.android.R;
import typical_if.android.adapter.ExpandableListAdapter;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_GROUP_POSITION = "selected_navigation_drawer_group_position";
    private static final String STATE_SELECTED_CHILD_POSITION = "selected_navigation_drawer_child_position";

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

    private DrawerLayout mDrawerLayout;
    private AnimatedExpandableListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedGroupPosition = 0;
    private int mCurrentSelectedChildPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    View v;
    List<String> listDataHeader;
    SparseArray<List<String>> listDataChild;

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
            mCurrentSelectedGroupPosition = savedInstanceState.getInt(STATE_SELECTED_GROUP_POSITION);
            mCurrentSelectedChildPosition = savedInstanceState.getInt(STATE_SELECTED_CHILD_POSITION);
            mFromSavedInstanceState = true;
        }
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedGroupPosition, mCurrentSelectedChildPosition);
    }

    public void refreshNavigationDrawer() {
        mExpandapleListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        setRetainInstance(true);
        mDrawerListView = (AnimatedExpandableListView) v.findViewById(R.id.navigation_drawer_exp_list);
        mDrawerListView.setGroupIndicator(null);

        prepareListData();

        mExpandapleListAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        mDrawerListView.setAdapter(mExpandapleListAdapter);

        mDrawerListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                mCurrentSelectedGroupPosition = groupPosition;
                if (listDataChild.get(groupPosition) == null) {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                        mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedGroupPosition, 0);
                    }
                } else {
                    if (mDrawerListView.isGroupExpanded(mCurrentSelectedGroupPosition)) {
                        mDrawerListView.collapseGroupWithAnimation(mCurrentSelectedGroupPosition);
                    } else {
                        mDrawerListView.expandGroupWithAnimation(mCurrentSelectedGroupPosition);
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
                mCurrentSelectedGroupPosition = groupPosition;
                mCurrentSelectedChildPosition = childPosition;
                List<String> list = listDataChild.get(groupPosition);
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                    mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedGroupPosition, mCurrentSelectedChildPosition);
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

        listDataHeader.add(getString(R.string.menu_group_title_tf));
        listDataHeader.add(getString(R.string.menu_group_title_tz));
        listDataHeader.add(getString(R.string.menu_group_title_fb));
        listDataHeader.add(getString(R.string.menu_group_title_fn));
        listDataHeader.add(getString(R.string.menu_group_title_events));
        listDataHeader.add("");

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
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
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
        mCurrentSelectedGroupPosition = groupPosition;
        mCurrentSelectedChildPosition = childPosition;

        if (mDrawerListView != null) {
            //mDrawerListView.setSelectedChild(groupPosition, childPosition, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(mCurrentSelectedGroupPosition, mCurrentSelectedChildPosition);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
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
        outState.putInt(STATE_SELECTED_GROUP_POSITION, mCurrentSelectedGroupPosition);
        outState.putInt(STATE_SELECTED_CHILD_POSITION, mCurrentSelectedChildPosition);
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
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
           // Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.menu_group_title_tf);
        actionBar.setIcon(R.drawable.tf_logo);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int groupPosition, int childPosition);
    }
}