package com.sbcode.cake.data.platforms.implementations.facebook.mappers.request;

import com.sbcode.cake.data.platforms.abstracts.AbstractJsonPostsRequestMapper;
import com.sbcode.cake.exceptions.NotImplementedException;

/**
 * Maps a request query the 'me/posts' endpoint for the Facebook Graph API.
 */
public class FacebookMePostsJsonPostsRequestMapper extends AbstractJsonPostsRequestMapper {

    private static final String ENCODED_PATH = "me/posts";
    private static final String DATE_FORMAT_KEY = "date_format";
    private static final String DATE_FORMAT_VALUE = "U";
    private static final String FIELDS_KEY = "fields";
    private static final String FIELDS_VALUE = "id,created_time,from,link,message,name," +
            "permalink_url,picture,updated_time,attachments,likes.summary(true)," +
            "comments.summary(true)";
    private static final String SINCE_KEY = "since";
    private static final String UNTIL_KEY = "until";
    private static final String LIMIT_KEY = "limit";

    @Override
    public void initializeParameters() {
        setPath(ENCODED_PATH);
        appendParameter(DATE_FORMAT_KEY, DATE_FORMAT_VALUE);
        appendParameter(FIELDS_KEY, FIELDS_VALUE);
    }

    @Override
    public void postsBefore(long date) {
        appendParameter(UNTIL_KEY, Long.toString(date));
    }

    @Override
    public void postsAfter(long date) {
        appendParameter(SINCE_KEY, Long.toString(date));
    }

    @Override
    public void setLimit(int limit) {
        appendParameter(LIMIT_KEY, Integer.toString(limit));
    }

    @Override
    public long getBefore() {
        return Long.valueOf(getUriBuilder().build().getQueryParameter(SINCE_KEY));
    }

    @Override
    public long getAfter() {
        return Long.valueOf(getUriBuilder().build().getQueryParameter(UNTIL_KEY));
    }

    @Override
    public int getLimit() {
        return Integer.valueOf(getUriBuilder().build().getQueryParameter(LIMIT_KEY));
    }

    @Override
    public void setUserId(String userId) {
        throw new NotImplementedException("This operation is not valid");
    }

    @Override
    public String getUserId() {
        throw new NotImplementedException("This operation is not valid");
    }
}
