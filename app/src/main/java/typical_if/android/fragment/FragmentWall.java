package typical_if.android.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.*;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Wall.Wall;


/**
 * Created by admin on 14.07.2014.
 */



public class FragmentWall extends Fragment {

    private static final String ARG_VK_GROUP_ID = "vk_group_id";
    private int mCurrentTransitionEffect = JazzyHelper.CARDS;

    JazzyListView wallListView;
    WallAdapter adapter;
    RelativeLayout spinnerLayout;
    String postColor;
    Long gid;
    View rootView;
    Bundle arguments;
    SharedPreferences sPref;
    String SAVED_JSON="saved_text";
    JSONObject jsonObj;
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
        spinnerLayout = (RelativeLayout)rootView.findViewById(R.id.spinner_layout);

        arguments = getArguments();
        gid = arguments.getLong(ARG_VK_GROUP_ID);
        postColor = getPostColor(gid);

        VKHelper.doGroupWallRequest(gid, new VKRequest.VKRequestListener() {
            //@Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                saveJSON(response.json, gid);
                initGroupWall(response.json, inflater, gid);
                spinnerLayout.setVisibility(View.GONE);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);

                if (gid == Constants.TF_ID){
                    initGroupWall(loadJSON(gid), inflater, gid);
                    spinnerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);

                initGroupWall(loadJSON(gid), inflater, gid);
                spinnerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });

        return rootView;
    }

    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater, long gid){
        Wall wall = Wall.getGroupWallFromJSON(jsonObject, gid);
        //wall.posts.parse()
        adapter = new WallAdapter(wall, inflater, postColor);
//adapter.
        wallListView = (JazzyListView)rootView.findViewById(R.id.listViewWall);
        wallListView.setAdapter(adapter);
        wallListView.setTransitionEffect(mCurrentTransitionEffect);

        wallListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        ImageLoader.getInstance().getDiskCache().toString();
    };

    public String getPostColor(long groupIndex) {
        if (groupIndex == Constants.TF_ID) {
            return "#3DA2A9";
        } else if (groupIndex == Constants.TZ_ID) {
            return "#D5902FA7";
        } else if (groupIndex == Constants.FB_ID) {
            return "#1799CD";
        } else {
            return "#DE9C0E";
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity) activity).onSectionAttached(getArguments().getLong(ARG_VK_GROUP_ID));
    }
    void saveJSON(JSONObject jsonObject, long gid) {
        sPref = VKUIHelper.getTopActivity().getPreferences(VKUIHelper.getTopActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
            String JsonString = jsonObject.toString();
           SAVED_JSON=String.valueOf(gid);
            ed.putString(SAVED_JSON, JsonString);
           // Log.d("/--------------jsonInString-------------------/",jsonObject.toString());
            ed.commit();
    }

    JSONObject loadJSON(long gid) {
        sPref = VKUIHelper.getTopActivity().getPreferences(VKUIHelper.getTopActivity().MODE_PRIVATE);
        SAVED_JSON = String.valueOf(gid);
        String savedText = sPref.getString(SAVED_JSON, "");
        try {
            jsonObj = new JSONObject(savedText);
        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("/---------------------savedJson--------ERROR-----------/",savedText);
        }
        return jsonObj;
    }
}