package com.example.cantuscodex.ui.bookmarked_events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cantuscodex.R;
import com.example.cantuscodex.adapter.EventAdapter;
import com.example.cantuscodex.databinding.FragmentBookmarkedEventsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


public class BookmarkedEventsFragment extends Fragment implements
        EventAdapter.OnEventSelectedListener{

    private FragmentBookmarkedEventsBinding binding;
    private EventAdapter mAdapter;
    private Query query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BookmarkedEventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(BookmarkedEventsViewModel.class);

        binding = FragmentBookmarkedEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.bookmarkedTextEvents;
        eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);



        return root;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        query = firestore.collection("user_events")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid())
                .limit(50);

        mAdapter = new EventAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    binding.bookmarkedEventsRecyclerView.setVisibility(View.GONE);
                    binding.bookmarkedTextEvents.setVisibility(View.VISIBLE);
                } else {
                    binding.bookmarkedEventsRecyclerView.setVisibility(View.VISIBLE);
                    binding.bookmarkedTextEvents.setVisibility(View.GONE);
                }
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e("TAG", "onError: ", e);
                // Show a snackbar on errors
                Snackbar.make(binding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };
        binding.bookmarkedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.bookmarkedEventsRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onEventSelected(DocumentSnapshot event) {
        Bundle s = new Bundle(1);
        s.putString("id", event.getId());
        s.putBoolean("bookmarked", true);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_event_details, s);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}