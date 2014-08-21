package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.AlbumCoverAdapter;
import typical_if.android.model.Album;

/**
 * Created by admin on 14.07.2014.
 */
public class FragmentAlbumsList extends Fragment {

    AlbumCoverAdapter albumCoverAdapter;
    JazzyListView listOfAlbums;
    private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private static final String ARG_VK_GROUP_ID = "vk_group_id";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentAlbumsList newInstance(long vkGroupId) {
        FragmentAlbumsList fragment = new FragmentAlbumsList();
        Bundle args = new Bundle();
        args.putLong(ARG_VK_GROUP_ID, vkGroupId);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentAlbumsList() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_albums_list, container, false);
        setRetainInstance(true);
        doRequest();
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getLong(ARG_VK_GROUP_ID));


    }

    private void doRequest() {
        final Bundle arguments = getArguments();
        VKHelper.getAlbumList(arguments.getLong(ARG_VK_GROUP_ID), new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                handleResponse(response);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });
    }

    protected void handleResponse(VKResponse response) {
        final Bundle arguments = getArguments();
        final ArrayList<Album> albums = Album.getAlbumFromJSONArray(response.json);

        try {
            listOfAlbums = (JazzyListView) getView().findViewById(R.id.listOfAlbums);
            albumCoverAdapter = new AlbumCoverAdapter(albums, getActivity().getLayoutInflater());
        } catch (NullPointerException e) {
            Log.d("Connection", "BAD CONNECTION (NULL POINTER EXCEPTION)");
        }

        listOfAlbums.setTransitionEffect(mCurrentTransitionEffect);
        listOfAlbums.setAdapter(albumCoverAdapter);
        listOfAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Module Item Trigger", arguments.getLong(ARG_VK_GROUP_ID) + "__" + albums.get(position).id + "");
                Fragment fragment = FragmentPhotoList.newInstance(arguments.getLong(ARG_VK_GROUP_ID), albums.get(position).id);
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack("AlbumList").commit();
            }
        });
    }
}