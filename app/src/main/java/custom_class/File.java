package custom_class;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class File {
    // Title of custom_files.File
    private String mTitle;
    // Description of custom_files.File
    private String mDescription;
    // Reference Link where the file is saved in FireBase.FireStore
    private String mLink;
    // Id of the file in Database
    private String mFileId;
    // Date of Creation
    private String mCreationDate;
    // Reference Link to Author
    private String mAuthor;
    // Extension of file
    private String mExtension;

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmFileId(String mFileId) {
        this.mFileId = mFileId;
    }

    public void setmCreationDate(String mCreationDate) {
        this.mCreationDate = mCreationDate;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmExtension() {
        return mExtension;
    }

    public void setmExtension(String mExtension) {
        this.mExtension = mExtension;
    }

    public File() {
        mTitle = mDescription = mLink = mExtension = mFileId= mAuthor = "";
        mCreationDate = "";
    }

    @SuppressLint("SimpleDateFormat")
    public File(String mTitle, String mDescription, String mLink, String mAuthor) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mLink = mLink;
        this.mAuthor = mAuthor;
        this.mFileId = "";
        SimpleDateFormat dateFormat =  new SimpleDateFormat("dd MMMM yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date now = Calendar.getInstance().getTime();
        this.mCreationDate = dateFormat.format(now);
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmLink() {
        return mLink;
    }

    public String getmCreationDate() {
        return mCreationDate;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmFileId() {
        return mFileId;
    }

    public void setmLink(String mLink) {
        this.mLink = mLink;
    }

    @Override
    public String toString() {
        return "File{" +
                "mTitle='" + mTitle + '\'' +
                ", mExtension='" + mExtension + '\'' +
                '}';
    }
}
