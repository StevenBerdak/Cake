
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class SubattachmentsDatum {

    @Json(name = "media")
    public Media_ media;
    @Json(name = "target")
    public Target_ target;
    @Json(name = "type")
    public String type;
    @Json(name = "url")
    public String url;

}
