package com.example.cantuscodex.ui.details;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.databinding.FragmentSongDetailsBinding;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SongDetailsFragment extends Fragment {

    private static final String TAG = "SongDetails";

    private FragmentSongDetailsBinding mBinding;
    private DocumentReference mSongRef;
    private FirebaseFirestore mFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSongDetailsBinding
                .inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bdl = getArguments();

        String songId = "";
        try {
            songId = bdl.getString("id");
            Log.i(TAG, "onViewCreated: "+ songId);
        }catch(final Exception e) {
            Log.e(TAG, "onViewCreated: ", e);
            // Do nothing
        }
        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Get reference to the restaurant
        mSongRef = mFirestore.collection(Song.FIELD_CLASSNAME).document(songId);
        mSongRef.get().addOnSuccessListener(documentSnapshot -> {
            Song song = documentSnapshot.toObject(Song.class);
            onSongLoaded(song);
        });

        mBinding.fabDeleteSong.setVisibility(View.VISIBLE);
        mBinding.fabDeleteSong.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
            mSongRef.delete();
        });
    }
    /**
     * Listener for the Restaurant document ({@link #mSongRef}).
     */
//    @Override
//    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
//        if (e != null) {
//            Log.w(TAG, "restaurant:onEvent", e);
//            return;
//        }
//        onSongLoaded(snapshot.toObject(Song.class));
//        Log.i(TAG, "onEvent: " +Song.class.toString());
//    }

    private void onSongLoaded(Song song) {
        mBinding.textContentNewSong.setText(song.getContent());
        mBinding.textNameNewSong.setText(song.getName());
        mBinding.textOriginNewSong.setText(song.getOrigin());
    }

}
