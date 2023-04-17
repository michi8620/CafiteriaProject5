package com.example.cafiteriaproject5;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WantedViewHolder extends RecyclerView.ViewHolder {
    TextView tvWantedProduct;
    ImageView deleteView;

    public WantedViewHolder(@NonNull View itemView, WantedAdapter.OnItemClickListener listener) {
        super(itemView);
        tvWantedProduct = itemView.findViewById(R.id.tvWantedProduct);
        deleteView = itemView.findViewById(R.id.delete_id);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick(getAdapterPosition());
            }
        });
    }
}
