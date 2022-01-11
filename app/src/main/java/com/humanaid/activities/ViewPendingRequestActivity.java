package com.humanaid.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
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
import com.google.firebase.storage.FirebaseStorage;
import com.humanaid.R;
import com.humanaid.activities.adapters.ViewPendingRequestAdapter;
import com.humanaid.models.DataRequestModel;
import com.humanaid.usefulClasses.ConnectionCheck;

import java.util.ArrayList;

public class ViewPendingRequestActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private ValueEventListener valueEventListener;

    private TextView textView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ViewPendingRequestAdapter adapter;
    private ArrayList<DataRequestModel> modelArrayList;
    private DataRequestModel dataRequestModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pending_request);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        textView = findViewById(R.id.pending_text_view);
        progressBar = findViewById(R.id.progressbar_pending);
        recyclerView = (RecyclerView) findViewById(R.id.pending_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences login_info = this.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        String uID = login_info.getString("userID", null);

        if (ConnectionCheck.isOnline(this)) {
            modelArrayList = new ArrayList<>();
            adapter = new ViewPendingRequestAdapter(ViewPendingRequestActivity.this, modelArrayList);
            recyclerView.setAdapter(adapter);

            //For Querying the Database
            Query firebaseQuery = databaseReference.child("UserRequests").orderByChild("requesterUserID").startAt(uID).endAt(uID);

            valueEventListener = firebaseQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    modelArrayList.clear();

                    for (DataSnapshot reqSnapshot : dataSnapshot.getChildren()) {
                        dataRequestModel = reqSnapshot.getValue(DataRequestModel.class);
                        modelArrayList.add(dataRequestModel);
                    }
                    if (modelArrayList.isEmpty()) {
                        textView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewPendingRequestActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(ViewPendingRequestActivity.this, "Please Enable Internet and Try Again!", Toast.LENGTH_SHORT).show();
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