package typical_if.android.model;

/**
 * Created by LJ on 27.07.2014.
 */
public class UploadPhotos {
    public String photosrc = null;
    public boolean ischecked = false;

    public UploadPhotos(String photosrc) {
        this.photosrc = photosrc;
    }

    public String getPhotoSrc() {
        return this.photosrc;
    }
}
