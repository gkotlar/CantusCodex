package com.example.cantuscodex.ui.details;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.job.JobScheduler;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cantuscodex.R;
import com.example.cantuscodex.adapter.SongAdapter;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.data.users.model.User;
import com.example.cantuscodex.databinding.FragmentEventDetailsBinding;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;

public class EventDetailsFragment extends Fragment implements
        SongAdapter.OnSongSelectedListener{

    private static final String TAG = "EventDetails";
    private FragmentEventDetailsBinding mBinding;
    private DocumentReference mEventRef;
    private FirebaseFirestore mFirestore;
    private SongAdapter mSongAdapter;
    private SharedPreferences mPreferences;
    private final String sharedPrefsFile = "com.example.cantuscodex";
    private boolean mIsAdmin;
    private static JobScheduler mScheduler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentEventDetailsBinding
                .inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        mPreferences = getActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        mIsAdmin = mPreferences.getBoolean(User.FIELD_IS_ADMIN, false);
        mScheduler = (JobScheduler) view.getContext().getSystemService(JOB_SCHEDULER_SERVICE);

        doMagic(getArguments());
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
    public void onSongSelected(DocumentSnapshot song) {
        Bundle s = new Bundle(1);
        s.putString("id", song.getId());
        NavHostFragment.findNavController(this).navigate(R.id.nav_song_details, s);
    }

    private void onEventLoaded(DocumentSnapshot documentSnapshot) {
        Event event = documentSnapshot.toObject(Event.class);

        mBinding.textNameEvent.setText(event.getName());
        mBinding.textDescriptionEvent.setText(event.getDescription());
        mBinding.textLocationEvent.setText(event.getLocation());
        mBinding.textOrganizersEvent.setText(event.getOrganizers());
        mBinding.textParticipantLimitEvent.setText(event.getParticipantLimit().toString());
        mBinding.textApplicationDeadlineEvent.setText(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(event.getApplicationDeadline().toDate()));
        mBinding.textStartDateEvent.setText(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT ).format(event.getStartDate().toDate()));

        CollectionReference ref = mFirestore.collection(Event.FIELD_CLASSNAME).document(documentSnapshot.getId()).collection(Song.FIELD_CLASSNAME);

        Query songsQuery = ref
                .orderBy(Song.FIELD_NAME, Query.Direction.DESCENDING)
                .limit(10);

        mSongAdapter = new SongAdapter(songsQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mBinding.songsEventsRecyclerView.setVisibility(View.GONE);
                    //mBinding.viewEmptyRatings.setVisibility(View.VISIBLE);
                } else {
                    mBinding.songsEventsRecyclerView.setVisibility(View.VISIBLE);
                    // mBinding.viewEmptyRatings.setVisibility(View.GONE);
                }
            }
        };
        mBinding.songsEventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.songsEventsRecyclerView.setAdapter(mSongAdapter);
    }

    public void doMagic(Bundle bundle){
        try {
            String eventId = bundle.getString("id");
            boolean bookmarked = bundle.getBoolean("bookmarked");

            // Get reference to the event
            if (bookmarked){
                mEventRef = mFirestore.collection("user_events").document(eventId);
            }else {
                mEventRef = mFirestore.collection(Event.FIELD_CLASSNAME).document(eventId);
            }

            mEventRef.get().addOnSuccessListener(documentSnapshot -> {
                Event event = documentSnapshot.toObject(Event.class);
                onEventLoaded(documentSnapshot);

                if (mIsAdmin){
                    mBinding.fabDeleteEvent.setVisibility(View.VISIBLE);
                    mBinding.fabDeleteEvent.setOnClickListener(v -> {
                        cancelJob(event);
                        Navigation.findNavController(v).popBackStack();
                        mEventRef.delete();
                    });
                }

            });

        }catch(final Exception e) {
            mBinding.textNameEvent.setText("Err");
            Log.e(TAG, "onViewCreated: ", e);
            // Do nothing
        }

    }

    private void cancelJob(Event event) {
        if (mScheduler != null){
            mScheduler.cancel(event.getStartDate().getNanoseconds());
        }
    }

}
