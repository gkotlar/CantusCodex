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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cantuscodex.R;
import com.example.cantuscodex.adapter.SongAdapter;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.databinding.FragmentEventDetailsBinding;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

//  implements EventListener<DocumentSnapshot>
public class EventDetailsFragment extends Fragment implements
        SongAdapter.OnSongSelectedListener{

    private static final String TAG = "EventDetails";

    private FragmentEventDetailsBinding mBinding;
    private DocumentReference mEventRef;
    private FirebaseFirestore mFirestore;
    private SongAdapter mSongAdapter;


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

        final Bundle bdl = getArguments();

        String eventId = "";
        try {
            eventId = bdl.getString("id");
            Log.i(TAG, "onViewCreated: "+ eventId);
        }catch(final Exception e) {
            Log.e(TAG, "onViewCreated: ", e);
            // Do nothing
        }
        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Get reference to the event
        mEventRef = mFirestore.collection(Event.FIELD_CLASSNAME).document(eventId);

        CollectionReference ref = mFirestore.collection(Song.FIELD_CLASSNAME);
        // Get ratings
        Query songsQuery = ref
                .orderBy(Song.FIELD_NAME, Query.Direction.DESCENDING)
                .limit(10);

        mEventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event event = documentSnapshot.toObject(Event.class);
                onEventLoaded(event);
            }
        });

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

        mBinding.fabDeleteEvent.setVisibility(View.VISIBLE);
        mBinding.fabDeleteEvent.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
            mEventRef.delete();
        });
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
    /**
     * Listener for the Restaurant document ({@link #mEventRef}).
     */
//    @Override
//    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
//        if (e != null) {
//            Log.w(TAG, "restaurant:onEvent", e);
//            return;
//        }
//        onEventLoaded(snapshot.toObject(Event.class));
//        Log.i(TAG, "onEvent: " +Event.class.toString());
//    }
    @Override
    public void onSongSelected(DocumentSnapshot song) {
        // Go to the details page for the selected restaurant
        Bundle s = new Bundle(1);
        s.putString("id", song.getId());
        NavHostFragment.findNavController(this).navigate(R.id.nav_song_details, s);
    }

    private void onEventLoaded(Event event) {
        mBinding.textNameEvent.setText(event.getName());
        mBinding.textDescriptionEvent.setText(event.getDescription());
        mBinding.textLocationEvent.setText(event.getLocation().toString());
        mBinding.textOrganizersEvent.setText(event.getOrganizers());

        mBinding.textParticipantLimitEvent.setText(event.getParticipantLimit().toString());

        mBinding.textApplicationDeadlineEvent.setText(event.getApplicationDeadline().toString());
        mBinding.textStartDateEvent.setText(event.getStartDate().toString());

    }

}
