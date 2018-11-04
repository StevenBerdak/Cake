package com.sbcode.cake.logic.indicators;

/**
 * An interface that must be implemented by model objects when they are used by a
 * ListRangeIndicators concrete implementation.
 */
public interface IndicatorComparable {

    int getCompareValue();
}
