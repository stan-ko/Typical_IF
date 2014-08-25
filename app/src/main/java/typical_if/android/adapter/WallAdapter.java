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
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.model.VKApiPost;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.Dialogs;
import typical_if.android.ItemDataSetter;
import typical_if.android.OfflineMode;
import typical_if.android.R;
import typical_if.android.fragment.FragmentPostCommentAndInfo;
import typical_if.android.model.Wall.Group;
import typical_if.android.model.Wall.Profile;
import typical_if.android.model.Wall.VKWallPostWrapper;
import typical_if.android.model.Wall.Wall;

import static com.vk.sdk.VKUIHelper.getApplicationContext;
import static java.lang.String.valueOf;

public class WallAdapter extends BaseAdapter {
    private Wall wall;
    private ArrayList<VKWallPostWrapper> posts;
    private LayoutInflater layoutInflater;
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


    public static void initViewHolder(ViewHolder viewHolder, final String postColor, final Wall wall, int position, final FragmentManager fragmentManager, final VKWallPostWrapper postWrapper, final Context context) {
        ItemDataSetter.wallViewHolder = viewHolder;
        ItemDataSetter.postColor = postColor;
        ItemDataSetter.wall = wall;
        ItemDataSetter.position = position;
        ItemDataSetter.fragmentManager = fragmentManager;

        final VKApiPost post = postWrapper.post;

        viewHolder.img_fixed_post.setVisibility(postWrapper.postPinnedVisibility);

        String copy_history_title = "";
        String copy_history_logo = "";
        String copy_history_name = "";

        viewHolder.txt_post_comment.setText(valueOf(post.comments_count));

        viewHolder.txt_post_like.setText(valueOf(post.likes_count));

        viewHolder.txt_post_share.setText(valueOf(post.reposts_count));

        viewHolder.txt_post_date.setText(ItemDataSetter.getFormattedDate(post.date));

        if (!isSuggested) {
            viewHolder.img_post_other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.reportDialog(Constants.mainActivity, wall.group.id, post.id);
                }
            });
            viewHolder.txt_post_comment.setVisibility(View.VISIBLE);
            viewHolder.txt_post_like.setVisibility(View.VISIBLE);
            viewHolder.txt_post_share.setVisibility(View.VISIBLE);
            viewHolder.img_post_comment.setVisibility(View.VISIBLE);
            viewHolder.img_post_like.setVisibility(View.VISIBLE);
            viewHolder.img_post_share.setVisibility(View.VISIBLE);

        } else {
            viewHolder.img_post_other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialogs.suggestPostDialog(Constants.mainActivity, wall.group.id * -1, post);
                }
            });
            viewHolder.txt_post_comment.setVisibility(View.GONE);
            viewHolder.txt_post_like.setVisibility(View.GONE);
            viewHolder.txt_post_share.setVisibility(View.GONE);
            viewHolder.img_post_comment.setVisibility(View.GONE);
            viewHolder.img_post_like.setVisibility(View.GONE);
            viewHolder.img_post_share.setVisibility(View.GONE);
        }

        viewHolder.postTextLayout.setVisibility(postWrapper.postTextVisibility);
        if (postWrapper.postTextChecker) {
            ItemDataSetter.setText(post.text, viewHolder.postTextLayout);
        }

        viewHolder.copyHistoryLayout.setVisibility(postWrapper.copyHistoryContainerVisibility);
        if (postWrapper.copyHistoryChecker) {
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

        viewHolder.txt_post_comment.setTag(new ParamsHolder(position, postWrapper));

        if (OfflineMode.isOnline(getApplicationContext()) | OfflineMode.isJsonNull(post.id)){
            viewHolder.txt_post_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamsHolder paramsHolder = (ParamsHolder) v.getTag();
                    FragmentPostCommentAndInfo fragment = FragmentPostCommentAndInfo.newInstance(postColor, paramsHolder.position, wall, paramsHolder.post);
                    fragmentManager.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                }
            });
        }else {
            viewHolder.txt_post_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), " comments are not available to this post. Please turn On the internet ", Toast.LENGTH_SHORT).show();
                }
            });
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

        public final TextView txt_post_date;

        public final TextView txt_post_like;
        public final ImageView img_post_like;
        public final TextView txt_post_share;
        public final ImageView img_post_share;
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
            this.img_post_like = (ImageView) convertView.findViewById(R.id.img_post_like);
            this.txt_post_share = (TextView) convertView.findViewById(R.id.txt_post_share);
            this.img_post_share = (ImageView) convertView.findViewById(R.id.img_post_share);
            this.txt_post_comment = (TextView) convertView.findViewById(R.id.txt_post_comment);
            this.img_post_comment = (ImageView) convertView.findViewById(R.id.img_post_comment);

            this.img_fixed_post = (ImageView) convertView.findViewById(R.id.img_fixed_post);
            this.cb_repost = (CheckBox) convertView.findViewById(R.id.cb_post_repost);

            this.img_post_other = (ImageView) convertView.findViewById(R.id.img_post_other);
        }
    }
}
