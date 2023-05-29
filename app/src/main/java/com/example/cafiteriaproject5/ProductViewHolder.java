package com.example.cafiteriaproject5;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    TextView tvProductCode;
    TextView tvProductName;
    TextView tvProductPrice;
    ImageView deleteView;

    public ProductViewHolder(@NonNull View itemView, ProductAdapter.OnItemClickListener listener) {
        super(itemView);
        tvProductCode = itemView.findViewById(R.id.tvProductCode);
        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        deleteView = itemView.findViewById(R.id.ivDeleteProduct);
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
