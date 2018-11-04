package com.sbcode.cake.data.room.models;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ExtraData {

    public static final String TYPE_MEDIA = "media";

    @Json(name = "data")
    public final List<Datum> data = new ArrayList<>();

    public void add(String type, String value) {
        Datum datum = new Datum();
        datum.type = type;
        datum.value = value;
        data.add(datum);
    }

    public static JsonAdapter<ExtraData> getJsonAdapter() {
        return new Moshi.Builder().build().adapter(ExtraData.class);
    }
}
