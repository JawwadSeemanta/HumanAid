package com.humanaid.activities.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.humanaid.R;
import com.humanaid.activities.viewHolders.ViewPendingRequestViewHolder;
import com.humanaid.models.DataRequestModel;
import com.humanaid.usefulClasses.ConnectionCheck;

import java.util.ArrayList;

public class ViewPendingRequestAdapter extends RecyclerView.Adapter<ViewPendingRequestViewHolder> {

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;

    private Context context;
    private ArrayList<DataRequestModel> models;

    public ViewPendingRequestAdapter(Context context, ArrayList<DataRequestModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewPendingRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_view_pending_request, parent, false);
        return new ViewPendingRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPendingRequestViewHolder holder, int position) {
        //Bind Data to Views
        DataRequestModel dataRequestModel = models.get(position);
        holder.responderName.setText(dataRequestModel.getHelperName());
        if(dataRequestModel.getHelperPhone() != null){
            holder.responderPhone.setText("+8801" + dataRequestModel.getHelperPhone());
        }
        else{
            holder.responderPhone.setText(dataRequestModel.getHelperPhone());
            holder.responderPhone.setVisibility(View.GONE);
            holder.responderName.setVisibility(View.GONE);
        }
        holder.helpType.setText(dataRequestModel.getRequestType());
        holder.requestStatus.setText(dataRequestModel.getRequestStatus());
        holder.requestTime.setText(dataRequestModel.getSubmissionTime());

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);

        holder.setOnItemClickListener(new myItemOnclickListener() {
            @Override
            public void OnItemClick(int position) {
                new AlertDialog.Builder(context).setMessage("Do you want to delete your request?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (ConnectionCheck.isOnline(context)) {
                                    // code to delete the entry
                                    databaseReference = FirebaseDatabase.getInstance().getReference();
                                    firebaseStorage = FirebaseStorage.getInstance();

                                    final String key = dataRequestModel.getRequestID();

                                    //Delete the image in the storage
                                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl(dataRequestModel.getImageURL());
                                    storageReference.delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //If Successful delete database entry
                                                    databaseReference.child("UserRequests").child(key).removeValue();
                                                    Toast.makeText(context, "Entry Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Request Delete Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public interface myItemOnclickListener {
        void OnItemClick(int position);
    }
}
