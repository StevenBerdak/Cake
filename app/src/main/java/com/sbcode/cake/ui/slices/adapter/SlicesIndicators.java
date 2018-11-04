package com.sbcode.cake.ui.slices.adapter;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.logic.indicators.ListRangeIndicators;

/**
 * Indicates the current position over a list of items by tracking the current position and
 * if the before or after bound is reached then performs the implemented action.
 */
public class SlicesIndicators extends ListRangeIndicators<Slice, PlatformType> {

    private final int mPrefetchDistance;

    SlicesIndicators(@SuppressWarnings("SameParameterValue") int prefetchDistance) {
        this.mPrefetchDistance = prefetchDistance;
    }

    @Override
    public int getPrefetchDistance() {
        return mPrefetchDistance;
    }

    @Override
    public PlatformType getCompareType(Slice slice) {
        return slice.platformType;
    }
}
