package com.example.cantuscodex.ui.songs;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
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
import com.example.cantuscodex.adapter.SongAdapter;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentSongsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class SongsFragment extends Fragment implements
        SongAdapter.OnSongSelectedListener{
    private FragmentSongsBinding binding;
    private SongAdapter mAdapter;
    private Query query;
    private boolean mIsAdmin;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SongsViewModel songsViewModel =
                new ViewModelProvider(this).get(SongsViewModel.class);

        binding = FragmentSongsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String sharedPrefsFile = "com.example.cantuscodex";
        SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        mIsAdmin = mPreferences.getBoolean(User.FIELD_IS_ADMIN, false);
        final TextView textView = binding.textSongs;
        songsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mIsAdmin){
            binding.fabSongs.setVisibility(View.VISIBLE);
            binding.fabSongs.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_new_song));}

        // Initialize Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        query = firestore.collection("songs")
                .orderBy(Song.FIELD_NAME, Query.Direction.DESCENDING)
                .limit(50);

        mAdapter = new SongAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    binding.songsRecyclerView.setVisibility(View.GONE);
                    binding.textSongs.setVisibility(View.VISIBLE);
                } else {
                    binding.songsRecyclerView.setVisibility(View.VISIBLE);
                    binding.textSongs.setVisibility(View.GONE);
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
        binding.songsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.songsRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onSongSelected(DocumentSnapshot song) {
        Bundle s = new Bundle(1);
        s.putString("id", song.getId());
        s.putString("from", "songs");
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_song_details, s);
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