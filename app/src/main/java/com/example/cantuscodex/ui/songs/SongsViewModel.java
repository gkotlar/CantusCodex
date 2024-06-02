package com.example.cantuscodex.ui.songs;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SongsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SongsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is songs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
