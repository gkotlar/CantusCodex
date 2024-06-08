package com.example.cantuscodex.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cantuscodex.R;
import com.example.cantuscodex.data.songs.model.Song;
import com.example.cantuscodex.databinding.CardSongsBinding;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of {@link Song}.
 */
public class SongAdapter extends FirestoreAdapter<SongAdapter.ViewHolder> {

    public interface OnSongSelectedListener {
        void onSongSelected(DocumentSnapshot song);
    }
    private OnSongSelectedListener mListener;
    public SongAdapter(Query query, OnSongSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(CardSongsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardSongsBinding binding;

        public ViewHolder(CardSongsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final  DocumentSnapshot snapshot,
                         final OnSongSelectedListener listener) {

            Song song = snapshot.toObject(Song.class);
            Resources resources = itemView.getResources();


            binding.tvName.setText(song.getName());
            binding.tvDescription.setText(song.getDescription());
            binding.tvOrigin.setText(song.getOrigin());

            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onSongSelected(snapshot);
                    binding.getRoot().setBackgroundColor(binding.getRoot().getResources().getColor(R.color.purple_200));
                }
            });

            binding.ivBookmarkSongs.setOnClickListener(v ->
                    binding.ivBookmarkSongs.setImageResource(R.drawable.card_bookmark_filled));

        }
    }

}
