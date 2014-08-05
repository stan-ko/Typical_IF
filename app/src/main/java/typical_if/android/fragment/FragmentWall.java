package typical_if.android.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
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
import com.vk.sdk.VKUIHelper;
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
    private int mCurrentTransitionEffect = JazzyHelper.TILT;

    JazzyListView wallListView;
    WallAdapter adapter;
    RelativeLayout spinnerLayout;
    String postColor;
    Long gid;
    View rootView;
    Bundle arguments;

    SharedPreferences sPref;
    final String SAVED_TEXT="saved_text";
    JSONObject jsonObj;

    String post_report;
    String post_copy_link;
    String post_report_spam;
    String post_report_offense;
    String post_report_adult;
    String post_report_drugs;
    String post_report_porno;
    String post_report_violence;

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

        post_report = getResources().getString(R.string.post_report);
        post_copy_link = getResources().getString(R.string.post_copy_link);
        post_report_spam = getResources().getString(R.string.post_report_spam);
        post_report_offense = getResources().getString(R.string.post_report_offense);
        post_report_adult = getResources().getString(R.string.post_report_adult);
        post_report_drugs = getResources().getString(R.string.post_report_drugs);
        post_report_porno = getResources().getString(R.string.post_report_porno);
        post_report_violence = getResources().getString(R.string.post_report_violence);

        VKHelper.doGroupWallRequest(gid, new VKRequest.VKRequestListener() {
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                saveJSON(response.json);
                initGroupWall(response.json, inflater, gid);
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

    public void initGroupWall(JSONObject jsonObject, LayoutInflater inflater, final long gid){
        Wall wall = Wall.getGroupWallFromJSON(jsonObject);
        adapter = new WallAdapter(wall, inflater, postColor);
        wallListView = (JazzyListView)rootView.findViewById(R.id.listViewWall);
        wallListView.setAdapter(adapter);
        wallListView.setTransitionEffect(mCurrentTransitionEffect);
        wallListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

//        wallListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                final VKApiPost post = (VKApiPost) parent.getAdapter().getItem(position);
//
//                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                final String[] items = {post_report, post_copy_link};
//
//                builder.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case 0:
//                                final AlertDialog.Builder builderIn = new AlertDialog.Builder(getActivity());
//                                builderIn.setTitle(post_report);
//                                final String[] items = {post_report_spam, post_report_offense, post_report_adult, post_report_drugs, post_report_porno, post_report_violence};
//
//                                builderIn.setItems(items, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        int reason = 0;
//                                        switch (which) {
//                                            case 0:
//                                                reason = 0;
//                                                break;
//                                            case 1:
//                                                reason = 6;
//                                                break;
//                                            case 2:
//                                                reason = 5;
//                                                break;
//                                            case 3:
//                                                reason = 4;
//                                                break;
//                                            case 4:
//                                                reason = 1;
//                                                break;
//                                            case 5:
//                                                reason = 3;
//                                                break;
//                                        }
//                                        VKHelper.doReportPost(gid, post.id, reason, new VKRequest.VKRequestListener() {
//                                            @Override
//                                            public void onComplete(VKResponse response) {
//                                                super.onComplete(response);
//                                                int isSuccessed = response.json.optInt("response");
//
//                                                if (isSuccessed == 1) {
//                                                    Toast.makeText(getActivity(), "Reported", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    }
//                                });
//                                builderIn.create();
//                                break;
//
//                            case 1:
//                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                                clipboard.setText("http://vk.com/wall" + gid + "_" + post.id);
//                                break;
//                        }
//                    }
//                });
//
//                builder.create();
//                return true;
//            }
//        });
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


    void saveJSON(JSONObject jsonObject) {
        sPref = VKUIHelper.getTopActivity().getPreferences(VKUIHelper.getTopActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();

            String JsonString = jsonObject.toString();
            ed.putString(SAVED_TEXT, JsonString);
           // Log.d("/--------------jsonInString-------------------/",jsonObject.toString());
            ed.commit();

    }

    JSONObject loadJSON() {
        sPref = VKUIHelper.getTopActivity().getPreferences(VKUIHelper.getTopActivity().MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        try {
            jsonObj = new JSONObject(savedText);
        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("/---------------------savedJson--------ERROR-----------/",savedText);
        }
        return jsonObj;
    }
}
