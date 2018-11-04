package com.sbcode.cake.ui.slices.adapter;

import android.support.v7.util.DiffUtil;

import com.sbcode.cake.data.room.models.Slice;

import java.util.List;

/**
 * Calculated the differences in the current and a new list of Slice data for optimised updates
 * to an adapter.
 */
class SlicesDiffCallback extends DiffUtil.Callback {

    private List<Slice> mOutBoundList, mInBoundList;

    void setLists(List<Slice> outBoundList, List<Slice> inBoundList) {
        this.mOutBoundList = outBoundList;
        this.mInBoundList = inBoundList;
    }

    @Override
    public int getOldListSize() {
        if (mOutBoundList == null)
            return 0;

        return mOutBoundList.size();
    }

    @Override
    public int getNewListSize() {
        if (mInBoundList == null)
            return 0;

        return mInBoundList.size();
    }

    @Override
    public boolean areItemsTheSame(int outPosition, int inPosition) {
        return mOutBoundList.get(outPosition).platformPostId
                .equals(mInBoundList.get(inPosition).platformPostId);
    }

    @Override
    public boolean areContentsTheSame(int outPosition, int inPosition) {

        Slice outboundSlice = mOutBoundList.get(outPosition);
        Slice inboundSlice = mInBoundList.get(inPosition);

        if (outboundSlice.sliceMessage == null && inboundSlice.sliceMessage == null)
            return true;
        else if (inboundSlice.sliceMessage == null || outboundSlice.sliceMessage == null)
            return false;
        else
            return outboundSlice.sliceMessage.equals(inboundSlice.sliceMessage) &&
                    outboundSlice.totalLikes.equals(inboundSlice.totalLikes) &&
                    outboundSlice.totalComments.equals(inboundSlice.totalComments);
    }
}
