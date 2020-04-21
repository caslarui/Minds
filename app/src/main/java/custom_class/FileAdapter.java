package custom_class;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minds.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private Context mContext;
    private List<File> mFiles;
    private OnItemClickListener listener;

    public FileAdapter(final Context context, List<File> files) {
        this.mContext = context;
        this.mFiles = files;
        this.listener = new OnItemClickListener() {
            @Override
            public void onItemClick(File item) {
                openFileDescription(item);
            }
        };
    }

    private void openFileDescription(final File item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
        final AlertDialog window;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View fileDescription = inflater.inflate(R.layout.file_description_window, null);
        builder.setView(fileDescription);

        window = builder.create();
        window.show();

        final ImageView mFileImg = fileDescription.findViewById(R.id.file_img);
        final TextView mFileName = fileDescription.findViewById(R.id.file_name);
        final TextView mFileDate = fileDescription.findViewById(R.id.file_date);
        final TextView mFileAuthor = fileDescription.findViewById(R.id.file_author);
        final TextView mFileDesc = fileDescription.findViewById(R.id.file_desc);
        final Button downloadBtn = fileDescription.findViewById(R.id.download_Btn);
        final Button favoriteBtn = fileDescription.findViewById(R.id.favorites_btn);

        loadImage(item, mFileImg);
        loadText(item, mFileName, mFileDate, mFileAuthor, mFileDesc);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            favoriteBtn.setVisibility(View.INVISIBLE);
        } else {
            loadUser(new MyCallBack() {
                @Override
                public void onCallback(final User mUser) {
                    if (mUser.isAuthor(item.getmFileId())) {
                        favoriteBtn.setText("Remove File");
                        favoriteBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mUser.removeUpload(item.getmFileId());
                                        removeFileFromUserUploads(mUser, item.getmFileId());
                                        removeFileFromStorage(item);
                                        removeFileFromDataBase(item.getmFileId());
                                        Toast.makeText(mContext, "File Removed", Toast.LENGTH_SHORT).show();
                                        window.dismiss();
                                        removeFileFromAdapter(item);
                                    }
                                }
                        );
                    } else {
                        if (mUser.checkFavorite(item.getmFileId())) {
                            favoriteBtn.setText("Remove Favorite");
                            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    favoriteBtn.setText("Add to Favorites");
                                    mUser.removeFavorite(item.getmFileId());
                                    updateFavorites(mUser);
                                    Toast.makeText(mContext, "Removed From Favorites", Toast.LENGTH_SHORT).show();
                                    window.dismiss();
                                    removeFileFromAdapter(item);
                                }
                            });
                        } else {
                            favoriteBtn.setText("Add to Favorites");
                            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    favoriteBtn.setText("Remove Favorite");
                                    mUser.addFavorite(item.getmFileId());
                                    updateFavorites(mUser);
                                    Toast.makeText(mContext, "Added to Favorites", Toast.LENGTH_SHORT).show();
                                    window.dismiss();
                                }
                            });
                        }
                    }
                }
            });
        }

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    // this will request for permission when permission is not true
                }else{
                    // Download code here
                    downloadFileFrom(item);
                }
            }
        });

    }

    private void downloadFileFrom(final File item) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getmLink()));
        String fileName = item.getmTitle() + "." + item.getmExtension();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(fileName);
        request.setDescription("Download.....");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    private void removeFileFromDataBase(final String getmFileId) {
        FirebaseFirestore
                .getInstance()
                .document(getmFileId)
                .delete()
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(Constants.TAG, "File ["
                                        + getmFileId + "] deleted from database");
                            }
                        }
                );
    }

    private void removeFileFromStorage(final File item) {
        String fileUrl = "";
        String[] words = item.getmFileId().split("/");
        for(String word : words) {
            if (word.equals(words[words.length - 1]))
                break;
            fileUrl += word + "/";
        }
        fileUrl += item.getmTitle();
        final String finalFileUrl = fileUrl + "." + item.getmExtension();
        System.out.println(finalFileUrl);
        try {
            FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(finalFileUrl)
                    .delete()
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(Constants.TAG, "File ["
                                            + finalFileUrl + "] deleted from storage");
                                }
                            }
                    );
        } catch (Exception e){
            Log.d(Constants.TAG, e.getMessage() + "\nFile to delete : " + finalFileUrl);
        }
    }

    private void removeFileFromUserUploads(User mUser, final String getmFileId) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection(Constants.DB_USERS)
                .document(uid)
                .update("mUploads", mUser.getmUploads())
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(Constants.TAG, "User's [" + uid +  "] Upload ["
                                + getmFileId + "] deleted");
                            }
                        }
                );
    }

    private void removeFileFromAdapter(File item) {
        mFiles.remove(item);
        notifyItemRemoved(mFiles.indexOf(item));
    }

    private void updateFavorites(User mUser) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection(Constants.DB_USERS)
                .document(uid)
                .update("mFavorites", mUser.getmFavorites())
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(Constants.TAG, "User's [" + uid +  "]Favorites Updated");
                            }
                        }
                );
    }

    public void loadUser(final MyCallBack myCallBack) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection(Constants.DB_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    User currentUser = documentSnapshot.toObject(User.class);
                                    myCallBack.onCallback(currentUser);
                                }
                            }
                        });
    }


    private void removeFile(File item) {

    }

    private void loadText(File item, TextView mFileName, TextView mFileDate, final TextView mFileAuthor, TextView mFileDesc) {
        mFileName.setText(item.getmTitle());
        mFileDate.setText(item.getmCreationDate());
        mFileDesc.setText(item.getmDescription());

        FirebaseFirestore.getInstance()
                .collection(Constants.DB_USERS)
                .document(item.getmAuthor()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String author = "Author name";
                        if (documentSnapshot.exists()) {
                            author = documentSnapshot.get("mFName") + " " + documentSnapshot.get("mSName");
                        }
                        mFileAuthor.setText(author);
                    }
                });

    }

    private void loadImage(File item, final ImageView mFileImg) {
        String icon = getIconByExtension(item);
        FirebaseStorage.getInstance()
                .getReference("Files")
                .child(icon)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.with(mContext)
                            .load(uri)
                            .fit()
                            .centerCrop()
                            .into(mFileImg);
                } catch (Exception e) {
                    Toast.makeText(mContext, "Fail loads Image : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileViewHolder holder, int position) {
        File fileCurrent = mFiles.get(position);
        holder.mFileName.setText(fileCurrent.getmTitle());
        holder.bind(mFiles.get(position), listener);
        try {
            String icon = getIconByExtension(fileCurrent);
            FirebaseStorage.getInstance()
                    .getReference("Files")
                    .child(icon)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        Picasso.with(mContext)
                                .load(uri)
                                .fit()
                                .centerCrop()
                                .into(holder.mFileImage);
                        holder.mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(mContext, "Fail loads Image : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.d(Constants.TAG, "Exception : " + e.getMessage());
        }
    }

    private String getIconByExtension(File fileCurrent) {
        String icon = "";

        switch (fileCurrent.getmExtension()) {
            case "pdf":
                icon = "pdf_file.png";
                break;
            case "txt":
                icon = "txt_file.png";
                break;
            default:
                icon = "img_file.png";
        }

        return icon;
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public interface MyCallBack {
        void onCallback(User mUser);
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        public TextView mFileName;
        public ImageView mFileImage;
        public ProgressBar mProgressBar;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.file_item_loading);
            mFileName = itemView.findViewById(R.id.card_file_name);
            mFileImage = itemView.findViewById(R.id.card_file_img);

        }

        public void bind(final File item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(File item);
    }
}
