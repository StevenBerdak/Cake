package com.sbcode.cake.data.platforms.interfaces;

import java.io.Serializable;

public interface RequestMapper extends Serializable {

    void setUserId(String userId);

    String getUserId();
}
