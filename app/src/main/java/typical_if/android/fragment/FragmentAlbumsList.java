package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.OfflineMode;
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
    ListView listOfAlbums;
    //private int mCurrentTransitionEffect = JazzyHelper.TILT;
    int type;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentAlbumsList newInstance(int type) {
        FragmentAlbumsList fragment = new FragmentAlbumsList();
        Bundle args = new Bundle();
        fragment.type = type;

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentAlbumsList() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_albums_list, container, false);
        setRetainInstance(true);
        doRequest(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(Constants.GROUP_ID);
    }

    private void doRequest(final View view) {
        if (type == 0) {
            VKHelper.getAlbumList(Constants.USER_ID, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    OfflineMode.saveJSON(response.json, Constants.GROUP_ID+"albums");
                    handleResponse(OfflineMode.loadJSON(Constants.GROUP_ID+"albums"), view);
                }
            });
        } else {
            VKHelper.getAlbumList(Constants.GROUP_ID, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    OfflineMode.saveJSON(response.json, Constants.GROUP_ID+"albums");
                    handleResponse(OfflineMode.loadJSON(Constants.GROUP_ID+"albums"), view);
                }
            });
        }
        if (!OfflineMode.isOnline(getActivity().getApplicationContext()) & OfflineMode.isJsonNull(Constants.GROUP_ID+"albums")) {
            handleResponse(OfflineMode.loadJSON(Constants.ALBUM_ID), view);
        }
    }

    protected void handleResponse(JSONObject jsonObject, View view) {

        final ArrayList<Album> albums = Album.getAlbumFromJSONArray(jsonObject);

        try {
            listOfAlbums = (ListView) view.findViewById(R.id.listOfAlbums);
            albumCoverAdapter = new AlbumCoverAdapter(albums, getActivity().getLayoutInflater());
        } catch (NullPointerException e) {
            Log.d("Connection", "BAD CONNECTION (NULL POINTER EXCEPTION)");
        }

        //listOfAlbums.setTransitionEffect(mCurrentTransitionEffect);
        listOfAlbums.setAdapter(albumCoverAdapter);
        listOfAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constants.ALBUM_ID = albums.get(position).id;
                Constants.TEMP_OWNER_ID = albums.get(position).owner_id;

                Fragment fragment = FragmentPhotoList.newInstance(type);
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack("AlbumList").commit();
            }
        });
    }
}