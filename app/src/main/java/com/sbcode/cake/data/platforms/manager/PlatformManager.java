package com.sbcode.cake.data.platforms.manager;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.Platform;

import java.util.HashSet;

public interface PlatformManager {

    void addPlatform(Platform platform);

    HashSet<Platform> getPlatforms();

    HashSet<Platform> getPlatformsSignedIn();

    boolean hasPlatformsSignedIn();

    Platform get(PlatformType type);
}
