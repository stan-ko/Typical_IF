package typical_if.android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKPostArray;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.model.Wall.Group;
import typical_if.android.model.Wall.Profile;
import typical_if.android.model.Wall.Wall;

import static java.lang.String.valueOf;

public class WallAdapter extends BaseAdapter {
    private Wall wall;
    private VKPostArray posts;
    private LayoutInflater layoutInflater;
    private static Context context;
    private static String postColor;

    public WallAdapter(Wall wall, LayoutInflater inflater, String postColor) {
        this.wall = wall;
        this.posts = wall.posts;
        this.layoutInflater = inflater;
        this.context = VKUIHelper.getApplicationContext();
        this.postColor = postColor;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.wall_lv_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (wall.isFixedPost && position == 0) {
            viewHolder.img_fixed_post.setVisibility(View.VISIBLE);
        } else {
            viewHolder.img_fixed_post.setVisibility(View.GONE);
        }

        ItemDataSetter.wallViewHolder = viewHolder;
        ItemDataSetter.postColor = postColor;
        ItemDataSetter.wall = wall;

        final VKApiPost post = posts.get(position);

        String copy_history_title = "";
        String copy_history_logo = "";
        String copy_history_name = "";


        viewHolder.txt_post_comment.setText(valueOf(post.comments_count));
        viewHolder.txt_post_like.setText(valueOf(post.likes_count));

        viewHolder.txt_post_share.setText(valueOf(post.reposts_count));

        viewHolder.txt_post_date.setText(ItemDataSetter.getFormattedDate(post.date));
/*
        if (post.user_reposted) {
            viewHolder.cb_repost.setChecked(true);
            viewHolder.cb_repost.setEnabled(false);
        } else {
            viewHolder.cb_repost.setChecked(false);
            viewHolder.cb_repost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(VKUIHelper.getTopActivity());
                    View view = inflater.inflate(R.layout.txt_dialog_comment, null);
                    dialog.setView(view);
                    dialog.setTitle(txt_dialog_comment);

                    final TextView text = (TextView) view.findViewById(R.id.txt_post_comment);

                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String pidFull = "wall-" + wall.group.id + "_" + post.id;
                            VKHelper.doRepost(pidFull, (String) text.getText(), new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    JSONObject object = response.json.optJSONObject("response");
                                    int isSuccessed = object.optInt("success");

                                    if (isSuccessed == 1) {
                                        Toast.makeText(context, "All is done", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            viewHolder.cb_repost.setChecked(true);
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.create();
                }
            });
        }*/

        viewHolder.img_post_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(VKUIHelper.getTopActivity());
                final String[] items = {Constants.POST_REPORT, Constants.POST_COPY_LINK};

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final AlertDialog.Builder builderIn = new AlertDialog.Builder(VKUIHelper.getTopActivity());
                                builderIn.setTitle(Constants.POST_REPORT);
                                final String[] items = {Constants.POST_REPORT_SPAM, Constants.POST_REPORT_OFFENSE, Constants.POST_REPORT_ADULT, Constants.POST_REPORT_DRUGS, Constants.POST_REPORT_PORNO, Constants.POST_REPORT_VIOLENCE};

                                builderIn.setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int reason = 0;
                                        switch (which) {
                                            case 0:
                                                reason = 0;
                                                break;
                                            case 1:
                                                reason = 6;
                                                break;
                                            case 2:
                                                reason = 5;
                                                break;
                                            case 3:
                                                reason = 4;
                                                break;
                                            case 4:
                                                reason = 1;
                                                break;
                                            case 5:
                                                reason = 3;
                                                break;
                                        }
                                        VKHelper.doReportPost(wall.group.id, post.id, reason, new VKRequest.VKRequestListener() {
                                            @Override
                                            public void onComplete(VKResponse response) {
                                                super.onComplete(response);
                                                int isSuccessed = response.json.optInt("response");

                                                if (isSuccessed == 1) {
                                                    Toast.makeText(context, "Reported", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                                builderIn.show();
                                break;

                            case 1:
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText("http://vk.com/wall-" + wall.group.id + "_" + post.id);
                                break;
                        }
                    }
                });

                builder.show();
            }
        });

        if (post.text.length() != 0) {
            ItemDataSetter.setText(post.text, viewHolder.postTextLayout);
        } else {
            viewHolder.postTextLayout.setVisibility(View.GONE);
        }

        if (post.copy_history != null && post.copy_history.size() != 0) {
            final VKApiPost copyHistory = post.copy_history.get(0);
            Group group;
            for (int i = 0; i < wall.groups.size(); i++) {
                group = wall.groups.get(i);
                if (copyHistory.from_id * (-1) == group.id) {
                    copy_history_title = group.name;
                    copy_history_logo = group.photo_100;
                    copy_history_name = group.screen_name;
                }
            }

            if (copy_history_title.equals("") && copy_history_logo.equals("")) {
                Profile profile;
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
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.viewer_chooser).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            ((TextView) copyHistoryLayout.getChildAt(1)).setText(copy_history_title);
            ((TextView) copyHistoryLayout.getChildAt(2)).setText(ItemDataSetter.getFormattedDate(copyHistory.date));


            ImageLoader.getInstance().displayImage(copy_history_logo, ((ImageView) copyHistoryLayout.getChildAt(0)));

            RelativeLayout parentCopyHistoryTextContainer = (RelativeLayout) copyHistoryList.findViewById(R.id.copyHistoryTextLayout);
            if (copyHistory.text.length() != 0) {
                ItemDataSetter.setText(copyHistory.text, parentCopyHistoryTextContainer);
            } else {
                parentCopyHistoryTextContainer.setVisibility(View.GONE);
            }

            LinearLayout parentCopyHistoryAttachmentsContainer = (LinearLayout) copyHistoryList.findViewById(R.id.copyHistoryAttachmentsLayout);
            if (copyHistory.attachments != null && copyHistory.attachments.size() != 0) {
                ItemDataSetter.setAttachemnts(copyHistory.attachments, parentCopyHistoryAttachmentsContainer, 0);
            } else {
                parentCopyHistoryAttachmentsContainer.setVisibility(View.GONE);
            }

            RelativeLayout copyHistoryGeoContainer = (RelativeLayout) copyHistoryList.findViewById(R.id.copyHistoryGeoLayout);
            if (copyHistory.geo != null) {
                ItemDataSetter.setGeo(copyHistory.geo, copyHistoryGeoContainer);
            } else {
                copyHistoryGeoContainer.setVisibility(View.GONE);
            }

            RelativeLayout copyHistorySignedContainer = (RelativeLayout) copyHistoryList.findViewById(R.id.copyHistorySignedLayout);
            if (copyHistory.signer_id != 0) {
                ItemDataSetter.setSigned(copyHistory.signer_id, copyHistorySignedContainer);
            } else {
                copyHistorySignedContainer.setVisibility(View.GONE);
            }

            viewHolder.copyHistoryLayout.addView(copyHistoryContainer);
        } else {
            viewHolder.copyHistoryLayout.setVisibility(View.GONE);
        }

        if (post.attachments != null && post.attachments.size() != 0) {
            ItemDataSetter.setAttachemnts(post.attachments, viewHolder.postAttachmentsLayout, 1);
        } else {
            viewHolder.postAttachmentsLayout.setVisibility(View.GONE);
        }

        if (post.geo != null) {
            ItemDataSetter.setGeo(post.geo, viewHolder.postGeoLayout);
        } else {
            viewHolder.postGeoLayout.setVisibility(View.GONE);
        }

        if (post.signer_id != 0) {
            ItemDataSetter.setSigned(post.signer_id, viewHolder.postSignedLayout);
        } else {
            viewHolder.postSignedLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {
        private final RelativeLayout postTextLayout;
        public final RelativeLayout postMediaLayout;
        public final LinearLayout postAudioLayout;
        private final RelativeLayout copyHistoryLayout;
        private final LinearLayout postAttachmentsLayout;
        public final LinearLayout postDocumentLayout;
        public final LinearLayout postAlbumLayout;
        private final RelativeLayout postGeoLayout;
        public final RelativeLayout postWikiPageLayout;
        public final RelativeLayout postLinkLayout;
        private final RelativeLayout postSignedLayout;
        public final RelativeLayout postPollLayout;

        private final TextView txt_post_date;
        private final TextView txt_post_like;
        private final TextView txt_post_share;
        private final TextView txt_post_comment;

        private final ImageView img_fixed_post;
        private final ImageView img_post_other;
        private final CheckBox cb_repost;


        private ViewHolder(View convertView) {
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

            this.txt_post_date = (TextView) convertView.findViewById(R.id.txt_post_date);
            this.txt_post_like = (TextView) convertView.findViewById(R.id.txt_post_like);
            this.txt_post_share = (TextView) convertView.findViewById(R.id.txt_post_share);
            this.txt_post_comment = (TextView) convertView.findViewById(R.id.txt_post_comment);

            this.img_fixed_post = (ImageView) convertView.findViewById(R.id.img_fixed_post);
            this.img_post_other = (ImageView) convertView.findViewById(R.id.img_post_other);

            this.cb_repost = (CheckBox) convertView.findViewById(R.id.cb_post_repost);
        }
    }
}
