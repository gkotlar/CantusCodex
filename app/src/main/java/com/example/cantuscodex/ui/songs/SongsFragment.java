package com.example.cantuscodex.ui.songs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cantuscodex.R;
import com.example.cantuscodex.databinding.FragmentSongsBinding;

public class SongsFragment extends Fragment {

    private FragmentSongsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SongsViewModel songsViewModel =
                new ViewModelProvider(this).get(SongsViewModel.class);
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSongs;
        songsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.fabSongs.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.nav_new_song));

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}