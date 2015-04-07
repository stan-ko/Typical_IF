package typical_if.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import typical_if.android.R;

/**
 * Created by gigamole on 12.02.15.
 */
public class DrawerListViewAdapter extends BaseAdapter{
    private final List<GroupObject> _listDataHeader;
    private final LayoutInflater inflater;


    public DrawerListViewAdapter(Context context, List<GroupObject> listDataHeader) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this._listDataHeader = listDataHeader;
    }

    @Override
    public int getCount() {
        return this._listDataHeader.size();
    }

    @Override
    public Object getItem(int position) {
        return this._listDataHeader.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int groupPosition, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        GroupObject header = (GroupObject) getItem(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_drawer_group_item, null);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        if (groupPosition == 4) {
            groupViewHolder.groupTitle.setTextColor(convertView.getResources().getColor(R.color.ST_BG_COLOR));
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

}
