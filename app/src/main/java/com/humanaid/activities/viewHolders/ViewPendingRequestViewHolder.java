package com.humanaid.activities.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.humanaid.R;
import com.humanaid.activities.adapters.ViewPendingRequestAdapter;

public class ViewPendingRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView responderName, responderPhone, helpType, requestStatus, requestTime;

    private ViewPendingRequestAdapter.myItemOnclickListener mListener;

    public ViewPendingRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        this.responderName = itemView.findViewById(R.id.responder_name);
        this.responderPhone = itemView.findViewById(R.id.responder_phone);
        this.helpType = itemView.findViewById(R.id.pending_help_type);
        this.requestStatus = itemView.findViewById(R.id.pending_request_status);
        this.requestTime = itemView.findViewById(R.id.request_submission_time);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mListener.OnItemClick(position);
            }
        }
    }

    public void setOnItemClickListener(ViewPendingRequestAdapter.myItemOnclickListener listener) {
        mListener = listener;
    }
}
