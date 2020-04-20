package custom_class;

import android.content.Context;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minds.R;
import com.google.android.gms.tasks.OnFailureListener;
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
    private User mCurrentUser;
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
        AlertDialog.Builder window = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View fileDescription = inflater.inflate(R.layout.file_description_window, null);
        window.setView(fileDescription);

        final ImageView mFileImg = fileDescription.findViewById(R.id.file_img);
        final TextView  mFileName = fileDescription.findViewById(R.id.file_name);
        final TextView  mFileDate = fileDescription.findViewById(R.id.file_date);
        final TextView  mFileAuthor = fileDescription.findViewById(R.id.file_author);
        final TextView  mFileDesc = fileDescription.findViewById(R.id.file_desc);
        Button downloadBtn = fileDescription.findViewById(R.id.download_Btn);
        final Button favoriteBtn = fileDescription.findViewById(R.id.favorites_btn);

        loadImage(item, mFileImg);
        loadText(item, mFileName, mFileDate, mFileAuthor, mFileDesc);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            favoriteBtn.setVisibility(View.INVISIBLE);
        } else {
            loadUser();
            if (mCurrentUser != null) {
                Log.d("TAG", mCurrentUser.getmFavorites().toString());
            } else {
                    Log.d("TAG", "\n\nNULL USER\n\n");
            }
//            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(item.getmAuthor())) {
//                favoriteBtn.setText("Remove File");
//                favoriteBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        removeFile(item);
//                    }
//                });
//            } else {
//                Log.d("TAG", "favorites size : " + favorites.size());
//                if (favorites.contains(item.getmFileId())) {
//                    Log.d("TAG", "\n\nCEVA\n\n");
//                    favoriteBtn.setText("Remove Favorite");
//                    favoriteBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            removeFromFavorites(item);
//                            favoriteBtn.setText("Add To Favorites");
//                        }
//                    });
//                } else {
//                    favoriteBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d(Constants.TAG, "File is not in favorites\n");
//                            addToFavorites(item);
//                            favoriteBtn.setText("Remove Favorite");
//                        }
//                    });
//                }
//            }
        }


        window.show();
    }

    private void loadUser() {
        final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection(Constants.DB_USERS)
                .document(uID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                mCurrentUser = documentSnapshot.toObject(User.class);
                            } else {
                                Log.d(Constants.TAG, "Failed to update User, user == null");
                            }
                        }
                    }
                });
    }

    private void removeFromFavorites(final File item) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection(Constants.DB_USERS)
                .document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.removeFavorite(item.getmFileId());
                            FirebaseFirestore.getInstance().collection(Constants.DB_USERS)
                                    .document(uid).update("mFavorites", user.getmFavorites());
                        } else {
                            Log.d(Constants.TAG, "User == null \n");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Constants.TAG, e.getMessage() + " \n");
            }
        });
    }

    private void addToFavorites(final File item) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection(Constants.DB_USERS)
                .document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.addFavorite(item.getmFileId());
                            FirebaseFirestore.getInstance().collection(Constants.DB_USERS)
                                    .document(uid).update("mFavorites", user.getmFavorites());
                        } else {
                            Log.d(Constants.TAG, "User == null \n");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Constants.TAG, e.getMessage() + " \n");
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
