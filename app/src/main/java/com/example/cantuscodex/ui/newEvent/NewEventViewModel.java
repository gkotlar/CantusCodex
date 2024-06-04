package com.example.cantuscodex.ui.newEvent;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewEventViewModel extends ViewModel {
    private final MutableLiveData<String> nameText;
    private final MutableLiveData<String> descriptionText;
    private final MutableLiveData<String> organizersText;
    private final MutableLiveData<String> participantLimitText;
    private final MutableLiveData<String> createText;
    private final MutableLiveData<String> cancelText;



    public NewEventViewModel() {
        nameText = new MutableLiveData<>();
        nameText.setValue("Event Name:");

        organizersText = new MutableLiveData<>();
        organizersText.setValue("Organizers:");

        participantLimitText = new MutableLiveData<>();
        participantLimitText.setValue("Maximum participants:");

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
    public LiveData<String> getOrganizersText() {
        return organizersText;
    }
    public LiveData<String> getParticipantLimitText() {
        return participantLimitText;
    }
    public LiveData<String> getDescriptionText() {
        return descriptionText;
    }
    public LiveData<String> getCreateText() { return createText; }
    public LiveData<String> getCancelText() { return cancelText; }


}