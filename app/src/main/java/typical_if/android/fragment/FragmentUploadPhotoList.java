package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.adapter.PhotoUploadAdapter;
import typical_if.android.model.UploadPhotos;

/**
 * Created by LJ on 25.07.2014.
 */
public class FragmentUploadPhotoList extends Fragment {
    static String category;
    static String[] uris;
    ArrayList<UploadPhotos> photolist = null;

    public static FragmentUploadPhotoList newInstance(String category, String[] uris) {
        FragmentUploadPhotoList fragment = new FragmentUploadPhotoList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentUploadPhotoList.category = category;
        FragmentUploadPhotoList.uris = uris;
        return fragment;
    }

    public FragmentUploadPhotoList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        float scalefactor = getResources().getDisplayMetrics().density * 100;
        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final int columns = (int) ((float) number / (float) scalefactor);
        photolist = new ArrayList<UploadPhotos>();
        for (int i = 0; i < uris.length; i++) {
            String[] temp = uris[i].split("/");
            if (temp[temp.length - 2].contains(category)) {
                photolist.add(new UploadPhotos(uris[i]));
            }
        }

        final View rootView = inflater.inflate(R.layout.fragment_add_photo_upload_list, container, false);
        handleResponse(rootView, inflater, photolist, columns);
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    protected void handleResponse(View rootView, LayoutInflater inflater, final ArrayList<UploadPhotos> photolist, int columns) {
        final GridView photos = (GridView) rootView.findViewById(R.id.adding_photo_upload);
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        PhotoUploadAdapter photoUploadAdapter = new PhotoUploadAdapter(category, inflater, photolist, fragmentManager);
        photos.setAdapter(photoUploadAdapter);
        photos.setNumColumns(columns);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.upload_captured_photo, menu);
        MenuItem item = menu.getItem(0);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //item.setEnabled(false);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                for (int j = 0; j < photolist.size(); j++) {
                    if (photolist.get(j).ischecked == true) {
                        final VKRequest req = VKApi.uploadAlbumPhotoRequest(new File(photolist.get(j).photosrc), 123513499, 8686797);
                        req.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                            }
                        });
                    }
                }
                return true;
            }
        });
    }


}
