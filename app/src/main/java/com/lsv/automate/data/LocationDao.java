package com.lsv.automate.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * from automation ORDER BY id ASC")
    LiveData<List<Location>> getAlphabetizedWords();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Location word);

    @Query("DELETE FROM automation")
    void deleteAll();

    @Query("SELECT * FROM automation WHERE id=:id ")
    LiveData<Location> getByID(int id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Location word);
}
