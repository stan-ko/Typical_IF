package typical_if.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPoll;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.VKHelper;
import typical_if.android.util.VKPoll;

/**
 * Created by Yurij on 05.02.2015.
 */
public class VoteItemAdapter extends BaseAdapter implements ListAdapter {

    public final ArrayList<VKApiPoll.Answer> answers;
    public ViewHolder temp_viewHolder;
    public View temp_convertView;
    public final LayoutInflater layoutInflater;
    public final Context context;
    public ListView votesList;
    public static  int pos;
    public boolean user_answered;
    public final VKPoll poll;
    public View listOfVotesParent;
    public  RelativeLayout l;
    public  RelativeLayout l1;

    public VoteItemAdapter(ListView listOfVotes, VKPoll poll, ArrayList<VKApiPoll.Answer> answers, Context context, boolean user_answered, View parent) {
        this.answers = answers;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.votesList = listOfVotes;
        this.user_answered = user_answered;
        this.poll = poll;
        this.listOfVotesParent = parent;
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
    public View getView(int position,View convertView, final ViewGroup parent) {

        pos = position;
        final VKApiPoll.Answer answer = (VKApiPoll.Answer) getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.vote_item_layout, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        temp_viewHolder=viewHolder;
        temp_convertView=convertView;

        final View listFooterView = layoutInflater.inflate(R.layout.list_of_votes_footer_view, null);
        votesList.addFooterView(listFooterView);
        votesList.invalidateViews();
        votesList.invalidate();

       l = ((RelativeLayout) convertView.findViewById(R.id.VotedLayout));
       l1 = ((RelativeLayout) convertView.findViewById(R.id.AddVoteLayout));

        if (poll.answer_id==0){
            user_answered=false;
        }else {
            user_answered=true;
        }

        if (!user_answered) {
            showAddButton(parent,viewHolder,answer,l,l1);
         } else {
            showProgress(answer);
        }

        if (pos == answers.size()) {
            votesList.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT, ListView.LayoutParams.WRAP_CONTENT));
        }

        return convertView;
    }




    public void showProgress (final VKApiPoll.Answer answer){
       // final  RelativeLayout l = ((RelativeLayout) temp_convertView.findViewById(R.id.VotedLayout));
       // final   RelativeLayout l1 = ((RelativeLayout) temp_convertView.findViewById(R.id.AddVoteLayout));
        l.setVisibility(View.VISIBLE);

        l1.setVisibility(View.GONE);

        temp_viewHolder.amount_of_answers.setText(String.valueOf(answer.votes));
        temp_viewHolder.vote_text.setText(answer.text);
        temp_viewHolder.bar.setMax(100);
        temp_viewHolder.bar.setProgress((int) answer.rate);
        temp_viewHolder.rate.setText(String.valueOf(answer.rate) + "%");
        poll.answer_id=answer.id;

        notifyDataSetChanged();
    }

    public void showAddButton (final View parent, final ViewHolder viewHolder,final VKApiPoll.Answer answer, final RelativeLayout l, final RelativeLayout l1){
        l.setVisibility(View.GONE);

        l1.setVisibility(View.VISIBLE);
        viewHolder.vote_title.setText(answer.text);
        viewHolder.add_vote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKHelper.addVote(poll.owner_id, poll.id, answer.id, 0, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Log.d("VOTE_ADDED", response.json.toString());
                        // RelativeLayout spinner=((RelativeLayout) listOfVotesParent.findViewById(R.id.changeVotesSpinnerLayout));
                        //   spinner.setVisibility(View.VISIBLE);

                        if (response.json.optInt("response") == 1) {
                            poll.answer_id = answer.id;
                            ItemDataSetter.refreshList(parent, votesList);
                            showProgress(answer);

                        } else
                            Toast.makeText(Constants.mainActivity.getApplicationContext(), "You've already made decision", Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });
        poll.answer_id=answer.id;

        notifyDataSetChanged();
    }




    public static class ViewHolder {
        public final ProgressBar bar;
        public final TextView vote_text;
        public final TextView rate;
        public final TextView amount_of_answers;
        public final TextView vote_title;
        public final Button add_vote_button;


        ViewHolder(View view) {
            this.vote_title = (TextView) view.findViewById(R.id.vote_text2);
            this.bar = (ProgressBar) view.findViewById(R.id.vote_progress);
            this.vote_text = (TextView) view.findViewById(R.id.vote_text);
            this.rate = (TextView) view.findViewById(R.id.rate_percents);
            this.amount_of_answers = (TextView) view.findViewById(R.id.rate_in_answers);
            this.add_vote_button = (Button) view.findViewById(R.id.add_vote_button);

        }
    }
}

