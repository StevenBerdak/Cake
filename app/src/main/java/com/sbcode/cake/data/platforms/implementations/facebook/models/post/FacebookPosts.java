
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import java.util.ArrayList;
import java.util.List;
import com.squareup.moshi.Json;

public class FacebookPosts {

    @Json(name = "data")
    public List<Datum> data = new ArrayList<Datum>();
    @Json(name = "paging")
    public Paging__ paging;

}
