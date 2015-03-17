package typical_if.android.adapter;


import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.event.EventShowContextMenu;


/**
 * Created by admin on 23.07.2014.
 */
public class CommentsListAdapter extends BaseAdapter {

    final ArrayList<VKApiComment> commentList;
    final ArrayList<VKApiUser> profilesList;
    final ArrayList<VKApiCommunity> groupsList;

    private final LayoutInflater layoutInflater;

    public ViewHolder viewHolder;
    String first_name = "";
    String last_name = "";
    String url = "";
    int position;
    ListView listView ;
    public RelativeLayout canvas ;


    final static Pattern matPattern = Pattern.compile("\\[(id)\\d+\\|[a-zA-ZА-Яа-яєЄіІїЇюЮйЙ 0-9(\\W)]+?\\]");

    public CommentsListAdapter(ArrayList<VKApiComment> commentList, ArrayList<VKApiUser> profilesList,
                               ArrayList<VKApiCommunity> groupsList, LayoutInflater inflater,
                               ListView list) {
        this.commentList = commentList;
        this.profilesList = profilesList;
        this.groupsList = groupsList;
        layoutInflater = inflater;
        this.listView=list;
    }

    public void UpdateCommentList(ArrayList<VKApiComment> commentList, ArrayList<VKApiUser> profilesList,
                                  ArrayList<VKApiCommunity> groupsList, ListView listView,boolean scrollToBottom,
                                  ListView list) {
        this.profilesList.clear();
        this.profilesList.addAll(profilesList);
        this.commentList.clear();
        this.commentList.addAll(commentList);
        this.groupsList.clear();
        this.groupsList.addAll(groupsList);
        this.listView=list;
        this.notifyDataSetChanged();
    if (scrollToBottom){
        scrollCommentsToBottom(listView);}
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
        VKApiCommunity community;

        for (int i = 0; i < profilesListCount; i++) {
            profile = profilesList.get(i);

            for (int j = 0; j < groupsList.size(); j++) {
                community = groupsList.get(j);

                if (comment.from_id == community.id * (-1)) {
                    first_name = community.name;
                    last_name = "";
                    url = community.photo_100;
                }
            }

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
        }
    }

    public void changeStateLikeForComment(boolean state, String count) {
        viewHolder.likes.setVisibility(View.VISIBLE);
        viewHolder.likes.setChecked(state);
        viewHolder.likes.setText(" " + count);
        viewHolder.likes.setEnabled(false);

        notifyDataSetChanged();
    }

    public void holderInitialize(final ViewHolder viewHolder, final VKApiComment comment) {
        RelativeLayout.LayoutParams params =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

        ImageLoader.getInstance().displayImage(url, viewHolder.user_avatar, TIFApp.additionalOptions);
        if (comment.likes == 0) {
            viewHolder.likes.setVisibility(View.GONE);
        } else {
            viewHolder.likes.setVisibility(View.VISIBLE);
            viewHolder.likes.setText(" " + String.valueOf(comment.likes));
            viewHolder.likes.setEnabled(false);

            if (!comment.user_likes) {
                viewHolder.likes.setChecked(false);
            } else {
                viewHolder.likes.setChecked(true);
            }
        }


        viewHolder.canvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.comment_list_clickable_canvas :
                        EventBus.getDefault().post(new EventShowContextMenu(listView.getPositionForView(v)));
                        break;

                    default :
                        break;
                }

            }
        });


        viewHolder.user_name.setText(last_name + " " + first_name);

        if (comment.text.length() != 0) {
            viewHolder.commentTextLayout.setVisibility(View.VISIBLE);
            ItemDataSetter.setText(ItemDataSetter.getParsedText(comment.text), viewHolder.commentTxtPost, viewHolder.commentCbPostAllText);
        } else {
            viewHolder.commentTextLayout.setVisibility(View.GONE);
        }

        String s = String.valueOf(ItemDataSetter.getFormattedDate(comment.date));
        if (s.contains("2014,")) {
            viewHolder.date_of_user_comment.setText(String.valueOf(s.replace(" 2014,", "")));
        } else {

            viewHolder.date_of_user_comment.setText(String.valueOf(ItemDataSetter.getFormattedDate(comment.date)));
        }



        if (comment.attachments != null && comment.attachments.size() != 0) {
            viewHolder.commentAttachmentsLayout.setVisibility(View.VISIBLE);

            ItemDataSetter.setAttachemnts(
                    comment.attachments,
                    viewHolder.commentMediaLayout,
                    viewHolder.commentMediaPager,
                    viewHolder.commentMediaPagerIndicator,
                    viewHolder.commentMediaPagerVideoButton,
                    viewHolder.commentAudioLayout,
                    viewHolder.commentAudioListView,
                    viewHolder.commentDocumentLayout,
                    viewHolder.commentAlbumLayout,
                    viewHolder.commentWikiPageLayout,
                    viewHolder.commentLinkLayout,
                    viewHolder.commentTxtLinkSrc,
                    viewHolder.commentTxtLinkTitle,
                    viewHolder.commentPollLayout,
                    viewHolder.commentTxtPollTitle

            );
            params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.commentAttachmentsLayout);
            viewHolder.canvas.setLayoutParams(params);

        } else {
            viewHolder.commentAttachmentsLayout.setVisibility(View.GONE);
            params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.commentTextLayout);
            viewHolder.canvas.setLayoutParams(params);
