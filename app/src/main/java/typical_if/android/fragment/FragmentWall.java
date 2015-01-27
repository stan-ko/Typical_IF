package typical_if.android.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.SwipeRefreshLayout.SwipeRefreshLayout;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.ActionBarArrayAdapter;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Wall.Wall;

import static com.vk.sdk.VKUIHelper.getApplicationContext;


/**
 * Created by admin on 14.07.2014.
 */


public class FragmentWall extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int INITIAL_DELAY_MILLIS = 300;


    static ListView wallListView;
    WallAdapter adapter;
    ActionBar actionBar;

    RelativeLayout spinnerLayout;
    View rootView;
    PauseOnScrollListener pauseOnScrollListener;
    LayoutInflater inflaterGlobal;
    final int offsetO = 0;
    final int countPostDefaultForOffset = 100;
    public static int playableLogoRes;
    String postColor;
    JSONObject jsonObjectOld;

    int Offset = Constants.TIF_VK_PRELOAD_POSTS_COUNT;//SplashActivity.getCountOfPosts();
    static boolean isSuggested;

    boolean temp = true;
    boolean temp2 = true;
    boolean enable = false;
    Bundle arguments;
    SwipeRefreshLayout swipeView;


//    final Thread t = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while(threadIsStarted){
//            Offset = Offset + 100;
//            endlessGet(Offset);}
//        }
//    });

    AbsListView.OnScrollListener onScrollListenerObject = new AbsListView.OnScrollListener() {

        int mLastFirstVisibleItem = 0;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            temp = true;
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {


            final int lastItem = firstVisibleItem + visibleItemCount;

            if (absListView.getId() == wallListView.getId()) {

                final int currentFirstVisibleItem = wallListView.getFirstVisiblePosition();
                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                actionBar.hide();
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    actionBar.show();
                }

                mLastFirstVisibleItem = currentFirstVisibleItem;
            }

            if (lastItem == totalItemCount - 20 & temp2) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                     Offset = Offset + 100;
                     endlessGet(Offset);}
                  }).start();
                     temp2 = false;
            }

            if (lastItem == totalItemCount & temp) {
                endlessAdd(lastItem);
                temp = false;
                temp2 = true;
            }

            if (absListView != null && absListView.getChildCount() > 0) {
                boolean firstItemVisible = absListView.getFirstVisiblePosition() == 0;
                boolean topOfFirstItemVisible = absListView.getChildAt(0).getTop() == 0;
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            swipeView.setEnabled(enable);

        }

    };


    public static FragmentWall newInstance(boolean isSuggestedParam) {
        FragmentWall fragment = new FragmentWall();
        Bundle args = new Bundle();
        isSuggested = isSuggestedParam;
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentWall() {
    }

    @Override
    public void onDetach() {
        if (isSuggested) {
            ((MainActivity) getActivity()).getSupportActionBar().show();
            ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
            setDisabledMenu();
        }

        super.onDetach();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wall, container, false);
        spinnerLayout = (RelativeLayout) rootView.findViewById(R.id.spinner_layout);
        inflaterGlobal = inflater;
        arguments = getArguments();
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();


        postColor = ItemDataSetter.getPostColor(Constants.GROUP_ID);
        playableLogoRes = ItemDataSetter.getPlayingLogo(Constants.GROUP_ID);
        pauseOnScrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, true, onScrollListenerObject);
        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);

        wallListView = (ListView) rootView.findViewById(R.id.listViewWall);

        TextView padding = new TextView(getApplicationContext());
        padding.setBackgroundColor(Color.TRANSPARENT);
        padding.setHeight(ItemDataSetter.setInDp(48));
        wallListView.addHeaderView(padding);

        if (!isSuggested) {

            jsonObjectOld = OfflineMode.loadJSON(Constants.GROUP_ID);
            initGroupWall(jsonObjectOld, inflater);

            swipeView.setOnRefreshListener(this);
            swipeView.setColorScheme(R.color.ab_text_color, android.R.color.holo_green_light, android.R.color.holo_orange_dark, R.color.music_progress);

        } else {
            setDisabledMenu();
            VKHelper.getSuggestedPosts(Constants.GROUP_ID, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(final VKResponse response) {
                    super.onComplete(response);
                    initGroupWall(response.json, inflater);
                    wallListView.setOnScrollListener(null);

                    swipeView.setOnRefreshListener(null);
                    swipeView.setEnabled(false);
                    swipeView.setRefreshing(false);
                }

                @Override
                public void onError(final VKError error) {
                    super.onError(error);
                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                }
            });
        }

        return rootView;
    }

    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater) {
        Wall wall = VKHelper.getGroupWallFromJSON(jsonObject);
        FragmentManager fragmentManager = getFragmentManager();

        if (wall.posts.size() == 0) {
            try {
                fragmentManager.popBackStack();
            }catch (NullPointerException npe){
                Toast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), getString(R.string.no_suggested_posts), Toast.LENGTH_SHORT).show();
        }

        if (adapter == null) {
            adapter = new WallAdapter(wall, inflater, fragmentManager, postColor, isSuggested);

//            SwingRightInAnimationAdapter swingBottomInAnimationAdapter = new SwingRightInAnimationAdapter(new SwipeDismissAdapter(adapter, onDismissCallback));
//            swingBottomInAnimationAdapter.setAbsListView(wallListView);
//            assert swingBottomInAnimationAdapter.getViewAnimator() != null;
//            swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
//
//            wallListView.setAdapter(swingBottomInAnimationAdapter);
           // setBottomAdapter(wallListView, adapter);
            wallListView.setAdapter(adapter);
            wallListView.setOnScrollListener(pauseOnScrollListener);
        } else {
            adapter.setWall(wall);
        }

        spinnerLayout.setVisibility(View.GONE);

    }

    int mCurCheckPosition = 0;
    private AnimationAdapter mAnimAdapter;

    private void setBottomAdapter(ListView list, WallAdapter mAdapter) {
        if (!(mAnimAdapter instanceof SwingBottomInAnimationAdapter)) {
            mAnimAdapter = new SwingBottomInAnimationAdapter(mAdapter);
            mAnimAdapter.setAbsListView(list);
            list.setAdapter(mAnimAdapter);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            wallListView.scrollTo(0, mCurCheckPosition);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
// ((MainActivity) getActivity()).getSupportActionBar().show();
  super.onCreate(savedInstanceState);
    }

    ActionBarArrayAdapter list ;
    ActionBar.OnNavigationListener navListener = new ActionBar.OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int i, long l) {
            return true;
        }
    };

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        Constants.makePostMenu = menu;


        if (OfflineMode.isOnline(getApplicationContext())) {
            setEnabledMenu();
        } else {
            setDisabledMenu();
        }

        super.onPrepareOptionsMenu(menu);
        VKHelper.isMember(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                Constants.isMember = response.json.optInt("response");
                if (VKSdk.isLoggedIn()) {
                    if (Constants.isMember == 0) {
                        try {
                            menu.findItem(R.id.join_leave_group).setTitle(getString(R.string.ab_title_group_join));


                        } catch (Exception e) {

                        }
                    } else {
                        try {
                            menu.findItem(R.id.join_leave_group).setTitle(getString(R.string.ab_title_group_leave));

                        } catch (Exception e) {

                        }
                    }
                } else {
                    menu.findItem(R.id.join_leave_group).setVisible(false);
                }

            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });
    }

    public static void setEnabledMenu() {
//        Constants.makePostMenu.
        if (Constants.makePostMenu.size() == 3) {
            Constants.makePostMenu.getItem(0).setVisible(true);
            Constants.makePostMenu.getItem(1).setVisible(true);
            Constants.makePostMenu.getItem(2).setVisible(true);
        }
    }

    public static void setDisabledMenu() {


        if (Constants.makePostMenu.size() == 3) {
            Constants.makePostMenu.getItem(0).setVisible(false);
            Constants.makePostMenu.getItem(1).setVisible(false);
            Constants.makePostMenu.getItem(2).setVisible(false);
        }
        Constants.makePostMenu.close();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        if (VKSdk.isLoggedIn()) {
            Constants.makePostMenu = menu;
            inflater.inflate(R.menu.make_post, menu);

            if (!isSuggested) {
                setEnabledMenu();
            } else {
                setDisabledMenu();
            }

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.make_post:
                ((MainActivity) getActivity()).addFragment(FragmentMakePost.newInstance(Constants.GROUP_ID, 0, 0));
                break;
            case R.id.suggested_posts:
                ((MainActivity) getActivity()).addFragment(FragmentWall.newInstance(true));
                break;
            case R.id.join_leave_group:
                if (Constants.isMember == 0) {
                    VKHelper.groupJoin(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), getString(R.string.group_joined), Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
                        }

                        @Override
                        public void onError(final VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                } else {
                    VKHelper.groupLeave(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), getString(R.string.group_leaved), Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).replaceFragment(FragmentWall.newInstance(false));
                        }

                        @Override
                        public void onError(final VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", wallListView.getScrollY());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        if (!OfflineMode.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_message_toast_en), Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VKHelper.doGroupWallRequest(offsetO, Offset, Constants.GROUP_ID, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        OfflineMode.saveJSON(response.json, Constants.GROUP_ID);
                        initGroupWall(OfflineMode.loadJSON(Constants.GROUP_ID), inflaterGlobal);
                    }

                    @Override
                    public void onError(final VKError error) {
                        super.onError(error);
                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                    }
                });

                swipeView.setRefreshing(false);
            }
        }, 1000);
    }

    private void endlessAdd(final int lastItem) {
        jsonObjectOld = OfflineMode.loadJSON(Constants.GROUP_ID);
        initGroupWall(jsonObjectOld, inflaterGlobal);
        scrollCommentsToBottom(wallListView, lastItem);
    }

    private void endlessGet(final int Offset) {
        VKHelper.doGroupWallRequest(Offset, countPostDefaultForOffset, Constants.GROUP_ID, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(OfflineMode.jsonPlus(jsonObjectOld, response.json), Constants.GROUP_ID);
            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });
    }

    private void scrollCommentsToBottom(final ListView listView, final int lastItem) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(lastItem - 2);
            }
        });

    }

    OnDismissCallback onDismissCallback = new OnDismissCallback() {
        @Override
        public void onDismiss(@NonNull ViewGroup viewGroup, @NonNull int[] ints) {

        }
    };
}