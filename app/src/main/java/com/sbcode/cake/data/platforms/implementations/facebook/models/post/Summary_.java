
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import com.squareup.moshi.Json;

public class Summary_ {

    @Json(name = "order")
    public String order;
    @Json(name = "total_count")
    public Integer totalCount;
    @Json(name = "can_comment")
    public Boolean canComment;

}
