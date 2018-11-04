package com.sbcode.cake.logic.indicators;

import com.sbcode.cake.logic.callbacks.CallbackManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ListRangeIndicators<ItemType extends IndicatorComparable, IndicatorType> {

    private static final int ACTION_INITIAL = 0;
    private static final int ACTION_BEFORE = 1;
    private static final int ACTION_AFTER = 2;

    private List<ItemType> mData;
    private final HashMap<IndicatorType, RangeIndicator<ItemType>> mRangeIndicators;
    private final CallbackManager<ListRangeIndicatorsCallback<ItemType>> mCallbackManager;

    private boolean mTriggerOnce = false;
    private boolean mInitialLoad = true;

    protected ListRangeIndicators() {
        mRangeIndicators = new HashMap<>();
        mCallbackManager = new CallbackManager<>();
    }

    protected abstract int getPrefetchDistance();

    protected abstract IndicatorType getCompareType(ItemType itemType);

    public void setData(List<ItemType> data) {
        mRangeIndicators.clear();
        this.mData = data;

        if (mData.size() == 0) {
            notifyCallbacks(ACTION_INITIAL, null);
        } else {
            buildIndicators();
        }
    }

    private void buildIndicators() {

        HashMap<IndicatorType, ArrayList<Integer>> itemIndicesMap = new HashMap<>();

        for (int i = 0; i < mData.size(); ++i) {
            ItemType currentItem = mData.get(i);

            // Put if absent
            if (!itemIndicesMap.containsKey(getCompareType(currentItem))) {
                itemIndicesMap.put(getCompareType(currentItem), new ArrayList<>());
            }

            itemIndicesMap.get(getCompareType(currentItem)).add(i);
        }

        for (IndicatorType type : itemIndicesMap.keySet()) {
            RangeIndicator<ItemType> indicator = new RangeIndicator<ItemType>() {

                private boolean isSameItem(ItemType item, ItemType listItem) {
                    return item.getCompareValue() == listItem.getCompareValue();
                }

                @Override
                public List<ItemType> getList() {
                    return mData;
                }

                @Override
                public void onBefore(ItemType item) {
                    if (mInitialLoad) {
                        if (isSameItem(item, getList().get(this.getBeforeBoundIndex() - 1))) {
                            mInitialLoad = false;
                        }
                        return;
                    }
                    if (mTriggerOnce && this.isBeforeSatisfied()) {
                        return;
                    }
                    this.setIsBeforeSatisfied(true);
                    notifyCallbacks(ACTION_BEFORE, item);
                }

                @Override
                public void onAfter(ItemType item) {
                    if (mTriggerOnce && this.isAfterSatisfied()) {
                        return;
                    }
                    this.setIsAfterSatisfied(true);
                    notifyCallbacks(ACTION_AFTER, item);
                }
            };
            int beforeIndex = clampIndex(getPrefetchDistance(), itemIndicesMap.get(type).size());
            int afterIndex = clampIndex((itemIndicesMap.get(type).size() - 1) -
                    getPrefetchDistance(), itemIndicesMap.get(type).size());
            indicator.setBounds(beforeIndex, afterIndex);

            mRangeIndicators.put(type, indicator);
        }
    }

    public void triggerOnce(boolean bool) {
        mTriggerOnce = bool;
    }

    public void checkIndicators(IndicatorType indicatorType, int position) {
        RangeIndicator indicator = mRangeIndicators.get(indicatorType);

        if (indicator != null) {
            indicator.checkBounds(position);
        }
    }

    public CallbackManager<ListRangeIndicatorsCallback<ItemType>> getCallbackManager() {
        return mCallbackManager;
    }

    private void notifyCallbacks(int actionCode, ItemType item) {
        switch (actionCode) {
            case ACTION_INITIAL:
                for (ListRangeIndicatorsCallback<ItemType> cb : mCallbackManager.getCallbacks()) {
                    if (cb != null)
                        cb.onListEmpty();
                }
                break;
            case ACTION_BEFORE:
                for (ListRangeIndicatorsCallback<ItemType> cb : mCallbackManager.getCallbacks()) {
                    if (cb != null)
                        cb.onBefore(item);
                }
                break;
            case ACTION_AFTER:
                for (ListRangeIndicatorsCallback<ItemType> cb : mCallbackManager.getCallbacks()) {
                    if (cb != null)
                        cb.onAfter(item);
                }
                break;
        }
    }

    private int clampIndex(int num, int listSize) {
        return clampInt(num, 0, listSize - 1);
    }

    private static int clampInt(int num, int low, int high) {
        if (high <= low)
            return low;
        else if (num < low)
            return low;
        else if (num > high)
            return high;
        else
            return num;
    }

    @Override
    public String toString() {
        return mRangeIndicators.toString();
    }

    /**
     * Used to provide indicator hints for the ListRangeIndicators class.
     *
     * @param <T> The type of data in a list.
     */
    abstract class RangeIndicator<T> {

        private int mBeforeBoundIndex, mAfterBoundIndex;
        private boolean mIsBeforeSatisfied = false;
        private boolean mIsAfterSatisfied = false;

        RangeIndicator() {

        }

        /**
         * Sets the before and after bounds of the indicator.
         *
         * @param beforeBound The before bound index.
         * @param afterBound  The after bound index.
         */
        final void setBounds(int beforeBound, int afterBound) {
            this.mBeforeBoundIndex = beforeBound;
            this.mAfterBoundIndex = afterBound;
            this.mIsAfterSatisfied = false;
        }

        final boolean isBeforeBoundReached(int position) {
            return position < mBeforeBoundIndex;
        }

        final boolean isAfterBoundReached(int position) {
            return position > mAfterBoundIndex;
        }

        final boolean isInBetweenBounds(int position) {
            return position >= mBeforeBoundIndex && position <= mAfterBoundIndex;
        }

        final void checkBounds(int position) {

            /* If the position is equal to before bound or after bound call method on Indicator */

            if (isBeforeBoundReached(position))
                onBefore(getListItem(position));
            else if (isAfterBoundReached(position))
                onAfter(getListItem(position));
            else if (isInBetweenBounds(position))
                resetSatisfied();
        }

        /**
         * Sets if the indicator has been reached to provide a hint that the indicator has
         * already been triggered.
         *
         * @param bool whether the indicator bound has been reached.
         */
        void setIsBeforeSatisfied(@SuppressWarnings("SameParameterValue") boolean bool) {
            mIsBeforeSatisfied = bool;
        }

        void setIsAfterSatisfied(@SuppressWarnings("SameParameterValue") boolean bool) {
            mIsAfterSatisfied = bool;
        }

        void resetSatisfied() {
            mIsBeforeSatisfied = false;
            mIsAfterSatisfied = false;
        }

        int getBeforeBoundIndex() {
            return mBeforeBoundIndex;
        }

        int getAfterBoundIndex() {
            return mAfterBoundIndex;
        }

        /**
         * Hint as to if the bound has already been called. Can be used to ensure a call does
         * not happen more than once for each pass of the bounds check.
         * <p>
         * For example:
         * <p>
         * if (!isBeforeSatisfied) {
         * performAction();
         * }
         */
        boolean isBeforeSatisfied() {
            return mIsBeforeSatisfied;
        }

        boolean isAfterSatisfied() {
            return mIsAfterSatisfied;
        }

        final T getListItem(int index) {
            return getList().get(index);
        }

        protected abstract List<T> getList();

        public abstract void onBefore(T item);

        public abstract void onAfter(T item);
    }

    /**
     * A callback interface for relaying the status of indicators.
     *
     * @param <T> The type of data in the list.
     */
    public interface ListRangeIndicatorsCallback<T> extends CallbackManager.Callback {

        void onListEmpty();

        void onBefore(T item);

        void onAfter(T item);
    }
}
