package typical_if.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPoll;

import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.util.VKPoll;

/**
 * Created by Yurij on 05.02.2015.
 */
public class VoteItemAdapter extends BaseAdapter {

    public final ArrayList<VKApiPoll.Answer> answers;

    public final LayoutInflater layoutInflater;
    public final Context context;
    public ListView votesList;
    public static int pos;
    boolean user_answered;
    public final VKPoll poll;

    public VoteItemAdapter(ListView listOfVotes,VKPoll poll,ArrayList<VKApiPoll.Answer> answers, Context context, boolean user_answered) {
        this.answers= answers;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.votesList=listOfVotes;
        this.user_answered=user_answered;
        this.poll=poll;
    }

    @Override
    public int getCount() {
        return answers.size();
    }

    @Override
    public Object getItem(int position) {
        return answers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       pos=position;
        final VKApiPoll.Answer answer = (VKApiPoll.Answer) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.vote_item_layout, null, false);
//          if (position >=0){
//            RelativeLayout l = ((RelativeLayout) convertView.findViewById(R.id.rootOfVote));
//            l.setVisibility(View.VISIBLE);}


            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!user_answered) {

            RelativeLayout l = ((RelativeLayout) convertView.findViewById(R.id.NotVotedLayout));
            l.setVisibility(View.GONE);
            RelativeLayout l1 = ((RelativeLayout) convertView.findViewById(R.id.AddVoteLayout));
            l1.setVisibility(View.VISIBLE);
            viewHolder.vote_title.setText(answer.text);
            viewHolder.add_vote_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKHelper.addVote(poll.owner_id,poll.id,answer.id,0, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Log.d(response.json.toString(),"VOTE_ADDED");

                        }
                    });
                }
            });

        }else {
            RelativeLayout l = ((RelativeLayout) convertView.findViewById(R.id.NotVotedLayout));
            l.setVisibility(View.VISIBLE);
            RelativeLayout l1 = ((RelativeLayout) convertView.findViewById(R.id.AddVoteLayout));
            l1.setVisibility(View.GONE);

            viewHolder.amount_of_answers.setText(String.valueOf(answer.votes));
            viewHolder.vote_text.setText(answer.text);
            viewHolder.bar.setMax(100);
            viewHolder.bar.setProgress((int)answer.rate);
            viewHolder.rate.setText(String.valueOf(answer.rate)+"%");

        }



        if (pos==answers.size()){
            votesList.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT, ListView.LayoutParams.WRAP_CONTENT));
        }





  return convertView;
    }





    public static class ViewHolder {
        public final ProgressBar bar;
        public final TextView vote_text;
        public final TextView rate;
        public final TextView amount_of_answers;
        public final TextView vote_title;
        public final Button add_vote_button;


        ViewHolder(View view) {
            this.vote_title = (TextView)view.findViewById(R.id.vote_text2);
            this.bar= (ProgressBar) view.findViewById(R.id.vote_progress);
            this.vote_text= (TextView) view.findViewById(R.id.vote_text);
            this.rate= (TextView) view.findViewById(R.id.rate_percents);
            this.amount_of_answers = (TextView) view.findViewById(R.id.rate_in_answers);
            this.add_vote_button=(Button)view.findViewById(R.id.add_vote_button);

        }
    }
}

