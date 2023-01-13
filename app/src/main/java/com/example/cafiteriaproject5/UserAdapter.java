package com.example.cafiteriaproject5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {
    private Context context;
    private ArrayList<User> userArrayList;

    public UserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> userArrayList){
        super(context, resource, userArrayList);
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.user_row, null, false);
        User user = userArrayList.get(position);
        view.setLayoutDirection(getContext().getResources().getConfiguration().getLayoutDirection());

        TextView tvUserGmail = view.findViewById(R.id.tvUserGmail);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserLastname = view.findViewById(R.id.tvUserLastname);
        TextView tvUserGrade = view.findViewById(R.id.tvUserGrade);
        TextView tvUserMoney = view.findViewById(R.id.tvUserMoney);


        tvUserGmail.setText(user.getGmail() + "");
        tvUserName.setText(user.getFirstName() + "");
        tvUserLastname.setText(user.getLastName() + "");
        tvUserGrade.setText(user.getGrade() + "");
        tvUserMoney.setText(user.getMoney() + "₪");

        return view;
    }

}
