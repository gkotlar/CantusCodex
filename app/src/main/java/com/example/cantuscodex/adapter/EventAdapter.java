package com.example.cantuscodex.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cantuscodex.R;
import com.example.cantuscodex.data.events.model.Event;
import com.example.cantuscodex.databinding.CardEventsBinding;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of {@link Event}.
 */
public class EventAdapter extends FirestoreAdapter<EventAdapter.ViewHolder> {

    public interface OnEventSelectedListener {
        void onEventSelected(DocumentSnapshot event);
    }
    private OnEventSelectedListener mListener;
    public EventAdapter(Query query, EventAdapter.OnEventSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(CardEventsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
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


        public void bind(final  DocumentSnapshot snapshot,
                         final OnEventSelectedListener listener) {


            Event event = snapshot.toObject(Event.class);
            Resources resources = itemView.getResources();

            binding.tvTitleEvents.setText(event.getName());
            binding.tvDescriptionEvents.setText(event.getDescription());
            binding.tvOrganizersEvents.setText(event.getOrganizers());
            binding.tvStartDateEvents.setText(event.getStartDate().toDate().toString());


            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onEventSelected(snapshot);
                }
            });

            binding.ivBookmarkEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.ivBookmarkEvents.setImageResource(R.drawable.card_bookmark_filled);
                }
            });


        }
    }

}
