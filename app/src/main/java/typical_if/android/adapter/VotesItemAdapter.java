package typical_if.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPoll;
import com.vk.sdk.api.model.VKList;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;
import typical_if.android.VKHelper;

/**
 * Created by Yurij on 05.02.2015.
 */
public class VotesItemAdapter extends BaseAdapter implements ListAdapter {

    public final VKList<VKApiPoll.Answer> answers;
    //  public final LayoutInflater layoutInflater;

    public final VKApiPoll poll;
    LayoutInflater layoutInflater;


    public VotesItemAdapter(VKApiPoll poll) {
        this.answers = poll.answers;
        this.layoutInflater = (LayoutInflater) ItemDataSetter.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.poll = poll;
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        final VKApiPoll.Answer answer = (VKApiPoll.Answer) getItem(position);

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.vote_item_v2, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.vote_text.setVisibility(View.VISIBLE);

        checkConditions(viewHolder, answer);



        viewHolder.amount_of_answers.setText(String.valueOf(answer.votes));
        viewHolder.vote_text.setText(answer.text);
        viewHolder.bar.setMax(100);
        viewHolder.bar.setProgress((int) answer.rate);
        viewHolder.rate_percents.setText(String.valueOf(answer.rate) + "%");
//        viewHolder.bar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VKHelper.deleteVote(poll.owner_id, poll.id, answer.id, 0, new VKRequest.VKRequestListener() {
//                    @Override
//                    public void onComplete(VKResponse response) {
//                        super.onComplete(response);
//                        //     poll.answer_id = 0;
//                        //   notifyDataSetChanged();
//                        Log.d(response.json.toString(), "VOTE_DELETED");
//
//                    }
//                });
//
//            }
//        });


        // }
        //  else {
        //   final ViewHolderAlreadyVotedVersion viewHolder;
        //    if (convertView == null) {
        //       convertView = layoutInflater.inflate(R.layout.vote_item_layout, null, false);
        //    already_voted_layout = ((RelativeLayout) convertView.findViewById(R.id.VotedLayout));
        //   not_yet_voted_layout= ((RelativeLayout) convertView.findViewById(R.id.AddVoteLayout));
        //        viewHolder = new ViewHolderAlreadyVotedVersion(convertView);
        //        convertView.setTag(viewHolder);
        //    } else {
        //       viewHolder = (ViewHolderAlreadyVotedVersion) convertView.getTag();
        //         showAlreadyVotedPollVersion(parent,answer,viewHolder);
        //   }

        viewHolder.add_vote_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKHelper.addVote(poll.owner_id, poll.id, answer.id, 0, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        int result = response.json.optInt("response");
                        if (result!=0) {
                            Log.d("YOU'RE_VOTE_ADDED",response.json.toString());
                            poll.answer_id=answer.id;
                            notifyDataSetChanged();
                            checkConditions(viewHolder,answer);
                            viewHolder.vote_text.setTypeface(null, Typeface.BOLD);

                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        Toast.makeText(Constants.mainActivity.getApplicationContext(),Constants.mainActivity.getResources().getString(R.string.error_during_voting),Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        viewHolder.bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKHelper.deleteVote(poll.owner_id, poll.id, answer.id, 0, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        int result = response.json.optInt("response");
                        if (result!=0) {
                            Log.d("YOU'RE_VOTE_DELETED",response.json.toString());
                            poll.answer_id=0;
                            notifyDataSetChanged();
                            checkConditions (viewHolder,answer);
                            viewHolder.vote_text.setTypeface(null, Typeface.NORMAL);
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        Toast.makeText(Constants.mainActivity.getApplicationContext(),Constants.mainActivity.getResources().getString(R.string.error_during_voting),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


   return convertView;
    }
    public void checkConditions (ViewHolder viewHolder, VKApiPoll.Answer answer){

        if (poll.answer_id==0){
            viewHolder.bar.setVisibility(View.INVISIBLE);
            viewHolder.rate_percents.setVisibility(View.INVISIBLE);
            viewHolder.amount_of_answers.setVisibility(View.INVISIBLE);

            viewHolder.addVoteButton.setVisibility(View.VISIBLE);
            viewHolder.add_vote_text.setVisibility(View.VISIBLE);
        }
        if  (poll.answer_id>0){
            viewHolder.addVoteButton.setVisibility(View.GONE);
            viewHolder.add_vote_text.setVisibility(View.GONE);
            viewHolder.bar.setVisibility(View.VISIBLE);
            viewHolder.rate_percents.setVisibility(View.VISIBLE);
            viewHolder.amount_of_answers.setVisibility(View.VISIBLE);
        }

        if (answer.id==poll.answer_id){
            viewHolder.vote_text.setTypeface(null, Typeface.BOLD);
            viewHolder.rate_percents.setTypeface(null, Typeface.BOLD);
            viewHolder.amount_of_answers.setTypeface(null, Typeface.BOLD);

            viewHolder.vote_text.setTextColor(Color.WHITE);
            viewHolder.rate_percents.setTextColor(Color.WHITE);
            viewHolder.amount_of_answers.setTextColor(Color.WHITE);
        } else {
            viewHolder.vote_text.setTypeface(null, Typeface.NORMAL);
            viewHolder.rate_percents.setTypeface(null, Typeface.NORMAL);
            viewHolder.amount_of_answers.setTypeface(null, Typeface.NORMAL);

            viewHolder.rate_percents.setTextColor(Constants.mainActivity.getResources().getColor(R.color.textFadeOutColor));;
            viewHolder.amount_of_answers.setTextColor(Constants.mainActivity.getResources().getColor(R.color.textFadeOutColor));;
            viewHolder.vote_text.setTextColor(Constants.mainActivity.getResources().getColor(R.color.textFadeOutColor));
        }

    }

    public static class ViewHolder {
        public final ProgressBar bar;
        public final TextView vote_text;
        public final TextView rate_percents;
        public final TextView amount_of_answers;
        public final TextView add_vote_text;
        public final ImageButton addVoteButton;


        ViewHolder(View view) {

            this.bar = (ProgressBar) view.findViewById(R.id.rate_bar);
            this.vote_text = (TextView) view.findViewById(R.id.variant_text);
            this.rate_percents = (TextView) view.findViewById(R.id.rate_percents_text);
            this.amount_of_answers = (TextView) view.findViewById(R.id.rate_amount_text);
            this.add_vote_text = ((TextView) view.findViewById(R.id.add_vote_text));
            this.addVoteButton = ((ImageButton) view.findViewById(R.id.add_your_vote_button));


        }


    }
}

