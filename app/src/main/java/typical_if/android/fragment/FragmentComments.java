package typical_if.android.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPoll;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import de.greenrobot.event.EventBus;
import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.CommentsListAdapter;
import typical_if.android.adapter.RecyclerWallAdapter;
import typical_if.android.event.EventSpinnerLayout;
import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;
import typical_if.android.util.PhotoUrlHelper;

import static com.vk.sdk.VKUIHelper.getApplicationContext;


public class FragmentComments extends Fragment {

    public static final String POSTED = "POSTED";
    final int displayHeight = TIFApp.getDisplayHeight();
    private OnFragmentInteractionListener mListener;


    private static final String ARG_VK_USER_ID = "user_id";
    public static VKApiPhoto photo = null;

    public static String TYPE;


    ListView listOfComments = null;
    ArrayList<VKApiComment> comments = null;
    ArrayList<VKApiUser> profiles = null;
    ArrayList<VKApiCommunity> groups = null;
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

    RecyclerWallAdapter.ViewHolder viewHolder = null;
    Button sendComment = null;
    boolean edit_status = false;
    static boolean loadFromWall = false;
    int position;
    LayoutInflater inflater;
    static long item_id;
    static long from_user;


    public static FragmentComments newInstanceForPhoto(VKApiPhoto photo, long vk_user_id) {
        loadFromWall = false;
        FragmentComments fragment = new FragmentComments();
        Bundle args = new Bundle();



        args.putLong(ARG_VK_USER_ID, vk_user_id);
        fragment.setArguments(args);
        FragmentComments.photo = photo;

        TYPE = "photo_comment";
        Constants.DELETE_COMMENT_METHOD_NAME = "photos.deleteComment";
        Constants.CREATE_COMMENT_METHOD_NAME = "photos.createComment";
        Constants.EDIT_COMMENT_METHOD_NAME = "photos.editComment";
        Constants.PARAM_NAME = "message";
        Constants.GET_COMMENTS_METHOD_NAME = "photos.getComments";
        Constants.PARAM_NAME2 = "photo_id";


        item_id = photo.id;
        group_id = photo.owner_id;
        if (photo.user_id == 0) {
            photo.user_id = photo.owner_id;
        }
        from_user = photo.user_id;


        return fragment;
    }

    public static FragmentComments newInstanceForWall(int position, Wall wall, VKWallPostWrapper post) {
        loadFromWall = true;
        Constants.isFragmentCommentsLoaded=true;
        FragmentComments fragment = new FragmentComments();

        fragment.wall = wall;
        fragment.position = position;
        FragmentComments.post = post;


        TYPE = "comment";
        Constants.DELETE_COMMENT_METHOD_NAME = "wall.deleteComment";
        Constants.CREATE_COMMENT_METHOD_NAME = "wall.addComment";
        Constants.EDIT_COMMENT_METHOD_NAME = "wall.editComment";
        Constants.PARAM_NAME = "text";
        Constants.GET_COMMENTS_METHOD_NAME = "wall.getComments";
        Constants.PARAM_NAME2 = "post_id";
        item_id = post.post.id;
        group_id = post.post.from_id;
        from_user = post.post.from_id;

        return fragment;
    }


