
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class Summary {

    @Json(name = "total_count")
    public Integer totalCount;
    @Json(name = "can_like")
    public Boolean canLike;
    @Json(name = "has_liked")
    public Boolean hasLiked;

}
