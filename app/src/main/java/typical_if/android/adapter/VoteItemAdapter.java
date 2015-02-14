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

import com.vk.sdk.api.model.VKApiPoll;

import java.util.ArrayList;

import typical_if.android.ItemDataSetter;
import typical_if.android.R;

/**
 * Created by Yurij on 05.02.2015.
 */
public class VoteItemAdapter extends BaseAdapter implements ListAdapter {

    public final ArrayList<VKApiPoll.Answer> answers;
    public final LayoutInflater layoutInflater;
    public ListView votesList;
    public static  int pos;
    public boolean user_answered;
    public final VKApiPoll poll;
    public View listOfVotesParent;
    public  RelativeLayout already_voted_layout ;
    public  RelativeLayout not_yet_voted_layout;

    public VoteItemAdapter(ListView listOfVotes, VKApiPoll poll, ArrayList<VKApiPoll.Answer> answers,View parent) {
        this.answers = answers;
        this.layoutInflater = (LayoutInflater) ItemDataSetter.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.votesList = listOfVotes;
        this.user_answered = ItemDataSetter.user_answered;
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
             final VKApiPoll.Answer answer = (VKApiPoll.Answer) getItem(position);
             user_answered= poll.answer_id==0?false:true;
        Log.d("poll.answer_id",poll.answer_id+";   in "+position);

        if (!user_answered) {
            final ViewHolderNotVotedVersion viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.vote_item_layout, null, false);
                already_voted_layout = ((RelativeLayout) convertView.findViewById(R.id.VotedLayout));
                not_yet_voted_layout= ((RelativeLayout) convertView.findViewById(R.id.AddVoteLayout));
                viewHolder = new ViewHolderNotVotedVersion(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderNotVotedVersion) convertView.getTag();
                showNotYetVotedPollVersion(parent, viewHolder, answer);
            }
        }
          else {
             final ViewHolderAlreadyVotedVersion viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.vote_item_layout, null, false);
                already_voted_layout = ((RelativeLayout) convertView.findViewById(R.id.VotedLayout));
                not_yet_voted_layout= ((RelativeLayout) convertView.findViewById(R.id.AddVoteLayout));
                viewHolder = new ViewHolderAlreadyVotedVersion(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderAlreadyVotedVersion) convertView.getTag();
                showAlreadyVotedPollVersion(parent,answer,viewHolder);
            }
        }

//        temp_viewHolder=viewHolder;
//        temp_convertView=convertView;

//        final View listFooterView = layoutInflater.inflate(R.layout.list_of_votes_footer_view, null);
//        votesList.addFooterView(listFooterView);
//        votesList.invalidateViews();
//        votesList.invalidate();


    //   user_answered= poll.answer_id==0?false:true;

//        //
//        if (pos == answers.size()) {
//            votesList.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT, ListView.LayoutParams.WRAP_CONTENT));
//        }

        return convertView;
    }

  public void showAlreadyVotedPollVersion (final View parent,final VKApiPoll.Answer answer,ViewHolderAlreadyVotedVersion holder){
        already_voted_layout.setVisibility(View.VISIBLE);
        not_yet_voted_layout.setVisibility(View.GONE);

        holder.amount_of_answers.setText(String.valueOf(answer.votes));
        holder.vote_text.setText(answer.text);
        holder.bar.setMax(100); holder.bar.setProgress((int) answer.rate);
        holder.rate.setText(String.valueOf(answer.rate) + "%");

        //poll.answer_id=answer.id;

      //  notifyDataSetChanged();
    }

    public void showNotYetVotedPollVersion (final View parent, final ViewHolderNotVotedVersion viewHolder,final VKApiPoll.Answer answer){
        already_voted_layout.setVisibility(View.GONE);
        not_yet_voted_layout.setVisibility(View.VISIBLE);
        viewHolder.vote_title.setText(answer.text);
//        viewHolder.add_vote_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VKHelper.addVote(poll.owner_id, poll.id, answer.id, 0, new VKRequest.VKRequestListener() {
//                    @Override
//                    public void onComplete(VKResponse response) {
//                        super.onComplete(response);
//                        Log.d("VOTE_ADDED", response.json.toString());
//                        // RelativeLayout spinner=((RelativeLayout) listOfVotesParent.findViewById(R.id.changeVotesSpinnerLayout));
//                        //   spinner.setVisibility(View.VISIBLE);
//
//                        if (response.json.optInt("response") == 1) {
//                            poll.answer_id = answer.id;
//                            ItemDataSetter.refreshList(parent, votesList);
//                            showProgress(answer);
//
//                        } else
//                            Toast.makeText(Constants.mainActivity.getApplicationContext(), "You've already made decision", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                });
//            }
//        });
    //    poll.answer_id=answer.id;
       // notifyDataSetChanged();
    }




    public static class ViewHolderNotVotedVersion {

        public final TextView vote_title;
        public final Button add_vote_button;


        ViewHolderNotVotedVersion(View view) {
            this.vote_title = (TextView) view.findViewById(R.id.vote_text2);
            this.add_vote_button = (Button) view.findViewById(R.id.add_vote_button);

        }
    }
        public static class ViewHolderAlreadyVotedVersion{
            public final ProgressBar bar;
            public final TextView vote_text;
            public final TextView rate;
            public final TextView amount_of_answers;




            ViewHolderAlreadyVotedVersion(View view) {

                this.bar = (ProgressBar) view.findViewById(R.id.vote_progress);
                this.vote_text = (TextView) view.findViewById(R.id.vote_text);
                this.rate = (TextView) view.findViewById(R.id.rate_percents);
                this.amount_of_answers = (TextView) view.findViewById(R.id.rate_in_answers);


            }


    }
}

