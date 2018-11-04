
package com.sbcode.cake.data.platforms.implementations.facebook.models.user;

import com.squareup.moshi.Json;

public class Data {

    @SuppressWarnings("unused")
    @Json(name = "height")
    public Integer height;
    @SuppressWarnings("unused")
    @Json(name = "is_silhouette")
    public Boolean isSilhouette;
    @SuppressWarnings("unused")
    @Json(name = "url")
    public String url;
    @SuppressWarnings("unused")
    @Json(name = "width")
    public Integer width;

}
