package typical_if.android.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.model.VKApiComment;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.model.Profile;


/**
 * Created by admin on 23.07.2014.
 */
public class CommentsListAdapter extends BaseAdapter {
    ArrayList<VKApiComment> commentList;
    ArrayList<Profile> profilesList;
    private LayoutInflater layoutInflater;
    private static Context context;

    String first_name = "";
    String last_name = "";
    String url = "";

    String postColor;

    public CommentsListAdapter(ArrayList<VKApiComment> commentList, ArrayList<Profile> profilesList, LayoutInflater inflater, String postColor) {
        this.commentList = commentList;
        this.profilesList = profilesList;
        layoutInflater = inflater;
        this.context = VKUIHelper.getApplicationContext();
        this.postColor = postColor;
    }

    public void updateCommentList(ArrayList<VKApiComment> commentList, ArrayList<Profile> profilesList, ListView listView) {
        this.profilesList = profilesList;
        this.commentList.clear();
        this.commentList.addAll(commentList);
        this.notifyDataSetChanged();
        scrollMyListViewToBottom(listView);
    }

    private void scrollMyListViewToBottom(final ListView myListView) {
        myListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                myListView.setSelection(getCount() - 1);
            }
        });
    }

    public void userIdentifier(VKApiComment comment) {
        for (int i = 0; i < profilesList.size(); i++) {
            if (comment.from_id == profilesList.get(i).id) {
                first_name = profilesList.get(i).first_name;
                last_name = profilesList.get(i).last_name;
                url = profilesList.get(i).photo_100;

            }
        }

        for (int k = 0; k < commentList.size(); k++) {
            if (commentList.get(k).reply_to_user > (long) 0) {
                final Matcher matReply = Pattern.compile("\\[(id)\\d+\\|[a-zA-ZА-Яа-яєЄіІїЇюЮйЙ 0-9(\\W)]+?\\]").matcher(commentList.get(k).text);
                StringBuilder stringB = new StringBuilder(commentList.get(k).text);
                int start;
                int end;
                while (matReply.find()) {
                    start = stringB.indexOf(matReply.group());
                    end = start + matReply.group().length();

                    final String[] replier = matReply.group().replaceFirst("\\[", "").replaceFirst("\\]", "").split("\\|");
                    stringB.replace(start, end, replier[1]);
                    break;
                }

                commentList.get(k).text =  stringB.toString();
            } else {
                commentList.get(k).text = commentList.get(k).text;
            }
        }
    }



    public void holderInitialize(ViewHolder viewHolder, VKApiComment comment) {
        ItemDataSetter.commentViewHolder = viewHolder;
        ItemDataSetter.postColor = postColor;

        ImageLoader.getInstance().displayImage(url, viewHolder.user_avatar);

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
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return commentList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VKApiComment comment = commentList.get(position);
        ViewHolder viewHolder;
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

        public ViewHolder(View convertView) {
            this.user_avatar = (ImageView) convertView.findViewById(R.id.img_user_avatar);

            this.user_name = (TextView) convertView.findViewById(R.id.user_name_textView);
            this.date_of_user_comment = (TextView) convertView.findViewById(R.id.text_date_of_comment);

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
        }
    }
}
