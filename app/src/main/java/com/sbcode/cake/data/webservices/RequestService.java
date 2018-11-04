package com.sbcode.cake.data.webservices;

import com.sbcode.cake.data.platforms.manager.PlatformManager;
import com.sbcode.cake.data.room.CakeDatabase;

interface RequestService {

    PlatformManager getPlatformManager();

    CakeDatabase getDatabase();
}
