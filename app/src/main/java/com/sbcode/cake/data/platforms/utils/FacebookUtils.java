package com.sbcode.cake.data.platforms.utils;

import com.sbcode.cake.data.room.models.Slice;

public class FacebookUtils implements PlatformUtils {

    public long getNormalDate(long facebookDate) {
        return facebookDate * 1000;
    }

    public long getFacebookDate(long normalDate) {
        return normalDate / 1000;
    }

    @Override
    public long getIndicatorLong(Slice slice) {
        return slice.createdDate;
    }
}