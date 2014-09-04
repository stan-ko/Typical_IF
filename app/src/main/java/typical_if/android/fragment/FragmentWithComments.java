package typical_if.android.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import typical_if.android.Constants;
import typical_if.android.Dialogs;
import typical_if.android.ItemDataSetter;
import typical_if.android.MyApplication;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.adapter.CommentsListAdapter;

import typical_if.android.adapter.WallAdapter;
import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;
import typical_if.android.util.PhotoUrlHelper;

public class FragmentWithComments extends Fragment {

    public static final String POSTED = "POSTED";
    final int displayHeight = MyApplication.getDisplayHeight();
    private OnFragmentInteractionListener mListener;


    private static final String ARG_VK_USER_ID = "user_id";
    public static VKApiPhoto photo = null;

    public static String TYPE;

    ListView listOfComments = null;
    ArrayList<VKApiComment> comments = null;
    ArrayList<VKApiUser> profiles = null;
    CommentsListAdapter adapter = null;
    VKApiUser postSender = null;

    String message = null;
    EditText commentMessage = null;
    View rootView = null;
    int reply_to_comment = 0;
    Wall wall = null;
    static VKWallPostWrapper post = null;
    String postColor = null;
    public int positionOfComment = 0;
    public static int group_id;

    WallAdapter.ViewHolder viewHolder = null;
    Button sendComment = null;
    boolean edit_status = false;
    static boolean loadFromWall = false;
    int position;
    LayoutInflater inflater;
    static long item_id;

    public static FragmentWithComments newInstanceForPhoto(VKApiPhoto photo, long vk_user_id) {
        loadFromWall = false;
        FragmentWithComments fragment = new FragmentWithComments();
        Bundle args = new Bundle();


        args.putLong(ARG_VK_USER_ID, vk_user_id);
        fragment.setArguments(args);
        FragmentWithComments.photo = photo;

        TYPE = "photo_comment";
        Constants.DELETE_COMMENT_METHOD_NAME = "photos.deleteComment";
        Constants.CREATE_COMMENT_METHOD_NAME = "photos.createComment";
        Constants.EDIT_COMMENT_METHOD_NAME = "photos.editComment";
        Constants.PARAM_NAME = "message";
        Constants.GET_COMMENTS_METHOD_NAME = "photos.getComments";
        Constants.PARAM_NAME2 = "photo_id";


        item_id = photo.id;
        group_id=photo.owner_id;

        return fragment;
    }

    public static FragmentWithComments newInstanceForWall(String postColor, int position, Wall wall, VKWallPostWrapper post) {
        loadFromWall = true;

        FragmentWithComments fragment = new FragmentWithComments();
        //Bundle args = new Bundle();

        fragment.postColor = postColor;
        fragment.wall = wall;
        fragment.position = position;
        FragmentWithComments.post = post;


        TYPE = "comment";
        Constants.DELETE_COMMENT_METHOD_NAME = "wall.deleteComment";
        Constants.CREATE_COMMENT_METHOD_NAME = "wall.addComment";
        Constants.EDIT_COMMENT_METHOD_NAME = "wall.editComment";
        Constants.PARAM_NAME = "text";
        Constants.GET_COMMENTS_METHOD_NAME = "wall.getComments";
        Constants.PARAM_NAME2 = "post_id";
        item_id = post.post.id;
        group_id=post.post.from_id;
        return fragment;
    }


