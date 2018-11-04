
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import java.util.ArrayList;
import java.util.List;
import com.squareup.moshi.Json;

public class Likes {

    @Json(name = "data")
    public List<Object> data = new ArrayList<Object>();
    @Json(name = "paging")
    public Paging paging;
    @Json(name = "summary")
    public Summary summary;

}
