package com.example.android.letsparty.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.android.letsparty.BuildConfig;
import com.example.android.letsparty.R;
import com.example.android.letsparty.model.Location;
import com.example.android.letsparty.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private TextView tv_interest;
    private String[] interestItems;
    private boolean[] checkedItems;
    private ArrayList<Integer> mInterestItems = new ArrayList<>();
    private ImageView imageView_item_etProfile;
    private EditText et_profile_username, et_profile_email, et_profile_city, et_profile_state;
    private Button btn_save, btn_camera;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageReference;
    private Uri mImageUri;
    private ProgressBar mProgressBar;
    private String cameraFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView_item_etProfile = (ImageView) findViewById(R.id.imageView_item_etProfile);
        et_profile_username = (EditText) findViewById(R.id.et_profile_username);
        et_profile_email = (EditText) findViewById(R.id.et_profile_email);
        et_profile_city = (EditText) findViewById(R.id.et_profile_city);
        et_profile_state = (EditText) findViewById(R.id.et_profile_state);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        mStorageReference = FirebaseStorage.getInstance().getReference("profileImages");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("users/" + firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                et_profile_username.setText(user.getUserName());
                et_profile_email.setText(user.getEmail());
                if (user.getLocation() != null) {
                    et_profile_city.setText(user.getLocation().getCity());
                    et_profile_state.setText(user.getLocation().getState());
                }
                if (user.getInterest() != null) {
                    tv_interest.setText(user.getInterest());
                }

                if (user.getProfileImageUrl() == null) {
                    imageView_item_etProfile.setImageResource(R.drawable.default_profile_logo);
                } else {
                    Picasso.get().load(user.getProfileImageUrl())
                            .fit()
                            .into(imageView_item_etProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageView_item_etProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });//pick a picture by clicking on the profile picture


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Location location = new Location(et_profile_city.getText().toString(), et_profile_state.getText().toString());
                if (mImageUri == null) {
                    User user = new User(et_profile_username.getText().toString(), et_profile_email.getText().toString(), location, tv_interest.getText().toString());
                    databaseReference.setValue(user);
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                } else {
                    final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

                    fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    User user = new User(et_profile_username.getText().toString(), downloadUrl, et_profile_email.getText().toString(), location, tv_interest.getText().toString());
                                    databaseReference.setValue(user);
                                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
                }
            }
        });

        tv_interest = (TextView) findViewById(R.id.tv_interest);
        interestItems = getResources().getStringArray(R.array.interest_array);
        checkedItems = new boolean[interestItems.length];

        tv_interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                mBuilder.setTitle(R.string.mBuilderTitle);
                mBuilder.setMultiChoiceItems(interestItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mInterestItems.contains(position)) {
                                mInterestItems.add(position);
                            }
                        } else {
                            if (mInterestItems.contains(position)) {
                                mInterestItems.remove(position);
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.mBuilderOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for (int i = 0; i < mInterestItems.size(); i++) {
                            item += interestItems[mInterestItems.get(i)];
                            if (i != mInterestItems.size() - 1) {
                                item += ", ";
                            }
                        }
                        tv_interest.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.mBuilderDismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.mBuilderClearAll, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                        }
                        mInterestItems.clear();
                        tv_interest.setText("");
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mBuilder.show();
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //trigger the camera
                //try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.e("aaaa", "put extra");
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile
                            //(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile())); //this one has some problem
                    Log.e("aaaa", "start");
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                //}
                // catch (IOException ex) {
                  //  ex.printStackTrace();
                //}
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(imageView_item_etProfile);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                createImageFile();
                mImageUri = Uri.parse(cameraFilePath);
                Picasso.get().load(mImageUri).into(imageView_item_etProfile);
            } catch (Exception e) {
                Log.e("aaa", "can't save image" + e.getMessage());
            }

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp+"_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();

        return image;
    }

}
