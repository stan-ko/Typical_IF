package typical_if.android.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.model.VKApiComment;

import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.model.Profile;


/**
 * Created by admin on 23.07.2014.
 */
public class CommentsListAdapter extends BaseAdapter {
    ArrayList<VKApiComment> commentList;
    ArrayList<Profile> profilesList;
    LayoutInflater layoutInflater;

    String first_name = "";
    String last_name = "";
    String url = "";

    public CommentsListAdapter(ArrayList<VKApiComment> commentList, ArrayList<Profile> profilesList, LayoutInflater inflater, AdapterView.OnItemClickListener listener) {
        this.commentList = commentList;
        this.profilesList = profilesList;
        layoutInflater = inflater;
    }

    public void UpdateCommentList(ArrayList<VKApiComment> commentList, ArrayList<Profile> profilesList, ListView listView) {
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

    public void UserIdentifier(VKApiComment comment) {
        for (int i = 0; i < profilesList.size(); i++) {
            if (comment.from_id == profilesList.get(i).id) {
                first_name = profilesList.get(i).first_name;
                last_name = profilesList.get(i).last_name;
                url = profilesList.get(i).photo_100;

            }
        }

        for (int k = 0; k < commentList.size(); k++) {
            if (commentList.get(k).reply_to_user > (long) 0) {
                commentList.get(k).text = commentList.get(k).text.replaceFirst("^(\\[id\\d+\\|)", "").replaceFirst("(\\])", "").toString();
            } else {
                commentList.get(k).text = commentList.get(k).text;
            }
        }
        ;
    }


    public void HolderInitialize(ViewHolder viewHolder, VKApiComment comment) {
        ImageLoader.getInstance().displayImage(url, viewHolder.user_avatar);
        viewHolder.user_name.setText(last_name + " " + first_name);
        viewHolder.user_comment.setText(String.valueOf(comment.text));
        Log.d("----------------------------------------->", comment.text + " " + comment.from_id);
        viewHolder.date_of_user_comment.setText(String.valueOf(WallAdapter.getFormattedDate(comment.date)));


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

        //  final Profile profile = profilesList.get(position);

        final VKApiComment comment = commentList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.comment_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserIdentifier(comment);

        //ImageLoader.getInstance().displayImage(url, viewHolder.user_avatar);
        HolderInitialize(viewHolder, comment);
        return convertView;
    }


    public static class ViewHolder {
        public final ImageView user_avatar;
        public final TextView user_name;
        public final TextView user_comment;
        public final TextView date_of_user_comment;


        public ViewHolder(View convertView) {
            this.user_avatar = (ImageView) convertView.findViewById(R.id.img_user_avatar);

            this.user_name = (TextView) convertView.findViewById(R.id.user_name_textView);
            this.user_comment = (TextView) convertView.findViewById(R.id.user_comment_text);
            this.date_of_user_comment = (TextView) convertView.findViewById(R.id.text_date_of_comment);

        }
    }


}
