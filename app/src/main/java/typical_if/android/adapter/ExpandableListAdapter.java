package typical_if.android.adapter;

/**
 * Created by admin on 14.07.2014.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import typical_if.android.R;
import typical_if.android.view.AnimatedExpandableListView;

public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final List<GroupObject> _listDataHeader;
    private final SparseArray<List<String>> _listDataChild;
    private final LayoutInflater inflater;


    public ExpandableListAdapter(Context context, List<GroupObject> listDataHeader, SparseArray<List<String>> listChildData) {
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
        if (this._listDataChild.get(groupPosition) == null) {
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
        GroupViewHolder groupViewHolder;
        GroupObject header = (GroupObject) getGroup(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_drawer_group_item, null);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
           groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        if (groupPosition == 4) {
            groupViewHolder.groupTitle.setTextColor(convertView.getResources().getColor(R.color.stantsiya_bg));
        } else {
            groupViewHolder.groupTitle.setTextColor(Color.WHITE);
        }

        groupViewHolder.groupTitle.setText(header.title);
        groupViewHolder.imgGroupItem.setImageResource(header.imgId);

        return convertView;
    }

    public static class GroupObject {
        public final String title;
        public final int imgId;

        public GroupObject(String title, int imgId) {
            this.title = title;
            this.imgId = imgId;
        }
    }

    private static class GroupViewHolder {
        public final ImageView imgGroupItem;
        public final TextView groupTitle;

        GroupViewHolder(View view) {
            this.imgGroupItem = (ImageView) view.findViewById(R.id.imgGroupItem);
            this.groupTitle = (TextView) view.findViewById(R.id.groupItem);
        }
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