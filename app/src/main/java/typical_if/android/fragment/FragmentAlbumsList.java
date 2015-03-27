package typical_if.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.vk.sdk.api.VKError;
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
    private int type;
    private int counter = 5;
    private boolean temp = true;
    private boolean isRequestNul;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentAlbumsList newInstance(int type) {
        Constants.isFragmentAlbumListLoaded=true;
        FragmentAlbumsList fragment = new FragmentAlbumsList();
        Bundle args = new Bundle();
        fragment.type = type;

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentAlbumsList() {
    }

    private AnimationAdapter mAnimAdapter;

    private void setBottomAdapter(final ListView list, AlbumCoverAdapter mAdapter)
    {
       try {
           if (!(mAnimAdapter instanceof SwingBottomInAnimationAdapter)) {
               mAnimAdapter = new SwingBottomInAnimationAdapter(mAdapter);
               mAnimAdapter.setAbsListView(list);

               list.setAdapter(mAnimAdapter);
           }

       }catch (NullPointerException c ){
           list.setAdapter(mAdapter);
       }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Constants.isFragmentAlbumListLoaded=true;
        ((MainActivity)getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        final View rootView = inflater.inflate(R.layout.fragment_albums_list, container, false);
        listOfAlbums = (ListView) rootView.findViewById(R.id.album_list);
        setRetainInstance(true);
        doRequest(rootView);

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Constants.makePostMenu = null;
       super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.isFragmentAlbumListLoaded=true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Constants.isFragmentAlbumListLoaded=false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.isFragmentAlbumListLoaded=false;
    }

    @Override
    public void onAttach(Activity activity) {
        Constants.isFragmentAlbumListLoaded=true;
        FragmentWall.setDisabledMenu();
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached( OfflineMode.loadLong(Constants.VK_GROUP_ID));
    }

    private boolean doRequest(final View view) {

        if (OfflineMode.isOnline()) {
            temp = false;
            if (type == 0) {

                VKHelper.getAlbumList(Constants.USER_ID, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        OfflineMode.saveJSON(response.json,  OfflineMode.loadLong(Constants.VK_GROUP_ID) + "albums");
                        handleResponse(OfflineMode.loadJSON( OfflineMode.loadLong(Constants.VK_GROUP_ID) + "albums"), view);

                    }
                    @Override
                    public void onError(final VKError error) {
                        super.onError(error);
                        OfflineMode.onErrorToast();
                    }
                });
            } else {
                VKHelper.getAlbumList( OfflineMode.loadLong(Constants.VK_GROUP_ID), new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(final VKResponse response) {
                        super.onComplete(response);
                        OfflineMode.saveJSON(response.json, OfflineMode.loadLong(Constants.VK_GROUP_ID) + "albums");

                        handleResponse(OfflineMode.loadJSON(OfflineMode.loadLong(Constants.VK_GROUP_ID) + "albums"), view);
                    }
                    @Override
                    public void onError(final VKError error) {
                        super.onError(error);
                        OfflineMode.onErrorToast();
                    }
                });
            }
            isRequestNul=  true;
        }
        if (!OfflineMode.isOnline() & OfflineMode.isJsonNull( OfflineMode.loadLong(Constants.VK_GROUP_ID) + "albums")) {
            handleResponse(OfflineMode.loadJSON( OfflineMode.loadLong(Constants.VK_GROUP_ID) + "albums"), view);
            isRequestNul = true;
        } else {
            if (temp) {
                OfflineMode.onErrorToast();
                isRequestNul = false;
            }
        }

        return isRequestNul;
    }

    protected void handleResponse(JSONObject jsonObject, View view) {
        final ArrayList<Album> albums = Album.getAlbumFromJSONArray(jsonObject);
        try {
           albumCoverAdapter = new AlbumCoverAdapter(albums, getActivity().getLayoutInflater());
        } catch (NullPointerException e) {  }
        //listOfAlbums.setTransitionEffect(mCurrentTransitionEffect);
        Log.d(listOfAlbums+ "listOfAlbums"+ albumCoverAdapter+"       albumCoverAdapter ","");
        setBottomAdapter(listOfAlbums, albumCoverAdapter);
       // listOfAlbums.setAdapter(albumCoverAdapter);

        listOfAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constants.ALBUM_ID = albums.get(position).id;
                String src =  albums.get(position).sizes.optJSONObject(2).optString("src");
                Constants.TEMP_OWNER_ID = albums.get(position).owner_id;
                Fragment fragment = FragmentPhotoList.newInstance(type, albums.get(position).size, albums.get(position).title, src);
                ((MainActivity)getActivity()).addFragment(fragment);
            }
        });
    }
}