package typical_if.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import typical_if.android.Constants;
import typical_if.android.R;


/**
 * Created by admin on 14.07.2014.
 */
public class LicenseFragment extends Fragment{

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public static LicenseFragment newInstance() {
        LicenseFragment fragment = new LicenseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.license_view_layout, container, false);
        TextView license = ((TextView) rootView.findViewById(R.id.license_text));
        license.setText(getStringFromAssetFile(getActivity()));
        setRetainInstance(true);



        return rootView;

    }

    private String getStringFromAssetFile(Activity activity)
    {
        String text = "licenses.xml";
        byte[] buffer = null;
        InputStream is;
        try {
            is = activity.getAssets().open(text);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str_data = new String(buffer);
        return str_data;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
