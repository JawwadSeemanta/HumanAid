package com.humanaid.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.humanaid.R;
import com.humanaid.activities.adapters.ViewAllRequestAdapter;
import com.humanaid.models.DataRequestModel;
import com.humanaid.usefulClasses.ConnectionCheck;

import java.util.ArrayList;

public class ViewAllRequestActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private TextView textView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ViewAllRequestAdapter adapter;
    private ArrayList<DataRequestModel> dataRequestModelArrayList;
    private DataRequestModel requestModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_request);

        textView = findViewById(R.id.tv_all);
        progressBar = findViewById(R.id.progressbar_all);
        recyclerView = findViewById(R.id.all_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Perform Permission Check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
            ActivityCompat.requestPermissions(this, permissions, 1234);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(ConnectionCheck.isOnline(this)){
            dataRequestModelArrayList = new ArrayList<>();
            adapter = new ViewAllRequestAdapter(ViewAllRequestActivity.this, dataRequestModelArrayList);
            recyclerView.setAdapter(adapter);

            //Create Database References
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            Query firebaseQuery = databaseReference.child("UserRequests").orderByChild("submissionTime");
            valueEventListener = firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataRequestModelArrayList.clear();
                    for (DataSnapshot reqSnapshot : snapshot.getChildren()) {
                        requestModel = reqSnapshot.getValue(DataRequestModel.class);
                        dataRequestModelArrayList.add(requestModel);
                    }

                    if (dataRequestModelArrayList.isEmpty()) {
                        textView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewAllRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                }
            });
        } else {
            Toast.makeText(ViewAllRequestActivity.this, "Please Enable Internet and Try Again!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}