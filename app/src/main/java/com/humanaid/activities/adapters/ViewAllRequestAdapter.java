package com.humanaid.activities.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.humanaid.R;
import com.humanaid.activities.ImageViewerActivity;
import com.humanaid.activities.MapViewerActivity;
import com.humanaid.activities.viewHolders.ViewAllRequestViewHolder;
import com.humanaid.interfaces.RecycleViewItemClickListener;
import com.humanaid.models.DataRequestModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ViewAllRequestAdapter extends RecyclerView.Adapter<ViewAllRequestViewHolder> {

    private Context context;
    private ArrayList<DataRequestModel> dataRequestModelArrayList;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String temporaryRequestID;

    public ViewAllRequestAdapter(Context context, ArrayList<DataRequestModel> dataRequestModelArrayList) {
        this.context = context;
        this.dataRequestModelArrayList = dataRequestModelArrayList;
    }

    @NonNull
    @Override
    public ViewAllRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_view_all_request, parent, false);
        return new ViewAllRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllRequestViewHolder holder, int position) {
        //Bind Data to views
        holder.requesterName.setText(dataRequestModelArrayList.get(position).getRequesterName());
        holder.requesterPhone.setText("+8801" + dataRequestModelArrayList.get(position).getRequesterPhone());
        holder.requestStatus.setText(dataRequestModelArrayList.get(position).getRequestStatus());
        holder.requestType.setText(dataRequestModelArrayList.get(position).getRequestType());
        holder.requestTime.setText(dataRequestModelArrayList.get(position).getSubmissionTime());
        Picasso.get()
                .load(dataRequestModelArrayList.get(position).getImageURL())
                .placeholder(R.drawable.app_icon)
                .fit()
                .into(holder.requestImage);

        //Slide in animation
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);

        // Handle click on models
        holder.setRecycleViewItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
                String responderPhone = sharedPreferences.getString("mobile", null);
                String responderName = sharedPreferences.getString("name", null);

                // Handle Clicks on images
                if(view.getId() == holder.requestImage.getId()){
                    context.startActivity(new Intent(context, ImageViewerActivity.class).putExtra("url", dataRequestModelArrayList.get(position).getImageURL()));
                }
                // Handle CLick on Map Button
                else if(view.getId() == holder.mapView.getId()){
                    temporaryRequestID = dataRequestModelArrayList.get(position).getRequestID();
                    context.startActivity(new Intent(context, MapViewerActivity.class)
                            .putExtra("requesterLat", dataRequestModelArrayList.get(position).getLatitude())
                            .putExtra("requesterLong", dataRequestModelArrayList.get(position).getLongitude()));

                }
                else{
                    //If the request is complete, delete it
                    if(dataRequestModelArrayList.get(position).getRequestStatus().equals("COMPLETE")){
                        new AlertDialog.Builder(view.getContext())
                                .setMessage("Do you want to delete the entry?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Delete the database entry
                                        databaseReference = FirebaseDatabase.getInstance().getReference();
                                        firebaseStorage = FirebaseStorage.getInstance();

                                        temporaryRequestID = dataRequestModelArrayList.get(position).getRequestID();

                                        //Delete the image in the storage first
                                        if(dataRequestModelArrayList.get(position).getImageURL() != null){
                                            storageReference = firebaseStorage
                                                    .getReferenceFromUrl(dataRequestModelArrayList.get(position).getImageURL());
                                            storageReference.delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //If successful, delete the database entry also
                                                            databaseReference.child("UserRequests").child(temporaryRequestID).removeValue();
                                                            Toast.makeText(context, "Database Entry Deleted!!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Entry Delete Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else{
                                            databaseReference.child("UserRequests").child(temporaryRequestID).removeValue();
                                            Toast.makeText(context, "Database Entry Deleted!!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    }

                    //If the request is accepted, then set status to complete
                    if(dataRequestModelArrayList.get(position).getRequestStatus().equals("ACCEPTED")
                    && dataRequestModelArrayList.get(position).getHelperPhone().equals(responderPhone)){
                        new AlertDialog.Builder(view.getContext())
                                .setMessage("Have you completed the request?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Change status from "ACCEPTED" to "COMPLETE"
                                        temporaryRequestID = dataRequestModelArrayList.get(position).getRequestID();
                                        //Change Status on the database
                                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                                .child("UserRequests").child(temporaryRequestID).child("requestStatus");
                                        databaseReference.setValue("COMPLETE");
                                        Toast.makeText(context, "Request Completed", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }

                    //If the request is Requested, set status to accepted
                    if(dataRequestModelArrayList.get(position).getRequestStatus().equals("Requested")){
                        new AlertDialog.Builder(view.getContext())
                                .setMessage("Do you want to respond to this request?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Change status from "REQUESTED" to "ACCEPTED
                                        //Update Data
                                        temporaryRequestID = dataRequestModelArrayList.get(position).getRequestID();
                                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                                .child("UserRequests").child(temporaryRequestID);
                                        databaseReference.child("requestStatus").setValue("ACCEPTED");
                                        databaseReference.child("helperName").setValue(responderName);
                                        databaseReference.child("helperPhone").setValue(responderPhone);
                                        Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataRequestModelArrayList.size();
    }
}
