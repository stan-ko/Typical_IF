package typical_if.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKPostArray;

import typical_if.android.Constants;
import typical_if.android.Dialogs;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.fragment.FragmentPostCommentAndInfo;
import typical_if.android.model.Wall.Group;
import typical_if.android.model.Wall.Profile;
import typical_if.android.model.Wall.Wall;

import static java.lang.String.valueOf;

public class WallAdapter extends BaseAdapter {
    private Wall wall;
    private VKPostArray posts;
    private LayoutInflater layoutInflater;
    private Context context;
    private String postColor;
    private FragmentManager fragmentManager;


    public WallAdapter(Wall wall, LayoutInflater inflater, FragmentManager fragmentManager, String postColor) {
        this.wall = wall;
        this.posts = wall.posts;
        this.layoutInflater = inflater;
        this.context = VKUIHelper.getApplicationContext();
        this.fragmentManager = fragmentManager;
        this.postColor = postColor;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.wall_lv_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final VKApiPost post = posts.get(position);

        initViewHolder(viewHolder, postColor, wall, position, fragmentManager, post, context);

        return convertView;
    }


    public static void initViewHolder(ViewHolder viewHolder, final String postColor, final Wall wall, int position, final FragmentManager fragmentManager, final VKApiPost post, final Context context) {
        ItemDataSetter.wallViewHolder = viewHolder;
        ItemDataSetter.postColor = postColor;
        ItemDataSetter.wall = wall;
        ItemDataSetter.position = position;
        ItemDataSetter.fragmentManager = fragmentManager;

        if (post.is_pinned == 1) {
            viewHolder.img_fixed_post.setVisibility(View.VISIBLE);
        } else {
            viewHolder.img_fixed_post.setVisibility(View.GONE);
        }

        String copy_history_title = "";
        String copy_history_logo = "";
        String copy_history_name = "";

        viewHolder.txt_post_comment.setText(valueOf(post.comments_count));

        viewHolder.txt_post_like.setText(valueOf(post.likes_count));

//        viewHolder.txt_post_share.setText(valueOf(post.reposts_count));

        viewHolder.txt_post_date.setText(ItemDataSetter.getFormattedDate(post.date));

        viewHolder.img_post_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs.reportDialog(context, wall.group.id, post.id);
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
                    context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri), Constants.VIEWER_CHOOSER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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


        viewHolder.txt_post_comment.setTag(new ParamsHolder(position, post));
        viewHolder.txt_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParamsHolder paramsHolder = (ParamsHolder) v.getTag();
                FragmentPostCommentAndInfo fragment = FragmentPostCommentAndInfo.newInstance(postColor, paramsHolder.position, wall, paramsHolder.post);
                fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

    }

    public static class ParamsHolder {
        public final int position;
        public final VKApiPost post;

        public ParamsHolder(int position, VKApiPost post) {
            this.position = position;
            this.post = post;
        }
    }

    public static class ViewHolder {
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

        public final TextView txt_post_date;

        public final TextView txt_post_like;
        //public final TextView txt_post_share;
        public final TextView txt_post_comment;
        public final ImageView img_post_comment;

        public final ImageView img_fixed_post;
        public final CheckBox cb_repost;

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
            this.txt_post_date = (TextView) convertView.findViewById(R.id.txt_post_date);

            this.txt_post_like = (TextView) convertView.findViewById(R.id.txt_post_like);
           // this.txt_post_share = (TextView) convertView.findViewById(R.id.txt_post_share);

            this.txt_post_comment = (TextView) convertView.findViewById(R.id.txt_post_comment);
            this.img_post_comment = (ImageView) convertView.findViewById(R.id.img_post_comment);

            this.img_fixed_post = (ImageView) convertView.findViewById(R.id.img_fixed_post);
            this.cb_repost = (CheckBox) convertView.findViewById(R.id.cb_post_repost);

            this.img_post_other = (ImageView) convertView.findViewById(R.id.img_post_other);
        }
    }
}
