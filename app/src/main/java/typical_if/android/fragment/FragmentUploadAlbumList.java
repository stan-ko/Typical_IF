package typical_if.android.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;



import java.util.ArrayList;

import typical_if.android.R;
import typical_if.android.activity.MainActivity;
import typical_if.android.adapter.AlbumUploadAdapter;

/**
 * Created by LJ on 24.07.2014.
 */
public class FragmentUploadAlbumList extends Fragment {
    private int count;
    private String[] arrPath;


    int which;
    long gid;

    public static FragmentUploadAlbumList newInstance(long vkGroupId, int which) {
        FragmentUploadAlbumList fragment = new FragmentUploadAlbumList();
        Bundle args = new Bundle();

        fragment.which = which;
        fragment.gid = vkGroupId;

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentUploadAlbumList() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        ((MainActivity)getActivity()).getSupportActionBar().hide();
        

        final View rootView = inflater.inflate(R.layout.fragment_album_upload_list, container, false);
        setRetainInstance(true);

        final ArrayList <String> albumtitles =  new ArrayList();
        String secondTemp = "";
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = getActivity().managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];

        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i]= imagecursor.getString(dataColumnIndex);
            String [] temp = arrPath[i].split("/");
            if (!secondTemp.contains(temp[temp.length-2])){
            albumtitles.add(temp[temp.length-2]);
                 secondTemp += temp[temp.length-2];
            }

        }

        ListView listofalbums = (ListView) rootView.findViewById(R.id.album_upload_listView);
        listofalbums.setAdapter(new AlbumUploadAdapter(albumtitles, inflater, arrPath));
        listofalbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                fragment = FragmentUploadPhotoList.newInstance(String.valueOf(albumtitles.get(position)), arrPath, gid,which);
                ((MainActivity)getActivity()).addFragment(fragment);
            }
        });

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        
        super.onAttach(activity);
    }
}
