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

public class ProviderDashboardActivity extends AppCompatActivity {
    private Button viewRequests, providerLogout;
    private TextView providerName;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        viewRequests = (Button) findViewById(R.id.viewAllRequests);
        providerLogout = (Button) findViewById(R.id.providerlogOut);
        providerName = (TextView) findViewById(R.id.providerName);

        //Display Username and Citizen Points
        sharedPreferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        providerName.setText(sharedPreferences.getString("name", null));

        // View All Pending Requests Button
        viewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProviderDashboardActivity.this, ViewAllRequestActivity.class));
            }
        });

        //Logout Button
        providerLogout.setOnClickListener(new View.OnClickListener() {
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

                startActivity(new Intent(ProviderDashboardActivity.this, LoginActivity.class));
                Toast.makeText(ProviderDashboardActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}