package com.example.cafiteriaproject5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


public class ProductAdapter extends ArrayAdapter<Product>{
    private Context context;
    private ArrayList<Product> productArrayList;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> productArrayList){
        super(context, resource, productArrayList);
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent){
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.product_row, null, false);
        Product product = productArrayList.get(position);
        view.setLayoutDirection(getContext().getResources().getConfiguration().getLayoutDirection());

        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvProductCode = view.findViewById(R.id.tvProductCode);
        TextView tvProductPrice = view.findViewById(R.id.tvProductPrice);

        tvProductName.setText(product.getName() + "");
        tvProductCode.setText(product.getCode() + "");
        tvProductPrice.setText(product.getPrice() + "₪");

        return view;
    }
}
