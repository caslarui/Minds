package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.minds.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import custom_class.File;
import custom_class.FileAdapter;


public class CoursePageList extends Fragment {
    private String          mFragmentName = "";
    private String          mLink = "";
    private List<File>      mFiles;

    private TextView        topMenuText;
    private RecyclerView    mRecycleView;
    private FileAdapter     mFileAdapter;
    private ProgressBar     mProgressBar;

    private FirebaseFirestore fireDatabase;


    public CoursePageList() { }

    public CoursePageList(String fragmentName, String link) {
        this.mFragmentName = fragmentName;
        this.mLink = link;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_course_page_list, container, false);

        initializeControllers(v);

        topMenuText.setText(mFragmentName);

        mFiles = new ArrayList<>();

        fireDatabase.collection(mLink).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        File file = documentSnapshot.toObject(File.class);
                        mFiles.add(file);
                    }
                    mFileAdapter = new FileAdapter(getContext(), mFiles);
                    mRecycleView.setAdapter(mFileAdapter);

                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        return v;
    }

    private void initializeControllers(View v) {
        topMenuText = (TextView) Objects.requireNonNull(getActivity()).findViewById(R.id.topMenu_text);
        mProgressBar = v.findViewById(R.id.loading_list);
        fireDatabase = FirebaseFirestore.getInstance();
        mRecycleView = v.findViewById(R.id.recycler_view);
        mRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecycleView.setHasFixedSize(true);
    }
}
