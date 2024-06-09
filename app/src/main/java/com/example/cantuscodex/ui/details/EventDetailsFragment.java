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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.Locale;

public class EventDetailsFragment extends Fragment implements
        SongAdapter.OnSongSelectedListener{

    private static final String TAG = "EventDetails";
    private FragmentEventDetailsBinding mBinding;
    private DocumentReference mEventRef;
    private SongAdapter mSongAdapter;
    private boolean mIsAdmin;
    private static JobScheduler mScheduler;
    private Query songsQuery;
    private boolean bookmarked;
    private String eventId;

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
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        String sharedPrefsFile = "com.example.cantuscodex";
        SharedPreferences mPreferences = requireActivity().getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
        mIsAdmin = mPreferences.getBoolean(User.FIELD_IS_ADMIN, false);


        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                eventId = bundle.getString("id");
            }
            if (bundle != null) {
                bookmarked = bundle.getBoolean("bookmarked");
            }

            if (bookmarked) {
                mEventRef = mFirestore.collection("user_events").document(eventId);
            } else {
                mEventRef = mFirestore.collection(Event.FIELD_CLASSNAME).document(eventId);
            }

            mEventRef.get().addOnSuccessListener(documentSnapshot -> {
                onEventLoaded(documentSnapshot);

                //FAB admin control for event and notification deletion and back navigation
                if (mIsAdmin) {
                    mBinding.fabDeleteEvent.setVisibility(View.VISIBLE);
                    mBinding.fabDeleteEvent.setOnClickListener(v -> {
                        Event event = documentSnapshot.toObject(Event.class);

                        mScheduler = (JobScheduler) requireContext().getSystemService(JOB_SCHEDULER_SERVICE);

                        if (mScheduler != null && event!=null){
                            mScheduler.cancel(event.getStartDate().getNanoseconds());
                        }

                        Navigation.findNavController(v).popBackStack();
                        mEventRef.delete();
                    });
                }
            });

            songsQuery = mEventRef.collection(Song.FIELD_CLASSNAME)
                    .orderBy(Song.FIELD_NAME, Query.Direction.DESCENDING)
                    .limit(10);

        } catch(final Exception e) {
        mBinding.textNameEvent.setText(R.string.error);
        Log.e(TAG, "onViewCreated: ", e);
        // Do nothing
    }

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



    private void onEventLoaded(DocumentSnapshot documentSnapshot) {

        Event event = documentSnapshot.toObject(Event.class);
        if (event != null) {
            mBinding.textNameEvent.setText(event.getName());
            mBinding.textDescriptionEvent.setText(event.getDescription());
            mBinding.textLocationEvent.setText(event.getLocation());
            mBinding.textOrganizersEvent.setText(event.getOrganizers());
            mBinding.textParticipantLimitEvent.setText(String.format(Locale.getDefault(),"%d", event.getParticipantLimit()));
            mBinding.textApplicationDeadlineEvent.setText(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(event.getApplicationDeadline().toDate()));
            mBinding.textStartDateEvent.setText(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT ).format(event.getStartDate().toDate()));
        }
    }

    @Override
    public void onSongSelected(DocumentSnapshot song) {
        Bundle s = new Bundle(1);
        s.putString("id", song.getId());
        s.putString("from", "details");
        s.putString("event", eventId);
        s.putBoolean("bookmarked", bookmarked);
        NavHostFragment.findNavController(this).navigate(R.id.nav_song_details, s);
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
        mBinding = null;
    }
}
