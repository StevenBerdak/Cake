
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class Datum {

    @Json(name = "id")
    public String id;
    @Json(name = "created_time")
    public Long createdTime;
    @Json(name = "from")
    public From from;
    @Json(name = "link")
    public String link;
    @Json(name = "name")
    public String name;
    @Json(name = "permalink_url")
    public String permalinkUrl;
    @Json(name = "picture")
    public String picture;
    @Json(name = "updated_time")
    public Integer updatedTime;
    @Json(name = "likes")
    public Likes likes;
    @Json(name = "comments")
    public Comments comments;
    @Json(name = "attachments")
    public Attachments attachments;
    @Json(name = "message")
    public String message;

}
