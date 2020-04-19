package com.example.minds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import custom_class.Constants;
import custom_class.File;
import custom_class.User;
import custom_class.Year;


public class UploadFilePage extends AppCompatActivity {

    private String                  uploadLink;
    private String                  mYear = "";
    private String                  mCourse;
    private String[]                mCourses;
    private String                  downloadUrl = "";
    private Uri                     mFileUri;

    private TextView                topMenuText;
    private TextInputLayout         mFileName;
    private EditText                mFileDesc;
    private Spinner                 mFileYear;
    private Spinner                 mFileCourse;
    private Button                  uploadBtn;
    private ImageButton             chooseFileBtn;
    private ProgressBar             progressBar;

    private FirebaseFirestore       fireDatabase;
    private FirebaseAuth            fireAuth;
    private StorageReference        fireStorage;
    private StorageReference        fileReference;


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
                openFileChooser();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!confirmInput()) {
                    return ;
                }

                uploadLink = mYear + "/" + mCourse + "/";

                Log.d(Constants.TAG, "\nMy Year is : " + mYear + "\nMy Course is : " + mCourse +
                        "\n My Upload Link is : " + uploadLink + "\n");

                uploadFile();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {
        if (mFileUri != null) {
            // Upload File to Storage
            fileReference = fireStorage.child(uploadLink + System.currentTimeMillis()
                    + "." + getFileExtension(mFileUri));
            fileReference.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 500);
                    Toast.makeText(UploadFilePage.this, "File Upload Successfully", Toast.LENGTH_LONG).show();

                    // Get the download Url of File
                    getDownloadUrl(fileReference);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadFilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDownloadUrl(final StorageReference fileReference) {
        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadUrl = uri.toString();
                // Update database -> add File Info's && add File to User MyDocs.
                File file = new File(mFileName.getEditText().getText().toString(),
                        mFileDesc.getText().toString(),
                        downloadUrl,
                        fireAuth.getUid());
                Log.d(Constants.TAG, "Download Url is : " + downloadUrl + "\n");
                updateDatabase(file);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Constants.TAG, "Can't get Download URL for file <" + mFileName + ">\n");
            }
        });
    }

    private void updateDatabase(File file) {
        fireDatabase.collection("Uploads/" + uploadLink).add(file)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                fireDatabase.collection("Uploads/" + uploadLink)
                        .document(documentReference.getId())
                        .update("mFileId", documentReference.getId());
                addToUserMyDocs(documentReference);
                Log.d(Constants.TAG, "\n ID is : " + documentReference.getId() + "\n");
            }
        });
    }

    private void addToUserMyDocs(final DocumentReference documentReference) {
        final String uid = fireAuth.getCurrentUser().getUid();
        fireDatabase.collection(Constants.DB_USERS)
                .document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.addUpload("Uploads/" + uploadLink + documentReference.getId());
                            fireDatabase.collection(Constants.DB_USERS)
                                    .document(uid).update("mUploads", user.getmUploads());
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

    private void openFileChooser() {
        String[] mimeTypes = {"text/*", "image/*", "application/pdf"};
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, Constants.FILE_TO_UPLOAD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.FILE_TO_UPLOAD_REQUEST && resultCode == RESULT_OK
                        && data != null && data.getData() != null) {
            mFileUri = data.getData();
            Toast.makeText(this, "File Selected Successfully", Toast.LENGTH_SHORT).show();
        }
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fireStorage = FirebaseStorage.getInstance().getReference("Uploads");
        fireDatabase = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
    }

}
