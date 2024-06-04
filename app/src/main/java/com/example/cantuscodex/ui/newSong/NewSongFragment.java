package com.example.cantuscodex.ui.newSong;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cantuscodex.databinding.FragmentNewSongBinding;

public class NewSongFragment extends Fragment {

    private FragmentNewSongBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewSongViewModel newSongViewModel =
                new ViewModelProvider(this).get(NewSongViewModel.class);

        binding = FragmentNewSongBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewName = binding.textNameNewSong;
        newSongViewModel.getNameText().observe(getViewLifecycleOwner(), textViewName::setText);

        final TextView textViewContent = binding.textContentNewSong;
        newSongViewModel.getContentText().observe(getViewLifecycleOwner(), textViewContent::setText);

        final TextView textViewOrigin = binding.textOriginNewSong;
        newSongViewModel.getOriginText().observe(getViewLifecycleOwner(), textViewOrigin::setText);

        final TextView textViewDescription = binding.textDescriptionNewSong;
        newSongViewModel.getDescriptionText().observe(getViewLifecycleOwner(), textViewDescription::setText);

        final EditText editTextName = binding.editNameNewSong;
        newSongViewModel.getNameEditText().observe(getViewLifecycleOwner(), t1 -> editTextName.getText());

        final EditText editTextContent = binding.editContentNewSong;
        newSongViewModel.getContentEditText().observe(getViewLifecycleOwner(), editTextContent::setText);

        final EditText editTextOrigin = binding.editOriginNewSong;
        newSongViewModel.getOriginEditText().observe(getViewLifecycleOwner(), editTextOrigin::setText);

        final EditText editTextDescription = binding.editDescriptionNewSong;
        newSongViewModel.getDescriptionEditText().observe(getViewLifecycleOwner(), t -> editTextDescription.getText());

        final Button btnCreateNewSong = binding.btnCreateNewSong;
        newSongViewModel.getCreateText().observe(getViewLifecycleOwner(), btnCreateNewSong::setText);
        btnCreateNewSong.setOnClickListener(view -> newSongViewModel.btnCreate());

        final Button btnCancelNewSong = binding.btnCancelNewSong;
        newSongViewModel.getCancelText().observe(getViewLifecycleOwner(), btnCancelNewSong::setText);
        btnCancelNewSong.setOnClickListener(view -> Navigation.findNavController(view).popBackStack());



        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }


//    private NewSongViewModel mViewModel;
//
//    public static NewSongFragment newInstance() {
//        return new NewSongFragment();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_new_song, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(NewSongViewModel.class);
//        // TODO: Use the ViewModel
//    }

}