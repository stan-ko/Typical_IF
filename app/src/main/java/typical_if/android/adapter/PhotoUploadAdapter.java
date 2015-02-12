package typical_if.android.adapter;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import typical_if.android.Constants;
import typical_if.android.R;
import typical_if.android.fragment.FragmentFullScreenViewFromPhone;
import typical_if.android.fragment.FragmentUploadPhotoList;
import typical_if.android.model.UploadPhotos;

;

/**
 * Created by LJ on 25.07.2014.
 */
public class PhotoUploadAdapter extends BaseAdapter {

    FragmentUploadPhotoList fragmentUploadPhotoList;
    String titlename;
    LayoutInflater layoutInflater;
    ArrayList<UploadPhotos> uploadphotolist;
    android.support.v4.app.FragmentManager manager;
    int which;

    public PhotoUploadAdapter(FragmentUploadPhotoList fragmentUploadPhotoList, String titlename, LayoutInflater inflater, ArrayList<UploadPhotos> uploadphotolist, FragmentManager fragmentManager, int which) {
        this.fragmentUploadPhotoList = fragmentUploadPhotoList;
        this.titlename = titlename;
        this.layoutInflater = inflater;
        this.uploadphotolist = uploadphotolist;
        this.manager = fragmentManager;
        this.which = which;
    }


    @Override
    public int getCount() {
        return uploadphotolist.size();
    }

    @Override
    public Object getItem(int position) {
        return uploadphotolist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_add_photo_item, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        File file = new File(uploadphotolist.get(position).photoSrc);
        ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), viewHolder.photo);

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uploadphotolist.get(position).isChecked = isChecked;

                if (which == 1) {
                    if (isChecked) {
                        viewHolder.background.setVisibility(View.VISIBLE);
                        viewHolder.checkbox.setChecked(true);
                    } else {
                        viewHolder.background.setVisibility(View.INVISIBLE);
                        viewHolder.checkbox.setChecked(false);
                    }
                } else {
                    if (isChecked) {
                        viewHolder.background.setVisibility(View.VISIBLE);
                        Constants.tempCurrentPhotoAttachCounter++;
                    } else {
                        viewHolder.background.setVisibility(View.INVISIBLE);
                        Constants.tempCurrentPhotoAttachCounter--;
                    }

                    viewHolder.checkbox.setChecked(isChecked);

                    FragmentUploadPhotoList.refreshCheckBoxes();
                }

                for (int i = 0; i < getCount(); i++) {
                    if(((UploadPhotos) getItem(i)).isChecked) {
                        fragmentUploadPhotoList.setFabVisibility(View.VISIBLE);
                        break;
                    } else {
                        fragmentUploadPhotoList.setFabVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        viewHolder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentFullScreenViewFromPhone fragmentFullScreenViewFromPhone = new FragmentFullScreenViewFromPhone().newInstance(position, uploadphotolist);
                manager.beginTransaction().add(R.id.container, fragmentFullScreenViewFromPhone).addToBackStack(null).commit();
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public final ImageView photo;
        public CheckBox checkbox;
        public final ImageView background;

        public ViewHolder(View convertView) {
            this.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox_for_upload);
            this.photo = (ImageView) convertView.findViewById(R.id.thumb_photo_from_sd);
            this.background = (ImageView) convertView.findViewById(R.id.background_check);
        }
    }
}
