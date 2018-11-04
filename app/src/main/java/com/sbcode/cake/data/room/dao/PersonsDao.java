package com.sbcode.cake.data.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sbcode.cake.data.room.models.Person;

import java.util.List;

/**
 * Dao interface for handling Persons table operations.
 */
@SuppressWarnings("ALL")
@Dao
public interface PersonsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Person person);

    @Query("SELECT * FROM " + Person.TABLE_NAME + " WHERE " + Person.COLUMN_PLATFORM_TYPE +
            " LIKE :platformType")
    List<Person> loadPlatformUsersAsList(String platformType);

    @Query("SELECT * FROM " + Person.TABLE_NAME + " WHERE " + Person.COLUMN_PLATFORM_TYPE +
            " LIKE :platformType")
    LiveData<List<Person>> loadPlatformUsersAsLiveData(String platformType);

    @Query("DELETE FROM " + Person.TABLE_NAME + " WHERE " + Person.COLUMN_PLATFORM_TYPE +
            " LIKE :platformType")
    void deletePlatformUsersByPlatform(String platformType);

    @Update
    int update(List<Person> persons);

    @Delete
    int delete(List<Person> persons);
}
