package com.sbcode.cake.data.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.room.models.Update;

import java.util.List;

/**
 * /**
 * Dao interface for handling Updates table operations.
 */
@SuppressWarnings("UnusedReturnValue")
@Dao
public interface UpdatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<Update> updatesList);

    @Query("SELECT * FROM " + Update.TABLE_NAME + " WHERE " + Update.COLUMN_IS_CONSUMED +
            " LIKE :isConsumed")
    LiveData<List<Update>> queryAllByConsumedForLiveData(boolean isConsumed);

    @Query("UPDATE " + Update.TABLE_NAME + " SET " +
            Update.COLUMN_IS_CONSUMED + " = :isConsumed WHERE " +
            Update.COLUMN_PLATFORM_TYPE + " = :platformType AND " +
            Update.COLUMN_PLATFORM_POST_ID + " IN (:platformPostId)")
    void updateIsConsumedByPlatform(PlatformType platformType, List<String> platformPostId,
                                    boolean isConsumed);

    @Query("SELECT * FROM " + Update.TABLE_NAME + " WHERE " + Update.COLUMN_IS_CONSUMED +
            " LIKE :isConsumed")
    List<Update> queryAllByConsumedForList(boolean isConsumed);

    @Query("UPDATE " + Update.TABLE_NAME + " SET " + Update.COLUMN_IS_CONSUMED + " = 'true'")
    void consumeAllEntries();
}
