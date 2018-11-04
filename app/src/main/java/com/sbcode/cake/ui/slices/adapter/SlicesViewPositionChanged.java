package com.sbcode.cake.ui.slices.adapter;

import android.view.View;

/**
 * A callback for notifying that the view position has changed and providing the current position
 * and associated view object.
 */
interface SlicesViewPositionChanged {

    void onViewPositionChanged(View view, int position);
}
