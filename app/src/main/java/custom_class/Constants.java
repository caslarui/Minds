package custom_class;

public final class Constants {

     /*
        Constants for custom_files.File uploading, adding to favorites etc.
     */
    public static final int FILE_ALREADY_EXISTS = 22;

    // Constants for adding file to Uploads
    public static final int FILE_TO_UPLOADS_FAILURE = 20;
    public static final int FILE_TO_UPLOADS_SUCCESS = 21;

    // Constants for adding file to Favorites
    public static final int FILE_TO_FAVORITES_FAILURE = 120;
    static final int FILE_TO_FAVORITES_SUCCESS = 121;

    public static final String TAG = "TAG";

    private Constants(){
        throw new AssertionError();
    }
}
