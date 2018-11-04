package com.sbcode.cake.data.platforms.interfaces;

import android.content.Context;

import com.sbcode.cake.data.PlatformType;

public interface PlatformRepository {

    void queryInitial(Context context);

    void queryBefore(Context context, long beforeValue);

    void queryAfter(Context context, long afterValue);

    void queryRange(Context context, long startRange, long stopRange);

    void deleteAllData(Context context);

    void increaseThrottleValue();

    boolean throttleLimitReached();

    PlatformType getPlatform();
}
