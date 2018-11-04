package com.sbcode.cake.data.platforms.callbacks;

import com.sbcode.cake.data.platforms.interfaces.ResponseReceived;

import java.util.List;

public interface ApiResponseReceived extends ResponseReceived {

    void onApiResponse(List<Object> data);
}
