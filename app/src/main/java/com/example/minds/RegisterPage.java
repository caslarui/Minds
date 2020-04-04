package com.example.minds;

import custom_class.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterPage extends AppCompatActivity {

    private TextInputLayout         email;
    private TextInputLayout         fName;
    private TextInputLayout         sName;
    private TextInputLayout         password;
    private Toolbar                 topMenu;
    private TextView                topMenuText;
    private Button                  registerBtn;
    private FirebaseAuth            fireAuth;
    private FirebaseFirestore       fireStore;
    private BottomNavigationView    bottomNavigationView;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        // Initialize The Buttons, Toolbars and staff like that
        initializeControllers();

        // Top Menu Text Setup
        setSupportActionBar(topMenu);
        topMenuText.setText("Register");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!confirmInput())
                    return;
                final User newUser = new User(email.getEditText().getText().toString().trim(),
                        fName.getEditText().getText().toString().trim(),
                        sName.getEditText().getText().toString().trim(), password
                        .getEditText().getText().toString());
                try {
                    fireAuth.createUserWithEmailAndPassword(newUser.getmEmail(),
                            newUser.getmPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        RegisterPage.this,
                                        "User Created",
                                        Toast.LENGTH_SHORT
                                ).show();
                                newUser.setmUID(fireAuth.getUid());
                                DocumentReference documentReference = fireStore.collection("users").document(newUser.getmUID());
                                documentReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(Constants.TAG, "User Profile is Created for : "+ newUser.getmUID());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), CourseSelector.class));
                            } else {
                                Toast.makeText(
                                        RegisterPage.this,
                                        "User not Created : " +
                                        task.getException().getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(
                            RegisterPage.this,
                            "User not Created : " +
                            e.getMessage(), Toast.LENGTH_LONG
                    ).show();
                }

            }
        });
    }


    private void initializeControllers() {
        topMenu = (Toolbar) findViewById(R.id.topMenu);
        topMenuText = (TextView) findViewById(R.id.topMenu_text);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        email = (TextInputLayout) findViewById(R.id.registerEmail);
        fName = (TextInputLayout) findViewById(R.id.registerFName);
        sName = (TextInputLayout) findViewById(R.id.registerSName);
        password = (TextInputLayout) findViewById(R.id.registerPassword);

        registerBtn = (Button) findViewById(R.id.sendRegister);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }

    private boolean validateEmail() {
        String emailInput = email.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            email.setError("Field can't be empty");
        } else {
            email.setError(null);
            return true;
        }
        return false;
    }

    private boolean validateFName() {
        String fNameInput = fName.getEditText().getText().toString().trim();

        if (fNameInput.isEmpty()) {
            fName.setError("Field can't be empty");
            return false;
        } else {
            fName.setError(null);
            return true;
        }
    }

    private boolean validateSName() {
        String sNameInput = sName.getEditText().getText().toString().trim();

        if (sNameInput.isEmpty()) {
            sName.setError("Field can't be empty");
            return false;
        } else {
            sName.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String inputPassword = password.getEditText().getText().toString().trim();

        if (inputPassword.isEmpty()) {
            password.setError("Field can't be empty");
        } else if (inputPassword.length() < 6){
            password.setError("Password size < 6");
            return false;
        } else if (inputPassword.length() > 12) {
            password.setError("Password size > 12");
            return false;
        } else {
            password.setError(null);
            return true;
        }
        return false;
    }

    public boolean confirmInput() {
        return (validateEmail() | validateFName() | validateSName() | validatePassword());
    }

}
