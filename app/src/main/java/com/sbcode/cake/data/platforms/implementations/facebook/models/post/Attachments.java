
package com.sbcode.cake.data.platforms.implementations.facebook.models.post;

import java.util.ArrayList;
import java.util.List;
import com.squareup.moshi.Json;

public class Attachments {

    @Json(name = "data")
    public List<AttachmentsDatum> data = new ArrayList<AttachmentsDatum>();

}