    public FragmentComments() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
        EventBus.getDefault().register(this);

    }


    private void loadPhotoPosts() {
        final VKApiPhoto photo = this.photo;


        listOfComments = ((ListView) this.rootView.findViewById(R.id.listOfComments));
        View listHeaderView = rootView.inflate(getActivity().getApplicationContext(), R.layout.image_header, null);
        ImageView headerView = (ImageView) listHeaderView.findViewById(R.id.list_header_image);
        TextView textView = (TextView) listHeaderView.findViewById(R.id.txt_photo_text);
        listOfComments.addHeaderView(listHeaderView);
        loadImage(photo, headerView, textView);


        final RelativeLayout photoUserSender = ((RelativeLayout) rootView.findViewById(R.id.user_photo_sender));
        final RoundedImageView postPhotoUserAvatar = (RoundedImageView) rootView.findViewById(R.id.post_user_avatar);

        final TextView postPhotoUserName = ((TextView) rootView.findViewById(R.id.post_user_name));
        final TextView postPhotoUserDateOfComment = ((TextView) rootView.findViewById(R.id.post_user_date_of_comment));

        VKHelper.getWhoIsPosted(from_user, "photo_50", new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(final VKResponse response) {
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
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());

            }
        });

        photoUserSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://vk.com/id" + postSender.id + "");
                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        updateCommentList(group_id, photo.id, listOfComments, inflater);



        commentMessage = (EditText) rootView.findViewById(R.id.field_of_message_for_comment);


        final CheckBox likePostPhoto = ((CheckBox) rootView.findViewById(R.id.like_post_photo_checkbox));

        if (photo.user_likes == 0) {

            likePostPhoto.setChecked(false);
            likePostPhoto.setText(String.valueOf(photo.likes));

        } else {

            likePostPhoto.setChecked(true);
            likePostPhoto.setText(String.valueOf(photo.likes));

        }

        likePostPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo.user_likes == 0) {
                    VKHelper.setLike("photo", group_id, photo.id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            photo.user_likes = 1;
                            ++photo.likes;
                            likePostPhoto.setText(String.valueOf(photo.likes));
                            likePostPhoto.setChecked(true);


                        }

                        @Override
                        public void onError(final VKError error) {
                            super.onError(error);
                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                        }
                    });

                } else {
                    VKHelper.deleteLike("photo", group_id, photo.id, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(final VKResponse response) {
                            super.onComplete(response);
                            photo.user_likes = 0;
                            --photo.likes;
                            likePostPhoto.setText(String.valueOf(photo.likes));
                            likePostPhoto.setChecked(false);

                        }

                        @Override
                        public void onError(final VKError error) {
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

        viewHolder = new RecyclerWallAdapter.ViewHolder(wallItem);

        RecyclerWallAdapter.initViewHolder(viewHolder, wall, position, getFragmentManager(), post, true);

        viewHolder.postRootLayout.setCardElevation(0);
        viewHolder.postRootLayout.setShadowPadding(0,0,0,0);

        viewHolder.postAuthorPanel.setVisibility(View.GONE);
        viewHolder.postFeatureLayout.setVisibility(View.GONE);

        final VKWallPostWrapper post = FragmentComments.post;

        if (post.post.text.length() != 0) {
            CheckBox checkBox = viewHolder.cbPostAllText;
            checkBox.setChecked(true);
            checkBox.setVisibility(View.GONE);
        }

        if (post.post.copy_history != null && post.post.copy_history.size() != 0) {
            if (post.post.copy_history.get(0).text.length() != 0) {

                CheckBox checkBox = viewHolder.copyHistoryCbPostAllText;
                checkBox.setChecked(true);
                checkBox.setVisibility(View.GONE);
            }
        }

        listOfComments.addHeaderView(wallItem);
        Constants.isFragmentCommentsLoaded=true;

        for (VKAttachments.VKApiAttachment attachment: post.post.attachments) {
            if (attachment.getType().equals(VKAttachments.TYPE_POLL)) {
                final VKApiPoll poll = ((VKApiPoll) attachment);
                ItemDataSetter.fillPollLayout(poll, viewHolder.postPollLayout);
            }
        }

        updateCommentList(group_id, post.post.id, listOfComments, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
    Constants.isFragmentCommentsLoaded=true;
    }



    RelativeLayout root;
    RelativeLayout coverGlobal;

    SwipeRefreshLayout swipeView;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        FragmentWall.setDisabledMenu();

        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_photo_comment_and_info, container, false);
        RelativeLayout rootLayoutShowHide = (RelativeLayout) rootView.findViewById(R.id.comment_bar_layout);
        root = rootLayoutShowHide;
        coverGlobal = (RelativeLayout) rootView.findViewById(R.id.while_loading_view_layout);
        listOfComments = ((ListView) rootView.findViewById(R.id.listOfComments));
//        RelativeLayout useless = ((RelativeLayout) rootView.findViewById(R.id.useless));

        RelativeLayout wrapper = (RelativeLayout) rootView.findViewById(R.id.list_of_comments_wrapper_layout);



        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshComments);
        swipeView.setColorSchemeResources(android.R.color.white, android.R.color.white, android.R.color.white);
        swipeView.setProgressBackgroundColor(R.color.music_progress);
        //swipeView.setProgressViewOffset(true, 0, 100);

        swipeView. setSize(SwipeRefreshLayout.DEFAULT);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!OfflineMode.isOnline(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_message_toast_en), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //TODO fuck
                        Log.d("-----------------------------", "-------------------------/////////////////////////////////////");

                        swipeView.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        listOfComments.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable= false;
                if (listOfComments.getChildCount() > 0) {
                    boolean firstItemVisible = listOfComments.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listOfComments.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeView.setEnabled(enable);
            }
        });




        if (VKSdk.isLoggedIn()) {
            if (OfflineMode.isOnline(Constants.mainActivity.getApplicationContext())) {

                rootLayoutShowHide.setVisibility(View.VISIBLE);
            }
        } else if (!VKSdk.isLoggedIn()) {
            rootLayoutShowHide.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.binding_iv);
            wrapper.setLayoutParams(lp);
        }
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
                if (!commentMessage.getText().toString().isEmpty()) {
                    message = commentMessage.getText().toString();
                    if (!edit_status) {
                        if (reply_to_comment == 0) {

                            VKHelper.createComment(group_id, item_id, message + "\n@club77149556 (Мобільний ТФ)", 0, new VKRequest.VKRequestListener() {

                                @Override
                                public void onComplete(final VKResponse response) {
                                    super.onComplete(response);
                                    commentMessage.setText("");
                                    updateCommentList(group_id, item_id, listOfComments, inflater);
                                }

                                @Override
                                public void onError(final VKError error) {
                                    super.onError(error);
                                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                }

                            });

                        } else {
                            VKHelper.createComment(group_id, item_id, message + "\n@club77149556 (Мобільний ТФ)", reply_to_comment, new VKRequest.VKRequestListener() {

                                @Override
                                public void onComplete(final VKResponse response) {
                                    super.onComplete(response);
                                    commentMessage.setText("");
                                    updateCommentList(group_id, item_id, listOfComments, inflater);
                                }

                                @Override
                                public void onError(final VKError error) {
                                    super.onError(error);
                                    OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                }
                            });

                        }
                    } else
                        VKHelper.editComment(group_id, comments.get(positionOfComment).id, message, null, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(final VKResponse response) {
                                super.onComplete(response);
                                updateCommentList(group_id, item_id, listOfComments, inflater);
                                commentMessage.setText("");
                                edit_status = false;
                            }

                            @Override
                            public void onError(final VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });

                    edit_status = false;
                }
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
                        EventBus.getDefault().post(new EventSpinnerLayout());
                    }
                }).start();
            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
            }

        });
        if (!OfflineMode.isOnline(getActivity().getApplicationContext()) & OfflineMode.isJsonNull(item_id)) {
            parseCommentList(OfflineMode.loadJSON(item_id));
            // If IsOnline and response from preferences not null then load JSON from preferences
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
//public static boolean isViewLoaded;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
       // isViewLoaded=true;
       // coverGlobal.setVisibility(View.GONE);


    }

    public  boolean isRunning;
    public boolean myComment = false;

    public void showContextMenu(int position) {


        VKApiComment comment = comments.get(position);


        String name = Identify(comments, profiles, groups, position);
        if (name == "" || name == null) {
            name = "Адресат";
        }
        CharSequence[] items = null;

        if (VKSdk.isLoggedIn()) {
            if (comment.from_id == Constants.USER_ID) {
                myComment = true;

                if (comment.reply_to_comment != 0) {
                    if (!comment.user_likes)
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_like), getResources().getString(R.string.comment_report), getResources().getString(R.string.comment_delete), getResources().getString(R.string.comment_edit), name};
                    else
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_unlike), getResources().getString(R.string.comment_report), getResources().getString(R.string.comment_delete), getResources().getString(R.string.comment_edit), name};
                } else if (!comment.user_likes) {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_like), getResources().getString(R.string.comment_report), getResources().getString(R.string.comment_delete), getResources().getString(R.string.comment_edit)};
                } else {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_unlike), getResources().getString(R.string.comment_report), getResources().getString(R.string.comment_delete), getResources().getString(R.string.comment_edit)};
                }
            } else {
                myComment = false;
                if (comment.reply_to_comment != 0) {
                    if (!comment.user_likes)
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_like), getResources().getString(R.string.comment_report), name};
                    else
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_unlike), getResources().getString(R.string.comment_report, name)};
                } else if (!comment.user_likes) {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_like), getResources().getString(R.string.comment_report)};
                } else {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.reply), getResources().getString(R.string.copy_text), getResources().getString(R.string.comment_unlike), getResources().getString(R.string.comment_report)};
                }

            }

        } else if (!VKSdk.isLoggedIn()) {
            if (comment.from_id == Constants.USER_ID) {
                myComment = true;

                if (comment.reply_to_comment != 0) {
                    if (!comment.user_likes)
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text), name};
                    else
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text), name};
                } else if (!comment.user_likes) {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text)};
                } else {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text)};
                }
            } else {
                myComment = false;
                if (comment.reply_to_comment != 0) {
                    if (!comment.user_likes)
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text), name};
                    else
                        items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text)};
                } else if (!comment.user_likes) {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text)};
                } else {
                    items = new CharSequence[]{getResources().getString(R.string.profile), getResources().getString(R.string.copy_text),};
                }

            }


        }

        onInitContextMenu(items, position);


    }


    public void parseCommentList(final JSONObject response) {

        JSONArray[] arrayOfComments = VKHelper.getResponseArrayOfComment(response);
        comments = VKHelper.getCommentsFromJSON(arrayOfComments[0]);
        Collections.sort(comments);
        profiles = VKHelper.getProfilesFromJSONArray(arrayOfComments[1]);
        groups = VKHelper.getGroupsFromJSONArray(arrayOfComments[2]);

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter == null) {
                        adapter = new CommentsListAdapter(comments, profiles, groups, inflater);
                        listOfComments.setAdapter(adapter);
                    } else {
                        adapter.UpdateCommentList(comments, profiles, groups, listOfComments);


                    }
                }
            });

        } catch (NullPointerException npe) {

        }
    }

    public void onInitContextMenu(final CharSequence[] items, final int position) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (VKSdk.isLoggedIn()) {
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0: {
                            Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).from_id + "");
                            getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        break;
                        case 1: {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    reply_to_comment = comments.get(position).id;
                                    commentMessage.setText(Identify(comments, profiles, groups, -1) + ", ");

                                }
                            });

                        }
                        break;
                        case 2: {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(comments.get(position).text);
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.text_has_been_copied_to_the_buffer), Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case 3: {
                            if (!comments.get(position).user_likes) {

                                VKHelper.setLike(TYPE, group_id, comments.get(position).id, new VKRequest.VKRequestListener() {
                                    @Override
                                    public void onComplete(final VKResponse response) {
                                        super.onComplete(response);
                                        ++comments.get(position).likes;
                                        comments.get(position).user_likes = true;
                                        adapter.changeStateLikeForComment(true, String.valueOf(comments.get(position).likes));
                                    }

                                    @Override
                                    public void onError(final VKError error) {
                                        super.onError(error);
                                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                    }
                                });

                            } else {
                                VKHelper.deleteLike(TYPE, group_id, comments.get(position).id, new VKRequest.VKRequestListener() {
                                    @Override
                                    public void onComplete(final VKResponse response) {
                                        super.onComplete(response);
                                        --comments.get(position).likes;
                                        comments.get(position).user_likes = false;
                                        adapter.changeStateLikeForComment(false, String.valueOf(comments.get(position).likes));
                                    }

                                    @Override
                                    public void onError(final VKError error) {
                                        super.onError(error);
                                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                    }
                                });
                            }
                        }
                        break;
                        case 4:
                            ((MainActivity) getActivity()).reportListDialog(group_id, comments.get(position).id);
                            break;
                        case 5: {
                            if (myComment) {
                                VKHelper.deleteComment(group_id, comments.get(position).id, new VKRequest.VKRequestListener() {
                                    @Override
                                    public void onComplete(final VKResponse response) {
                                        super.onComplete(response);
                                        updateCommentList(group_id, item_id, listOfComments, inflater);
                                    }

                                    @Override
                                    public void onError(final VKError error) {
                                        super.onError(error);
                                        OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                    }


                                });
                            } else {
                                Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).reply_to_user + "");
                                getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
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
                            getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                            Toast.makeText(getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                        }

                        default:
                            Toast.makeText(getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else if (!VKSdk.isLoggedIn()) {
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0: {
                            Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).from_id + "");
                            getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        break;
                        case 1: {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(comments.get(position).text);
                            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.text_has_been_copied_to_the_buffer),Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case 2: {
                            Uri uri = Uri.parse("http://vk.com/id" + comments.get(position).reply_to_user + "");
                            getActivity().getApplicationContext().startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.BROWSER_CHOOSER)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        break;

                    }


                }

                ;
            });
        }

        builder.show();
    }

    public String Identify(ArrayList<VKApiComment> commentsList, ArrayList<VKApiUser> profilesList, ArrayList<VKApiCommunity> groupsList, int position) {
        String name = "";
        if (position >= 0) {
            reply_to_comment = commentsList.get(position).reply_to_comment;
        }

        for (int i = 0; i < commentsList.size(); i++) {
            if (commentsList.get(i).id == reply_to_comment) {
                for (int j = 0; j < profilesList.size(); j++) {
                    if (commentsList.get(i).from_id == profilesList.get(j).id) {
                        if (groupsList.size() == 0) {
                            name = profilesList.get(j).first_name;
                        } else {
                            for (int k = 0; k < groupsList.size(); k++) {

                                if (commentsList.get(i).from_id == groupsList.get(k).id) {

                                    name = profilesList.get(j).first_name;

                                }
                            }

                        }
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
        inflater.inflate(R.menu.main, menu);
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
        EventBus.getDefault().unregister(this);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void loadImage(VKApiPhoto photo, ImageView imageView, TextView textView) {
        if (!photo.text.equals("") && !(photo.text.length() == 0)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(photo.text);
        } else {
            textView.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(PhotoUrlHelper.getFullScreenUrl(photo), imageView);
    }

    public final static Animation animationFadeOut = AnimationUtils.loadAnimation(Constants.mainActivity.getApplicationContext(), R.anim.fade_out);


    public void onEventMainThread(EventSpinnerLayout event) {
        coverGlobal.startAnimation(animationFadeOut);
    }

}