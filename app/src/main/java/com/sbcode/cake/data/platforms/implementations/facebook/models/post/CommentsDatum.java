
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class CommentsDatum {

    @Json(name = "created_time")
    public Integer createdTime;
    @Json(name = "from")
    public From_ from;
    @Json(name = "message")
    public String message;
    @Json(name = "id")
    public String id;

}
