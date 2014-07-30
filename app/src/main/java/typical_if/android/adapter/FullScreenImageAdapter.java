package typical_if.android.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;

import typical_if.android.MyApplication;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.fragment.FragmentPhotoCommentAndInfo;
import typical_if.android.model.Photo;

/**
 * Created by LJ on 21.07.2014.
 */
public class FullScreenImageAdapter extends PagerAdapter {

    LayoutInflater inflater;
    private ArrayList<Photo> imagePaths;
    private DisplayImageOptions options;
    final int displayHeight = MyApplication.getDisplayHeight();
    private static final String FILTER = "likes";
    private static long user_id;
    private static final String TYPE = "photo";
    private static long groupID;
    private static long albumID;
     FragmentManager fragmentManager;
    int isLiked ;
    Bundle arguments;

    public FullScreenImageAdapter(ArrayList<Photo> photos, LayoutInflater inflater, Bundle arguments, long groupID, long albumID, long userID, FragmentManager fragmentManager) {
        imagePaths = photos;
        this.inflater = inflater;
        this.arguments=arguments;
        this.groupID=groupID;
        this.albumID=albumID;
        this.user_id=userID;
        this.fragmentManager=fragmentManager;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // TODO resource or drawable
                .showImageForEmptyUri(R.drawable.ic_empty_url) // TODO resource or drawable
                .showImageOnFail(R.drawable.ic_error) // TODO resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
//            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
//            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .build();
    }


    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView;
        View viewLayout = inflater.inflate(R.layout.fragment_fragment_full_screen_image_photo_viewer, null);
        final Button like = (Button)viewLayout.findViewById(R.id.like_photo);
        final Button comment = (Button)viewLayout.findViewById(R.id.comment_photo);
        final TextView countLikes = (TextView)viewLayout.findViewById(R.id.count_of_likes);
        final TextView countComments = (TextView)viewLayout.findViewById(R.id.count_of_comments);
        final ImageView notLikedPhoto = (ImageView)viewLayout.findViewById(R.id.image_not_liked);
        final CheckBox likedOrNotLikedBox = ((CheckBox) viewLayout.findViewById(R.id.liked_or_not_liked_checkbox));
        final TextView photoHeader = (TextView)viewLayout.findViewById(R.id.photoHeader);


        imageView = (ImageView) viewLayout.findViewById(R.id.full_screen_photo);
        ((ViewPager) container).addView(viewLayout);
        Log.d("Current VIEW", position + "");
        loadImage(imagePaths.get(position), imageView);

        photoHeader.setText(imagePaths.get(position).text);
        countLikes.setText(String.valueOf(imagePaths.get(position).likes));
        countComments.setText(String.valueOf(imagePaths.get(position).comments));
//for (int i = 0 ; i<imagePaths.size();i++) {


    VKHelper.isLIked(TYPE, imagePaths.get(position).owner_id, imagePaths.get(position).id, new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);

            isLiked = (response.json.optJSONObject("response")).optInt("liked");
            if (isLiked == 0) {
                notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_up));
                likedOrNotLikedBox.setChecked(false);
            }
            if (isLiked == 1) {
                notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
                likedOrNotLikedBox.setChecked(true);
            }

            arguments.putInt("isLiked", isLiked);


        }
    });
//}
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked == 0) {
                    VKHelper.setLike(TYPE, imagePaths.get(position).owner_id, imagePaths.get(position).id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
                            likedOrNotLikedBox.setChecked(true);
                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString())+1));

                            Toast.makeText(VKUIHelper.getApplicationContext(), "LIKED: " + isLiked, Toast.LENGTH_SHORT).show();
                            isLiked=1;

                        }});

                }if (isLiked==1){
                    VKHelper.deleteLike(TYPE, imagePaths.get(position).owner_id, imagePaths.get(position).id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            likedOrNotLikedBox.setChecked(false);
                            notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_up));
                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString())-1));
                            isLiked=0;

                            Toast.makeText(VKUIHelper.getApplicationContext(), "LIKE DELETED", Toast.LENGTH_SHORT).show();

                        }});
                }
            }});

                comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(getActivity().getApplicationContext(), "You clicked comment", Toast.LENGTH_SHORT).show();
                FragmentPhotoCommentAndInfo fragment = FragmentPhotoCommentAndInfo.newInstance(groupID,
                        albumID,
                        imagePaths, user_id,
                        arguments.getInt("isLiked"), position);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });


                    return viewLayout;
                }

        @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    public void loadImage(Photo photo, ImageView imageView) {
        String url = null;
        if (!TextUtils.isEmpty(photo.photo_2048) && displayHeight > 1199) {
            url = photo.photo_2048;
        } else if (!TextUtils.isEmpty(photo.photo_1280) && displayHeight > 799) {
            url = photo.photo_1280;
        } else if (!TextUtils.isEmpty(photo.photo_807) && displayHeight > 600) {
            url = photo.photo_807;
        } else if (!TextUtils.isEmpty(photo.photo_604)) {
            url = photo.photo_604;
        } else if (!TextUtils.isEmpty(photo.photo_130)) {
            url = photo.photo_130;
        } else if (!TextUtils.isEmpty(photo.photo_75)) {
            url = photo.photo_75;
        }
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }


}
