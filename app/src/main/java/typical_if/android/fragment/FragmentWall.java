package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import typical_if.android.OfflineMode;
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
    final OfflineMode offlineMode = new OfflineMode();

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

        arguments = getArguments();
        gid = arguments.getLong(ARG_VK_GROUP_ID);
        postColor = ItemDataSetter.getPostColor(gid);


        //  initGroupWall(offlineMode.loadJSON(gid), inflater);
        // spinnerLayout.setVisibility(View.GONE);
        VKHelper.doGroupWallRequest(gid, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
               // offlineMode.saveJSON(response.json, Constants.TF_ID);
                initGroupWall(response.json, inflater);
                spinnerLayout.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater) {
        Wall wall = Wall.getGroupWallFromJSON(jsonObject);
        adapter = new WallAdapter(wall, inflater, postColor);
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
