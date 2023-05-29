package com.example.cafiteriaproject5;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductDeliveryViewHolder extends RecyclerView.ViewHolder{
    TextView tvProductName;
    TextView tvProductQuantity;
    TextView tvProductTotal;
    ImageView deleteView;

    public ProductDeliveryViewHolder(@NonNull View itemView, ProductDeliveryAdapter.OnItemClickListener listener) {
        super(itemView);
        tvProductName = itemView.findViewById(R.id.tvProductNameShopping);
        tvProductQuantity = itemView.findViewById(R.id.tvProductQuantityShopping);
        tvProductTotal = itemView.findViewById(R.id.tvProductTotal);
        deleteView = itemView.findViewById(R.id.ivDeleteDelivery);
        if(deleteView!=null){
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(getAdapterPosition());
                }
            });
        }
    }
}
