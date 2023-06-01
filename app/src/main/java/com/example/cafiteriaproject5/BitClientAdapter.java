package com.example.cafiteriaproject5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BitClientAdapter extends RecyclerView.Adapter<BitClientViewHolder>{

    Context context;
    List<BitClient> bitList;

    private BitClientAdapter.OnItemClickListener listener;

    //interface for clicking the imageView
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    //a method for clicking the image
    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public BitClientAdapter(@NonNull Context context, @NonNull List<BitClient> bitList){
        this.context = context;
        this.bitList = bitList;
    }

    @NonNull
    @Override
    public BitClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View v = layoutInflater.inflate(R.layout.bit_row, parent, false);
        return new BitClientViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BitClientViewHolder holder, int position) {
        holder.tvNameBit.setText(bitList.get(position).getName());
        holder.tvLastNameBit.setText(bitList.get(position).getLastName());
        holder.tvProductBit.setText(bitList.get(position).getProduct());
        holder.tvQuantityBit.setText(bitList.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        return bitList.size();
    }
}
