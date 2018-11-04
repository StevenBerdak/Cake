
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class AttachmentsDatum {

    @Json(name = "description")
    public String description;
    @Json(name = "media")
    public Media media;
    @Json(name = "target")
    public Target target;
    @Json(name = "title")
    public String title;
    @Json(name = "type")
    public String type;
    @Json(name = "url")
    public String url;
    @Json(name = "subattachments")
    public Subattachments subattachments;

}
