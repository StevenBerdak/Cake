package com.sbcode.cake.data.room.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.sbcode.cake.data.PlatformType;

@SuppressWarnings("WeakerAccess")
@Entity(tableName = Person.TABLE_NAME, indices = {@Index(value = {Person.COLUMN_PLATFORM_TYPE,
        Person.COLUMN_PLATFORM_USER_ID}, unique = true)})
public class Person {

    public static final String TABLE_NAME = "persons";
    public static final String COLUMN_PLATFORM_TYPE = "platform_type";
    public static final String COLUMN_PLATFORM_USER_ID = "platform_user_id";
    public static final String COLUMN_USER_DISPLAY_HANDLE = "user_display_handle";
    public static final String COLUMN_USER_IMAGE_URL = "user_image_url";


    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = COLUMN_PLATFORM_TYPE)
    public PlatformType platformType;
    @ColumnInfo(name = COLUMN_PLATFORM_USER_ID)
    public String platformUserId;
    @ColumnInfo(name = COLUMN_USER_DISPLAY_HANDLE)
    public String userDisplayHandle;
    @ColumnInfo(name = COLUMN_USER_IMAGE_URL)
    public String userImageUrl;
}
