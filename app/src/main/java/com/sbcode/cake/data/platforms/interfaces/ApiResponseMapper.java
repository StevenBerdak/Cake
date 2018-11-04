package com.sbcode.cake.data.platforms.interfaces;

import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;

import java.util.List;

public interface ApiResponseMapper extends ResponseMapper {

    List<Slice> mapSlices(List<Object> items);

    Person mapPerson(Object item);
}