    public FragmentWithComments() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    private void loadPhotoPosts() {
        final VKApiPhoto photo = this.photo;


        listOfComments = ((ListView) this.rootView.findViewById(R.id.listOfComments));
        View listHeaderView = rootView.inflate(getActivity().getApplicationContext(), R.layout.image_header, null);
        ImageView headerView = (ImageView) listHeaderView.findViewById(R.id.list_header_image);
        listOfComments.addHeaderView(listHeaderView);
        loadImage(photo, headerView);


        final RelativeLayout photoUserSender = ((RelativeLayout) rootView.findViewById(R.id.user_photo_sender));
        final ImageView postPhotoUserAvatar = (ImageView) rootView.findViewById(R.id.post_user_avatar);
        final TextView postPhotoUserName = ((TextView) rootView.findViewById(R.id.post_user_name));
        final TextView postPhotoUserDateOfComment = ((TextView) rootView.findViewById(R.id.post_user_date_of_comment));


        VKHelper.getWhoIsPosted(photo.user_id, "photo_50", new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONArray array = response.json.optJSONArray("response");
                    JSONObject object = array.optJSONObject(0);
                    postSender = new VKApiUser(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ImageLoader.getInstance().displayImage(postSender.photo_50, postPhotoUserAvatar);
                postPhotoUserName.setText(postSender.last_name + " " + postSender.first_name);
                postPhotoUserDateOfComment.setText(ItemDataSetter.getFormattedDate(photo.date));

            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }
        });

        photoUserSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://vk.com/id" + postSender.id + "");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), "Відкрити за допомогою")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        updateCommentList(group_id, photo.id, listOfComments, inflater);
        commentMessage = (EditText) rootView.findViewById(R.id.field_of_message_for_comment);


        final CheckBox likePostPhoto = ((CheckBox) rootView.findViewById(R.id.like_post_photo_checkbox));

        if (photo.user_likes == 0) {
            likePostPhoto.setBackgroundColor(Color.WHITE);
            likePostPhoto.setChecked(false);
            likePostPhoto.setText(String.valueOf(photo.likes));
            likePostPhoto.setTextColor(Color.GRAY);
        } else {
            likePostPhoto.setBackgroundColor(Color.BLUE);
            likePostPhoto.setChecked(true);
            likePostPhoto.setText(String.valueOf(photo.likes));
            likePostPhoto.setTextColor(Color.WHITE);
        }

        likePostPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo.user_likes == 0) {
                    VKHelper.setLike("photo", group_id, photo.id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            photo.user_likes = 1;

                            likePostPhoto.setText(String.valueOf(++photo.likes));
                            likePostPhoto.setBackgroundColor(Color.BLUE);
                            likePostPhoto.setChecked(true);
                            likePostPhoto.setTextColor(Color.WHITE);
                        }
                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });

                } else {
                    VKHelper.deleteLike("photo", group_id, photo.id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            photo.user_likes = 0;
                            likePostPhoto.setText(String.valueOf(--photo.likes));
                            likePostPhoto.setBackgroundColor(Color.WHITE);
                            likePostPhoto.setChecked(false);
                            likePostPhoto.setTextColor(Color.GRAY);
                        }
                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });
                }
            }
        });


    }

    private void loadWallPosts() {
        final View wallItem = inflater.inflate(R.layout.wall_lv_item, null);
        viewHolder = new WallAdapter.ViewHolder(wallItem);
        WallAdapter.initViewHolder(viewHolder, postColor, wall, position, getFragmentManager(), post, getActivity().getBaseContext());

        viewHolder.cb_post_comment.setVisibility(View.GONE);
        viewHolder.button_comment.setVisibility(View.GONE);
        viewHolder.img_post_other.setVisibility(View.GONE);

        final VKWallPostWrapper post = this.post;

        if (post.post.text.length() != 0) {
            RelativeLayout textLayout = (RelativeLayout) viewHolder.postTextLayout.getChildAt(0);
            CheckBox checkBox = (CheckBox) textLayout.getChildAt(1);
            checkBox.setChecked(true);
            checkBox.setVisibility(View.GONE);
        }

        if (post.post.copy_history != null && post.post.copy_history.size() != 0) {
            if (post.post.copy_history.get(0).text.length() != 0) {
                LinearLayout copyHistoryContainer = (LinearLayout) ((RelativeLayout) viewHolder.copyHistoryLayout.getChildAt(0)).getChildAt(0);
                RelativeLayout parentCopyHistoryTextContainer = (RelativeLayout) copyHistoryContainer.findViewById(R.id.copyHistoryTextLayout);
                RelativeLayout textLayout = (RelativeLayout) parentCopyHistoryTextContainer.getChildAt(0);
                CheckBox checkBox = (CheckBox) textLayout.getChildAt(1);
                checkBox.setChecked(true);
                checkBox.setVisibility(View.GONE);
            }
        }
        listOfComments.addHeaderView(wallItem);

        updateCommentList(group_id, post.post.id, listOfComments, inflater);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_photo_comment_and_info, container, false);

        listOfComments = ((ListView) rootView.findViewById(R.id.listOfComments));
        sendComment = (Button) rootView.findViewById(R.id.buttonSendComment);
        commentMessage = (EditText) rootView.findViewById(R.id.field_of_message_for_comment);
        if (loadFromWall) {
            loadWallPosts();
        } else {

            loadPhotoPosts();
        }

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = commentMessage.getText().toString();
                if (!edit_status) {
                    if (reply_to_comment == 0) {

                        VKHelper.createComment(group_id, item_id, message + "\n@club26363301 (fromMobileIF)", 0, new VKRequest.VKRequestListener() {

                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Toast.makeText(getActivity().getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
                                commentMessage.setText("");
                                updateCommentList(group_id, item_id, listOfComments, inflater);
                            }
                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }

                        });

                    } else {
                        VKHelper.createComment(group_id, item_id, message + "\n@club26363301 (fromMobileIF)", reply_to_comment, new VKRequest.VKRequestListener() {

                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Toast.makeText(getActivity().getApplicationContext(), POSTED, Toast.LENGTH_SHORT).show();
                                commentMessage.setText("");
                                updateCommentList(group_id, item_id, listOfComments, inflater);
                            }
                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });

                    }
                } else
                    VKHelper.editComment(group_id, comments.get(positionOfComment).id, message, null, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            updateCommentList(group_id, item_id, listOfComments, inflater);
                            commentMessage.setText("");
                            edit_status = false;
                        }
                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });

                edit_status = false;
            }
        });
        setRetainInstance(true);
        return rootView;
    }


    public void updateCommentList(long owner_id, final long item_id, final ListView listOfComments, final LayoutInflater inflater) {

        VKHelper.getComments(owner_id, item_id, new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(final VKResponse response) {
                super.onComplete(response);

                OfflineMode.saveJSON(response.json, item_id);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        parseCommentList(OfflineMode.loadJSON(item_id));
                    }
                }).start();
            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }

        });
        if (!OfflineMode.isOnline(getActivity().getApplicationContext()) & OfflineMode.isJsonNull(item_id)) {
            parseCommentList(OfflineMode.loadJSON(item_id));
            // If IsOnline and response from preferenses not null then load Json from preferenses
        }

        listOfComments.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        if (position > 0) {
                            showContextMenu(position - 1);
                        }

                    }
                }
        );
    }

    public boolean myComment = false;

    public void showContextMenu(int position) {
        VKApiComment comment = comments.get(position);

        String name = Identify(comments, profiles, position);
        if (name == "" || name == null) {
            name = comment.text.substring(0, 2);
        }
        CharSequence[] items;


        if (comment.from_id == Constants.USER_ID) {
            myComment = true;
            if (comment.reply_to_comment != 0) {
                if (!comment.user_likes)
                    items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Поскаржитись", "Видалити", "Редагувати", name};
                else
                    items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені не подобається", "Поскаржитись", "Видалити", "Редагувати", name};
            } else if (!comment.user_likes) {
                items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Поскаржитись", "Видалити", "Редагувати"};
            } else {
                items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені не подобається", "Поскаржитись", "Видалити", "Редагувати"};
            }
        } else {
            myComment = false;
            if (comment.reply_to_comment != 0) {
                if (!comment.user_likes)
                    items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Поскаржитись", name};
                else
                    items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені не подобається", "Поскаржитись", name};
            } else if (!comment.user_likes) {
                items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені подобається", "Поскаржитись"};
            } else {
                items = new CharSequence[]{"Профіль", "Відповісти", "Копіювати текст", "Мені не подобається", "Поскаржитись"};
            }

        }


        onInitContextMenu(items, position);
    }


    public void parseCommentList(final JSONObject response) {

        JSONArray[] arrayOfComments = VKHelper.getResponseArrayOfComment(response);
        comments = VKHelper.getCommentsFromJSON(arrayOfComments[0]);
        Collections.sort(comments);
        profiles = VKHelper.getProfilesFromJSONArray(arrayOfComments[1]);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    String postColor = ItemDataSetter.getPostColor(Constants.GROUP_ID);
                    adapter = new CommentsListAdapter(comments, profiles, inflater, postColor);
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
                        Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).from_id + "");
                        getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), "Відкрити за допомогою")
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    break;
                    case 1: {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reply_to_comment = comments.get(position).id;
                                commentMessage.setText(Identify(comments, profiles, -1) + ", ");

                            }
                        });

                    }
                    break;
                    case 2: {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(comments.get(position).text);
                    }
                    break;
                    case 3: {
                        if (!comments.get(position).user_likes) {

                            VKHelper.setLike(TYPE, group_id, comments.get(position).id, new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                ++comments.get(position).likes;
                                    comments.get(position).user_likes = true;
                                    adapter.changeStateLikeForComment(true,String.valueOf(comments.get(position).likes));
                                    Toast.makeText(getActivity().getApplicationContext(), comments.get(position).likes + "", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onError(VKError error) {
                                    super.onError(error);
                                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                }
                            });

                        } else {
                            VKHelper.deleteLike(TYPE, group_id, comments.get(position).id, new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    --comments.get(position).likes;
                                    comments.get(position).user_likes = false;
                                    adapter.changeStateLikeForComment(false,String.valueOf(comments.get(position).likes));
                                    Toast.makeText(getActivity().getApplicationContext(), comments.get(position).likes + "", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onError(VKError error) {
                                    super.onError(error);
                                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                }
                            });
                        }

                        Toast.makeText(getActivity().getApplicationContext(), comments.get(position).likes + "", Toast.LENGTH_LONG).show();
                    }
                    break;
                    case 4:
                        Dialogs.reportListDialog(VKUIHelper.getApplicationContext(), group_id, comments.get(position).id);
                        break;
                    case 5: {
                        if (myComment) {
                            VKHelper.deleteComment(group_id, comments.get(position).id, new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    updateCommentList(group_id, item_id, listOfComments, inflater);
                                }
                                @Override
                                public void onError(VKError error) {
                                    super.onError(error);
                                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                }


                            });
                        } else {
                            Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).reply_to_user + "");
                            getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), "Відкрити за допомогою")
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }

                    }
                    break;
                    case 6: {
                        edit_status = true;
                        positionOfComment = position;
                        commentMessage.setText(comments.get(position).text);


                    }

                    break;
                    case 7: {

                        Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).reply_to_user + "");
                        getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), "Відкрити за допомогою")
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


                        Toast.makeText(getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                    }

                    default:
                        Toast.makeText(getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.show();

    }

    public String Identify(ArrayList<VKApiComment> commentsList, ArrayList<VKApiUser> profilesList, int position) {
        String name = "";
        if (position >= 0) {
            reply_to_comment = commentsList.get(position).reply_to_comment;
        }
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

    public void loadImage(VKApiPhoto photo, ImageView imageView) {

        ImageLoader.getInstance().displayImage(PhotoUrlHelper.getFullScreenUrl(photo), imageView);
    }
}