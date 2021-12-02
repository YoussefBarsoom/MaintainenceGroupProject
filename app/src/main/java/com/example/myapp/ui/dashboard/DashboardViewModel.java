package com.example.myapp.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapp.Login.MY_PREFS_NAME;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
      //  SharedPreferences prefs = .getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
      //   String userType = prefs.getString("type","No name defined");
    }

    public LiveData<String> getText() {
        return mText;
    }
}