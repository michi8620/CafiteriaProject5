package com.example.cafiteriaproject5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder>{
    Context context;
    List<Product> productList;
    LayoutInflater inflater;
    Fragment fragment;

    private ProductAdapter.OnItemClickListener listener;

    //interface for clicking the imageView
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    //a method for clicking the image
    public void setOnItemClickListener(ProductAdapter.OnItemClickListener clickListener){
        listener = clickListener;
    }

    public ProductAdapter(@NonNull Context context, @NonNull List<Product> productList, LayoutInflater inflater, Fragment fragment){
        this.context = context;
        this.productList = productList;
        this.inflater = inflater;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v;
        if(fragment instanceof HomeFragment || fragment instanceof ShoppingFragment){
            v = inflater.inflate(R.layout.product_client_row, parent, false);
        } else if(fragment instanceof EditShministFragment){
            v = layoutInflater.inflate(R.layout.product_row, parent, false);
        }
        else{
            v = layoutInflater.inflate(R.layout.product_row, parent, false);
        }
        return new ProductViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.tvProductCode.setText(productList.get(position).getCode()+"");
        holder.tvProductName.setText(productList.get(position).getName());
        holder.tvProductPrice.setText(productList.get(position).getPrice()+"");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
