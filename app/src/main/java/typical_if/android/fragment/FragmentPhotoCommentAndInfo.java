package typical_if.android.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import typical_if.android.MyApplication;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.CommentsListAdapter;
import typical_if.android.adapter.FullScreenImageAdapter;
import typical_if.android.model.Photo;
import typical_if.android.model.Profile;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPhotoCommentAndInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPhotoCommentAndInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPhotoCommentAndInfo extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static String ARG_VK_GROUP_ID = "vk_group_id";
    private static final String ARG_VK_ALBUM_ID = "vk_album_id";
    private static final String ARG_VK_USER_ID = "user_id";
    public static ArrayList<Photo> photo;
    Bundle arguments;

    ListView listOfComments;
    ArrayList<VKApiComment> comments;
    ArrayList<Profile> profiles;
    CommentsListAdapter adapter;
    String message = null;
    EditText commentMessage;
    CheckBox likes;
    View rootView;
    int isLiked;
    int countLikes;
    static int currentPosition;
    int reply_to_comment = 0;
    static int like_status;


    public static FragmentPhotoCommentAndInfo newInstance(long vk_group_id, long vk_album_id,
                                                          ArrayList<Photo> photo, long vk_user_id,
                                                          int isLiked, int currentPosition, int like_st) {

        FragmentPhotoCommentAndInfo fragment = new FragmentPhotoCommentAndInfo();
        Bundle args = new Bundle();
        args.putLong(ARG_VK_GROUP_ID, vk_group_id);
        args.putLong(ARG_VK_ALBUM_ID, vk_album_id);
        args.putLong(ARG_VK_USER_ID, vk_user_id);
        args.putInt("isLiked", isLiked);
        FragmentPhotoCommentAndInfo.currentPosition = currentPosition;

        fragment.setArguments(args);

        FragmentPhotoCommentAndInfo.photo = photo;
        like_status = like_st;

        return fragment;
    }

    public FragmentPhotoCommentAndInfo() {  }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }


    @Override
    public void onResume() {
        super.onResume(); VKUIHelper.onResume(getActivity());

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Bundle arguments = getArguments();
        final View rootView = inflater.inflate(R.layout.fragment_photo_comment_and_info, container, false);
        //likes = ((CheckBox) rootView.findViewById(R.id.liked_or_not_liked_checkbox1));
        listOfComments = ((ListView) rootView.findViewById(R.id.listOfComments));

        // final ImageView imageOnCommentFragment = (ImageView) rootView.findViewById(R.id.imageView_on_photo_comment_fragment);
        View listHeaderView = rootView.inflate(getActivity().getApplicationContext(), R.layout.image_header, null);
        //RelativeLayout photoUserSender = ((RelativeLayout) rootView.findViewById(R.id.user_photo_sender));

        ImageView headerView = (ImageView) listHeaderView.findViewById(R.id.list_header_image);
        listOfComments.addHeaderView(listHeaderView);
       // ImageLoader.getInstance().displayImage(photo.get(currentPosition).photo_75, headerView);
        loadImage(photo.get(currentPosition),headerView);

        final Button sendComment = (Button) rootView.findViewById(R.id.buttonSendComment);
        final ArrayList<Photo> photo = this.photo;

        commentMessage = (EditText) rootView.findViewById(R.id.field_of_message_for_comment);





        UpdateCommentList(arguments.getLong(ARG_VK_GROUP_ID), listOfComments, inflater);




        final CheckBox likePostPhoto = ((CheckBox) rootView.findViewById(R.id.like_post_photo_checkbox));

        likePostPhoto.setText(String.valueOf(photo.get(currentPosition).likes));

        VKHelper.isLIked("photo",arguments.getLong(ARG_VK_GROUP_ID),photo.get(currentPosition).id,new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                JSONObject j = response.json.optJSONObject("response");
                isLiked= j.optInt("liked");

            }
        });





        if (isLiked == 0) {

            likePostPhoto.setBackgroundColor(Color.WHITE);
            likePostPhoto.setChecked(true);

            likePostPhoto.setTextColor(Color.GRAY);
        }
        if (isLiked == 1) {
            likePostPhoto.setBackgroundColor(Color.BLUE);
            likePostPhoto.setChecked(false);

            likePostPhoto.setTextColor(Color.WHITE);
        }


