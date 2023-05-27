package com.example.cafiteriaproject5;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersViewHolder extends RecyclerView.ViewHolder{
    TextView tvUserGmail;
    TextView tvUserName;
    TextView tvUserLastname;
    TextView tvUserGrade;
    TextView tvUserMoney;
    ImageView deleteView;

    public UsersViewHolder(@NonNull View itemView, UserAdapter.OnItemClickListener listener) {
        super(itemView);
        tvUserGmail = itemView.findViewById(R.id.tvUserGmail);
        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvUserLastname = itemView.findViewById(R.id.tvUserLastname);
        tvUserGrade = itemView.findViewById(R.id.tvUserGrade);
        tvUserMoney = itemView.findViewById(R.id.tvUserMoney);
        deleteView = itemView.findViewById(R.id.ivDeleteUser);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick(getAdapterPosition());
            }
        });
    }
}
