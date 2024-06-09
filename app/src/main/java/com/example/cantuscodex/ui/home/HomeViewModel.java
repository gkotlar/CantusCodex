package com.example.cantuscodex.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
    }

    public LiveData<String> getText() {
        return mText;
    }
}