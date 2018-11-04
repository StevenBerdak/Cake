package com.sbcode.cake.data.platforms.interfaces;

import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.room.models.Person;

import java.util.List;

public interface JsonResponseMapper extends ResponseMapper {

    List<Slice> mapSlices(String json);

    Person mapPerson(String json);
}
