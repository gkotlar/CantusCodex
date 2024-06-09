package com.example.cantuscodex.adapter;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cantuscodex.R;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.databinding.CardEventsBinding;


import com.example.cantuscodex.notifications.NotificationJobService;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.Map;
import java.util.Objects;

/**
 * RecyclerView adapter for a list of {@link Event}.
 */
public class EventAdapter extends FirestoreAdapter<EventAdapter.ViewHolder> {

    public interface OnEventSelectedListener {
        void onEventSelected(DocumentSnapshot event);
    }
    private final OnEventSelectedListener mListener;
    private static JobScheduler mScheduler;

    public EventAdapter(Query query, EventAdapter.OnEventSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(CardEventsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
        mScheduler = (JobScheduler) holder.binding.getRoot().getContext().getSystemService(JOB_SCHEDULER_SERVICE);

    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardEventsBinding binding;

        public ViewHolder(CardEventsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }
        public void bind(final DocumentSnapshot snapshot,
                         final OnEventSelectedListener listener) {
            Event event = snapshot.toObject(Event.class);
          //  Resources resources = itemView.getResources();
            if (event != null) {
                binding.tvTitleEvents.setText(event.getName());
                binding.tvDescriptionEvents.setText(event.getDescription());
                binding.tvOrganizersEvents.setText(event.getOrganizers());
                binding.tvStartDateEvents.setText(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT ).format(event.getStartDate().toDate()));
            }
            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onEventSelected(snapshot);
                }
            });

            if (snapshot.get("id") != null){
                binding.ivBookmarkEvents.setVisibility(View.INVISIBLE);
            }else {
                binding.ivBookmarkEvents.setOnClickListener(v -> {
                    binding.ivBookmarkEvents.setImageResource(R.drawable.card_bookmark_filled);

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    Map<String, Object> result = null;

                    if (event != null) {
                        result = event.toMap();
                        result.put("userId", FirebaseAuth.getInstance().getUid());
                        result.put("id", snapshot.getId());
                    }

                    if (event != null) {
                        scheduleJob( event, binding.getRoot().getContext());
                    }

                    if (result != null) {
                        firestore.collection("user_events").document(snapshot.getId()).set(result);
                    }

                    firestore.collection(Event.FIELD_CLASSNAME).document(snapshot.getId())
                            .collection(Song.FIELD_CLASSNAME).get().addOnSuccessListener(queryDocumentSnapshots
                                    -> queryDocumentSnapshots.getDocuments().forEach(documentSnapshot
                                        -> firestore.collection("user_events").document(snapshot.getId())
                                            .collection(Song.FIELD_CLASSNAME).document(documentSnapshot.getId())
                                                .set(Objects.requireNonNull(documentSnapshot.toObject(Song.class)))));
                    });
            }
        }
    }

    private static void scheduleJob(Event event, Context context) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(Event.FIELD_NAME, event.getName());
        bundle.putInt(Event.FIELD_START_DATE, event.getStartDate().getNanoseconds());

        int time = Math.toIntExact(event.getStartDate().getSeconds() - Timestamp.now().getSeconds());

        ComponentName serviceName = new ComponentName(context.getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(event.getStartDate().getNanoseconds(), serviceName)
                .setMinimumLatency((time - 60*60) /*1 hour before the event start*/)
                .setExtras(bundle)
                .setPersisted(true);
        JobInfo myJobInfo = builder.build();
        mScheduler.schedule(myJobInfo);
    }

}
