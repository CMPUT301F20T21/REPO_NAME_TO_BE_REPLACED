/*
 * SearchedUserListAdapter.java
 * Version: 1.0
 * Date: November 4, 2020
 * Copyright (c) 2020. Book Friends Team
 * All rights reserved.
 * github URL: https://github.com/CMPUT301F20T21/Book_Friends
 */

package com.cmput301f20t21.bookfriends.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput301f20t21.bookfriends.R;
import com.cmput301f20t21.bookfriends.entities.User;

import java.util.ArrayList;

/**
 * Adapter used for {@link ProfileSearchFragment}
 */
public class SearchedUserListAdapter extends RecyclerView.Adapter<SearchedUserListAdapter.ViewHolder> {
    private ArrayList<User> users;
    private Context context;

    public SearchedUserListAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchedUserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searched_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = this.users.get(position);
        holder.onBind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameView;
        private User user;

        public ViewHolder(View v) {
            super(v);
            this.usernameView = v.findViewById(R.id.searched_user_username);
            v.setOnClickListener(itemView -> {
                Intent intent = new Intent(context, ProfileViewUserActivity.class);
                intent.putExtra(ProfileViewUserActivity.USERNAME_KEY, user.getUsername());
                context.startActivity(intent);
            });
        }

        public void onBind(User user) {
            this.user = user;
            this.usernameView.setText(user.getUsername());
        }
    }
}
