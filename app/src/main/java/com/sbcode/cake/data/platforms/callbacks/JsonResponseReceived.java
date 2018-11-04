package com.sbcode.cake.data.platforms.callbacks;

import com.sbcode.cake.data.platforms.interfaces.ResponseReceived;

public interface JsonResponseReceived extends ResponseReceived {

    void onJsonResponse(String jsonResponseRaw);
}