//        likePostPhoto.setText(photo.get(currentPosition).likes);
        likePostPhoto.setOnClickListener
                (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked == 0){
                    VKHelper.setLike("photo", photo.get(currentPosition).owner_id, photo.get(currentPosition).id, new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    {  likePostPhoto.setBackgroundColor(Color.BLUE);
                                    likePostPhoto.setChecked(true);
                                    likePostPhoto.setText(String.valueOf(Integer.parseInt(likePostPhoto.getText().toString()) + 1));
                                    FullScreenImageAdapter.photos.get(currentPosition).likes+=1;
                                    likePostPhoto.setTextColor(Color.WHITE);
                                    FullScreenImageAdapter.static_like_status =1;}
                                }
                            });
}
                if (isLiked == 1) {
                    VKHelper.deleteLike("photo", photo.get(currentPosition).owner_id, photo.get(currentPosition).id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                           {  likePostPhoto.setBackgroundColor(Color.WHITE);
                            likePostPhoto.setChecked(false);
                            likePostPhoto.setText(String.valueOf(Integer.parseInt(likePostPhoto.getText().toString()) - 1));
                            FullScreenImageAdapter.photos.get(currentPosition).likes-=1;
                            likePostPhoto.setTextColor(Color.GRAY);
                            FullScreenImageAdapter.static_like_status =0;}
                        }
                    });




                }
            }
        });


        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = commentMessage.getText().toString();

                if (reply_to_comment == 0) {

                    VKHelper.createCommentForPhoto(arguments.getLong(ARG_VK_GROUP_ID), photo.get(currentPosition).id, message, 0, 0, new VKRequest.VKRequestListener() {

                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                          //  Toast.makeText(getActivity().getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
                            commentMessage.setText("");
                            UpdateCommentList(arguments.getLong(ARG_VK_GROUP_ID), listOfComments, inflater);
                        }
                    });

                } else {
                    VKHelper.createCommentForPhoto(arguments.getLong(ARG_VK_GROUP_ID), photo.get(currentPosition).id, message, 0, reply_to_comment, new VKRequest.VKRequestListener() {

                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                          //  Toast.makeText(getActivity().getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
                            commentMessage.setText("");
                            UpdateCommentList(arguments.getLong(ARG_VK_GROUP_ID), listOfComments, inflater);
                        }
                    });

                }


            }
        });

        ImageView likedImage = ((ImageView) rootView.findViewById(R.id.image_like_or_not_like));

//for(int i = 0 ; i < comments.size();i++){
//    if (comments.get(i).user_likes==true){
//        likedImage.setBackgroundResource(R.drawable.ic_post_btn_like_selected);
//        if (comments.get(i).user_likes==false){ likedImage.setBackgroundResource(R.drawable.ic_post_btn_like_up);}
//    }
//}
//
//
//        VKHelper.isLIked("photo", photo.get(currentPosition).owner_id, photo.get(currentPosition).id,new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//
//                isLiked= (response.json.optJSONObject("response")).optInt("liked");
//                if(isLiked==0){
//                    notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
//                    likedOrNotLikedBox.setChecked(false);}
//                if(isLiked==1){notLikedPhoto.setBackgroundResource((R.drawable.ic_post_btn_like_selected));
//                    likedOrNotLikedBox.setChecked(true);}
//
//                arguments.putInt("isLiked",isLiked);
//
//
//            }
//        });


        return rootView;

    }


    public void UpdateCommentList(long owner_id, final ListView listOfComments, final LayoutInflater inflater) {
        VKHelper.getCommentsForPhoto(owner_id, photo.get(currentPosition).id, new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        parseCommentList(response);
                    }
                }).start();


            }

        });


        listOfComments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position > 0) {
                    showContextMenu(id, position - 1);
                }

            }
        });
    }


    public void showContextMenu(long id, int position) {
        VKApiComment comment = comments.get(position);
        final long user_id = getArguments().getLong(ARG_VK_USER_ID);
        if (comment.from_id == user_id) {
            final CharSequence[] itemsForMe = {"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Видалити"};
            onInitContextMenu(itemsForMe, position);
        } else {
            final CharSequence[] items = {"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Оцінили"};
            onInitContextMenu(items, position);

        }


    }


    public void parseCommentList(final VKResponse response) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        JSONArray[] arrayOfComments = VKHelper.getResponseArrayOfComment(response);

        comments = VKHelper.getCommentsFromJSON(arrayOfComments[0]);
        Collections.sort(comments);
        profiles = Profile.getProfilesFromJSONArray(arrayOfComments[1]);
        //profile=profiles;


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    adapter = new CommentsListAdapter(comments, profiles, inflater, null);
                    listOfComments.setAdapter(adapter);
                } else {
                    adapter.UpdateCommentList(comments, profiles, listOfComments);

                }
            }
        });
    }

    public void onInitContextMenu(final CharSequence[] items, final int position) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: {
                        //onVkontakteBtnPressed();
                        Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).from_id + "");
                        getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), "Відкрити за допомогою")
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    break;
                    case 1: {
                        //////////////////////////////////////////////////////////////////////
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                reply_to_comment = comments.get(position).id;
                                commentMessage.setText(Identify(comments, profiles) + ", ");


                            }
                        });


                        ////////////////////////////////////////////////////////////////////
                    }
                    break;
                    case 2: {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(comments.get(position).text);
                    }
                    break;
                    case 3: {
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        Toast.makeText(getActivity().getApplicationContext(), comments.get(position).likes + "", Toast.LENGTH_LONG).show();
                        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                    break;
                    case 4: {
                        VKHelper.deleteCommentForPhoto(getArguments().getLong(ARG_VK_GROUP_ID), comments.get(position).id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);

                                UpdateCommentList(getArguments().getLong(ARG_VK_GROUP_ID), listOfComments, inflater);
                            }

                            @Override
                            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                                super.attemptFailed(request, attemptNumber, totalAttempts);
                               // Toast.makeText(getActivity().getApplicationContext(), "attemptFailed", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                              //  Toast.makeText(getActivity().getApplicationContext(), "onError", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                                super.onProgress(progressType, bytesLoaded, bytesTotal);
                               // Toast.makeText(getActivity().getApplicationContext(), "onProgress", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    break;

                    default:
                      //  Toast.makeText(getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                }

            }
        });
        alert = builder.create();
        alert.show();
    }


