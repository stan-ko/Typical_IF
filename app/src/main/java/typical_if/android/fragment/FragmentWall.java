package typical_if.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

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
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.activity.SplashActivity;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Wall.Wall;

import static com.vk.sdk.VKUIHelper.getApplicationContext;


/**
 * Created by admin on 14.07.2014.
 */


public class FragmentWall extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


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

    int Offset = SplashActivity.getCountOfPosts();
    static boolean isSuggested;
    static int isMember;
    boolean temp = true;
    boolean temp2 = true;
    boolean enable = false;
    Bundle arguments;
    SwipeRefreshLayout swipeView;

    final Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            Offset = Offset + 100;
            endlessGet(Offset);
        }
    });

    AbsListView.OnScrollListener onScrollListenerObject = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            temp = true;
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {

            final int lastItem = firstVisibleItem + visibleItemCount;

            if (lastItem == totalItemCount - 20 & temp2) {
                t.start();
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

        if (!isSuggested) {
            jsonObjectOld = OfflineMode.loadJSON(Constants.GROUP_ID);
            initGroupWall(jsonObjectOld, inflater);

            swipeView.setOnRefreshListener(this);
            swipeView.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        } else {
            VKHelper.getSuggestedPosts(Constants.GROUP_ID, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    initGroupWall(response.json, inflater);
                    wallListView.setOnScrollListener(null);

                    swipeView.setOnRefreshListener(null);
                    swipeView.setEnabled(false);
                    swipeView.setRefreshing(false);
                }
                @Override
                public void onError(VKError error) {
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
        adapter = new WallAdapter(wall, inflater, fragmentManager, postColor, isSuggested);
        wallListView = (ListView) rootView.findViewById(R.id.listViewWall);
        wallListView.setAdapter(adapter);
        wallListView.setOnScrollListener(pauseOnScrollListener);
        spinnerLayout.setVisibility(View.GONE);
        if (wall.posts.size() == 0) {
            fragmentManager.popBackStack();
            Toast.makeText(getApplicationContext(), getString(R.string.no_suggested_posts), Toast.LENGTH_SHORT).show();
        }
    }

    int mCurCheckPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            wallListView.scrollTo(0, mCurCheckPosition);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        VKHelper.isMember(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                isMember = response.json.optInt("response");
                if (VKSdk.isLoggedIn()) {
                    if (isMember == 0) {
                        menu.findItem(R.id.join_leave_group).setTitle(getString(R.string.ab_title_group_join));
                    } else {
                        menu.findItem(R.id.join_leave_group).setTitle(getString(R.string.ab_title_group_leave));
                    }
                } else {
                    menu.findItem(R.id.join_leave_group).setVisible(false);
                }

            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.make_post, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.make_post:
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, FragmentMakePost.newInstance(Constants.GROUP_ID, 0, 0)).addToBackStack("makePostFragment").commit();
                break;
            case R.id.suggested_posts:
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, FragmentWall.newInstance(true)).addToBackStack(null).commit();
                break;
            case R.id.join_leave_group:
                if (isMember == 0) {
                    VKHelper.groupJoin(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), getString(R.string.group_joined), Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, FragmentWall.newInstance(false)).commit();
                        }
                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                } else {
                    VKHelper.groupLeave(Constants.GROUP_ID * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), getString(R.string.group_leaved), Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, FragmentWall.newInstance(false)).commit();
                        }
                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    ;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", wallListView.getScrollY());
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
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        OfflineMode.saveJSON(response.json, Constants.GROUP_ID);
                        initGroupWall(OfflineMode.loadJSON(Constants.GROUP_ID), inflaterGlobal);
                    }
                    @Override
                    public void onError(VKError error) {
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
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(OfflineMode.jsonPlus(jsonObjectOld, response.json), Constants.GROUP_ID);
            }
            @Override
            public void onError(VKError error) {
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
}
