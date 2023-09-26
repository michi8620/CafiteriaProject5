package com.example.cafiteriaproject5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductDeliveryAdapter extends RecyclerView.Adapter<ProductDeliveryViewHolder> {
    Context context;
    List<ProductDelivery> deliveryList;

    private ProductDeliveryAdapter.OnItemClickListener listener;

    //interface for clicking the imageView
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    //a method for clicking the image
    public void setOnItemClickListener(ProductDeliveryAdapter.OnItemClickListener clickListener){
        listener = clickListener;
    }

    public ProductDeliveryAdapter(@NonNull Context context, @NonNull List<ProductDelivery> deliveryList){
        this.context = context;
        this.deliveryList = deliveryList;
    }

    @NonNull
    @Override
    public ProductDeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.product_shopping_row, parent, false);
        return new ProductDeliveryViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDeliveryViewHolder holder, int position) {
        holder.tvProductName.setText(deliveryList.get(position).getName());
        holder.tvProductQuantity.setText(deliveryList.get(position).getQuantity()+"");
        holder.tvProductTotal.setText(deliveryList.get(position).getTotal()+"₪");
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

}
