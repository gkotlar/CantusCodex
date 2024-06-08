package com.example.cantuscodex.ui.bookmarked_events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookmarkedEventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BookmarkedEventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("No events present");
    }

    public LiveData<String> getText() {
        return mText;
    }
}