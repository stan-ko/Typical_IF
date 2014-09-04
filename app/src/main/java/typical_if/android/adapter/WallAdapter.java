package typical_if.android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONObject;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.Dialogs;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.fragment.FragmentWithComments;
import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;

import static com.vk.sdk.VKUIHelper.getApplicationContext;
import static java.lang.String.valueOf;

public class WallAdapter extends BaseAdapter {
    public static final String LIKE_HAS_DELETED = "Like has deleted";
    public static final String YOUR_COMENNT = "Ваш коментар";
    public static final String ALL_IS_DONE = "All is done";
    public static final String somethink_wrong = "Щось пішло не так...Оновіть будь-ласка сторінку";
    public static final String COMMENTS_ARE_NOT_AVAILABLE_TO_THIS_POST_PLEASE_TURN_ON_THE_INTERNET = " comments are not available to this post. Please turn On the internet ";
    private Wall wall;
    private ArrayList<VKWallPostWrapper> posts;
    private static LayoutInflater layoutInflater;
    private Context context;
    private String postColor;
    private FragmentManager fragmentManager;
    private static boolean isSuggested;

    public WallAdapter(Wall wall, LayoutInflater inflater, FragmentManager fragmentManager, String postColor, boolean isSuggested) {
        this.wall = wall;
        this.layoutInflater = inflater;
        this.context = VKUIHelper.getApplicationContext();
        this.fragmentManager = fragmentManager;
        this.postColor = postColor;
        this.wall = wall;
        this.posts = wall.posts;
        this.layoutInflater = inflater;
        this.context = VKUIHelper.getApplicationContext();
        this.postColor = postColor;
        this.isSuggested = isSuggested;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return posts.get(position).id;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.wall_lv_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final VKWallPostWrapper post = posts.get(position);

        initViewHolder(viewHolder, postColor, wall, position, fragmentManager, post, context);

        return convertView;
    }

    static String copy_history_title = "";
    static String copy_history_logo = "";
    static String copy_history_name = "";
    static AlertDialog.Builder dialog;

