package com.sbcode.cake.data.platforms.manager;

import com.facebook.FacebookSdk;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.implementations.facebook.FacebookAccount;
import com.sbcode.cake.data.platforms.implementations.facebook.FacebookPlatform;
import com.sbcode.cake.data.platforms.implementations.facebook.FacebookRepository;
import com.sbcode.cake.data.platforms.implementations.twitter.TwitterAccount;
import com.sbcode.cake.data.platforms.implementations.twitter.TwitterPlatform;
import com.sbcode.cake.data.platforms.implementations.twitter.TwitterRepository;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.twitter.sdk.android.core.Twitter;

import java.util.HashMap;
import java.util.HashSet;

import javax.inject.Inject;

import timber.log.Timber;

public class CakePlatformManager implements PlatformManager {

    private final HashMap<PlatformType, Platform> mPlatforms;

    @Inject
    public CakePlatformManager() {
        mPlatforms = new HashMap<>();

        int defaultQueryLimit = 100;
        int defaultRequestsPerMinute = 10;

        if (FacebookSdk.isInitialized())
            addPlatform(new FacebookPlatform(new FacebookAccount(),
                    new FacebookRepository(this,
                            defaultQueryLimit,
                            defaultRequestsPerMinute))
            );

        try {
            Twitter twitterInstance = Twitter.getInstance();
            addPlatform(new TwitterPlatform(new TwitterAccount(),
                    new TwitterRepository(this,
                            defaultQueryLimit,
                            defaultRequestsPerMinute))
            );
        } catch(IllegalStateException e) {
            Timber.e("Twitter not initialized");
        }
    }

    @Override
    public void addPlatform(Platform platform) {
        mPlatforms.put(platform.getPlatformType(), platform);
    }

    @Override
    public HashSet<Platform> getPlatforms() {
        return new HashSet<>(mPlatforms.values());
    }

    @Override
    public HashSet<Platform> getPlatformsSignedIn() {
        HashSet<Platform> result = new HashSet<>();

        for (Platform platform : getPlatforms()) {
            if (platform.getAccount().isSignedIn())
                result.add(platform);
        }

        return result;
    }

    @Override
    public boolean hasPlatformsSignedIn() {
        return getPlatformsSignedIn().size() > 0;
    }

    @Override
    public Platform get(PlatformType type) {
        return mPlatforms.get(type);
    }
}
