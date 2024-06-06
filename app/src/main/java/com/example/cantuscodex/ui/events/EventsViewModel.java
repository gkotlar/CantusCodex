package com.example.cantuscodex.ui.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("No events present");
    }

    public LiveData<String> getText() {
        return mText;
    }
}