//            if (viewHolder.canvas.isPressed()){
//
//            }else {
//               /// viewHolder.commentAttachmentsLayout.bringToFront();
//            }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        public final RelativeLayout canvas ;
        public final RoundedImageView user_avatar;
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
        public final LinearLayout commentDataLayout;
        public final TextView commentTxtPost;
        public final CheckBox commentCbPostAllText;
        public final ImageView commentImgWikiPage;
        public final TextView commentTxtWikiPage;
        public final ImageView commentImgLink;
        public final TextView commentTxtLinkTitle;
        public final TextView commentTxtLinkSrc;
        public final ImageView commentImgPoll;
        public final TextView commentTxtPollTitle;
        public final ViewPager commentMediaPager;
        public final CirclePageIndicator commentMediaPagerIndicator;
        public final ListView commentAudioListView;
        public final ImageButton commentMediaPagerVideoButton;

        public ViewHolder(View convertView) {
            this.canvas = ((RelativeLayout) convertView.findViewById(R.id.comment_list_clickable_canvas));
            this.user_avatar = (RoundedImageView) convertView.findViewById(R.id.img_user_avatar);
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
            this.commentDataLayout = (LinearLayout) convertView.findViewById(R.id.commentDataLayout);
            this.commentTxtPost = (TextView) convertView.findViewById(R.id.txt_post);
            this.commentCbPostAllText = (CheckBox) convertView.findViewById(R.id.cb_show_all_text);
            this.commentImgWikiPage = (ImageView) convertView.findViewById(R.id.img_wiki_page);
            this.commentTxtWikiPage = (TextView) convertView.findViewById(R.id.txt_wiki_page);
            this.commentImgLink = (ImageView) convertView.findViewById(R.id.img_link);
            this.commentTxtLinkTitle = (TextView) convertView.findViewById(R.id.txt_link_title);
            this.commentTxtLinkSrc = (TextView) convertView.findViewById(R.id.txt_link_src);
            this.commentImgPoll = (ImageView) convertView.findViewById(R.id.img_poll_post_preview);
            this.commentTxtPollTitle = (TextView) convertView.findViewById(R.id.txt_poll_title_preview);
            this.commentMediaPager = (ViewPager) convertView.findViewById(R.id.media_pager);
            this.commentMediaPagerIndicator = (CirclePageIndicator) convertView.findViewById(R.id.media_circle_indicator);
            this.commentAudioListView = (ListView) convertView.findViewById(R.id.lv_simple);
            this.commentMediaPagerVideoButton = (ImageButton) convertView.findViewById(R.id.ib_goto_video_page);
        }
    }
}