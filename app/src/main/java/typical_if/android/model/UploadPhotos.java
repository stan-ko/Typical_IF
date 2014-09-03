package typical_if.android.model;

/**
 * Created by LJ on 27.07.2014.
 */
public class UploadPhotos {
    public String photoSrc = null;
    public boolean isChecked = false;

    public UploadPhotos(String photoSrc) {
        this.photoSrc = photoSrc;
    }

    public String getPhotoSrc() {
        return this.photoSrc;
    }
}