    public static void initViewHolder(final ViewHolder viewHolder, final String postColor, final Wall wall, int position, final FragmentManager fragmentManager, final VKWallPostWrapper postWrapper, final Context context) {
        try {
            ItemDataSetter.wallViewHolder = viewHolder;
            ItemDataSetter.postColor = postColor;
            ItemDataSetter.wall = wall;
            ItemDataSetter.position = position;
            ItemDataSetter.fragmentManager = fragmentManager;

            final VKApiPost post = postWrapper.post;

            viewHolder.img_fixed_post.setVisibility(postWrapper.postPinnedVisibility);

            if (post.user_likes) {
                viewHolder.cb_post_like.setChecked(true);
            } else {
                viewHolder.cb_post_like.setChecked(false);
            }

            viewHolder.cb_post_comment.setText(valueOf(post.comments_count));
            viewHolder.cb_post_like.setText(valueOf(post.likes_count));
            viewHolder.cb_post_repost.setText(String.valueOf(post.reposts_count));
            viewHolder.txt_post_date.setText(ItemDataSetter.getFormattedDate(post.date));


            viewHolder.button_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!post.user_likes) {
                        VKHelper.setLike("post", Constants.GROUP_ID, post.id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                Toast.makeText(VKUIHelper.getApplicationContext(), context.getString(R.string.Liked), Toast.LENGTH_SHORT).show();
                                super.onComplete(response);
                                viewHolder.cb_post_like.setText(String.valueOf(++post.likes_count));
                                viewHolder.cb_post_like.setChecked(true);
                                post.user_likes = true;
                            }
                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                                OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                            }
                        });
                    } else {

                        VKHelper.deleteLike("post", Constants.GROUP_ID, post.id, new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                Toast.makeText(VKUIHelper.getApplicationContext(), LIKE_HAS_DELETED, Toast.LENGTH_SHORT).show();
                                viewHolder.cb_post_like.setText(String.valueOf(--post.likes_count));
                                viewHolder.cb_post_like.setChecked(false);
                                post.user_likes = false;
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


            if (post.user_reposted) {
                viewHolder.cb_post_repost.setChecked(true);
                viewHolder.cb_post_repost.setOnClickListener(null);
            } else {
                viewHolder.cb_post_repost.setChecked(false);
                viewHolder.button_repost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dialog = new AlertDialog.Builder(Constants.mainActivity);
//
                            View view = layoutInflater.inflate(R.layout.txt_dialog_comment, null);
                            dialog.setView(view);
                            dialog.setTitle(YOUR_COMENNT);
//
                            final EditText text = (EditText) view.findViewById(R.id.txt_dialog_comment);
//
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String pidFull = "wall" + Constants.GROUP_ID + "_" + post.id;
                                    VKHelper.doRepost(pidFull, text.getText().toString(), new VKRequest.VKRequestListener() {
                                        @Override
                                        public void onComplete(VKResponse response) {
                                            super.onComplete(response);
                                            JSONObject object = response.json.optJSONObject("response");
                                            int isSuccessed = object.optInt("success");
//
                                            if (isSuccessed == 1) {
//
                                                Toast.makeText(context, ALL_IS_DONE, Toast.LENGTH_SHORT).show();
                                                viewHolder.cb_post_repost.setChecked(true);
                                                viewHolder.cb_post_repost.setText(String.valueOf(++post.reposts_count));
//
                                                if (!post.user_likes) {

                                                    VKHelper.setLike("post", (wall.group.id * (-1)), post.id, new VKRequest.VKRequestListener() {
                                                        @Override
                                                        public void onComplete(VKResponse response) {
                                                            super.onComplete(response);

                                                            viewHolder.cb_post_like.setText(String.valueOf(++post.likes_count));
                                                            viewHolder.cb_post_like.setChecked(true);
                                                            post.user_likes = true;

                                                        }
                                                    });
                                                }
                                                viewHolder.cb_post_repost.setChecked(true);
                                                viewHolder.cb_post_repost.setEnabled(false);
                                            } else {
                                                viewHolder.cb_post_repost.setChecked(false);
                                            }
                                        }
                                        @Override
                                        public void onError(VKError error) {
                                            super.onError(error);
                                            OfflineMode.onErrorToast(Constants.mainActivity.getApplicationContext());
                                        }
                                    });

                                }
                            });
                            dialog.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.setCancelable(true);
                                }
                            });
                            dialog.create().show();
