package typical_if.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
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
    TextView answers_anonymous_text;
    TextView answers_anonymous_text_preview;
    String isAnonymous_preview;
    String isAnonymous;
    Button changeDecision;
    int answer_id;


    public VotesItemAdapter(VKApiPoll poll, TextView answers_anonymous_text, TextView answers_anonymous_text_preview, String isAnonymous, String isAnonymous_preview,
                            Button changeDecision) {
        this.answers = poll.answers;
        this.layoutInflater = (LayoutInflater) ItemDataSetter.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.answers_anonymous_text=answers_anonymous_text;
        this.answers_anonymous_text_preview=answers_anonymous_text_preview;
        this.isAnonymous=isAnonymous;
        this.isAnonymous_preview=isAnonymous_preview;
        this.poll = poll;
        this.changeDecision=changeDecision;
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




        viewHolder.addVoteButton.setOnClickListener(new View.OnClickListener() {
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
                            ++poll.votes;
                            ++answer.votes;
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


       changeDecision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (poll.answer_id!=0){
                for (int i =0 ; i <poll.answers.size(); i++){
                    if (poll.answer_id==answers.get(i).id){
                        answer_id=answers.get(i).id;
                    }
                }
                VKHelper.deleteVote(poll.owner_id, poll.id, answer_id, 0, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        int result = response.json.optInt("response");
                        if (result!=0) {
                            Log.d("YOU'RE_VOTE_DELETED",response.json.toString());
                            poll.answer_id=0;
                            --poll.votes;
                            --answer.votes;
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

            }
        });

        viewHolder.bar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                VKHelper.deleteVote(poll.owner_id, poll.id, answer.id, 0, new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        int result = response.json.optInt("response");
                        if (result!=0) {
                            Log.d("YOU'RE_VOTE_DELETED",response.json.toString());
                            poll.answer_id=0;
                            --poll.votes;
                            --answer.votes;
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

            return true;
            }
        });


   return convertView;
    }
    public void checkConditions (ViewHolder viewHolder, VKApiPoll.Answer answer){


        answers_anonymous_text_preview.setText(isAnonymous_preview + " " + poll.votes);
        answers_anonymous_text.setText(isAnonymous + " " + poll.votes);

        viewHolder.amount_of_answers.setText(String.valueOf(answer.votes));
        viewHolder.vote_text.setText(answer.text);
        viewHolder.bar.setMax(100);
        viewHolder.bar.setProgress((int) answer.rate);

        if (poll.answer_id==0){
            changeDecision.setVisibility(View.INVISIBLE);
            viewHolder.bar.setVisibility(View.INVISIBLE);
            viewHolder.amount_of_answers.setVisibility(View.INVISIBLE);
            viewHolder.addVoteButton.setVisibility(View.VISIBLE);

        }
        if  (poll.answer_id>0){
            changeDecision.setVisibility(View.VISIBLE);
            viewHolder.addVoteButton.setVisibility(View.GONE);
            viewHolder.bar.setVisibility(View.VISIBLE);

            viewHolder.amount_of_answers.setVisibility(View.VISIBLE);
        }

        if (answer.id==poll.answer_id){
            viewHolder.vote_text.setTypeface(null, Typeface.BOLD);
            viewHolder.amount_of_answers.setTypeface(null, Typeface.BOLD);

            viewHolder.vote_text.setTextColor(Color.WHITE);
            viewHolder.amount_of_answers.setTextColor(Color.WHITE);
        } else {
            viewHolder.vote_text.setTypeface(null, Typeface.NORMAL);
            viewHolder.amount_of_answers.setTypeface(null, Typeface.NORMAL);
            viewHolder.amount_of_answers.setTextColor(Constants.mainActivity.getResources().getColor(R.color.textFadeOutColor));;
            viewHolder.vote_text.setTextColor(Constants.mainActivity.getResources().getColor(R.color.textFadeOutColor));
        }

    }

    public static class ViewHolder {
        public final NumberProgressBar bar;
        public final TextView vote_text;
     //   public final TextView rate_percents;
        public final TextView amount_of_answers;

        public final Button addVoteButton;


        ViewHolder(View view) {

            this.bar = (NumberProgressBar) view.findViewById(R.id.rate_bar);
            this.vote_text = (TextView) view.findViewById(R.id.variant_text);
        //   this.rate_percents = (TextView) view.findViewById(R.id.rate_percents_text);
            this.amount_of_answers = (TextView) view.findViewById(R.id.rate_amount_text);

            this.addVoteButton = ((Button) view.findViewById(R.id.add_your_vote_button));


        }


    }
}

