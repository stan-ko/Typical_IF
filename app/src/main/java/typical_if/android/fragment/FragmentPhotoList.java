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
import android.widget.Toast;

import com.twotoasters.jazzylistview.JazzyGridView;
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

public class FragmentPhotoList extends Fragment implements AbsListView.OnScrollListener {

    public ArrayList<VKApiPhoto> photos2 = new ArrayList<VKApiPhoto>();
    private OnFragmentInteractionListener mListener;
    private static final String ARG_VK_GROUP_ID = "vk_group_id";
    private static final String ARG_VK_ALBUM_ID = "vk_album_id";
    //private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private static final int PICK_FROM_CAMERA = 1;

    final int PIC_CROP = 2;
    private static Uri mImageCaptureUri;

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.getItem(0).setEnabled(true);
        //MenuItem item1 = menu.getItem(1).setEnabled(true);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            gridOfPhotos = (JazzyGridView) view.findViewById(R.id.gridOfPhotos);
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
                        fragmentManager.beginTransaction().add(R.id.container, fragmentUploadPhotoList).addToBackStack("PhotoList").commit();
                        dialog.cancel();
                        break;
                    case 1:


                        Intent second = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        mImageCaptureUri = Uri.fromFile(file);

                        try {
                            startActivityForResult(second, PICK_FROM_CAMERA);

                            FragmentPhotoFromCamera fragmentPhotoFromCamera = new FragmentPhotoFromCamera().newInstance("mImageCaptureUri");
                            android.support.v4.app.FragmentManager fragmentManagers = getFragmentManager();
                            fragmentManagers.beginTransaction().add(R.id.container, fragmentPhotoFromCamera).addToBackStack("PhotoList").commit();
                        } catch (ActivityNotFoundException anfe) {
                            Toast.makeText(getActivity().getApplicationContext(), "Whoops - your device doesn't support capturing images!", Toast.LENGTH_LONG);
                        }

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


    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(mImageCaptureUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(final AbsListView view, int firstVisibleItem, final int visibleItemCount, int totalItemCount) {
//        if (firstVisibleItem + visibleItemCount >= totalItemCount) {
//
//            VKHelper.getPhotoList(getArguments().getLong(ARG_VK_GROUP_ID), getArguments().getLong(ARG_VK_ALBUM_ID), 0, new VKRequest.VKRequestListener() {
//                @Override
//                public void onComplete(VKResponse response) {
//                    super.onComplete(response);
//                    handleResponse(response, columns, view);
//                    Log.d("----------------------------------------------------------------------------->", visibleItemCount + "");
//                }
//
//            });
//        } else {
//        }
    }

}
