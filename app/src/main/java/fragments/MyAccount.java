package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minds.MainActivity;
import com.example.minds.R;
import com.example.minds.UploadFilePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MyAccount extends Fragment {

    private final String mFragmentName = "My Account";

    private TextView            userName;
    private TextView            topMenuText;
    private FirebaseAuth        fireAuth;
    private FirebaseFirestore   fireStore;
    private Button              myDocs;
    private Button              uploads;
    private Button              settings;
    private Button              logout;
    private LinearLayout        btn_layout;



    public MyAccount() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        initializeControllers(view);

        topMenuText.setText(mFragmentName);

        // If the user is not authenticated hide the Button for Upload, My Docs and Settings
        if (fireAuth.getCurrentUser() == null) {
            myDocs.setVisibility(View.GONE);
            uploads.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);
            logout.setText("Login");
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            });
        }

        // If the User is authenticated then show the user name
        getUserName();

        uploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent uploadPage = new Intent(v.getContext(), UploadFilePage.class);
                v.getContext().startActivity(uploadPage);
            }
        });

        return view;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void initializeControllers(View v) {
        userName = (TextView) v.findViewById(R.id.userName);
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        myDocs = (Button) v.findViewById(R.id.MyDocsBtn);
        uploads = (Button) v.findViewById(R.id.account_UploadBtn);
        settings = (Button) v.findViewById(R.id.account_SettingsBtn);
        logout = (Button) v.findViewById(R.id.account_LogOut);
        btn_layout = (LinearLayout) v.findViewById(R.id.layout_btn);
        topMenuText = (TextView) Objects.requireNonNull(getActivity()).findViewById(R.id.topMenu_text);
    }

    private void getUserName() {
        try {
            String uid = fireAuth.getUid();

            if (uid != null) {
                fireStore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.get("mFName") + " " + documentSnapshot.get("mSName");
                            userName.setText(name);
                        } else {
                            Toast.makeText(getContext(), "Document Does not Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Fail : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Fail : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
