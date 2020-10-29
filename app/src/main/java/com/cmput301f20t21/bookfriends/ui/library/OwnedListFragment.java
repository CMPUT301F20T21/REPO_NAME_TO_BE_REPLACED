package com.cmput301f20t21.bookfriends.ui.library;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmput301f20t21.bookfriends.R;
import com.cmput301f20t21.bookfriends.entities.Book;
import com.cmput301f20t21.bookfriends.enums.BOOK_ACTION;
import com.cmput301f20t21.bookfriends.enums.BOOK_ERROR;
import com.cmput301f20t21.bookfriends.ui.add.AddEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OwnedListFragment extends Fragment {
    public static final String BOOK_ACTION_KEY = "com.cmput301f20t21.bookfriends.BOOK_ACTION";

    private OwnedViewModel vm;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(OwnedViewModel.class);
        View root = inflater.inflate(R.layout.owned_list_book, container, false);
        final FloatingActionButton addBookButton = root.findViewById(R.id.add_button);

        addBookButton.setOnClickListener(
                view -> openAddEditActivity()
        );

        return root;
    }

    /**
     * function allows user to jump into the add/edit screen when click on the floating button
     */
    private void openAddEditActivity() {
        // TODO: Change the enum when calling the activity for editing
        Intent intent = new Intent(this.getActivity(), AddEditActivity.class);
        intent.putExtra(BOOK_ACTION_KEY, BOOK_ACTION.ADD);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.owned_recycler_list_book);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // set a temporary adapter
        recyclerView.setAdapter(new OwnedListAdapter(new ArrayList<>()));

        vm.getBooks().observe(getViewLifecycleOwner(), (List<Book> books) -> {
            mAdapter = new OwnedListAdapter(books);
            recyclerView.setAdapter(mAdapter);
        });

        vm.getUpdatedPosition().observe(getViewLifecycleOwner(), (Integer pos) -> {
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(pos);
            }
        });

        vm.getErrorMessageObserver().observe(getViewLifecycleOwner(), (BOOK_ERROR error) -> {
            if (error == BOOK_ERROR.FAIL_TO_GET_BOOKS) {
                Toast.makeText(getActivity(), getString(R.string.fail_to_get_books), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
