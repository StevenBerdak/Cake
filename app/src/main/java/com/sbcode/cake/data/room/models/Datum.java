package com.sbcode.cake.data.room.models;

import com.squareup.moshi.Json;

@SuppressWarnings("unused")
public class Datum {

    @Json(name = "type")
    public String type;
    @Json(name = "value")
    public String value;
}
