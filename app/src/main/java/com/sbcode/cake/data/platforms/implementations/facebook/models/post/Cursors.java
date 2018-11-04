
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class Cursors {

    @Json(name = "before")
    public String before;
    @Json(name = "after")
    public String after;

}
