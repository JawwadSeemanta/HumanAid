package com.humanaid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.humanaid.usefulClasses.ConnectionCheck;
import com.humanaid.R;
import com.humanaid.models.UserModel;

public class LoginActivity extends AppCompatActivity {
    private Button login, create;
    private EditText mobile, pin;
    private SwitchCompat aSwitch;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Check the login status
        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        boolean logged_in = sharedPreferences.getBoolean("status", false);

        if (logged_in) {
            if (sharedPreferences.getString("userType", null).matches("User")) {
                startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                finish();
            }
            if (sharedPreferences.getString("userType", null).matches("Personnel")) {
                startActivity(new Intent(LoginActivity.this, ProviderDashboardActivity.class));
                finish();
            }
        }

        login = (Button) findViewById(R.id.login_button);
        create = (Button) findViewById(R.id.create_account_button);
        mobile = (EditText) findViewById(R.id.login_mobile);
        pin = (EditText) findViewById(R.id.login_PIN);
        aSwitch = (SwitchCompat) findViewById(R.id.login_userMode);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(ConnectionCheck.isOnline(LoginActivity.this))){
                    Toast.makeText(LoginActivity.this, "Please Enable Internet", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (aSwitch.isChecked()) {
                        final String m = mobile.getText().toString().trim();
                        final String p = pin.getText().toString().trim();

                        // Get User Data from database
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("UserDatabase").child("Personnel").child("Personnel_" + m)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // Check if the user actually exists
                                        if (snapshot.exists()) {
                                            UserModel u = new UserModel();
                                            u = snapshot.getValue(UserModel.class);
                                            assert u != null;
                                            if (u.getPin_number().matches(p)) {
                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                                //Change the login status if successful
                                                changeLoginStatus(u.getUser_id(), u.getUser_name(), u.getMobile_number(), u.getUser_type(), u.getCitizen_points());
                                                startActivity(new Intent(LoginActivity.this, ProviderDashboardActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Recheck PIN", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Recheck Mobile Number!!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Database Read Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        final String m = mobile.getText().toString().trim();
                        final String p = pin.getText().toString().trim();

                        // Get User Data from database
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        valueEventListener = databaseReference.child("UserDatabase").child("User").child("User_" + m)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // Check if the user actually exists
                                        if (snapshot.exists()) {
                                            UserModel u = new UserModel();
                                            u = snapshot.getValue(UserModel.class);
                                            assert u != null;
                                            if (u.getPin_number().matches(p)) {
                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                                //Change the login status if successful
                                                changeLoginStatus(u.getUser_id(), u.getUser_name(), u.getMobile_number(), u.getUser_type(), u.getCitizen_points());
                                                startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Recheck PIN", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Recheck Mobile Number!!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Database Read Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UserCreationActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    private void changeLoginStatus(String userID, String name, String MobileNumber, String userType, int citizen_point) {
        SharedPreferences login_info = this.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        login_info.edit()
                .putString("name", name)
                .putString("userID", userID)
                .putString("mobile", MobileNumber)
                .putString("userType", userType)
                .putInt("CP", citizen_point)
                .putBoolean("status", true)
                .apply();
    }
}