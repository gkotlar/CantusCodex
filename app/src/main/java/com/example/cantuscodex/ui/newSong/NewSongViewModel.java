package com.example.cantuscodex.ui.newSong;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewSongViewModel extends ViewModel {
    private final MutableLiveData<String> nameText;
    private final MutableLiveData<String> contentText;
    private final MutableLiveData<String> originText;
    private final MutableLiveData<String> descriptionText;
    private final MutableLiveData<String> createText;
    private final MutableLiveData<String> cancelText;



    public NewSongViewModel() {
        nameText = new MutableLiveData<>();
        nameText.setValue("Name:");

        contentText = new MutableLiveData<>();
        contentText.setValue("Content:");

        originText = new MutableLiveData<>();
        originText.setValue("Origin:");

        descriptionText = new MutableLiveData<>();
        descriptionText.setValue("Description:");

        createText = new MutableLiveData<>();
        createText.setValue("Create");

        cancelText = new MutableLiveData<>();
        cancelText.setValue("Cancel");
    }

    public LiveData<String> getNameText() {
        return nameText;
    }
    public LiveData<String> getContentText() {
        return contentText;
    }
    public LiveData<String> getOriginText() {
        return originText;
    }
    public LiveData<String> getDescriptionText() {
        return descriptionText;
    }
    public LiveData<String> getCreateText() { return createText; }
    public LiveData<String> getCancelText() { return cancelText; }


}