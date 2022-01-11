package com.humanaid.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.humanaid.usefulClasses.ConnectionCheck;
import com.humanaid.usefulClasses.ImageCompression;
import com.humanaid.R;
import com.humanaid.models.DataRequestModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestHelpActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private String compressedPhotoPath;
    private String imageFileName;
    private Button submit, l, p;
    private LinearLayout picture, loc;
    private EditText name, mobile;
    private TextView latlang;
    private FusedLocationProviderClient fusedLocationClient;
    private double Latitude, Longitude;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Spinner spinner;
    private String selected_help_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_help);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        name = (EditText) findViewById(R.id.requestingName);
        mobile = (EditText) findViewById(R.id.requestingMobile);
        submit = (Button) findViewById(R.id.submit_request);
        picture = (LinearLayout) findViewById(R.id.pictureTaker);
        loc = (LinearLayout) findViewById(R.id.locationview);
        latlang = (TextView) findViewById(R.id.latlang);

        l = (Button) findViewById(R.id.loc_ic);
        p = (Button) findViewById(R.id.pic_ic);

        //Get Name and Number from Login
        setInfo();

        // User Choice Spinner
        spinner = (Spinner) findViewById(R.id.requestType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Type_of_Help_Needed, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_help_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

                if (ActivityCompat.checkSelfPermission(RequestHelpActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(RequestHelpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(RequestHelpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RequestHelpActivity.this, permissions, 28);
                } else {
                    takePicture();
                    p.setBackground(getDrawable(R.drawable.done_green));
                }
            }
        });

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionCheck.isOnline(RequestHelpActivity.this)){
                    new AlertDialog.Builder(RequestHelpActivity.this)
                            .setMessage("Submit Request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    submitRequest();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
                else{
                    Toast.makeText(RequestHelpActivity.this, "Please Enable Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void submitRequest() {
        final String Name, Phone, reqType, userID, reqID;
        final double Lat, Lang;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        reqType = selected_help_type;

        //Populate Values
        SharedPreferences login_info = this.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        Name = login_info.getString("name", null);
        Phone = login_info.getString("mobile", null);
        userID = login_info.getString("userID", null);
        reqID = reqType + "_" + Phone + "_" + timeStamp;

        //Check if the image was taken
        if (currentPhotoPath != null) {
            File f = new File(currentPhotoPath);
            if (f.length() / 1024 < 50) {
                f.delete();
                currentPhotoPath = null;
            }
        }

        if (currentPhotoPath == null
                || (Latitude == 0.0 && Longitude == 0.0)) {
            if (currentPhotoPath == null) {
                Toast.makeText(this, "Please Capture Photo of the Incident", Toast.LENGTH_SHORT).show();
            }
            if (Latitude == 0.0 && Longitude == 0.0) {
                Toast.makeText(this, "Please Give Location of the Incident", Toast.LENGTH_SHORT).show();
            }
        }
        else {

            submit.setVisibility(View.GONE);

            //Compress Image
            compressedPhotoPath = ImageCompression.compressImage(currentPhotoPath, imageFileName);
            new File(currentPhotoPath).delete();
            Uri mCompressedPhotoURI = Uri.fromFile(new File(compressedPhotoPath));

            //Upload Image File
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference fileReference = storageReference.child(imageFileName + ".jpg");
            UploadTask uploadTask = fileReference.putFile(mCompressedPhotoURI);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {

                            String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            Log.d("Link", "URL " + photoStringLink);
                            String ImageURL = photoStringLink;

                            // Database Upload
                            if (ImageURL != null && ImageURL.contains("firebasestorage.googleapis.com")) {
                                //Upload Data to Database
                                //Preparing Data for Entry
                                DataRequestModel requestModel = new DataRequestModel(reqID, Name, Phone, userID, reqType, "Requested", timeStamp, ImageURL, Latitude, Longitude,null,null);

                                //Uploading Data to Firebase
                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("UserRequests").child(reqID).setValue(requestModel);

                                submit.setVisibility(View.GONE);
                                new File(compressedPhotoPath).delete();

                                //Increase Citizen Point
                                databaseReference = FirebaseDatabase.getInstance().getReference()
                                        .child("UserDatabase").child("User").child(userID).child("citizen_points");
                                int temp = login_info.getInt("CP", 0) + 1;
                                databaseReference.setValue(temp);
                                login_info.edit().putInt("CP", temp).apply();

                                Toast.makeText(RequestHelpActivity.this, "Request Submission Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RequestHelpActivity.this, UserDashboardActivity.class));
                                finish();
                            }
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RequestHelpActivity.this, "Photo Upload Failed", Toast.LENGTH_SHORT).show();
                    submit.setVisibility(View.VISIBLE);
                }
            });
        }


    }

    private void setLocation() {
        if (ActivityCompat.checkSelfPermission(RequestHelpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(RequestHelpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(RequestHelpActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    29);
        } else {
            // permission has been granted, continue as usual
            fusedLocationClient
                    .getLastLocation()
                    .addOnSuccessListener(RequestHelpActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Latitude = location.getLatitude();
                                Longitude = location.getLongitude();

                                //Set the Latitude and Longitude
                                String LatLangString = Latitude + " , " + Longitude;
                                latlang.setText(LatLangString);
                                Toast.makeText(RequestHelpActivity.this, "Location Saved Successfully", Toast.LENGTH_SHORT).show();
                                l.setBackground(getDrawable(R.drawable.done_green));

                            } else {
                                // TODO: BUG: Open Google Maps First, then this app. Then can get location
                                Toast.makeText(RequestHelpActivity.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }


    }


    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.humanaid.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        SharedPreferences login_info = this.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        imageFileName = login_info.getString("userID", null) + "_(" + timeStamp + ")";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        String[] p = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        String[] l = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};

        if (requestCode == 28) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RequestHelpActivity.this, p, 28);
                    }
                }
                takePicture();
            }
        } else if (requestCode == 29) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RequestHelpActivity.this, l, 29);
                    }
                }
                setLocation();
            }
        }
    }

    private void setInfo() {
        SharedPreferences login_info = this.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        name.setText(login_info.getString("name", null));
        mobile.setText(login_info.getString("mobile", null));
    }
}