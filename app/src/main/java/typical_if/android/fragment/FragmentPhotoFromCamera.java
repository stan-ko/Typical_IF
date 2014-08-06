package typical_if.android.fragment;


import android.app.Activity;
import android.app.FragmentManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import typical_if.android.MyApplication;
import typical_if.android.R;

/**
 * Created by LJ on 29.07.2014.
 */
public class FragmentPhotoFromCamera extends Fragment {

    private static Uri uri;
    private File file = null;
    private ImageView photofromcamera;
    private ImageLoader imageLoader;
    final int displayHeight = MyApplication.getDisplayHeight();
    final int displayWidth = MyApplication.getDisplayWidth();

    public static FragmentPhotoFromCamera newInstance(Uri uri) {
        FragmentPhotoFromCamera fragment = new FragmentPhotoFromCamera();
        FragmentPhotoFromCamera.uri = uri;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload_photo_from_camera, container, false);
        setRetainInstance(true);
        photofromcamera = (ImageView) rootView.findViewById(R.id.image_from_photocamera);

//        Matrix matrix = photofromcamera.getImageMatrix();
//        RotateAnimation animation = new RotateAnimation(0, 90,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setInterpolator(new LinearInterpolator());
//        animation.setDuration(1);
//        animation.setFillAfter(true);
//        RectF drawableRect = new RectF(0, 0, 300, 300);
//        RectF viewRect = new RectF(0, 0, displayWidth, displayHeight);
//        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
//        matrix.postScale(photofromcamera.getWidth(), photofromcamera.getHeight());
//        photofromcamera.setAnimation(animation);
//        imageLoader.getInstance().displayImage(String.valueOf(Uri.fromFile(file)), photofromcamera);
        file = new File(getRealPathFromURI(uri));
        photofromcamera.setImageBitmap(getRotatedBitmapByExif(file));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (file.exists()) {
            getActivity().getContentResolver().delete(uri, null, null);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.upload_captured_photo, menu);
        MenuItem item =  menu.getItem(0);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final VKRequest req = VKApi.uploadAlbumPhotoRequest(file, 123513499, 8686797);
                req.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                    }
                });
                return true;
            }
        });
    }

    public static Bitmap getRotatedBitmapByExif(File targetFile){
        Bitmap bitmap = null;
        // определяем необходимость поворота фотки
        try {
            final Matrix matrix = new Matrix();

            ExifInterface exifReader = new ExifInterface(targetFile.getAbsolutePath());

            int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            boolean isRotationNeeded = false;

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    isRotationNeeded = true;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    isRotationNeeded = true;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    isRotationNeeded = true;
                    break;

                default: // ExifInterface.ORIENTATION_NORMAL
                    // Do nothing. The original image is fine.
                    break;
            }

            BitmapFactory.Options bmfOtions = new BitmapFactory.Options();
            bmfOtions.inPurgeable = true;

            if (isRotationNeeded){
                FileInputStream fileInputStream = null;
                FileDescriptor fileDescriptor = null;
                try {
                    fileInputStream = new FileInputStream(targetFile);
                    try {
                        fileDescriptor = fileInputStream.getFD();
                    } catch (IOException ignored) {}

                    if (fileDescriptor != null)
                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, bmfOtions);
                    else
                        bitmap = BitmapFactory.decodeStream(fileInputStream, null, bmfOtions);
                } catch (FileNotFoundException e){}
                finally {
                    if (fileInputStream != null)
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {}
                }
                if (bitmap!=null)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            else {
                FileInputStream fileInputStream = null;
                FileDescriptor fileDescriptor = null;
                try {
                    fileInputStream = new FileInputStream(targetFile);
                    try {
                        fileDescriptor = fileInputStream.getFD();
                    } catch (IOException ignored) {}

                    if (fileDescriptor != null)
                        bitmap =  BitmapFactory.decodeFileDescriptor(fileDescriptor, null, bmfOtions);
                    else
                        bitmap = BitmapFactory.decodeStream(fileInputStream, null, bmfOtions);
                } catch (FileNotFoundException e){}
                finally {
                    if (fileInputStream != null)
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {}
                }
            }

        }
//        catch (IOException e) {
//            Log.e("ImageUtils", e);
//        }
//        catch (Exception e) {
//            // like there is no EXIF support?
//            Log.e("ImageUtils", e);
//        }
        catch (Throwable e) {
            // Out of stupid vedroid's memory
            Log.e("ImageUtils", e.toString());
        }

        return bitmap;
    }
}
