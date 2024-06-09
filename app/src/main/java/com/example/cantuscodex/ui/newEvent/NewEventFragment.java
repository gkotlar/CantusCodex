package com.example.cantuscodex.ui.newEvent;


import com.example.cantuscodex.adapter.SongAdapter;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.databinding.FragmentNewEventBinding;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class NewEventFragment extends Fragment implements SongAdapter.OnSongSelectedListener {

    private FragmentNewEventBinding binding;
    private static final String TAG = "NewEventFragment";
    private static final String REQUIRED = "Required";
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Calendar cldr1;
    private Calendar cldr2;
    private SongAdapter mSongAdapter;

    private ArrayList<Song> songs;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewEventViewModel newEventViewModel =
                new ViewModelProvider(this).get(NewEventViewModel.class);

        binding = FragmentNewEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        can transfer the text to the viewmodel
//        final TextView textViewApplicationDeadline = binding.textApplicationDeadlineNewEvent;
//        final TextView textViewStartDate = binding.textStartDateNewEvent
        final TextView textViewLocation = binding.textLocationNewEvent;

        final TextView textViewName = binding.textNameNewEvent;
        final TextView textViewDescription = binding.textDescriptionNewEvent;
        final TextView textViewOrganizers = binding.textOrganizersNewEvent;
        final TextView textViewPaxLimit = binding.textParticipantLimitNewEvent;

        final Button btnCreateNewEvent = binding.btnCreateNewEvent;
        final Button btnCancelNewEvent = binding.btnCancelNewEvent;

        newEventViewModel.getNameText().observe(getViewLifecycleOwner(), textViewName::setText);
        newEventViewModel.getOrganizersText().observe(getViewLifecycleOwner(), textViewOrganizers::setText);
        newEventViewModel.getParticipantLimitText().observe(getViewLifecycleOwner(), textViewPaxLimit::setText);
        newEventViewModel.getDescriptionText().observe(getViewLifecycleOwner(), textViewDescription::setText);
        newEventViewModel.getLocationText().observe(getViewLifecycleOwner(), textViewLocation::setText);

        newEventViewModel.getCreateText().observe(getViewLifecycleOwner(), btnCreateNewEvent::setText);
        newEventViewModel.getCancelText().observe(getViewLifecycleOwner(), btnCancelNewEvent::setText);

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        cldr1 = Calendar.getInstance();
        cldr2 = Calendar.getInstance();
        songs = new ArrayList<>();

        binding.btnCreateNewEvent.setOnClickListener(v -> submitPost());
        binding.btnCancelNewEvent.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        binding.textApplicationDeadlineNewEvent.setOnClickListener(v -> getDate(v, cldr1));
        binding.textStartDateNewEvent.setOnClickListener(v -> getDate(v,cldr2));

        Query query = firestore.collection(Song.FIELD_CLASSNAME)
                .orderBy(Song.FIELD_NAME, Query.Direction.DESCENDING)
                .limit(50);

        mSongAdapter = new SongAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    binding.newEventRecyclerView.setVisibility(View.GONE);
                //    binding.textEvents.setVisibility(View.VISIBLE);
                } else {
                    binding.newEventRecyclerView.setVisibility(View.VISIBLE);
                //    binding.textEvents.setVisibility(View.GONE);
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
        binding.newEventRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.newEventRecyclerView.setAdapter(mSongAdapter);
    }

    private void getDate(View viewer, Calendar cldr){
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minute = cldr.get(Calendar.MINUTE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(NewEventFragment.this.requireContext(),
                (view, year1, month1, day1) -> {
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.YEAR, year1);
                    mCalendar.set(Calendar.MONTH, month1);
                    mCalendar.set(Calendar.DAY_OF_MONTH, day1);

                    // on below line we are initializing our Time Picker Dialog after selecting the date from the date picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(NewEventFragment.this.getContext(),
                            (view1, hour1, minute1) -> {
                                Calendar mCalendar1 = Calendar.getInstance();
                                mCalendar1.set(Calendar.HOUR, hour1);
                                mCalendar1.set(Calendar.MINUTE, minute1);
                                cldr.set(year1, month1, day1, hour1, minute1);
                                int id = viewer.getId();
                                Button btn = viewer.findViewById(id);
                                btn.setText(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(cldr.getTime()));
                            }, hour, minute, false);
                    timePickerDialog.show();
                },year, month, day );
        datePickerDialog.show();
    }
    private void submitPost() {
        final String name = binding.editNameNewEvent.getText().toString();
        final String paxLimit = binding.editParticipantLimitNewEvent.getText().toString();
        final String organizers = binding.editOrganizersNewEvent.getText().toString();
        final String description = binding.editDescriptionNewEvent.getText().toString();
        final String location = binding.editLocationNewEvent.getText().toString();
        final String announcer = mAuth.getUid();
        final Timestamp startDate = new Timestamp(cldr1.getTime());
        final Timestamp applicationDeadline = new Timestamp(cldr2.getTime());
        final Integer participantLimit = 10;
        //final ArrayList<DocumentReference> songs1 = songs;

        // Title is required
        if (TextUtils.isEmpty(name)) {
            binding.editNameNewEvent.setError(REQUIRED);
            return;
        }
        // description is required
        if (TextUtils.isEmpty(description)) {
            binding.editDescriptionNewEvent.setError(REQUIRED);
            return;
        }
        // origin is required
        if (TextUtils.isEmpty(paxLimit)) {
            binding.editParticipantLimitNewEvent.setError(REQUIRED);
            return;
        }
        // content is required
        if (TextUtils.isEmpty(organizers)) {
            binding.editOrganizersNewEvent.setError(REQUIRED);
            return;
        }
        // location is required
        if (TextUtils.isEmpty(location)) {
            binding.editLocationNewEvent.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();
        writeNewPost(announcer, name, startDate, applicationDeadline, participantLimit, location,  organizers, description, songs);
        NavHostFragment.findNavController(NewEventFragment.this).popBackStack();
        setEditingEnabled(true);
    }

    private void setEditingEnabled(boolean enabled) {
        binding.editOrganizersNewEvent.setEnabled(enabled);
        binding.editParticipantLimitNewEvent.setEnabled(enabled);
        binding.editNameNewEvent.setEnabled(enabled);
        binding.editDescriptionNewEvent.setEnabled(enabled);
        binding.btnCreateNewEvent.setEnabled(enabled);
    }

    private void writeNewPost(String announcer,
                              String name,
                              Timestamp startDate,
                              Timestamp applicationDeadline,
                              Integer participantLimit,
                              String location,
                              String organizers,
                              String description,
                              ArrayList<Song> songs) {

        Log.d(TAG, "writeNewPost() returned: " + songs.toString());
        Event event = new Event(announcer, name, startDate, applicationDeadline, participantLimit, location,  organizers, description);
        Map<String, Object> eventValues = event.toMap();
        firestore.collection(Event.FIELD_CLASSNAME).add(eventValues)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    songs.forEach(song -> firestore.collection(Event.FIELD_CLASSNAME)
                            .document(documentReference.getId())
                    .collection(Song.FIELD_CLASSNAME).add(song));
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error adding document", e));
    }

    @Override
    public void onStart() {
        super.onStart();
        mSongAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSongAdapter.stopListening();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSongSelected(DocumentSnapshot song) {
        songs.add(song.toObject(Song.class));
    }

}