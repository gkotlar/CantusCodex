package com.example.cantuscodex.ui.newSong;

import com.example.cantuscodex.R;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentNewSongBinding;


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
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewSongFragment extends Fragment {

    private FragmentNewSongBinding binding;
    private static final String TAG = "NewSongFragment";
    private static final String REQUIRED = "Required";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseFirestore firestore;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewSongViewModel newSongViewModel =
                new ViewModelProvider(this).get(NewSongViewModel.class);

        binding = FragmentNewSongBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewName = binding.textNameNewSong;
        final TextView textViewContent = binding.textContentNewSong;
        final TextView textViewOrigin = binding.textOriginNewSong;
        final TextView textViewDescription = binding.textDescriptionNewSong;

        final Button btnCreateNewSong = binding.btnCreateNewSong;
        final Button btnCancelNewSong = binding.btnCancelNewSong;

        newSongViewModel.getNameText().observe(getViewLifecycleOwner(), textViewName::setText);
        newSongViewModel.getContentText().observe(getViewLifecycleOwner(), textViewContent::setText);
        newSongViewModel.getOriginText().observe(getViewLifecycleOwner(), textViewOrigin::setText);
        newSongViewModel.getDescriptionText().observe(getViewLifecycleOwner(), textViewDescription::setText);

        newSongViewModel.getCreateText().observe(getViewLifecycleOwner(), btnCreateNewSong::setText);
        newSongViewModel.getCancelText().observe(getViewLifecycleOwner(), btnCancelNewSong::setText);

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.btnCreateNewSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        binding.btnCancelNewSong.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void submitPost() {
        final String name = binding.editNameNewSong.getText().toString();
        final String content = binding.editContentNewSong.getText().toString();
        final String origin = binding.editOriginNewSong.getText().toString();
        final String description = binding.editDescriptionNewSong.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(name)) {
            binding.editNameNewSong.setError(REQUIRED);
            return;
        }
        // description is required
        if (TextUtils.isEmpty(description)) {
            binding.editDescriptionNewSong.setError(REQUIRED);
            return;
        }
        // origin is required
        if (TextUtils.isEmpty(origin)) {
            binding.editOriginNewSong.setError(REQUIRED);
            return;
        }
        // content is required
        if (TextUtils.isEmpty(content)) {
            binding.editContentNewSong.setError(REQUIRED);
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
                            writeNewPost(userId, name, content, origin, description);
                        }

                        setEditingEnabled(true);
                        NavHostFragment.findNavController(NewSongFragment.this).popBackStack();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        binding.editContentNewSong.setEnabled(enabled);
        binding.editOriginNewSong.setEnabled(enabled);
        binding.editNameNewSong.setEnabled(enabled);
        binding.editDescriptionNewSong.setEnabled(enabled);
        binding.btnCreateNewSong.setEnabled(enabled);
    }

    private void writeNewPost(String id, String name, String content, String origin, String description) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        String key = mDatabase.child("songs").push().getKey();
        Log.d(TAG, "writeNewPost: key = " + key);
        Song song = new Song(id, name, content, origin, description);
        Map<String, Object> songValues = song.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/songs/" + key, songValues);
     //   childUpdates.put("/event-songs/" + id + "/" + key, songValues);

        mDatabase.updateChildren(childUpdates);


        firestore.collection("songs").add(song).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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