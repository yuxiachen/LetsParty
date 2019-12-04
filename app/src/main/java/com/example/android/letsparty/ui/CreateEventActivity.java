package com.example.android.letsparty.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letsparty.R;
import com.example.android.letsparty.model.Event;
import com.example.android.letsparty.model.Location;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mUri;
    private ImageView imageView;
    private EditText et_title, et_date, et_time, et_street, et_city, et_state, et_country, et_zipcode, et_description, et_minPeople;
    private Button btn_post;
    private DatabaseReference dbReference;
    private StorageReference mStorageReference;
    private final Calendar calendar = Calendar.getInstance();
    private Location location;
    private Spinner spinnerCategory;
    private ToggleButton tb_friendsOnly;
    private boolean friendsOnly;
    private Dialog dialog;
    private View inflate;
    private Button choosePhoto, cancelUpload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        dbReference = FirebaseDatabase.getInstance().getReference("events");
        imageView = (ImageView)findViewById(R.id.iv_event_photo);
        et_title = (EditText)findViewById(R.id.et_event_title);
        et_date = (EditText)findViewById(R.id.et_event_date);
        et_time = (EditText)findViewById(R.id.et_event_time);
        et_street = (EditText)findViewById(R.id.et_location_street);
        et_city = (EditText)findViewById(R.id.et_location_city);
        et_state = (EditText)findViewById(R.id.et_location_state);
        et_country = (EditText)findViewById(R.id.et_location_country);
        et_zipcode = (EditText)findViewById(R.id.et_location_zipcode);
        et_description = (EditText)findViewById(R.id.et_description);
        et_minPeople = (EditText)findViewById(R.id.et_minPeople) ;
        btn_post = (Button)findViewById(R.id.btn_post);
        mStorageReference = FirebaseStorage.getInstance().getReference("eventImages");
        spinnerCategory = (Spinner)findViewById(R.id.spinner_category);
        tb_friendsOnly = (ToggleButton)findViewById(R.id.tb_friendsOnly);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.createEvent));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);;
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateEventActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        String output = String.format("%02d:%02d", hourOfDay, minute);
                        et_time.setText(output);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        tb_friendsOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    friendsOnly = true;
                }
                else{
                    friendsOnly = false;
                }
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUri == null){
                    Toast.makeText(CreateEventActivity.this, "Please pick an event image.", Toast.LENGTH_SHORT).show();
                }
                else {
                    final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mUri));

                    fileReference.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    String title = et_title.getText().toString();
                                    long time = calendar.getTimeInMillis();
                                    String category = spinnerCategory.getSelectedItem().toString().toLowerCase();
                                    String description = et_description.getText().toString().trim();
                                    String street = et_street.getText().toString();
                                    String city = et_city.getText().toString();
                                    String state = et_state.getText().toString();
                                    String country = et_country.getText().toString();
                                    String zipcode = et_zipcode.getText().toString();
                                    String organizer = FirebaseAuth.getInstance().getUid();
                                    int minPeople = Integer.parseInt(et_minPeople.getText().toString());
                                    int currentPeople = 1;

                                    if (time <= System.currentTimeMillis()) {
                                        Toast.makeText(CreateEventActivity.this, "Please pick a valid date and time.", Toast.LENGTH_SHORT).show();
                                    }

                                    String output = "";

                                    if (title.equals("")) {
                                        output += "Event Title, ";
                                    }

                                    if (street.equals("")) {
                                        output += "Street, ";
                                    }

                                    if (city.equals("")) {
                                        output += "City, ";
                                    }

                                    if (state.equals("")) {
                                        output += "State, ";
                                    }

                                    if (country.equals("")) {
                                        output += "Country, ";
                                    }

                                    if (zipcode.equals("")) {
                                        output += "Zip Code, ";
                                    }

                                    if (description.equals("")) {
                                        output += "Event Description, ";
                                    }

                                    if (!output.equals("")) {
                                        output = output.substring(0, output.length() - 2) + " can not be empty.";
                                        Toast.makeText(CreateEventActivity.this, output, Toast.LENGTH_LONG).show();
                                    } else {
                                        location = new Location(street, city, state, country, zipcode);
                                        Event event = new Event(title, downloadUrl, time, location, friendsOnly, category, description, organizer, minPeople, currentPeople);
                                        String id = dbReference.push().getKey();

                                        dbReference.child(id).setValue(event);
                                        FirebaseDatabase.getInstance().getReference("postedEvents").child(organizer).child(id).setValue(true);
                                        FirebaseDatabase.getInstance().getReference("joinedEvents").child(organizer).child(id).setValue(true);

                                        Toast.makeText(CreateEventActivity.this, "Post Event successfully.", Toast.LENGTH_LONG).show();

                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    });
                }
            }
        });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void updateDate(){
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        et_date.setText(sdf.format(calendar.getTime()));
    }

    public void showPopupWindow(View view) {
        dialog = new Dialog(this);

        inflate = LayoutInflater.from(this).inflate(R.layout.event_popup_window, null);

        choosePhoto = (Button) inflate.findViewById(R.id.choose_photo);
        cancelUpload = (Button) inflate.findViewById(R.id.cancel_upload);

        dialog.setContentView(inflate);

        Window dialogWindow = dialog.getWindow();

        dialogWindow.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        dialogWindow.setAttributes(lp);

        dialog.show();

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
            mUri = data.getData();
            Picasso.get().load(mUri).into(imageView);
        }

        dialog.dismiss();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
