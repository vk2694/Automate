package com.lsv.automate.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class LocationRepository {
    private LocationDao mWordDao;
    private LiveData<List<Location>> mAllWords;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public LocationRepository(Application application) {
        LocationRoomDatabase db = LocationRoomDatabase.getDatabase(application);
        mWordDao = db.locationDao();
        mAllWords = mWordDao.getAlphabetizedWords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Location>> getAllWords() {
        return mAllWords;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Location word) {
        LocationRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }

    void update(Location word) {
        LocationRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.update(word);
        });
    }

    public LiveData<Location> getID(int id) {
        return mWordDao.getByID(id);
    }
}
