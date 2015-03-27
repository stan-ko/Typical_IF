package typical_if.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;

import java.io.File;

import typical_if.android.Constants;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.UploadPhotoService;
import typical_if.android.VKRequestListener;

/**
 * Created by LJ on 29.07.2014.
 */
public class FragmentPhotoFromCamera extends Fragment {

    private static String path;
    private ImageView photofromcamera;
    private ImageView uploadPhotoFromCamera;
    private int displayWidth = TIFApp.getDisplayWidth();
    private int displayHeight = TIFApp.getDisplayHeight();

    public static FragmentPhotoFromCamera newInstance(String path) {
        FragmentPhotoFromCamera fragment = new FragmentPhotoFromCamera();
        FragmentPhotoFromCamera.path=path;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("My ID", String.valueOf( OfflineMode.loadLong(Constants.VK_GROUP_ID)));
        View rootView = inflater.inflate(R.layout.fragment_upload_photo_from_camera, container, false);
        setRetainInstance(true);
        File imageFile = new File(path);
        photofromcamera = (ImageView) rootView.findViewById(R.id.image_from_photocamera);
        photofromcamera.setImageBitmap(rotate(shrinkmethod(path, displayWidth, displayHeight), getCameraPhotoOrientation(getActivity().getApplicationContext(), Uri.fromFile(imageFile), path)));
        uploadPhotoFromCamera = (ImageView) rootView.findViewById(R.id.upload_photo_from_camera);
        uploadPhotoFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File tempFile = new File(path);
                getActivity().startService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
                final VKRequest req = VKApi.uploadAlbumPhotoRequest(tempFile, Constants.ALBUM_ID, (int)( OfflineMode.loadLong(Constants.VK_GROUP_ID)*(-1)));
                req.executeWithListener(new VKRequestListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("MY response", vkResponse.responseString);
                        //getActivity().stopService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
                    }
//                    @Override
//                    public void onError(VKError error) {
//                        super.onError(error);
//                        OfflineMode.onErrorToast();
//                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity().getApplicationContext(), UploadPhotoService.class));
    }


    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();

            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                throw ex;
            }
        }
        return b;
    }

    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    Bitmap shrinkmethod(String file,int width,int height){
        BitmapFactory.Options bitopt=new BitmapFactory.Options();
        bitopt.inJustDecodeBounds=true;
        Bitmap bit=BitmapFactory.decodeFile(file, bitopt);

        int h=(int) Math.ceil(bitopt.outHeight/(float)height);
        int w=(int) Math.ceil(bitopt.outWidth/(float)width);

        if(h>1 || w>1){
            if(h>w){
                bitopt.inSampleSize=h;

            }else{
                bitopt.inSampleSize=w;
            }
        }
        bitopt.inJustDecodeBounds=false;
        bit=BitmapFactory.decodeFile(file, bitopt);

        return bit;
    }




}
