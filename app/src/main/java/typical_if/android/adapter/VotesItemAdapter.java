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
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiPoll;
import com.vk.sdk.api.model.VKList;

import typical_if.android.R;
import typical_if.android.TIFApp;
import typical_if.android.VKHelper;
import typical_if.android.VKRequestListener;

/**
 * Created by Yurij on 05.02.2015.
 */
public class VotesItemAdapter extends BaseAdapter implements ListAdapter {

    public final VKList<VKApiPoll.Answer> answers;
    //  public final LayoutInflater mLayoutInflater;

    public VKApiPoll poll;
    final Context appContext;
    final int textFadeOutColor;
    LayoutInflater layoutInflater;
    TextView answers_anonymous_text;
    TextView answers_anonymous_text_preview;
    String isAnonymous_preview;
    String isAnonymous;
    Button changeDecision;
    int answer_id;
    int rule = 0;


    public VotesItemAdapter(final Context activity, VKApiPoll poll, TextView answers_anonymous_text, TextView answers_anonymous_text_preview, String isAnonymous, String isAnonymous_preview,
                            Button changeDecision) {
        this.answers = poll.answers;
        this.appContext = activity.getApplicationContext();
        this.textFadeOutColor = appContext.getResources().getColor(R.color.textFadeOutColor);
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.answers_anonymous_text = answers_anonymous_text;
        this.answers_anonymous_text_preview = answers_anonymous_text_preview;
        this.isAnonymous = isAnonymous;
        this.isAnonymous_preview = isAnonymous_preview;
        this.poll = poll;
        this.changeDecision = changeDecision;
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


        //   viewHolder.amount_of_answers.setText(String.valueOf(answer.votes));
        //    viewHolder.vote_text.setText(answer.text);
        //    viewHolder.bar.setMax(100);

        ///  if (viewHolder.wasAlreadyCalled){
        ///     viewHolder.bar.setProgress((int) answer.rate);
        //  }else {
        viewHolder.bar.setProgress(0);
        //  }


        checkConditions(viewHolder, answer);


        viewHolder.addVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKHelper.addVote(poll.owner_id, poll.id, answer.id, 0, new VKRequestListener() {
                    @Override
                    public void onSuccess() {
                        if (hasJson) {
                            int result = vkJson.optInt("response");
                            if (result != 0) {
                                Log.d("YOU'RE_VOTE_ADDED", vkJson.toString());
                                poll.answer_id = answer.id;
                                ++poll.votes;
                                ++answer.votes;
                                notifyDataSetChanged();
                                checkConditions(viewHolder, answer);
                                viewHolder.vote_text.setTypeface(null, Typeface.BOLD);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(TIFApp.getAppContext(), R.string.error_during_voting, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        changeDecision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (poll.answer_id != 0) {
                    for (int i = 0; i < poll.answers.size(); i++) {
                        if (poll.answer_id == answers.get(i).id) {
                            answer_id = answers.get(i).id;
                        }
                    }
                    VKHelper.deleteVote(poll.owner_id, poll.id, answer_id, 0, new VKRequestListener() {
                        @Override
                        public void onSuccess() {
                            int result = vkJson.optInt(VKHelper.TIF_VK_SDK_KEY_RESPONSE);
                            if (result != 0) {
                                Log.d("YOU'RE_VOTE_DELETED", vkJson.toString());
                                poll.answer_id = 0;
                                --poll.votes;
                                --answer.votes;
                                notifyDataSetChanged();
                                checkConditions(viewHolder, answer);
                                viewHolder.vote_text.setTypeface(null, Typeface.NORMAL);
                            }
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(TIFApp.getAppContext(), R.string.error_during_voting, Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

        viewHolder.bar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                VKHelper.deleteVote(poll.owner_id, poll.id, answer.id, 0, new VKRequestListener() {
                    @Override
                    public void onSuccess() {
                        int result = vkJson.optInt(VKHelper.TIF_VK_SDK_KEY_RESPONSE);
                        if (result != 0) {
                            Log.d("YOU'RE_VOTE_DELETED", vkJson.toString());
                            poll.answer_id = 0;
                            --poll.votes;
                            --answer.votes;
                            notifyDataSetChanged();
                            checkConditions(viewHolder, answer);
                            viewHolder.vote_text.setTypeface(null, Typeface.NORMAL);
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(TIFApp.getAppContext(), R.string.error_during_voting, Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
        });


        return convertView;
    }

    //  Thread t;
    public void checkConditions(final ViewHolder viewHolder, final VKApiPoll.Answer answer) {


        answers_anonymous_text_preview.setText(isAnonymous_preview + " " + poll.votes);
        answers_anonymous_text.setText(isAnonymous + " " + poll.votes);

        viewHolder.amount_of_answers.setText(String.valueOf(answer.votes));
        viewHolder.vote_text.setText(answer.text);
        viewHolder.bar.setMax(100);
        // viewHolder.bar.setProgress((int) answer.rate);

        if (VKSdk.isLoggedIn()) {
            viewHolder.bar.setProgress((int) answer.rate);
            if (poll.answer_id == 0) {
                changeDecision.setVisibility(View.INVISIBLE);

                viewHolder.bar.setVisibility(View.INVISIBLE);
                viewHolder.amount_of_answers.setVisibility(View.INVISIBLE);
                viewHolder.addVoteButton.setVisibility(View.VISIBLE);

            }
            if (poll.answer_id > 0) {
                changeDecision.setVisibility(View.VISIBLE);
                viewHolder.addVoteButton.setVisibility(View.GONE);
                viewHolder.bar.setVisibility(View.VISIBLE);

                viewHolder.amount_of_answers.setVisibility(View.VISIBLE);
            }

            if (answer.id == poll.answer_id) {
                viewHolder.vote_text.setTypeface(null, Typeface.BOLD);
                viewHolder.amount_of_answers.setTypeface(null, Typeface.BOLD);

                viewHolder.vote_text.setTextColor(Color.WHITE);
                viewHolder.amount_of_answers.setTextColor(Color.WHITE);
            } else {
                viewHolder.vote_text.setTypeface(null, Typeface.NORMAL);
                viewHolder.amount_of_answers.setTypeface(null, Typeface.NORMAL);
                viewHolder.amount_of_answers.setTextColor(textFadeOutColor);
                viewHolder.vote_text.setTextColor(textFadeOutColor);
            }


        } else {

            changeDecision.setVisibility(View.INVISIBLE);
            viewHolder.addVoteButton.setVisibility(View.GONE);
            viewHolder.bar.setVisibility(View.VISIBLE);


//            if (!viewHolder.wasAlreadyCalled) {
//                final ObjectAnimator animation = ObjectAnimator.ofInt(viewHolder.bar, "progress", (int) answer.rate);
//                animation.setDuration(1200); // 0.5 second
//                animation.setInterpolator(new DecelerateInterpolator());


//                try {
//                    t.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
            //   if (viewHolder.wasAlreadyCalled)
            viewHolder.bar.setProgress((int) answer.rate);


            viewHolder.amount_of_answers.setVisibility(View.VISIBLE);

        }
    }


    public static class ViewHolder {
        public final NumberProgressBar bar;
        public final TextView vote_text;
        public final TextView amount_of_answers;
        public final Button addVoteButton;
        public boolean wasAlreadyCalled;

        ViewHolder(View view) {
            this.wasAlreadyCalled = false;
            this.bar = (NumberProgressBar) view.findViewById(R.id.rate_bar);
            this.vote_text = (TextView) view.findViewById(R.id.variant_text);
            this.amount_of_answers = (TextView) view.findViewById(R.id.rate_amount_text);
            this.addVoteButton = ((Button) view.findViewById(R.id.add_your_vote_button));


        }


    }
}

