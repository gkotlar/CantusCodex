package com.example.cantuscodex.ui.events;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cantuscodex.R;
import com.example.cantuscodex.adapter.EventAdapter;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentEventsBinding;
import com.example.cantuscodex.ui.details.EventDetailsFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


public class EventsFragment extends Fragment implements
        EventAdapter.OnEventSelectedListener{
    private FragmentEventsBinding binding;
    private EventAdapter mAdapter;
    private Query query;
    private boolean mIsAdmin;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEvents;
        eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        String sharedPrefsFile = "com.example.cantuscodex";
        SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        mIsAdmin = mPreferences.getBoolean(User.FIELD_IS_ADMIN, false);

        return root;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (mIsAdmin){
            binding.fabEvents.setVisibility(View.VISIBLE);
            binding.fabEvents.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_new_event);
            Snackbar.make(v, "Events", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab_events).show();
            });
        }


        // Get the next events
        query = firestore.collection("events")
                .whereGreaterThan(Event.FIELD_APPLICATION_DEADLINE, Timestamp.now())
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
                Log.e("TAG", "onError: ", e);
                Snackbar.make(binding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };
        binding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventsRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onEventSelected(DocumentSnapshot event) {
        Bundle s = new Bundle(1);
        s.putString("id", event.getId());

        //see if the view is portrait or landscape, navigate to new fragment if portrait/ update other fragment if landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            EventDetailsFragment newFragment = new EventDetailsFragment();
            newFragment.setArguments(s);

            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment2, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        }else {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_event_details, s);
        }
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