package com.humanaid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.humanaid.usefulClasses.ConnectionCheck;
import com.humanaid.R;
import com.humanaid.models.UserModel;

public class UserCreationActivity extends AppCompatActivity {
    private Button createUser;
    private EditText name, phone, pin;
    private SwitchCompat aSwitch;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_creation);

        createUser = (Button) findViewById(R.id.create_new_account);
        name = (EditText) findViewById(R.id.create_account_name);
        phone = (EditText) findViewById(R.id.create_account_mobile);
        pin = (EditText) findViewById(R.id.create_account_pin);
        aSwitch = (SwitchCompat) findViewById(R.id.createAccount_userType);

        // Change status from GONE to VISIBLE to create provider account
        aSwitch.setVisibility(View.GONE);



        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(ConnectionCheck.isOnline(UserCreationActivity.this))){
                    Toast.makeText(UserCreationActivity.this, "Please Enable Internet", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Submit info to Firebase
                    //Confirmation Dialogue Box
                    new AlertDialog.Builder(UserCreationActivity.this).setMessage("Submit Account Details?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Get Field Data
                            final String mobileNumber = phone.getText().toString().trim();
                            final String fullName = name.getText().toString().trim();
                            final String pinNumber = pin.getText().toString().trim();
                            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(pinNumber)) {
                                if (TextUtils.isEmpty(fullName) || fullName.trim().equals("")) {
                                    Toast.makeText(UserCreationActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                                }
                                if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() < 9) {
                                    Toast.makeText(UserCreationActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                                }
                                if (TextUtils.isEmpty(pinNumber) || pinNumber.length() < 6) {
                                    Toast.makeText(UserCreationActivity.this, "Please Enter PIN Number", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String TAG;
                                if (aSwitch.isChecked()) {
                                    TAG = "Personnel";

                                    final String uid = TAG + "_" + mobileNumber;
                                    createUser.setVisibility(View.GONE); //Make the submit button disappear

                                    //Upload to Database
                                    UserModel user = new UserModel(fullName, mobileNumber, pinNumber, TAG, uid);

                                    databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("UserDatabase").child(TAG).child(uid).setValue(user);

                                    //After Finishing GO TO Login Page
                                    Toast.makeText(UserCreationActivity.this, "Account Creation Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UserCreationActivity.this, LoginActivity.class));
                                    finish();

                                } else {
                                    TAG = "User";

                                    final String uid = TAG + "_" + mobileNumber;
                                    createUser.setVisibility(View.GONE); //Make the submit button disappear

                                    //Upload to Database
                                    UserModel user = new UserModel(fullName, mobileNumber, pinNumber, TAG, uid,1);

                                    databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("UserDatabase").child(TAG).child(uid).setValue(user);

                                    //After Finishing GO TO Login Page
                                    Toast.makeText(UserCreationActivity.this, "Account Creation Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UserCreationActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            }
        });


    }
}