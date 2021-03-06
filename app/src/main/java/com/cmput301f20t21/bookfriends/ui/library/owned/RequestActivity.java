/*
 * RequestActivity.java
 * Version: 1.0
 * Date: November 4, 2020
 * Copyright (c) 2020. Book Friends Team
 * All rights reserved.
 * github URL: https://github.com/CMPUT301F20T21/Book_Friends
 */

package com.cmput301f20t21.bookfriends.ui.library.owned;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput301f20t21.bookfriends.R;
import com.cmput301f20t21.bookfriends.entities.Book;
import com.cmput301f20t21.bookfriends.entities.Request;
import com.cmput301f20t21.bookfriends.ui.component.BaseDetailActivity;
import com.cmput301f20t21.bookfriends.utils.ImagePainter;

import java.util.ArrayList;

/**
 * An activity to view all requests for a requested book
 */
public class RequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView isbnTextView;
    private TextView bookStatus;
    private ImageView bookImage;
    private Book book;

    private RequestViewModel vm;

    /**
     * Called when creating the activity view
     * @param savedInstanceState the saved objects, should contain nothing for this activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_requests);

        // bind the text view
        titleTextView = findViewById(R.id.detail_title);
        authorTextView = findViewById(R.id.detail_author);
        isbnTextView = findViewById(R.id.detail_ISBN);
        // the text of the book status might change according to user action
        bookStatus = findViewById(R.id.status_text_view);
        bookImage = findViewById(R.id.book_image_view);

        vm = new ViewModelProvider(this).get(RequestViewModel.class);

        // getting book ID from previous activity
        String bookId = getIntent().getStringExtra(OwnedListFragment.VIEW_REQUEST_KEY);
        // since the data is mutable live, set the observer so the content will change accordingly
        vm.getBook(bookId).observe(this, book -> {
            this.book = book;
            titleTextView.setText(book.getTitle());
            authorTextView.setText(book.getAuthor());
            isbnTextView.setText(book.getIsbn());
            bookStatus.setText(book.getStatus().toString().toLowerCase());
            ImagePainter.paintImage(bookImage, book.getImageUrl());
        });

        // set up the back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_white_18);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.request_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the requests live data. fetched once, but we keep the reference for multiple usage
        LiveData<ArrayList<Request>> requestsLiveData = vm.getRequests(bookId);
        requestAdapter = new RequestAdapter(requestsLiveData.getValue());
        requestAdapter.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
            @Override
            public void onRejectClick(int position) {
                removeItem(position);
            }

            @Override
            public void onAcceptClick(int position) {
                openDialog(position);
            }
        });

        recyclerView.setAdapter(requestAdapter);
        // see https://github.com/CMPUT301F20T21/Book_Friends/pull/90/files#r516310260
        requestsLiveData.observe(this, requests -> requestAdapter.notifyDataSetChanged());
        // we don't update a single item for simplicity, instead, just update requests array and let recycler figure out what changed
    }

    /**
     * setup the back button on the title
     *
     * @param item the item that is clicked
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(BaseDetailActivity.BOOK_DATA_KEY, book);
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * function to remove an item when we click on Reject button
     *
     * @param position that needs removing
     */
    public void removeItem(int position) {
        if (position != RecyclerView.NO_POSITION) {
            vm.removeRequest(position);
            // don't notify changes, let's let the requests array observer do everything for us
        }
    }

    /**
     * When user click on the accept button
     * popup a dialog to prompt user about their action:
     * accept one item and remove all other items
     *
     * @param position that we accept the item
     */
    public void openDialog(int position) {
        LayoutInflater inflater = getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater.inflate(R.layout.dialog_confirm, null))
                .setTitle(getString(R.string.accept_this_request))
                .setPositiveButton(R.string.edit_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MapDialog mapDialog = new MapDialog(getApplicationContext(), vm, position);
                        mapDialog.show(getSupportFragmentManager(), "map");
                    }
                })
                .setNegativeButton(R.string.edit_cancel, null)
                .show();
    }
}
