package com.example.minds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import custom_class.Constants;
import custom_class.Year;


public class UploadFilePage extends AppCompatActivity {

    private String                  uploadLink;
    private String                  mYear = "";
    private String                  mCourse;
    private String[]                mCourses;

    private TextView                topMenuText;
    private TextInputLayout         mFileName;
    private EditText                mFileDesc;
    private Spinner                 mFileYear;
    private Spinner                 mFileCourse;
    private Button                  uploadBtn;
    private ImageButton             chooseFileBtn;

    private FirebaseFirestore       fireDatabase;
    private FirebaseStorage         fireStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file_page);

        initializeControllers();

        topMenuText.setText("Upload File");

        mFileYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter;
                switch (position) {
                    case 0:
                        mYear = Year.YEAR_1.getYear();
                        mCourses = getResources().getStringArray(R.array.Year1_list);
                        break;
                    case 1 :
                        mYear = Year.YEAR_2.getYear();
                        mCourses = getResources().getStringArray(R.array.Year2_list);
                        break;
                    case 2 :
                        mYear = Year.YEAR_3.getYear();
                        mCourses = getResources().getStringArray(R.array.Year3_list);
                        break;
                    case 3 :
                        mYear = Year.YEAR_4.getYear();
                        mCourses = getResources().getStringArray(R.array.Year4_list);
                        break;
                    default: break;
                }
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, mCourses);
                mFileCourse.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        mFileCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] courseWords = mCourses[position].split(" ");
                StringBuilder ret = new StringBuilder();
                for(String word : courseWords) {
                    ret.append(word.charAt(0));
                }
                mCourse = ret.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        chooseFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!confirmInput()) {
                    return ;
                }

                uploadLink = mYear + "/" + mCourse + "/" + mFileName.getEditText().getText().toString();

                Log.d(Constants.TAG, "\nMy Year is : " + mYear + "\nMy Course is : " + mCourse +
                        "\n My Upload Link is : " + uploadLink + "\n");
            }
        });
    }

    private boolean confirmInput() {
        String name = mFileName.getEditText().getText().toString().trim();

        if (name.isEmpty()) {
            mFileName.setError("Field can't be empty");
        } else {
            mFileName.setError(null);
            return true;
        }
        return false;
    }

    private void initializeControllers() {
        topMenuText = (TextView) findViewById(R.id.topMenu_text);
        mFileYear = (Spinner) findViewById(R.id.selectedYear);
        mFileCourse = (Spinner) findViewById(R.id.selectedCourse);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        mFileName = (TextInputLayout) findViewById(R.id.selectedFileName);
        chooseFileBtn = (ImageButton) findViewById(R.id.chooseFileBtn);
        mFileDesc = (EditText) findViewById(R.id.fileDesc);
        fireStorage = FirebaseStorage.getInstance();
        fireDatabase = FirebaseFirestore.getInstance();
    }

}
