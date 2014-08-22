package typical_if.android.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.twotoasters.jazzylistview.JazzyGridView;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.Dialogs;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.PhotoListAdapter;

public class FragmentPhotoList extends Fragment implements AbsListView.OnScrollListener {

    public ArrayList<VKApiPhoto> photos2 = new ArrayList<VKApiPhoto>();
    private OnFragmentInteractionListener mListener;
    private static final String ARG_VK_GROUP_ID = "vk_group_id";
    private static final String ARG_VK_ALBUM_ID = "vk_album_id";
    //private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private static final int PICK_FROM_CAMERA = 1;

    final int PIC_CROP = 2;
    private static Uri mImageCaptureUri;

    GridView gridOfPhotos;

    public static FragmentPhotoList newInstance(long vk_group_id, long vk_album_id) {
        FragmentPhotoList fragment = new FragmentPhotoList();
        Bundle args = new Bundle();
        args.putLong(ARG_VK_GROUP_ID, vk_group_id);
        args.putLong(ARG_VK_ALBUM_ID, vk_album_id);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPhotoList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_photo_list, container, false);
        setRetainInstance(true);
        doRequest(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.getItem(0).setEnabled(true);
        //MenuItem item1 = menu.getItem(1).setEnabled(true);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Dialogs.addPhotoFrom().show();
                VKHelper.getPhotoList(getArguments().getLong(ARG_VK_GROUP_ID), getArguments().getLong(ARG_VK_ALBUM_ID), 1, new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        handleResponse(response, columns, view);
                        addPhoto().show();
                    }
                });

                return true;
            }
        });

        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    int columns;
    View view = null;

    public void doRequest(final View view) {
        this.view = view;

        final Bundle arguments = getArguments();
        float scaleFactor = getResources().getDisplayMetrics().density * 50;
        float scalefactor = getResources().getDisplayMetrics().density * 80;
        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final int columns = (int) ((float) number / (float) scaleFactor);
        this.columns = columns;
        VKHelper.count = 0;
        VKHelper.getPhotoList(arguments.getLong(ARG_VK_GROUP_ID), arguments.getLong(ARG_VK_ALBUM_ID), 1, new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                handleResponse(response, columns, view);
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


    FragmentTransaction transaction;
    ObjectAnimator objectAnimator = new ObjectAnimator();

    //
//    protected void handleResponse (VKResponse response, final int columns, View view) {
//
//        final ArrayList<Photo> photos = Photo.getPhotosFromJSONArray(response.json);
//        for(int i =0; i<photos.size();i++){
//            photos2.add(photos.get(i));
//        }
//
//        try {
//            gridOfPhotos = (JazzyGridView) view.findViewById(R.id.gridOfPhotos);
//            // gridOfPhotos.setTransitionEffect(mCurrentTransitionEffect);
//        } catch (NullPointerException e) {
//            Log.d("Loadding failed", "Not complete");
//        }
//        final Animation a;
//        a = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.abc_slide_in_bottom);
//        gridOfPhotos.setNumColumns(columns);
//        final PhotoListAdapter photoListAdapter = new PhotoListAdapter(photos2, getActivity().getLayoutInflater());
//        gridOfPhotos.setAdapter(photoListAdapter);
//        gridOfPhotos.setAnimation(a);
//        gridOfPhotos.setOnScrollListener(this);
//        gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Fragment fragment = FragmentFullScreenImagePhotoViewer.newInstance(photos, position, getArguments().getLong(ARG_VK_GROUP_ID), getArguments().getLong(ARG_VK_ALBUM_ID));
//                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
//                transaction = fragmentManager.beginTransaction();
//
//                transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
//                transaction.replace(R.id.container, fragment).addToBackStack("String").commit();
//            }
//        });
//
//    }
    public static int albumSize;

    protected void handleResponse(VKResponse response, final int columns, View view) {

        final ArrayList<VKApiPhoto> photos = VKHelper.getPhotosFromJSONArray(response.json);
        albumSize = VKHelper.countOfPhotos;
        for (int i = 0; i < photos.size(); i++) {
            photos2.add(photos.get(i));
        }

        try {
            gridOfPhotos = (GridView) view.findViewById(R.id.gridOfPhotos);
            // gridOfPhotos.setTransitionEffect(mCurrentTransitionEffect);
        } catch (NullPointerException e) {
            Log.d("Loadding failed", "Not complete");
        }


        final Animation a;
        a = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.abc_slide_in_bottom);

        gridOfPhotos.setNumColumns(columns);
        final PhotoListAdapter photoListAdapter = new PhotoListAdapter(photos2, getActivity().getLayoutInflater());
        gridOfPhotos.setAdapter(photoListAdapter);
        gridOfPhotos.setAnimation(a);
        gridOfPhotos.setOnScrollListener(this);

        if (getArguments().getLong(ARG_VK_GROUP_ID) > 0) {
            gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().getSupportFragmentManager().popBackStack();
                    Constants.tempPostAttachCounter++;
                    Constants.tempPhotoPostAttach.add(photos.get(position));
                    FragmentMakePost.refreshMakePostFragment(0);
                }
            });
        } else {
            gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment fragment = null;
                    fragment = FragmentFullScreenImagePhotoViewer.newInstance(photos, position, getArguments().getLong(ARG_VK_GROUP_ID), getArguments().getLong(ARG_VK_ALBUM_ID));
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });
        }

    }
}
