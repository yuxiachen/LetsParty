package com.example.android.letsparty.ui;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private TextView tv_interest;
    private String[] interestItems;
    private boolean[] checkedItems;
    private ArrayList<Integer> mInterestItems = new ArrayList<>();
    private ImageView imageView_item_etProfile;
    private EditText et_profile_username, et_profile_email, et_profile_city, et_profile_state;
    private EditText et_profile_country, et_profile_zipCode;
    private Button btn_save;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageReference;
    private Uri mImageUri;
    private ProgressBar mProgressBar;
    private String cameraFilePath;
    private Dialog dialog;
    private View inflate;
    private Button takePhoto, choosePhoto, cancelUpload;
    private Set<String> set;
    private String originalImageUrl;
    private DatabaseReference databaseReference;
    private Location location;
    private StorageReference fileReference;
    private String downloadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView_item_etProfile = (ImageView) findViewById(R.id.imageView_item_etProfile);
        et_profile_username = (EditText) findViewById(R.id.et_profile_username);
        et_profile_email = (EditText) findViewById(R.id.et_profile_email);
        et_profile_country = (EditText) findViewById(R.id.et_profile_country);
        et_profile_city = (EditText) findViewById(R.id.et_profile_city);
        et_profile_state = (EditText) findViewById(R.id.et_profile_state);
        et_profile_zipCode = (EditText) findViewById(R.id.et_profile_zipcode);
        tv_interest = (TextView) findViewById(R.id.tv_interest);
        btn_save = (Button) findViewById(R.id.btn_save);
        mStorageReference = FirebaseStorage.getInstance().getReference("profileImages");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        set = new HashSet<>();

        interestItems = getResources().getStringArray(R.array.interest_array);

        checkedItems = new boolean[interestItems.length];

        databaseReference = firebaseDatabase.getReference(getString(R.string.db_user) + "/" + firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                setupView(user);

                originalImageUrl = user.getProfileImageUrl();

                for (int i = 0; i < interestItems.length; i++) {
                    if (set.contains(interestItems[i])) {
                        checkedItems[i] = true;

                        mInterestItems.add(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Pop Up a Window by Clicking on the Profile Picture
        imageView_item_etProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = new Location(et_profile_city.getText().toString(), et_profile_country.getText().toString(), et_profile_state.getText().toString(), et_profile_zipCode.getText().toString());

                if (mImageUri == null) {
                    User user = new User(et_profile_username.getText().toString(), originalImageUrl, et_profile_email.getText().toString(), location, tv_interest.getText().toString());

                    databaseReference.setValue(user);

                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                } else {
                    fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

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
                                    downloadUrl = uri.toString();

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
    }

    private void setupView(User user) {
        if (user == null)   return;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(user.getUserName());

        et_profile_username.setText(user.getUserName());
        et_profile_email.setText(user.getEmail());
        if (user.getLocation() != null) {
            et_profile_country.setText(user.getLocation().getCountry());
            et_profile_city.setText(user.getLocation().getCity());
            et_profile_state.setText(user.getLocation().getState());
            et_profile_zipCode.setText(user.getLocation().getZipCode());
        }
        if (user.getInterest() != null) {
            tv_interest.setText(user.getInterest());

            set.addAll(Arrays.asList(user.getInterest().split(", ")));
        }

        if (user.getProfileImageUrl() != null) {
            Picasso.get().load(user.getProfileImageUrl())
                    .fit()
                    .into(imageView_item_etProfile);
        }
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showPopupWindow(View view) {
        dialog = new Dialog(this);

        inflate = LayoutInflater.from(this).inflate(R.layout.popup_window, null);

        takePhoto = (Button) inflate.findViewById(R.id.take_photo);
        choosePhoto = (Button) inflate.findViewById(R.id.choose_photo);
        cancelUpload = (Button) inflate.findViewById(R.id.cancel_upload);

        dialog.setContentView(inflate);

        Window dialogWindow = dialog.getWindow();

        dialogWindow.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        dialogWindow.setAttributes(lp);

        dialog.show();

        // Trigger the Camera
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Log.e("aaaa", "put extra");

                // This one has some Problem
                /*
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile
                (getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
                */

                Log.e("aaaa", "start");

                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        cancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
