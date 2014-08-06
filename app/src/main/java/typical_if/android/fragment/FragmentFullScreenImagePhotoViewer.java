package typical_if.android.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.FullScreenImageAdapter;
import typical_if.android.model.Photo;

public class FragmentFullScreenImagePhotoViewer extends Fragment implements ViewPager.OnPageChangeListener {


    private OnFragmentInteractionListener mListener;
    public static ArrayList<Photo> photos;
    private ViewPager imagepager;
    public static int currentPosition;

    public static final String ARG_VK_GROUP_ID = "vk_group_id";
    public static final String ARG_VK_ALBUM_ID = "vk_album_id";

    public static final String ARG_VK_USER_ID = "user_id";
    public static final String TYPE = "photo";
    public static Bundle args;
    public View rootView;

    int isLiked;
    long user_id;

        TextView countLikes;
        TextView countComments;
        ImageView like;
        ImageView comment;
        TextView photoHeader;
        CheckBox likedOrNotLikedBox;
        TextView counterOfPhotos;
        TextView albumSize;

    public static FragmentFullScreenImagePhotoViewer newInstance(ArrayList<Photo> photos, int currentposition, long vk_group_id, long vk_album_id) {

        FragmentFullScreenImagePhotoViewer fragment = new FragmentFullScreenImagePhotoViewer();
        args = new Bundle();
        fragment.setArguments(args);
        FragmentFullScreenImagePhotoViewer.photos = photos;

        FragmentFullScreenImagePhotoViewer.currentPosition = currentposition;
        args.putLong(ARG_VK_GROUP_ID, vk_group_id);
        args.putLong(ARG_VK_ALBUM_ID, vk_album_id);
        return fragment;
    }

    public FragmentFullScreenImagePhotoViewer() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }




    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();



        rootView = inflater.inflate(R.layout.fragment_fullscreen_list, container, false);

        countLikes = (TextView) rootView.findViewById(R.id.count_of_likes);
        countComments = (TextView) rootView.findViewById(R.id.count_of_comments);
         like = (ImageView) rootView.findViewById(R.id.image_not_liked);
         comment = (ImageView) rootView.findViewById(R.id.image_comment);
         likedOrNotLikedBox = ((CheckBox) rootView.findViewById(R.id.liked_or_not_liked_checkbox));
         photoHeader = (TextView) rootView.findViewById(R.id.photoHeader);
        counterOfPhotos = (TextView) rootView.findViewById(R.id.counterOfPhotos);
         albumSize = (TextView) rootView.findViewById(R.id.amountOfPhotos);

        FragmentManager manager = getFragmentManager();
        imagepager = (ViewPager) rootView.findViewById(R.id.pager);
        imagepager.setOnPageChangeListener(this);

        imagepager.setAdapter(new FullScreenImageAdapter(photos, getLayoutInflater(arguments), arguments, arguments.getLong(ARG_VK_GROUP_ID),
        arguments.getLong(ARG_VK_ALBUM_ID), arguments.getLong(ARG_VK_USER_ID), manager, rootView));
        imagepager.setCurrentItem(currentPosition);





        Log.d("Current VIEW", photos.get(imagepager.getCurrentItem()).text);
        VKHelper.getUserInfo(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                JSONArray arr = response.json.optJSONArray("response");
                JSONObject jsonObject = arr.optJSONObject(0);
                user_id = jsonObject.optLong("id");
                arguments.putLong(ARG_VK_USER_ID, user_id);

            }
        });


        setRetainInstance(true);

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
    public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
        Log.d("positionOnPageScrolled^---------------------------------------------------------------->"+"   ",position+"");
        photoHeader.setText(photos.get(position).text);
        countLikes.setText(String.valueOf(photos.get(position).likes));
        countComments.setText(String.valueOf(photos.get(position).comments));
        counterOfPhotos.setText(String.valueOf(position+1));
        albumSize.setText(String.valueOf(photos.size()));

//        //////////////////
        VKHelper.isLIked("photo", FragmentFullScreenImagePhotoViewer.args.getLong(FragmentFullScreenImagePhotoViewer.ARG_VK_GROUP_ID), photos.get(position).id, new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                JSONObject j = response.json.optJSONObject("response");
                isLiked = j.optInt("liked");
                photos.get(position).user_likes = isLiked;

            }
        });
//////////////////////////////////////////////////////////////////////////////////////
        if (photos.get(position).user_likes == 0) {
            //static_like_status = 0;
            photos.get(position).user_likes = isLiked;
            like.setBackgroundResource((R.drawable.ic_post_btn_like_up));
          //  Toast.makeText(VKUIHelper.getApplicationContext(), "ISLIKED==0 ", Toast.LENGTH_SHORT).show();
            likedOrNotLikedBox.setChecked(false);
        }
        if (photos.get(position).user_likes == 1) {
            //static_like_status = 1;
            //Toast.makeText(VKUIHelper.getApplicationContext(), "ISLIKED==1 ", Toast.LENGTH_SHORT).show();
            photos.get(photos.get(position).user_likes).user_likes = isLiked;
            like.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
            likedOrNotLikedBox.setChecked(true);
        }

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked == 0) {
                    VKHelper.setLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            like.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
                            likedOrNotLikedBox.setChecked(true);

                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString()) + 1));
                            photos.get(position).likes += 1;
                            Toast.makeText(VKUIHelper.getApplicationContext(), "LIKED: ", Toast.LENGTH_SHORT).show();
                            isLiked = 1;
                            photos.get(position).user_likes = 1;

                        }
                    });


                }
                if (isLiked == 1) {
                    VKHelper.deleteLike(TYPE, photos.get(position).owner_id, photos.get(position).id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            likedOrNotLikedBox.setChecked(false);

                            like.setBackgroundResource((R.drawable.ic_post_btn_like_up));
                            countLikes.setText(String.valueOf(Integer.parseInt(countLikes.getText().toString()) - 1));
                            photos.get(position).likes -= 1;
                            photos.get(position).user_likes = 0;

                            //  like_status=0;
                            //static_like_status=0;

                            Toast.makeText(VKUIHelper.getApplicationContext(), "LIKE DELETED", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(VKUIHelper.getApplicationContext(), "position is: " + position, Toast.LENGTH_SHORT).show();

                FragmentPhotoCommentAndInfo fragment = FragmentPhotoCommentAndInfo.newInstance(args.getLong(ARG_VK_GROUP_ID), args.getLong(ARG_VK_ALBUM_ID),
                                                                                                photos, user_id, args.getInt("isLiked"), position, isLiked);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });



    }

    @Override
    public void onPageSelected(final int position) {

        Log.d("positionOnPageSelected^---------------------------------------------------------------->"+"   ",position+"");


    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d("stateOnPageScrollStateChanged^---------------------------------------------------------------->"+"   ",state+"");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



//        fullScreenPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickCounter++;
//                if(clickCounter%2==0) {
//                    { likePhoto.startAnimation(animTextViewUp);
//                    commentPhoto.startAnimation(animTextViewUp);}
//                }else{
//                    {commentPhoto.startAnimation(animTextViewDown);
//                    likePhoto.startAnimation(animTextViewDown);}
//                }
//
//int clickCounter = -1;
//
//            }
//        });



}
