
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import java.util.ArrayList;
import java.util.List;
import com.squareup.moshi.Json;

public class Comments {

    @Json(name = "data")
    public List<CommentsDatum> data = new ArrayList<CommentsDatum>();
    @Json(name = "summary")
    public Summary_ summary;
    @Json(name = "paging")
    public Paging_ paging;

}
