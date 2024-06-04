package com.example.cantuscodex.ui.newSong;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewSongViewModel extends ViewModel {
    private final MutableLiveData<String> nameText;
    private final MutableLiveData<String> contentText;
    private final MutableLiveData<String> originText;
    private final MutableLiveData<String> descriptionText;

    private final MutableLiveData<String> nameEditText;
    private final MutableLiveData<String> contentEditText;
    private final MutableLiveData<String> originEditText;
    private final MutableLiveData<String> descriptionEditText;


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

        nameEditText = new MutableLiveData<>();
        contentEditText = new MutableLiveData<>();
        originEditText = new MutableLiveData<>();
        descriptionEditText = new MutableLiveData<>();

        createText = new MutableLiveData<>();
        createText.setValue("Create");

        cancelText = new MutableLiveData<>();
        cancelText.setValue("Cancel");
    }

    public void btnCreate(){
        Log.d("TAG", "btnCreate: " + nameEditText.getValue());
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
    public LiveData<String> getNameEditText() {
        return nameEditText;
    }
    public LiveData<String> getContentEditText() {
        return contentEditText;
    }
    public LiveData<String> getOriginEditText() {
        return originEditText;
    }
    public LiveData<String> getDescriptionEditText() {
        return descriptionEditText;
    }
    public LiveData<String> getCreateText() { return createText; }
    public LiveData<String> getCancelText() { return cancelText; }


}