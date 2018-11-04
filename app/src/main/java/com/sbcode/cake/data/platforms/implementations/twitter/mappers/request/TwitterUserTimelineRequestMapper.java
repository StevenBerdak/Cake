package com.sbcode.cake.data.platforms.implementations.twitter.mappers.request;

import com.sbcode.cake.data.platforms.abstracts.AbstractApiPostsRequestMapper;

/**
 * Maps post request from a Titter user's timeline via the Twitter Android kit.
 */
public class TwitterUserTimelineRequestMapper extends AbstractApiPostsRequestMapper {

    private String mUserId;
    private long mPostsBefore, mPostsAfter;
    private int mLimit;

    public TwitterUserTimelineRequestMapper() {

    }

    @Override
    public void postsBefore(long date) {
        mPostsBefore = date;
    }

    @Override
    public void postsAfter(long date) {
        mPostsAfter = date;
    }

    @Override
    public void setLimit(int limit) {
        mLimit = limit;
    }

    @Override
    public long getBefore() {
        return mPostsBefore;
    }

    @Override
    public long getAfter() {
        return mPostsAfter;
    }

    @Override
    public int getLimit() {
        return mLimit;
    }

    @Override
    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    @Override
    public String getUserId() {
        return mUserId;
    }
}
