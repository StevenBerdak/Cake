package com.sbcode.cake.data.platforms.abstracts;

import java.util.HashMap;

abstract class AbstractApiRequest {

    public AbstractApiRequest() {

    }

    private HashMap<String, String> mOptions;

    public void setOptions(HashMap<String, String> kvMap) {
        this.mOptions = kvMap;
    }

    public HashMap<String, String> getOptions() {
        return mOptions;
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
