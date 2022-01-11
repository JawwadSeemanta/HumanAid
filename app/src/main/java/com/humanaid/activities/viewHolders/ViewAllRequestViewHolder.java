package com.humanaid.activities.viewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.humanaid.R;
import com.humanaid.interfaces.RecycleViewItemClickListener;

public class ViewAllRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView requestImage;
    public TextView requesterName, requesterPhone, requestStatus, requestType, requestTime;
    public Button mapView;
    private RecycleViewItemClickListener recycleViewItemClickListener;

    public ViewAllRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        this.requestImage = itemView.findViewById(R.id.all_request_imageView);
        this.requesterName = itemView.findViewById(R.id.all_name);
        this.requesterPhone = itemView.findViewById(R.id.all_phone);
        this.requestStatus = itemView.findViewById(R.id.all_request_status);
        this.requestType = itemView.findViewById(R.id.all_help_type);
        this.requestTime = itemView.findViewById(R.id.all_request_submission_time);
        this.mapView = itemView.findViewById(R.id.view_map_button);

        itemView.setOnClickListener(this);
        requestImage.setOnClickListener(this);
        mapView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.recycleViewItemClickListener.onItemClick(view, getLayoutPosition());
    }

    public void setRecycleViewItemClickListener(RecycleViewItemClickListener recycleViewItemClickListener) {
        this.recycleViewItemClickListener = recycleViewItemClickListener;
    }
}
