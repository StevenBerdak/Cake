package com.sbcode.cake.data.platforms.abstracts;

import com.sbcode.cake.data.platforms.callbacks.JsonResponseReceived;
import com.sbcode.cake.data.platforms.interfaces.JsonRequestHandler;

public abstract class AbstractJsonRequestHandler implements
        JsonRequestHandler<String, String, JsonResponseReceived> {
}
