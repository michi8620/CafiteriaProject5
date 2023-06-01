package com.example.cafiteriaproject5;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BitClientViewHolder extends RecyclerView.ViewHolder{
    TextView tvNameBit;
    TextView tvLastNameBit;
    TextView tvProductBit;
    TextView tvQuantityBit;
    ImageView deleteView;

    public BitClientViewHolder(@NonNull View itemView, BitClientAdapter.OnItemClickListener listener) {
        super(itemView);
        tvNameBit = itemView.findViewById(R.id.tvNameBit);
        tvLastNameBit = itemView.findViewById(R.id.tvLastNameBit);
        tvProductBit = itemView.findViewById(R.id.tvProductBit);
        tvQuantityBit = itemView.findViewById(R.id.tvQuantityBit);
        deleteView = itemView.findViewById(R.id.ivDeletePayment);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick(getAdapterPosition());
            }
        });
    }
}
