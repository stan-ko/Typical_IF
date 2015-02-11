package typical_if.android.util;

import android.util.Log;

import com.vk.sdk.api.model.VKApiPoll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yurij on 05.02.2015.
 */
public class VKPoll extends VKApiPoll {
    /**
     * Poll ID to get information about it using polls.getById method;
     */
    public int id;

    /**
     * ID of the user or community that owns this poll.
     */
    public int owner_id;

    /**
     * Date (in Unix time) the poll was created.
     */
    public long created;

    /**
     * Question in the poll.
     */
    public String question;

    /**
     * The total number of users answered.
     */
    public int votes;

    /**
     * Response ID of the current user(if the current user has not yet posted in this poll, it contains 0)
     */
    public int answer_id;

    public int anonymous;
    /**
     * Array of answers for this question.
     */
    public ArrayList<VKApiPoll.Answer> answers;


    public VKPoll parse (JSONObject source1) {

        JSONObject source = source1.optJSONObject("response");
        id = source.optInt("id");
        owner_id = source.optInt("owner_id");
        created = source.optLong("created");
        question = source.optString("question");
        votes = source.optInt("votes");
        answer_id = source.optInt("answer_id");
        answers = new ArrayList<VKApiPoll.Answer>();

        JSONArray jsonAnswers = source.optJSONArray("answers");
        Log.d(jsonAnswers.length()+" ", "ANSWER_INDEX");
        for (int i= 0 ; i<jsonAnswers.length();i++){
            try {
                Log.d(i+" ", "ANSWER_INDEX");
                VKApiPoll.Answer answer = new VKApiPoll.Answer().parse((JSONObject)jsonAnswers.get(i));
                answers.add(answer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        anonymous = source.optInt("anonymous");

        return this;
    }



}
