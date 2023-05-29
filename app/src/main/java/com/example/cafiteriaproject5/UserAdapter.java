package com.example.cafiteriaproject5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    Context context;
    List<User> userList;

    private OnItemClickListener listener;

    //interface for clicking the imageView
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    //a method for clicking the image
    public void setOnItemClickListener(UserAdapter.OnItemClickListener clickListener){
        listener = clickListener;
    }

    public UserAdapter(@NonNull Context context, @NonNull List<User> userList){
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View v = layoutInflater.inflate(R.layout.user_row, parent, false);
        return new UsersViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.tvUserGmail.setText(userList.get(position).getGmail());
        holder.tvUserName.setText(userList.get(position).getFirstName());
        holder.tvUserLastname.setText(userList.get(position).getLastName());
        holder.tvUserGrade.setText(userList.get(position).getGrade());
        holder.tvUserMoney.setText(userList.get(position).getMoney()+"");
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
