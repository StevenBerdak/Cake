package com.sbcode.cake.data.platforms.implementations.twitter.mappers.request;

import com.sbcode.cake.data.platforms.abstracts.AbstractApiUsersRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.UsersRequestMapper;

/**
 * Maps a user request on the Twitter Android kit.
 */
public class TwitterUsersLookupRequestMapper extends AbstractApiUsersRequestMapper
        implements UsersRequestMapper {

    private String mUserId;

    public TwitterUsersLookupRequestMapper() {

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
