package typical_if.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Yurij on 23.01.2015.
 */
public class ActionBarArrayAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private CharSequence mTitle;
    public static boolean isJoined;




    public ActionBarArrayAdapter(Context context, String[] items,CharSequence mTitle) {


        super(context, 0, items);


        mContext = context;
        this.mTitle=mTitle;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    android.R.layout.simple_spinner_item, null);
        }

        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText(mTitle);
        return convertView;
    }
}
