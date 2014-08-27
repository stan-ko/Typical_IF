package typical_if.android.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import typical_if.android.ItemDataSetter;
import typical_if.android.MyApplication;
import typical_if.android.R;
import typical_if.android.model.Profile;


/**
 * Created by admin on 23.07.2014.
 */
public class CommentsListAdapter extends BaseAdapter {

    final ArrayList<VKApiComment> commentList;
    final ArrayList<VKApiUser> profilesList;

    private final LayoutInflater layoutInflater;
    private static final Context appContext = MyApplication.getAppContext();

    public ViewHolder viewHolder;
    String first_name = "";
    String last_name = "";
    String url = "";
    int position;

    final String postColor;

    final static Pattern matPattern = Pattern.compile("\\[(id)\\d+\\|[a-zA-ZА-Яа-яєЄіІїЇюЮйЙ 0-9(\\W)]+?\\]");

    public CommentsListAdapter(ArrayList<VKApiComment> commentList, ArrayList<VKApiUser> profilesList, LayoutInflater inflater, String postColor) {
        this.commentList = commentList;
        this.profilesList = profilesList;
        layoutInflater = inflater;
        this.postColor = postColor;
    }

    public void UpdateCommentList(ArrayList<VKApiComment> commentList, ArrayList<VKApiUser> profilesList, ListView listView) {
        this.profilesList.clear();
        this.profilesList.addAll(profilesList);
        this.commentList.clear();
        this.commentList.addAll(commentList);
        this.notifyDataSetChanged();
        scrollCommentsToBottom(listView);
    }

    private void scrollCommentsToBottom(final ListView listView) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(getCount() - 1);
            }
        });
    }

    public void userIdentifier(VKApiComment comment) {
        final int profilesListCount = profilesList.size();
        VKApiUser profile;
        for (int i = 0; i < profilesListCount; i++) {
            profile = profilesList.get(i);
            if (comment.from_id == profile.id) {
                first_name = profile.first_name;
                last_name = profile.last_name;
                url = profile.photo_100;

            }
        }

        final int commentListCount = commentList.size();
        VKApiComment vkApiComment;
        for (int k = 0; k < commentListCount; k++) {
            vkApiComment = commentList.get(k);
            if (vkApiComment.reply_to_user > (long) 0) {
                final Matcher matReply = matPattern.matcher(vkApiComment.text);
                StringBuilder stringB = new StringBuilder(vkApiComment.text);
                int start;
                int end;
                while (matReply.find()) {
                    start = stringB.indexOf(matReply.group());
                    end = start + matReply.group().length();

                    final String[] replier = matReply.group().replaceFirst("\\[", "").replaceFirst("\\]", "").split("\\|");
                    stringB.replace(start, end, replier[1]);
                    break;
                }

                vkApiComment.text = stringB.toString();
            }
//            else {
//                vkApiComment.text = vkApiComment.text;
//            }
        }
    }

    public void changeStateLikeForComment(boolean state, String count) {

        viewHolder.likes.setVisibility(View.VISIBLE);

        viewHolder.likes.setChecked(state);
        viewHolder.likes.setText(count);
        notifyDataSetChanged();

    }


    public void holderInitialize(final ViewHolder viewHolder, final VKApiComment comment) {

        ItemDataSetter.commentViewHolder = viewHolder;
        ItemDataSetter.postColor = postColor;

        ImageLoader.getInstance().displayImage(url, viewHolder.user_avatar);
        if (comment.likes == 0) {
            viewHolder.likes.setVisibility(View.GONE);
        } else {
            viewHolder.likes.setVisibility(View.VISIBLE);
            viewHolder.likes.setText(String.valueOf(comment.likes));

            if (comment.user_likes == false) {

                viewHolder.likes.setChecked(false);
            } else {
                viewHolder.likes.setChecked(true);
            }
        }

        viewHolder.user_name.setText(last_name + " " + first_name);

        if (comment.text.length() != 0) {
            ItemDataSetter.setText(comment.text, viewHolder.commentTextLayout);
        } else {
            viewHolder.commentTextLayout.setVisibility(View.GONE);
        }

        viewHolder.date_of_user_comment.setText(String.valueOf(ItemDataSetter.getFormattedDate(comment.date)));

        if (comment.attachments != null && comment.attachments.size() != 0) {
            ItemDataSetter.setAttachemnts(comment.attachments, viewHolder.commentAttachmentsLayout, 2);
        } else {
            viewHolder.commentAttachmentsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public VKApiComment getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return commentList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.position = position;

        final VKApiComment comment = commentList.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.comment_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        userIdentifier(comment);
        holderInitialize(viewHolder, comment);
        return convertView;
    }


    public static class ViewHolder {
        public final ImageView user_avatar;
        public final TextView user_name;
        public final TextView date_of_user_comment;
        public final CheckBox likes;

        public final RelativeLayout commentMediaLayout;
        private final RelativeLayout commentTextLayout;
        public final LinearLayout commentAudioLayout;
        private final LinearLayout commentAttachmentsLayout;
        public final LinearLayout commentDocumentLayout;
        public final LinearLayout commentAlbumLayout;
        public final RelativeLayout commentWikiPageLayout;
        public final RelativeLayout commentLinkLayout;
        public final RelativeLayout commentPollLayout;
        public final RelativeLayout commentParentLayout;
        public final RelativeLayout commentDataLayout;

        public ViewHolder(View convertView) {
            this.user_avatar = (ImageView) convertView.findViewById(R.id.img_user_avatar);

            this.user_name = (TextView) convertView.findViewById(R.id.user_name_textView);
            this.date_of_user_comment = (TextView) convertView.findViewById(R.id.text_date_of_comment);
            this.likes = (CheckBox) convertView.findViewById(R.id.post_comment_like_checkbox);

            this.commentAttachmentsLayout = (LinearLayout) convertView.findViewById(R.id.commentAttachmentsLayout);
            this.commentMediaLayout = (RelativeLayout) convertView.findViewById(R.id.commentMediaLayout);
            this.commentAudioLayout = (LinearLayout) convertView.findViewById(R.id.commentAudioLayout);
            this.commentDocumentLayout = (LinearLayout) convertView.findViewById(R.id.commentDocumentLayout);
            this.commentAlbumLayout = (LinearLayout) convertView.findViewById(R.id.commentAlbumLayout);
            this.commentWikiPageLayout = (RelativeLayout) convertView.findViewById(R.id.commentWikiPageLayout);
            this.commentLinkLayout = (RelativeLayout) convertView.findViewById(R.id.commentLinkLayout);
            this.commentTextLayout = (RelativeLayout) convertView.findViewById(R.id.commentTextLayout);
            this.commentPollLayout = (RelativeLayout) convertView.findViewById(R.id.commentPollLayout);
            this.commentParentLayout = (RelativeLayout) convertView.findViewById(R.id.commentParentLayout);
            this.commentDataLayout = (RelativeLayout) convertView.findViewById(R.id.commentDataLayout);
        }
    }


}