package com.sbcode.cake.data.platforms.abstracts;

import android.net.Uri;

public abstract class AbstractJsonRequest {

    private final Uri.Builder mUriBuilder;

    AbstractJsonRequest() {
        this.mUriBuilder = new Uri.Builder();
        initializeParameters();
    }

    public final void setAuthority(String encodedAuthority) {
        mUriBuilder.encodedAuthority(encodedAuthority);
    }

    protected final void setPath(String encodedPath) {
        mUriBuilder.appendEncodedPath(encodedPath);
    }

    protected final String getPath() {
        return mUriBuilder.build().getPath();
    }

    protected final void appendParameter(String key, String value) {
        mUriBuilder.appendQueryParameter(key, value);
    }

    protected final Uri.Builder getUriBuilder() {
        return mUriBuilder;
    }

    protected abstract void initializeParameters();

    @Override
    public String toString() {
        return mUriBuilder.build().toString();
    }
}
