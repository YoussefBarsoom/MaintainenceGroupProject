package com.example.myapp.ui.maintain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MaintanenceViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MaintanenceViewModel() {

    }

    public LiveData<String> getText() {
        return mText;
    }
}