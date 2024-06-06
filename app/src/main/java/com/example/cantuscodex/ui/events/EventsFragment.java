package com.example.cantuscodex.ui.events;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.cantuscodex.adapter.SongAdapter;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentEventsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


public class EventsFragment extends Fragment implements
        EventAdapter.OnEventSelectedListener{

    private FragmentEventsBinding binding;
    private EventAdapter mAdapter;
    private FirebaseFirestore firestore;
    private Query query;
    private boolean mIsAdmin;
    private SharedPreferences mPreferences;
    private String sharedPrefsFile = "com.example.cantuscodex";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEvents;
        eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        mPreferences = getActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        mIsAdmin = mPreferences.getBoolean(User.FIELD_IS_ADMIN, false);

        return root;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsAdmin){
            binding.fabEvents.setVisibility(View.VISIBLE);
            binding.fabEvents.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_new_event);
            Snackbar.make(v, "Events", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab_events).show();
            });
        }

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        // Get ${LIMIT} restaurants
        query = firestore.collection("events")
                .orderBy(Event.FIELD_START_DATE, Query.Direction.DESCENDING)
                .limit(50);

        mAdapter = new EventAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    binding.eventsRecyclerView.setVisibility(View.GONE);
                    binding.textEvents.setVisibility(View.VISIBLE);
                } else {
                    binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                    binding.textEvents.setVisibility(View.GONE);
                }
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(binding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };
        binding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onEventSelected(DocumentSnapshot event) {
        // Go to the details page for the selected restaurant
        Bundle s = new Bundle(1);
        s.putString("id", event.getId());
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