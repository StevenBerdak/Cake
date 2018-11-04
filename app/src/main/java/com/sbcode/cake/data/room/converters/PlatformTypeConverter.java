package com.sbcode.cake.data.room.converters;

import android.arch.persistence.room.TypeConverter;

import com.sbcode.cake.data.PlatformType;

/**
 * Converts PlatformType objects to String objects so that they can be stored properly in the
 * SQL database.
 */
public class PlatformTypeConverter {

    @TypeConverter
    public static String toString(PlatformType platformType) {
        return platformType.toString();
    }

    @TypeConverter
    public static PlatformType toPlatformType(String str) {
        return PlatformType.valueOf(str);
    }
}
