package com.sbcode.cake.data;

import com.sbcode.cake.R;

/**
 * Provides an enum representation of platform types available for consistency across the
 * application.
 */
public enum PlatformType {

    FACEBOOK, TWITTER, NULL;

    public static int getTitleResource(PlatformType platformType) {
        switch (platformType) {
            case FACEBOOK:
                return R.string.facebook_title;
            case TWITTER:
                return R.string.twitter_title;
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
