package custom_class;

import java.util.List;

public class User {
    // custom_files.User Email
    private String mEmail;
    // custom_files.User First Name
    private String mFName;
    // custom_files.User Second Name
    private String mSName;
    // custom_files.User UID
    private String mUID;
    // custom_files.User Password
    private String mPassword;
    // This is the List of Files uploaded by the current custom_files.User
    private List<File> mUploads;
    // This is the List of Files marked as "Add to favorites" by the current custom_files.User
    private List<File> mFavorites;

    public User() {
        mEmail = mFName = mSName = mPassword = mUID = null;
        mUploads = mFavorites = null;
    }

    public User(String mEmail, String mFName, String mSName, String mPassword) {
        this.mEmail = mEmail;
        this.mFName = mFName;
        this.mSName = mSName;
        this.mPassword = mPassword;
        mUploads = mFavorites = null;
    }

    public int addUpload(File toUpload) {
        try {
            mUploads.add(toUpload);
        } catch (ArrayStoreException e) {
            System.out.println("Failed to add custom_files.File < " + toUpload.getmTitle() + " > to " +
                    "uploads due to error :" + e.getMessage());
            return Constants.FILE_TO_UPLOADS_FAILURE;
        }
        return Constants.FILE_TO_UPLOADS_SUCCESS;
    }

    public String getmUID() {
        return mUID;
    }

    public int addFavorite(File toFavorites) {
        if(mFavorites.contains(toFavorites) || mUploads.contains(toFavorites)) {
            return Constants.FILE_ALREADY_EXISTS;
        } else {
            try {
                mFavorites.add(toFavorites);
            } catch (ArrayStoreException e) {
                System.out.println("Failed to add custom_files.File < " + toFavorites.getmTitle() + " > to " +
                        "favorites due to error :" + e.getMessage());
                return Constants.FILE_TO_FAVORITES_FAILURE;
            }
        }
        return Constants.FILE_TO_FAVORITES_SUCCESS;
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    public String getmPassword() {
        return mPassword;
    }

    public String getmFName() {
        return mFName;
    }

    public String getmSName() {
        return mSName;
    }

    public List<File> getmUploads() {
        return mUploads;
    }

    public List<File> getmFavorites() {
        return mFavorites;
    }

    public String getmEmail() {
        return mEmail;
    }
}