//
                        } catch (NullPointerException npe) {
                            Toast.makeText(getApplicationContext(), somethink_wrong, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }


            if (!isSuggested) {
                viewHolder.img_post_other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialogs.reportDialog(Constants.mainActivity, wall.group.id, post.id);
                    }
                });

                viewHolder.button_comment.setVisibility(View.VISIBLE);
                viewHolder.button_repost.setVisibility(View.VISIBLE);
                viewHolder.button_like.setVisibility(View.VISIBLE);
                viewHolder.cb_post_repost.setVisibility(View.VISIBLE);
                viewHolder.cb_post_comment.setVisibility(View.VISIBLE);
                viewHolder.cb_post_like.setVisibility(View.VISIBLE);

            } else {
                viewHolder.img_post_other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialogs.suggestPostDialog(Constants.mainActivity, wall.group.id * -1, post);
                    }
                });

                viewHolder.button_comment.setVisibility(View.INVISIBLE);
                viewHolder.button_repost.setVisibility(View.INVISIBLE);
                viewHolder.button_like.setVisibility(View.INVISIBLE);
                viewHolder.cb_post_repost.setVisibility(View.INVISIBLE);
                viewHolder.cb_post_comment.setVisibility(View.INVISIBLE);
                viewHolder.cb_post_like.setVisibility(View.INVISIBLE);
            }

            viewHolder.postTextLayout.setVisibility(postWrapper.postTextVisibility);
            if (postWrapper.postTextChecker) {
                ItemDataSetter.setText(post.text, viewHolder.postTextLayout);
            }

            viewHolder.copyHistoryLayout.setVisibility(postWrapper.copyHistoryContainerVisibility);
            if (postWrapper.copyHistoryChecker) {
                final VKApiPost copyHistory = post.copy_history.get(0);
                VKApiCommunity group;
                for (int i = 0; i < wall.groups.size(); i++) {
                    group = wall.groups.get(i);
                    if (copyHistory.from_id * (-1) == group.id) {
                        copy_history_title = group.name;
                        copy_history_logo = group.photo_100;
                        copy_history_name = group.screen_name;
                    }
                }

                if (copy_history_title.equals("") && copy_history_logo.equals("")) {
                    VKApiUser profile;
                    for (int i = 0; i < wall.profiles.size(); i++) {
                        profile = wall.profiles.get(i);
                        if (copyHistory.from_id == profile.id) {
                            copy_history_title = profile.last_name + " " + profile.first_name;
                            copy_history_logo = profile.photo_100;
                            copy_history_name = profile.screen_name;
                        }
                    }
                }

                ViewGroup copyHistoryContainer = ItemDataSetter.getPreparedView(viewHolder.copyHistoryLayout, R.layout.copy_history_layout);
                //RelativeLayout leftLine = (RelativeLayout) copyHistoryContainer.findViewById(R.id.leftLine);
                //leftLine.setVisibility(View.VISIBLE);
                //leftLine.setBackgroundColor(Color.parseColor(postColor));

                LinearLayout copyHistoryList = (LinearLayout) copyHistoryContainer.getChildAt(0);
                RelativeLayout copyHistoryLayout = (RelativeLayout) copyHistoryList.getChildAt(0);
                final String finalCopy_history_name = copy_history_name;
                copyHistoryLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://vk.com/" + finalCopy_history_name);
                        context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
                ((TextView) copyHistoryLayout.getChildAt(1)).setText(copy_history_title);
                ((TextView) copyHistoryLayout.getChildAt(2)).setText(ItemDataSetter.getFormattedDate(copyHistory.date));


                ImageLoader.getInstance().displayImage(copy_history_logo, ((ImageView) copyHistoryLayout.getChildAt(0)));

                RelativeLayout parentCopyHistoryTextContainer = (RelativeLayout) copyHistoryList.findViewById(R.id.copyHistoryTextLayout);
                parentCopyHistoryTextContainer.setVisibility(postWrapper.copyHistoryTextContainerVisibility);
                if (postWrapper.copyHistoryTextChecker) {
                    ItemDataSetter.setText(copyHistory.text, parentCopyHistoryTextContainer);
                }

                LinearLayout parentCopyHistoryAttachmentsContainer = (LinearLayout) copyHistoryList.findViewById(R.id.copyHistoryAttachmentsLayout);
                parentCopyHistoryAttachmentsContainer.setVisibility(postWrapper.copyHistoryAttachmentsContainerVisibility);
                if (postWrapper.copyHistoryAttachmentsChecker) {
                    ItemDataSetter.setAttachemnts(copyHistory.attachments, parentCopyHistoryAttachmentsContainer, 0);
                }

                RelativeLayout copyHistoryGeoContainer = (RelativeLayout) copyHistoryList.findViewById(R.id.copyHistoryGeoLayout);
                copyHistoryGeoContainer.setVisibility(postWrapper.copyHistoryGeoContainerVisibility);
                if (postWrapper.copyHistoryGeoChecker) {
                    ItemDataSetter.setGeo(copyHistory.geo, copyHistoryGeoContainer);
                }

                RelativeLayout copyHistorySignedContainer = (RelativeLayout) copyHistoryList.findViewById(R.id.copyHistorySignedLayout);
                copyHistorySignedContainer.setVisibility(postWrapper.copyHistorySignedContainerVisibility);
                if (postWrapper.copyHistorySignedChecker) {
                    ItemDataSetter.setSigned(copyHistory.signer_id, copyHistorySignedContainer);
                }

                viewHolder.copyHistoryLayout.addView(copyHistoryContainer);
            }

            viewHolder.postAttachmentsLayout.setVisibility(postWrapper.postAttachmentsVisibility);
            if (postWrapper.postAttachmentsChecker) {
                ItemDataSetter.setAttachemnts(post.attachments, viewHolder.postAttachmentsLayout, 1);
            }

            viewHolder.postGeoLayout.setVisibility(postWrapper.postGeoVisibility);
            if (postWrapper.postGeoChecker) {
                ItemDataSetter.setGeo(post.geo, viewHolder.postGeoLayout);
            }

            viewHolder.postSignedLayout.setVisibility(postWrapper.postSignedVisibility);
            if (postWrapper.postSignedChecker) {
                ItemDataSetter.setSigned(post.signer_id, viewHolder.postSignedLayout);
            }

            viewHolder.button_comment.setTag(new ParamsHolder(position, postWrapper));

            if (OfflineMode.isOnline(getApplicationContext()) | OfflineMode.isJsonNull(post.id)) {
                viewHolder.button_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParamsHolder paramsHolder = (ParamsHolder) v.getTag();
                        FragmentWithComments fragment = FragmentWithComments.newInstanceForWall(postColor, paramsHolder.position, wall, paramsHolder.post);
                        fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    }
                });
            } else {
                viewHolder.button_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), COMMENTS_ARE_NOT_AVAILABLE_TO_THIS_POST_PLEASE_TURN_ON_THE_INTERNET, Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (Throwable a) {

        }
    }

    public static class ParamsHolder {
        public final int position;
        public final VKWallPostWrapper post;

        public ParamsHolder(int position, VKWallPostWrapper post) {
            this.position = position;
            this.post = post;
        }
    }

    public static class ViewHolder {
        public int position;

        public final RelativeLayout postTextLayout;
        public final RelativeLayout postMediaLayout;
        public final LinearLayout postAudioLayout;
        public final RelativeLayout copyHistoryLayout;
        public final LinearLayout postAttachmentsLayout;
        public final LinearLayout postDocumentLayout;
        public final LinearLayout postAlbumLayout;
        public final RelativeLayout postGeoLayout;
        public final RelativeLayout postWikiPageLayout;
        public final RelativeLayout postLinkLayout;
        public final RelativeLayout postSignedLayout;
        public final RelativeLayout postPollLayout;

        public final CheckBox cb_post_like;
        public final CheckBox cb_post_repost;
        public final CheckBox cb_post_comment;
        public final Button button_like;
        public final Button button_repost;
        public final Button button_comment;
        public final TextView txt_post_date;

        public final ImageView img_fixed_post;


        public final ImageView img_post_other;

        public ViewHolder(View convertView) {
            this.postAttachmentsLayout = (LinearLayout) convertView.findViewById(R.id.postAttachmentsLayout);
            this.postTextLayout = (RelativeLayout) convertView.findViewById(R.id.postTextLayout);
            this.postMediaLayout = (RelativeLayout) convertView.findViewById(R.id.postMediaLayout);
            this.postAudioLayout = (LinearLayout) convertView.findViewById(R.id.postAudioLayout);
            this.postDocumentLayout = (LinearLayout) convertView.findViewById(R.id.postDocumentLayout);
            this.copyHistoryLayout = (RelativeLayout) convertView.findViewById(R.id.copyHistoryLayout);
            this.postAlbumLayout = (LinearLayout) convertView.findViewById(R.id.postAlbumLayout);
            this.postGeoLayout = (RelativeLayout) convertView.findViewById(R.id.postGeoLayout);
            this.postWikiPageLayout = (RelativeLayout) convertView.findViewById(R.id.postWikiPageLayout);
            this.postLinkLayout = (RelativeLayout) convertView.findViewById(R.id.postLinkLayout);
            this.postSignedLayout = (RelativeLayout) convertView.findViewById(R.id.postSignedLayout);
            this.postPollLayout = (RelativeLayout) convertView.findViewById(R.id.postPollLayout);
            this.img_fixed_post = (ImageView) convertView.findViewById(R.id.img_fixed_post);
            this.img_post_other = (ImageView) convertView.findViewById(R.id.img_post_other_actions);

            this.cb_post_like = (CheckBox) convertView.findViewById(R.id.cb_like);
            this.cb_post_comment = (CheckBox) convertView.findViewById(R.id.cb_comment);
            this.cb_post_repost = (CheckBox) convertView.findViewById(R.id.cb_repost);
            this.button_like = ((Button) convertView.findViewById(R.id.button_like));
            this.button_comment = ((Button) convertView.findViewById(R.id.button_comment));
            this.button_repost = ((Button) convertView.findViewById(R.id.button_repost));
            this.txt_post_date = ((TextView) convertView.findViewById(R.id.txt_post_date_of_comment));

        }
    }
}
