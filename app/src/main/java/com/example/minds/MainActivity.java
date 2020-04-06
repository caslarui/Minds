package com.example.minds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ImageButton     registerBtn;
    private ImageButton     loginBtn;
    private ImageButton     continueBtn;
    private FirebaseAuth    fireAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), CourseSelector.class));
            finish();
        }

        initializeControllers();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(v.getContext(), RegisterPage.class);
                v.getContext().startActivity(registerIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginWindow();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContinueWindow();
            }
        });
    }

    private void initializeControllers() {
        fireAuth = FirebaseAuth.getInstance();

        registerBtn = (ImageButton) findViewById(R.id.registerBtn);
        loginBtn = (ImageButton) findViewById(R.id.loginBtn);
        continueBtn = (ImageButton) findViewById(R.id.continueBtn);
    }


    private void showContinueWindow() {
        AlertDialog.Builder window = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View continueWindow = inflater.inflate(R.layout.continue_window, null);
        window.setView(continueWindow);

        Button          continueDialogBtn;
        CheckBox        checkBox;

        continueDialogBtn = (Button) continueWindow.findViewById(R.id.continueDialogBtn);
        checkBox = (CheckBox) continueWindow.findViewById(R.id.checkBox);

        window.show();
    }

    private void showLoginWindow() {
        AlertDialog.Builder window = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View loginWindow = inflater.inflate(R.layout.login_window, null);
        window.setView(loginWindow);

        Button                  loginDialogBtn;
        final TextInputLayout   email;
        final TextInputLayout   password;

        email = (TextInputLayout) loginWindow.findViewById(R.id.emailLoginDiaog);
        password = (TextInputLayout) loginWindow.findViewById(R.id.passwordLoginDialog);
        loginDialogBtn = (Button) loginWindow.findViewById(R.id.loginDialogBtn);

        loginDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!confirmInput(email, password)) {
                    return ;
                }
                try {
                    fireAuth.signInWithEmailAndPassword(email.getEditText().getText().toString(),
                            password.getEditText().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Logged in Successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                        startActivity(
                                                new Intent(getApplicationContext(),
                                                        CourseSelector.class)
                                        );
                                    } else {
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Login Failure : " +
                                                task.getException().getMessage(),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Login Failure : " +
                            e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        window.show();
    }

    public void startCoursePage(View view) {
        Intent continueIntent = new Intent(getApplicationContext(), CourseSelector.class);
        continueIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(continueIntent);
    }

    private boolean validateEmail(TextInputLayout email) {
        String emailInput = email.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            email.setError("Field can't be empty");
        } else {
            email.setError(null);
            return true;
        }
        return false;
    }
    private boolean validatePassword(TextInputLayout password) {
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

    public boolean confirmInput(TextInputLayout email, TextInputLayout password) {
        return (validateEmail(email) | validatePassword(password));
    }

}
