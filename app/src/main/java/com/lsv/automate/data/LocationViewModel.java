package com.lsv.automate.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private LocationRepository mRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Location>> mAllWords;

    public LocationViewModel(Application application) {
        super(application);
        mRepository = new LocationRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    public LiveData<List<Location>> getAllWords() {
        return mAllWords;
    }

    public void insert(Location word) {
        mRepository.insert(word);
    }

    public void update(Location word) {
        mRepository.update(word);
    }

    public LiveData<Location> getID(int id) {
        return mRepository.getID(id);
    }
}
