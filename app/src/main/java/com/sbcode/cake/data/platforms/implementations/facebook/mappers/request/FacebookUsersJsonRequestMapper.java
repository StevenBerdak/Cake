package com.sbcode.cake.data.platforms.implementations.facebook.mappers.request;

import com.sbcode.cake.data.platforms.abstracts.AbstractJsonUsersRequestMapper;

/**
 * Maps a user request query for the Facebook Graph API.
 */
public class FacebookUsersJsonRequestMapper extends AbstractJsonUsersRequestMapper {

    private static final String FIELDS_KEY = "fields";
    private static final String FIELDS_VALUE = "id,name,picture";

    public FacebookUsersJsonRequestMapper() {

    }

    @Override
    public void initializeParameters() {
        appendParameter(FIELDS_KEY, FIELDS_VALUE);
    }


    @Override
    public void setUserId(String userId) {
        setPath(userId);
    }

    @Override
    public String getUserId() {
        return getPath();
    }
}
