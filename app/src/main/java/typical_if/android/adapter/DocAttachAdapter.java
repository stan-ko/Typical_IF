package typical_if.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vk.sdk.api.model.VKApiDocument;

import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.ItemDataSetter;
import typical_if.android.R;

/**
 * Created by admin on 19.08.2014.
 */
public class DocAttachAdapter extends BaseAdapter {

    private ArrayList<VKApiDocument> docs;
    private LayoutInflater layoutInflater;

    public DocAttachAdapter(ArrayList<VKApiDocument> docs, LayoutInflater inflater) {
        this.docs = docs;
        this.layoutInflater = inflater;
    }


    @Override
    public int getCount() {
        return docs.size();
    }

    @Override
    public Object getItem(int position) {
        return docs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return docs.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.document_container, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VKApiDocument doc = docs.get(position);

        if (doc.isGif()) {
            ImageLoader.getInstance().displayImage(doc.photo_100, viewHolder.img_doc_attach);
            viewHolder.txt_doc_attach_size.setText(Constants.DOC_TYPE_ANIMATION + " " + ItemDataSetter.readableFileSize(doc.size));
        } else if(doc.isImage()) {
            ImageLoader.getInstance().displayImage(doc.photo_100, viewHolder.img_doc_attach);
            viewHolder.txt_doc_attach_size.setText(Constants.DOC_TYPE_IMAGE + " " + ItemDataSetter.readableFileSize(doc.size));
        } else {
            viewHolder.img_doc_attach.setImageDrawable(Constants.RESOURCES.getDrawable(android.R.drawable.ic_menu_save));
            viewHolder.img_doc_attach.setLayoutParams(new RelativeLayout.LayoutParams(ItemDataSetter.setInDp(50), ItemDataSetter.setInDp(50)));

            RelativeLayout.LayoutParams paramsForTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsForTitle.setMargins(ItemDataSetter.setInDp(55), 0, 0, 0);
            viewHolder.txt_doc_attach_title.setLayoutParams(paramsForTitle);

            RelativeLayout.LayoutParams paramsForSize = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsForSize.setMargins(ItemDataSetter.setInDp(55), ItemDataSetter.setInDp(20), 0, 0);
            viewHolder.txt_doc_attach_size.setLayoutParams(paramsForSize);
            viewHolder.txt_doc_attach_size.setText(Constants.DOC_TYPE_DOCUMENT + " " + ItemDataSetter.readableFileSize(doc.size));
        }

        viewHolder.txt_doc_attach_title.setText(doc.title);

        return convertView;
    }

    public static class ViewHolder {
        public final TextView txt_doc_attach_size;
        public final TextView txt_doc_attach_title;
        public final ImageView img_doc_attach;

        public ViewHolder(View convertView) {
            this.txt_doc_attach_size = (TextView) convertView.findViewById(R.id.txt_document_size);
            this.txt_doc_attach_title = (TextView) convertView.findViewById(R.id.txt_document_title);
            this.img_doc_attach = (ImageView) convertView.findViewById(R.id.img_document);
        }
    }
}
