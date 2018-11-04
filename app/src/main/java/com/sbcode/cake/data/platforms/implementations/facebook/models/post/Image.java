
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class Image {

    @Json(name = "height")
    public Integer height;
    @Json(name = "src")
    public String src;
    @Json(name = "width")
    public Integer width;

}