//    public void ReplyToUser(int position,final Bundle arguments, final LayoutInflater inflater){
//
// message="test";
//                VKHelper.createCommentForPhoto(arguments.getLong(ARG_VK_GROUP_ID),
//                        photo.get(currentPosition).id,
//                        "test", 0,
//                        562650,
//                        new VKRequest.VKRequestListener() {
//
//                    @Override
//                    public void onComplete(VKResponse response) {
//                        super.onComplete(response);
//                        Toast.makeText(getActivity().getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
//                      //  commentMessage.setText("");
//                      //  UpdateCommentList(arguments.getLong(ARG_VK_GROUP_ID), listOfComments, inflater);
//                    }
//
//
//                });
//
//
//    }

    public String Identify(ArrayList<VKApiComment> commentsList, ArrayList<Profile> profilesList) {
        String name = "";
        for (int i = 0; i < commentsList.size(); i++) {
            if (commentsList.get(i).id == reply_to_comment) {
                for (int j = 0; j < profilesList.size(); j++) {
                    if (commentsList.get(i).from_id == profilesList.get(j).id) {
                        name = profilesList.get(j).first_name;
                    }
                }

            }

        }
        return name;

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_device_item_remove, menu);

    }

    AlertDialog alert;

    private void shareViaSystemPrompt() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "shareText");
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "share_via_other_client"));
        //onShareSuccess();
    }


    private void onVkontakteBtnPressed() {
        if (!shareViaIntent(SHARE_TYPE_VKONTAKTE))
            shareViaSystemPrompt();
        //ShareViaVK(); // т.е. если ВК не установлен то шарить через апи
    }

    private static final String SHARE_TYPE_VKONTAKTE = "com.vkontakte.android";

    private boolean shareViaIntent(String type) {
        boolean found = false;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");// ("image/jpeg");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase(Locale.US).contains(type)
                        || info.activityInfo.name.toLowerCase(Locale.US).contains(type)) {

                    share.putExtra(Intent.EXTRA_SUBJECT, "shareTitle");
                    share.putExtra(Intent.EXTRA_TEXT, "shareText");

                    // share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new
                    // File(myPath)) ); // Optional, just if you wanna share an
                    // image.
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (found) {
                getActivity().startActivity(Intent.createChooser(share, "SHARE_PROMPT"));
                // onShareSuccess();
            }
        }
        return found;
    }


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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
        ImageLoader.getInstance().displayImage(url, imageView);
    }

    final int displayHeight = MyApplication.getDisplayHeight();
}