package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.minds.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import custom_class.Constants;
import custom_class.File;
import custom_class.FileAdapter;
import custom_class.User;


public class MyDocsList extends Fragment {

    private String              mFragmentName = "My Docs";

    private TextView            topMenuText;
    private RecyclerView        mRecycleView;
    private FileAdapter         mFileAdapter;
    private ProgressBar         mProgressBar;

    private List<File>          mFiles;
    private List<String>        mFileLinks;

    private FirebaseFirestore   fireDatabase;
    private FirebaseAuth        fireAuth;



    public MyDocsList() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_docs_list, container, false);

        initializeControllers(v);

        mFiles = new ArrayList<>();
        mFileLinks = new ArrayList<>();

        getFileLinks();

        return v;
    }

    private void getFileLinks() {
        fireDatabase.collection(Constants.DB_USERS)
                .document(fireAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User mUser = documentSnapshot.toObject(User.class);
                        if (mUser.getmFavorites() != null) {
                            if (!mUser.getmFavorites().isEmpty()) {
                                Log.d(Constants.TAG, "User Has Favorites");
                                mFileLinks.addAll(mUser.getmFavorites());
                                Log.d(Constants.TAG, "mFileLinks Size : "+ mFileLinks.size());
                            } else {
                                Log.d(Constants.TAG, "User has no favorites");
                            }
                        }
                        if (!mUser.getmUploads().isEmpty()) {
                            mFileLinks.addAll(mUser.getmUploads());
                            Log.d(Constants.TAG, "\n Uploads : " + mFileLinks.toString());
                        } else {
                            Log.d(Constants.TAG, "User has no uploads");
                        }
                        loadFiles(mFileLinks);
                    }
                } else {
                    Log.d(Constants.TAG, "Unable to find the user : " + fireAuth.getCurrentUser().getUid());
                }
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loadFiles(final List<String> mFileLinks) {

        for(final String link : mFileLinks) {
            fireDatabase.document(link).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            File file = documentSnapshot.toObject(File.class);
                            if (file != null) {
                                mFiles.add(file);
                                Log.d(Constants.TAG, file.toString());
                            }
                        }
                    }
                    if (mFiles.size() == mFileLinks.size()) {
                        Log.d(Constants.TAG, "My Docs : " + mFiles.toString());
                        mFileAdapter = new FileAdapter(getContext(), mFiles);
                        mRecycleView.setAdapter(mFileAdapter);
                    }
                }
            });
        }
    }

    private void initializeControllers(View v) {
        topMenuText = (TextView) Objects.requireNonNull(getActivity()).findViewById(R.id.topMenu_text);
        topMenuText.setText(mFragmentName);
        mRecycleView = v.findViewById(R.id.recycler_view);
        mProgressBar = v.findViewById(R.id.my_docs_loading);
        mRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecycleView.setHasFixedSize(true);
        fireDatabase = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
    }
}
