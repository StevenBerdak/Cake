package com.sbcode.cake.data.platforms.utils;

import com.sbcode.cake.data.room.models.Slice;

public class TwitterUtils implements PlatformUtils {


    @Override
    public long getIndicatorLong(Slice slice) {
        return Long.valueOf(slice.platformAuthorId);
    }
}
