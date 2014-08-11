package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Wall.Wall;


/**
 * Created by admin on 14.07.2014.
 */


public class FragmentWall extends Fragment {
    private static final String ARG_VK_GROUP_ID = "vk_group_id";

    private int mCurrentTransitionEffect = JazzyHelper.TILT;
    JazzyListView wallListView;
    WallAdapter adapter;

    RelativeLayout spinnerLayout;
    View rootView;

    String postColor;
    Long gid;

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
        this.setRetainInstance(true);

        rootView = inflater.inflate(R.layout.fragment_wall, container, false);
        spinnerLayout = (RelativeLayout) rootView.findViewById(R.id.spinner_layout);

        arguments = getArguments();
        gid = arguments.getLong(ARG_VK_GROUP_ID);
        postColor = ItemDataSetter.getPostColor(gid);

        VKHelper.doGroupWallRequest(gid, new VKRequest.VKRequestListener() {
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                initGroupWall(response.json, inflater);
                spinnerLayout.setVisibility(View.GONE);
            }
/*
            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                    initGroupWall(loadJSON(), inflater, gid);
                    spinnerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                initGroupWall(loadJSON(), inflater, gid);
                spinnerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }*/
        });

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
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity) activity).onSectionAttached(getArguments().getLong(ARG_VK_GROUP_ID));
    }
}
