package com.example.cantuscodex.ui.details;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.cantuscodex.R;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentSongDetailsBinding;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class SongDetailsFragment extends Fragment {

    private static final String TAG = "SongDetails";
    private FragmentSongDetailsBinding mBinding;
    private DocumentReference mSongRef;

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

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        String sharedPrefsFile = "com.example.cantuscodex";
        SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        boolean mIsAdmin = mPreferences.getBoolean(User.FIELD_IS_ADMIN, false);

        final Bundle bdl = getArguments();

        try {
            assert bdl != null;
            String songId = bdl.getString("id");
            String from = bdl.getString("from");
            boolean bookmarked = bdl.getBoolean("bookmarked", false);

            if(Objects.equals(from, "songs")){
                if (songId != null) {
                    mSongRef = mFirestore.collection(Song.FIELD_CLASSNAME).document(songId);
                }
            }else if(Objects.equals(from, "details")){
                String eventId = bdl.getString("event");

                if (bookmarked){
                    if (eventId != null && songId != null) {
                        mSongRef = mFirestore.collection("user_events").document(eventId)
                                .collection(Song.FIELD_CLASSNAME).document(songId);
                    }
                }else {
                    if (eventId != null && songId != null) {
                        mSongRef = mFirestore.collection(Event.FIELD_CLASSNAME).document(eventId)
                                .collection(Song.FIELD_CLASSNAME).document(songId);
                    }
                }
            }
            Log.i(TAG, "onViewCreated: "+ songId);
            mSongRef.get().addOnSuccessListener(documentSnapshot -> {
                Song song = documentSnapshot.toObject(Song.class);
                if (song != null) {
                    onSongLoaded(song);
                }
            });

        }catch(final Exception e) {
            mBinding.textNameNewSong.setText(R.string.error);
            Log.e(TAG, "onViewCreated: ", e);
            // Do nothing
        }

        if(mIsAdmin){
            mBinding.fabDeleteSong.setVisibility(View.VISIBLE);
            mBinding.fabDeleteSong.setOnClickListener(v -> {
                Navigation.findNavController(v).popBackStack();
                mSongRef.delete();
            });
        }
    }

    private void onSongLoaded(Song song) {
        mBinding.textContentNewSong.setText(song.getContent());
        mBinding.textNameNewSong.setText(song.getName());
        mBinding.textOriginNewSong.setText(song.getOrigin());
    }
}
