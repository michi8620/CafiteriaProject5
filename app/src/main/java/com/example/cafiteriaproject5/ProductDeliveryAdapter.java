package com.example.cafiteriaproject5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProductDeliveryAdapter extends ArrayAdapter<ProductDelivery> {
    private Context context;
    private ArrayList<ProductDelivery> deliveryArrayList;

    public ProductDeliveryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProductDelivery> deliveryArrayList){
        super(context, resource, deliveryArrayList);
        this.context = context;
        this.deliveryArrayList = deliveryArrayList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent){
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.product_shopping_row, null, false);
        ProductDelivery productDelivery = deliveryArrayList.get(position);
        view.setLayoutDirection(getContext().getResources().getConfiguration().getLayoutDirection());

        TextView tvIndex = view.findViewById(R.id.tvIndex);
        TextView tvProductName = view.findViewById(R.id.tvProductNameShopping);
        TextView tvProductQuantity = view.findViewById(R.id.tvProductQuantityShopping);
        TextView tvProductTotal = view.findViewById(R.id.tvProductTotal);

        tvIndex.setText(productDelivery.getIndex() + "");
        tvProductName.setText(productDelivery.getName() + "");
        tvProductQuantity.setText(productDelivery.getQuantity() + "");
        tvProductTotal.setText(productDelivery.getTotal() + "₪");

        return view;
    }
}
