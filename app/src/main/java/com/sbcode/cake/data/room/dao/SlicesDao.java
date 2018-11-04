package com.sbcode.cake.data.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.room.models.Slice;

import java.util.List;

/**
 * Dao interface for handling Persons table operations.
 */
@SuppressWarnings("UnusedReturnValue")
@Dao
public interface SlicesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<Slice> slices);

    @Query("SELECT * FROM " + Slice.TABLE_NAME + " ORDER BY " + Slice.COLUMN_CREATED_DATE + " DESC")
    List<Slice> queryAllForList();

    @Query("SELECT * FROM " + Slice.TABLE_NAME + " ORDER BY " + Slice.COLUMN_CREATED_DATE + " DESC")
    LiveData<List<Slice>> queryAllForLiveData();

    @Query("SELECT * FROM " + Slice.TABLE_NAME + " WHERE " + Slice.COLUMN_PLATFORM_TYPE + " LIKE " +
            ":platformType ORDER BY " + Slice.COLUMN_CREATED_DATE + " DESC")
    List<Slice> queryAllByPlatformForList(PlatformType platformType);

    @Query("SELECT * FROM " + Slice.TABLE_NAME + " WHERE " + Slice.COLUMN_PLATFORM_TYPE + " LIKE " +
            " :platformType AND " + Slice.COLUMN_PLATFORM_POST_ID + " IN (:idList)")
    List<Slice> queryByPlatformAndPlatformIdForList(PlatformType platformType,
                                                                  List<String> idList);

    @Update()
    int update(List<Slice> slices);

    @Delete
    int delete(List<Slice> slices);
}
