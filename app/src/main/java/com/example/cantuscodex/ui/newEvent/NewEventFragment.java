package com.example.cantuscodex.ui.newEvent;

import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentNewEventBinding;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewEventFragment extends Fragment {

    private FragmentNewEventBinding binding;
    private static final String TAG = "NewEventFragment";
    private static final String REQUIRED = "Required";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseFirestore firestore;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewEventViewModel newEventViewModel =
                new ViewModelProvider(this).get(NewEventViewModel.class);

        binding = FragmentNewEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewAplicationDeadline = binding.textApplicationDeadlineNewEvent;
        final TextView textViewStartDate = binding.textStartDateNewEvent;
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

        newEventViewModel.getCreateText().observe(getViewLifecycleOwner(), btnCreateNewEvent::setText);
        newEventViewModel.getCancelText().observe(getViewLifecycleOwner(), btnCancelNewEvent::setText);

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.btnCreateNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        binding.btnCancelNewEvent.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void submitPost() {
        final String name = binding.editNameNewEvent.getText().toString();
        final String paxLimit = binding.editParticipantLimitNewEvent.getText().toString();
        final String organizers = binding.editOrganizersNewEvent.getText().toString();
        final String description = binding.editDescriptionNewEvent.getText().toString();
        final String announcer = mAuth.getUid();
        final Timestamp startDate = Timestamp.now();
        final Timestamp applicationDeadline = Timestamp.now();
        final Integer participantLimit = 10;
        final GeoPoint location = new GeoPoint(0,0);
        final ArrayList<DocumentReference> songs = new ArrayList<>();

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

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();

        final String userId = mAuth.getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // User is null, error out
                            Toast.makeText(getContext(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, name, startDate, applicationDeadline, participantLimit, location,  organizers, description, songs);
                        }

                        setEditingEnabled(true);
                        NavHostFragment.findNavController(NewEventFragment.this).popBackStack();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
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
                              GeoPoint location,
                              String organizers,
                              String description,
                              ArrayList<DocumentReference> songs) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        String key = mDatabase.child("events").push().getKey();
        Log.d(TAG, "writeNewPost: key = " + key);
        Event event = new Event(announcer, name, startDate, applicationDeadline, participantLimit, location,  organizers, description, songs);
        Map<String, Object> eventValues = event.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, eventValues);
        //   childUpdates.put("/event-events/" + id + "/" + key, eventValues);

        mDatabase.updateChildren(childUpdates);


        firestore.collection("events").add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

}