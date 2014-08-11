package typical_if.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.twotoasters.jazzylistview.JazzyGridView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.PhotoListAdapter;

public class FragmentPhotoList extends Fragment {
    static Uri mImageUri;
    private static final String ARG_VK_GROUP_ID = "vk_group_id";
    private static final String ARG_VK_ALBUM_ID = "vk_album_id";
    private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private static final int PICK_FROM_CAMERA = 1;
    JazzyGridView gridOfPhotos;

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.getItem(0).setEnabled(true);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addPhoto().show();

                return true;
            }
        });
    }

    public void doRequest(final View view) {
        final Bundle arguments = getArguments();
        float scalefactor = getResources().getDisplayMetrics().density * 80;
        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final int columns = (int) ((float) number / (float) scalefactor);

        VKHelper.getPhotoList(arguments.getLong(ARG_VK_GROUP_ID), arguments.getLong(ARG_VK_ALBUM_ID), new VKRequest.VKRequestListener() {
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

    protected void handleResponse(VKResponse response, int columns, View view) {
        final ArrayList<VKApiPhoto> photos = VKHelper.getPhotosFromJSONArray(response.json);
        gridOfPhotos = (JazzyGridView) view.findViewById(R.id.gridOfPhotos);
        gridOfPhotos.setTransitionEffect(mCurrentTransitionEffect);
        gridOfPhotos.setNumColumns(columns);
        final PhotoListAdapter photoListAdapter = new PhotoListAdapter(photos, getActivity().getLayoutInflater());
        gridOfPhotos.setAdapter(photoListAdapter);
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

    public Dialog addPhoto() {
        final String[] items = {"З карти памяті", "З камери"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Завантажити фото ?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        FragmentUploadAlbumList fragmentUploadPhotoList = new FragmentUploadAlbumList();
                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, fragmentUploadPhotoList).addToBackStack("PhotoList").commit();
                        dialog.cancel();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        dialog.cancel();

                        break;
                    default:
                        break;
                }
            }
        });
        builder.setCancelable(true);

        return builder.create();
    }

    public void takePhotoFromCamera() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "pic_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        if (file == null)
            return;
        Constants.tempCameraPhotoFile = file.getAbsolutePath();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputFileUri = Uri.fromFile(file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        getActivity().startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
    }


}
