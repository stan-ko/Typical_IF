package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

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
    private OnFragmentInteractionListener mListener;
    static String category;
    static String [] uris;

    public static FragmentUploadPhotoList newInstance(String category, String [] uris) {
        FragmentUploadPhotoList fragment = new FragmentUploadPhotoList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentUploadPhotoList.category=category;
        FragmentUploadPhotoList.uris=uris;
        return fragment;
    }
    public FragmentUploadPhotoList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        float scalefactor = getResources().getDisplayMetrics().density * 100;
        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final int columns = (int) ((float) number / (float) scalefactor);
        final ArrayList<UploadPhotos> photolist = new ArrayList<UploadPhotos>();
        for (int i=0; i<uris.length; i++){
            String [] temp = uris[i].split("/");
            if (temp[temp.length-2].contains(category)){
                photolist.add(new UploadPhotos(uris[i]));
            }
        }

        final View rootView = inflater.inflate(R.layout.fragment_add_photo_upload_list, container, false);
        handleResponse(rootView, inflater,photolist,columns);
//        final GridView photos = (GridView) rootView.findViewById(R.id.adding_photo_upload);
//        PhotoUploadAdapter photoUploadAdapter = new PhotoUploadAdapter(category, inflater, photolist);
//        photos.setAdapter(photoUploadAdapter);
//        Button but = (Button) rootView.findViewById(R.id.uploadPhotoButton);
//        but.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i=0; i<photolist.size(); i++)
//                    if (photolist.get(i).ischecked == true) {
//                        final VKRequest req = VKApi.uploadAlbumPhotoRequest(new File(photolist.get(i).photosrc), 123513499, 8686797);
//                        req.executeWithListener(new VKRequest.VKRequestListener() {
//                            @Override
//                            public void onComplete(VKResponse response) {
//                                super.onComplete(response);
//                                Log.d("My-------------------------->", response.responseString);
//                            }
//                        });
//                    }
//            }
//        });
//        photos.setNumColumns(columns);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
//        inflater.inflate(R.menu.main, menu);
//        MenuItem item =  menu.getItem(0).setEnabled(true);
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                addPhoto().show();
//                return true;
//            }
//        });
//
//        //super.onCreateOptionsMenu(menu, inflater);
//    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    protected void handleResponse (View rootView, LayoutInflater inflater, final ArrayList<UploadPhotos> photolist, int columns){
        final GridView photos = (GridView) rootView.findViewById(R.id.adding_photo_upload);
        PhotoUploadAdapter photoUploadAdapter = new PhotoUploadAdapter(category, inflater, photolist);
        photos.setAdapter(photoUploadAdapter);
        Button but = (Button) rootView.findViewById(R.id.uploadPhotoButton);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int j = 0; j < photolist.size(); j++)
                    if (photolist.get(j).ischecked == true) {
                        final VKRequest req = VKApi.uploadAlbumPhotoRequest(new File(photolist.get(j).photosrc), 123513499, 8686797);
                        req.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Log.d("My-------------------------->", response.responseString);
                            }
                        });
                    }
            }
        });
        photos.setNumColumns(columns);
    }
}
