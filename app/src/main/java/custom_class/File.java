package custom_class;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
public class File {
    // Title of custom_files.File
    private String mTitle;
    // Description of custom_files.File
    private String mDescription;
    // Reference Link where the file is saved in FireBase.FireStore
    private String mLink;
    // Date of Creation
    private SimpleDateFormat mCreationDate;
    // Reference Link to Author
    private String mAuthor;

    public File() {
        mTitle = mDescription = mLink = mAuthor = null;
        mCreationDate = null;
    }

    @SuppressLint("SimpleDateFormat")
    public File(String mTitle, String mDescription, String mLink, String mAuthor) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mLink = mLink;
        this.mAuthor = mAuthor;
        this.mCreationDate = new SimpleDateFormat("dd-MMMMM-yyyy");
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

    public SimpleDateFormat getmCreationDate() {
        return mCreationDate;
    }

    public String getmAuthor() {
        return mAuthor;
    }
}
