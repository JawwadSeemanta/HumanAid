package com.humanaid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.humanaid.R;

public class UserDashboardActivity extends AppCompatActivity {

    private Button request, view, logout;
    private TextView username, citizenPoint;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        request = (Button) findViewById(R.id.requestHelp);
        view = (Button) findViewById(R.id.viewRequests);
        logout = (Button) findViewById(R.id.logOut);

        username = (TextView) findViewById(R.id.userName);
        citizenPoint = (TextView) findViewById(R.id.cp);

        //Display Username and Citizen Points
        sharedPreferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        username.setText(sharedPreferences.getString("name", null));
        int point = sharedPreferences.getInt("CP", 0);
        if(point < 10){
            citizenPoint.setText("0" + Integer.toString(point));
        } else{
            citizenPoint.setText(Integer.toString(point));
        }


        //Request Help Button
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, RequestHelpActivity.class));
            }
        });

        // View Pending Button
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDashboardActivity.this, ViewPendingRequestActivity.class));
            }
        });

        //Logout Button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Change Login Status and clear the login data in shared preferences
                sharedPreferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
                sharedPreferences.edit()
                        .putString("name", null)
                        .putString("userID", null)
                        .putString("mobile", null)
                        .putString("userType", null)
                        .putBoolean("status", false)
                        .putInt("CP", 0)
                        .apply();

                startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
                Toast.makeText(UserDashboardActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

}