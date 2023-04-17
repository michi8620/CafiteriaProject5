package com.example.cafiteriaproject5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WantedAdapter extends RecyclerView.Adapter<WantedViewHolder> {

    Context context;
    List<WantedProduct> wantedProducts;

    private OnItemClickListener listener;

    //interface for clicking the imageView
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    //a method for clicking the image
    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public WantedAdapter(Context context, List<WantedProduct> wantedProducts) {
        this.context = context;
        this.wantedProducts = wantedProducts;
    }

    @NonNull
    @Override
    public WantedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View v = layoutInflater.inflate(R.layout.wanted_row, parent, false);
        return new WantedViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WantedViewHolder holder, int position) {
        holder.tvWantedProduct.setText(wantedProducts.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return wantedProducts.size();
    }


}
