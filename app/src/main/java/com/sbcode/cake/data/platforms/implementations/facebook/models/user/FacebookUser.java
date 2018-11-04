
package com.sbcode.cake.data.platforms.implementations.facebook.models.user;

import com.squareup.moshi.Json;

public class FacebookUser {

    @Json(name = "id")
    public String id;
    @Json(name = "name")
    public String name;
    @Json(name = "picture")
    public Picture picture;

}
