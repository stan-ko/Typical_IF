package typical_if.android.adapter;

/**
 * Created by admin on 14.07.2014.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vk.sdk.VKSdk;

import java.util.List;

import typical_if.android.view.AnimatedExpandableListView;
import typical_if.android.R;

public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final List<String> _listDataHeader;
    private final SparseArray<List<String>> _listDataChild;
    private final LayoutInflater inflater;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, SparseArray<List<String>> listChildData) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(groupPosition).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_drawer_child_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.childItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        if(this._listDataChild.get(groupPosition) == null){
            return 0;
        }
        return this._listDataChild.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_drawer_group_item, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.groupItem);
        lblListHeader.setTypeface(null, Typeface.BOLD);

        if(getGroupCount()-1 == groupPosition){
            if (VKSdk.wakeUpSession() && VKSdk.isLoggedIn()){
                lblListHeader.setText(R.string.title_logout);
            }else{
                lblListHeader.setText(R.string.title_login);
            }
        }else{
            lblListHeader.setText(headerTitle);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}