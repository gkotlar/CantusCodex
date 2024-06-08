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

import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentSongDetailsBinding;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SongDetailsFragment extends Fragment {

    private static final String TAG = "SongDetails";
    private FragmentSongDetailsBinding mBinding;
    private DocumentReference mSongRef;
    private FirebaseFirestore mFirestore;
    private SharedPreferences mPreferences;
    private final String sharedPrefsFile = "com.example.cantuscodex";

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
        mFirestore = FirebaseFirestore.getInstance();

        mPreferences = getActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        boolean mIsAdmin = mPreferences.getBoolean(User.FIELD_IS_ADMIN, false);

        final Bundle bdl = getArguments();

        String songId;
        try {
            songId = bdl.getString("id");
            Log.i(TAG, "onViewCreated: "+ songId);
            mSongRef = mFirestore.collection(Song.FIELD_CLASSNAME).document(songId);
            mSongRef.get().addOnSuccessListener(documentSnapshot -> {
                Song song = documentSnapshot.toObject(Song.class);
                onSongLoaded(song);
            });
        }catch(final Exception e) {
            mBinding.textNameNewSong.setText("Err");
            Log.e(TAG, "onViewCreated: ", e);
            // Do nothing
        }
        // Initialize Firestore
        // Get reference to the restaurant

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
