package typical_if.android.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.twotoasters.jazzylistview.JazzyGridView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.PhotoListAdapter;
import typical_if.android.model.Photo;
//import typical_if.android.model.UploadPhotos;

public class FragmentPhotoList extends Fragment {

    private static final String ARG_VK_GROUP_ID = "vk_group_id";
    private static final String ARG_VK_ALBUM_ID = "vk_album_id";
    private int mCurrentTransitionEffect = JazzyHelper.TILT;
    private OnFragmentInteractionListener mListener;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_photo_list, container, false);
        setRetainInstance(true);
        doRequest(rootView);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.main, menu);
        MenuItem item =  menu.getItem(0).setEnabled(true);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
        addPhoto().show();
                return true;
            }
        });

        //super.onCreateOptionsMenu(menu, inflater);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void doRequest(final View view){
        final Bundle arguments = getArguments();
        float scalefactor = getResources().getDisplayMetrics().density * 100;
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

    protected void handleResponse (VKResponse response, int columns, View view){
   //     Log.d("-------------------yutyut------------->",response.json.toString()+"");
         final ArrayList<Photo> photos = Photo.getPhotosFromJSONArray(response.json);



        try {
           gridOfPhotos = (JazzyGridView) view.findViewById(R.id.gridOfPhotos);
           gridOfPhotos.setTransitionEffect(mCurrentTransitionEffect);
        }
        catch (NullPointerException e){
            Log.d("Loadding failed", "Not complete");}

        gridOfPhotos.setNumColumns(columns);
        final  PhotoListAdapter photoListAdapter = new PhotoListAdapter(photos, getActivity().getLayoutInflater());
        gridOfPhotos.setAdapter(photoListAdapter);
        gridOfPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = FragmentFullScreenImagePhotoViewer.newInstance(photos, position ,getArguments().getLong(ARG_VK_GROUP_ID),getArguments().getLong(ARG_VK_ALBUM_ID));
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack("String").commit();
            }
        });
    }

    public Dialog addPhoto(){
        final String [] items = {"З карти памяті", "З камери"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Завантажити фото ?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        FragmentUploadAlbumList fragmentUploadPhotoList = new FragmentUploadAlbumList();
                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, fragmentUploadPhotoList).addToBackStack("PhotoList").commit();
                        dialog.cancel();
                        break;
                    case 1:


                        Intent second  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file        = new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        mImageCaptureUri = Uri.fromFile(file);

                        try {
                            //second.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            //second.putExtra("return-data", true);

                            startActivityForResult(second, PICK_FROM_CAMERA);


                          //  Log.d("URI -_---->>>>>>>>>>>>>>>", mImageCaptureUri.toString());
                                FragmentPhotoFromCamera fragmentPhotoFromCamera = new FragmentPhotoFromCamera().newInstance(mImageCaptureUri);
                                android.support.v4.app.FragmentManager fragmentManagers = getFragmentManager();
                                fragmentManagers.beginTransaction().replace(R.id.container, fragmentPhotoFromCamera).addToBackStack("PhotoList").commit();
                        } catch(ActivityNotFoundException anfe){
                            //display an error message
                            String errorMessage = "Whoops - your device doesn't support capturing images!";
                         //   Toast toast = Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                            //toast.show();
                        }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == PICK_FROM_CAMERA){
                mImageCaptureUri = data.getData();
                performCrop();

        }
    }

//    private void doCrop() {
//        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
//        /**
//         * Open image crop app by starting an intent
//         * ‘com.android.camera.action.CROP‘.
//         */
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setType("image/*");
//        /**
//         * Check if there is image cropper app installed.
//         */
//        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
//        int size = list.size();
//
//        /**
//         * If there is no image cropper app, display warning message
//         */
//        if (size == 0) {
//            Toast.makeText(this, cropEmpty,
//                    Toast.LENGTH_SHORT).show();
//
//            return;
//        } else {
//            /**
//             * Specify the image path, crop dimension and scale
//             */
//            intent.setData(mImageCaptureUri);
//            intent.putExtra("outputX", 200);
//            intent.putExtra("outputY", 200);
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("scale", true);
//            intent.putExtra("return-data", true);
//            /**
//             * There is posibility when more than one image cropper app exist,
//             * so we have to check for it first. If there is only one app, open
//             * then app.
//             */
//            if (size == 1) {
//                Intent i = new Intent(intent);
//                ResolveInfo res = list.get(0);
//                i.setComponent(new ComponentName(res.activityInfo.packageName,
//                        res.activityInfo.name));
//                startActivityForResult(i, CROP_FROM_CAMERA);
//            } else {
//                /**
//                 * If there are several app exist, create a custom chooser to
//                 * let user selects the app.
//                 */
//                for (ResolveInfo res : list) {
//                    final CropOption co = new CropOption();
//                    co.title = getPackageManager().getApplicationLabel(
//                            res.activityInfo.applicationInfo);
//                    co.icon = getPackageManager().getApplicationIcon(
//                            res.activityInfo.applicationInfo);
//                    co.appIntent = new Intent(intent);
//                    co.appIntent
//                            .setComponent(new ComponentName(
//                                    res.activityInfo.packageName,
//                                    res.activityInfo.name));
//
//                    cropOptions.add(co);
//                }
//                CropOptionAdapter adapter = new CropOptionAdapter(
//                        getApplicationContext(), cropOptions);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(openWith);
//                builder.setAdapter(adapter,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item) {
//                                startActivityForResult(cropOptions.get(item).appIntent,CROP_FROM_CAMERA);
//                            }
//                        });
//                builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//
//                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//
//                        if (mImageCaptureUri != null) {
//                            getContentResolver().delete(mImageCaptureUri, null,
//                                    null);
//                            mImageCaptureUri = null;
//                        }
//                    }
//                });
//
//                AlertDialog alert = builder.create();
//
//                alert.show();
//            }
//        }
//    }


    private void performCrop(){
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
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
