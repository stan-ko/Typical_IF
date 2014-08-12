package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

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

    SwipeRefreshLayout swipeView;
    AbsListView.OnScrollListener onScrollListenerObject;
    Bundle arguments;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
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


       // wallListView.setOnScrollListener(this);
        onScrollListenerObject = this;
        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        initGroupWall(OfflineMode.loadJSON(gid), inflater);
        spinnerLayout.setVisibility(View.GONE);
        wallListView.setOnScrollListener(this);


        return rootView;
    }

    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater) {
        //if (OfflineMode.isOnline())
        Wall wall = Wall.getGroupWallFromJSON(jsonObject);
        FragmentManager fragmentManager = getFragmentManager();
        adapter = new WallAdapter(wall, inflater, fragmentManager, postColor);
        wallListView = (JazzyListView) rootView.findViewById(R.id.listViewWall);
        wallListView.setAdapter(adapter);
        wallListView.setTransitionEffect(mCurrentTransitionEffect);
        wallListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        boolean enable = false;
        if (absListView != null && absListView.getChildCount() > 0) {
            // check if the first item of the list is visible
            boolean firstItemVisible = absListView.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = absListView.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        swipeView.setEnabled(enable);
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeView.setRefreshing(false);
                VKHelper.doGroupWallRequest(gid, new VKRequest.VKRequestListener() {
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
}
