package typical_if.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
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


public class FragmentWall extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_VK_GROUP_ID = "vk_group_id";

    private int mCurrentTransitionEffect = JazzyHelper.TRANSPARENT;
    JazzyListView wallListView;
    WallAdapter adapter;

    RelativeLayout spinnerLayout;
    View rootView;
    LayoutInflater inflaterGlobal;

    String postColor;
    Long gid;
    int countPost=10;
    boolean temp = true;

    SwipeRefreshLayout swipeView;
    AbsListView.OnScrollListener onScrollListenerObject;
    Bundle arguments;

    public static FragmentWall newInstance(long vkGroupId) {
        FragmentWall fragment = new FragmentWall();
        Bundle args = new Bundle();
        args.putLong(ARG_VK_GROUP_ID, vkGroupId);
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
        initGroupWall(OfflineMode.loadJSON(gid), inflater);
        spinnerLayout.setVisibility(View.GONE);
        wallListView.setOnScrollListener(this);
        onScrollListenerObject = this;
        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        return rootView;


    }

    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater) {
        Wall wall = Wall.getGroupWallFromJSON(jsonObject);
        FragmentManager fragmentManager = getFragmentManager();
        adapter = new WallAdapter(wall, inflater, fragmentManager, postColor);
        wallListView = (JazzyListView) rootView.findViewById(R.id.listViewWall);
        wallListView.setAdapter(adapter);
        wallListView.setTransitionEffect(mCurrentTransitionEffect);
        wallListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
       // endlessPosition();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", wallListView.getScrollY());
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        temp=true;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
//        Log.d("**********************************", firstVisibleItem+"---"+visibleItemCount+"------"+totalItemCount);

        if (lastItem == totalItemCount & temp) {
                countPost = countPost + 10;
                endlessAdd(countPost,lastItem);
                temp=false;
            Log.d("**********************************", countPost + "-----------"+lastItem);
        }

        boolean topEnable = false;
        if (absListView != null && absListView.getChildCount() > 0) {
            boolean firstItemVisible = absListView.getFirstVisiblePosition() == 0;
            boolean topOfFirstItemVisible = absListView.getChildAt(0).getTop() == 0;
            topEnable = firstItemVisible && topOfFirstItemVisible;
            swipeView.setEnabled(topEnable);
        }
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
                        wallListView.setOnScrollListener(onScrollListenerObject);

                    }
                });
            }
        }, 1000);
    }

    private void endlessAdd (int countPost, final int lastItem){
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
    //public  void endlessPosition(int lastItem){
      //  wallListView.smoothScrollToPosition(lastItem);

    //}

}
