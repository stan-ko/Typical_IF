package typical_if.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Wall.Wall;

import static com.vk.sdk.VKUIHelper.getApplicationContext;


/**
 * Created by admin on 14.07.2014.
 */


public class FragmentWall extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_VK_GROUP_ID = "vk_group_id";

    private int mCurrentTransitionEffect = JazzyHelper.TRANSPARENT;
    JazzyListView wallListView;
    WallAdapter adapter;

    RelativeLayout spinnerLayout;
    View rootView;
    LayoutInflater inflaterGlobal;

    String postColor;
    long gid;

    int countPost = 10;
    static boolean isSuggested;
    static int isMember;
    boolean temp =true;

    SwipeRefreshLayout swipeView;
    AbsListView.OnScrollListener onScrollListenerObject = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
temp = true;
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            final int lastItem = firstVisibleItem + visibleItemCount;
//        Log.d("**********************************", firstVisibleItem+"---"+visibleItemCount+"------"+totalItemCount);

            if (lastItem == totalItemCount & temp) {
                countPost = countPost + 10;
                endlessAdd(countPost, lastItem);
                temp = false;
                Log.d("**********************************", countPost + "-----------" + lastItem);
            }

            boolean enable = false;

            if (absListView != null && absListView.getChildCount() > 0) {
                boolean firstItemVisible = absListView.getFirstVisiblePosition() == 0;
                boolean topOfFirstItemVisible = absListView.getChildAt(0).getTop() == 0;
                enable = firstItemVisible && topOfFirstItemVisible;
            }

            swipeView.setEnabled(enable);
        }
    };
    Bundle arguments;

    public static FragmentWall newInstance(long vkGroupId, boolean isSuggestedParam) {
        FragmentWall fragment = new FragmentWall();
        Bundle args = new Bundle();
        args.putLong(ARG_VK_GROUP_ID, vkGroupId);
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
        gid = arguments.getLong(ARG_VK_GROUP_ID);
        postColor = ItemDataSetter.getPostColor(gid);

        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);

        if (!isSuggested) {
            initGroupWall(OfflineMode.loadJSON(gid), inflater);

            swipeView.setOnRefreshListener(this);
            swipeView.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        } else {
            VKHelper.getSuggestedPosts(gid, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    initGroupWall(response.json, inflater);
                    wallListView.setOnScrollListener(null);

                    swipeView.setOnRefreshListener(null);
                    swipeView.setEnabled(false);
                    swipeView.setRefreshing(false);
                }
            });
        }
        return rootView;
    }


    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater) {
        Wall wall = Wall.getGroupWallFromJSON(jsonObject);
        FragmentManager fragmentManager = getFragmentManager();
        adapter = new WallAdapter(wall, inflater, fragmentManager, postColor, isSuggested);
        wallListView = (JazzyListView) rootView.findViewById(R.id.listViewWall);
        wallListView.setAdapter(adapter);
        wallListView.setTransitionEffect(mCurrentTransitionEffect);
        wallListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, onScrollListenerObject));
        spinnerLayout.setVisibility(View.GONE);
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
        VKHelper.isMember(gid * (-1), new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                isMember = response.json.optInt("response");

                if (isMember == 0) {
                    menu.findItem(R.id.join_leave_group).setTitle("Join");
                } else {
                    menu.findItem(R.id.join_leave_group).setTitle("Leave");
                }
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
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, FragmentMakePost.newInstance(gid, 0, 0)).addToBackStack("makePostFragment").commit();
                break;
            case R.id.suggested_posts:
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, FragmentWall.newInstance(gid, true)).addToBackStack(null).commit();
                break;
            case R.id.join_leave_group:
                if (isMember == 0) {
                    VKHelper.groupJoin(gid * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), "Joined", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, FragmentWall.newInstance(gid, false)).commit();
                        }
                    });
                } else {
                    VKHelper.groupLeave(gid * (-1), new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getActivity(), "Leaved", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, FragmentWall.newInstance(gid, false)).commit();
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
        if (OfflineMode.isOnline(getApplicationContext()) == false) {
            Toast.makeText(getApplicationContext(), getString(R.string.noInternetMessageFromToast_EN), Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeView.setRefreshing(false);

                VKHelper.doGroupWallRequest(countPost, gid, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        OfflineMode.saveJSON(response.json, gid);
                        initGroupWall(OfflineMode.loadJSON(gid), inflaterGlobal);
                    }
                });
            }
        }, 1000);
    }

    private void endlessAdd(int countPost, final int lastItem) {
        VKHelper.doGroupWallRequest(countPost, gid, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                OfflineMode.saveJSON(response.json, gid);
                initGroupWall(OfflineMode.loadJSON(gid), inflaterGlobal);
                // endlessPosition(lastItem);
                wallListView.setOnScrollListener(onScrollListenerObject);

            }
        });
    }
}