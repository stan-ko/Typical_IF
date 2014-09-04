package typical_if.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiVideo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.AudioAttachAdapter;
import typical_if.android.adapter.DocAttachAdapter;
import typical_if.android.adapter.VideoAttachAdapter;

/**
 * Created by admin on 19.08.2014.
 */
public class FragmentAttachPostList extends Fragment {

    ListView attachList;
    RelativeLayout spinnerLayout;
    private static int type;

    public static FragmentAttachPostList newInstance(int typeParam) {
        FragmentAttachPostList fragment = new FragmentAttachPostList();
        Bundle args = new Bundle();
        type = typeParam;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attach_list, container, false);

        attachList = (ListView) rootView.findViewById(R.id.lv_post_attach);
        spinnerLayout = (RelativeLayout) rootView.findViewById(R.id.attach_list_spinner_layout);

        switch (type) {
            case 2:
                VKHelper.getUserAudios(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        JSONObject responseObject = response.json.optJSONObject("response");
                        JSONArray items = responseObject.optJSONArray("items");
                        final ArrayList<VKApiAudio> audios = parseAudioItems(items);
                        attachList.setAdapter(new AudioAttachAdapter(audios, inflater));
                        attachList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getActivity().getSupportFragmentManager().popBackStack();
                                Constants.tempPostAttachCounter++;
                                Constants.tempAudioPostAttach.add(audios.get(position));
                                FragmentMakePost.refreshMakePostFragment(2);
                            }
                        });

                        spinnerLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                    }
                });
                break;
            case 1:
                VKHelper.getUserVideos(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        JSONObject responseObject = response.json.optJSONObject("response");
                        JSONArray items = responseObject.optJSONArray("items");

                        final ArrayList<VKApiVideo> videos = parseVideoItems(items);
                        attachList.setAdapter(new VideoAttachAdapter(videos, inflater));
                        attachList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getActivity().getSupportFragmentManager().popBackStack();
                                Constants.tempPostAttachCounter++;
                                Constants.tempVideoPostAttach.add(videos.get(position));
                                FragmentMakePost.refreshMakePostFragment(1);
                            }
                        });

                        spinnerLayout.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                    }
                });
                break;
            case 3:
                VKHelper.getUserDocs(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        JSONObject responseObject = response.json.optJSONObject("response");
                        JSONArray items = responseObject.optJSONArray("items");

                        final ArrayList<VKApiDocument> docs = parseDocItems(items);
                        attachList.setAdapter(new DocAttachAdapter(docs, inflater));
                        attachList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getActivity().getSupportFragmentManager().popBackStack();
                                Constants.tempPostAttachCounter++;
                                Constants.tempDocPostAttach.add(docs.get(position));
                                FragmentMakePost.refreshMakePostFragment(3);
                            }
                        });

                        spinnerLayout.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                    }
                });
        }

        setRetainInstance(true);
        return rootView;
    }

    private static ArrayList<VKApiAudio> parseAudioItems(JSONArray items) {
        ArrayList<VKApiAudio> audios = new ArrayList<VKApiAudio>();
        VKApiAudio audio;

        for (int i = 0; i < items.length(); i++) {
            audio = new VKApiAudio().parse(items.optJSONObject(i));
            audios.add(audio);
        }

        return audios;
    }

    private static ArrayList<VKApiVideo> parseVideoItems(JSONArray items) {
        ArrayList<VKApiVideo> videos = new ArrayList<VKApiVideo>();
        VKApiVideo video;

        for (int i = 0; i < items.length(); i++) {
            video = new VKApiVideo().parse(items.optJSONObject(i));
            videos.add(video);
        }

        return videos;
    }

    private static ArrayList<VKApiDocument> parseDocItems(JSONArray items) {
        ArrayList<VKApiDocument> docs = new ArrayList<VKApiDocument>();
        VKApiDocument doc;

        for (int i = 0; i < items.length(); i++) {
            doc = new VKApiDocument().parse(items.optJSONObject(i));
            docs.add(doc);
        }

        return docs;
    }
}
