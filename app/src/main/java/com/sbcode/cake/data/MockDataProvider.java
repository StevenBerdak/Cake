package com.sbcode.cake.data;

import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides mock data for testing purposes.
 */
public class MockDataProvider {

    public static Person provideMockPerson() {

        Person person = new Person();
        person.platformType = PlatformType.NULL;
        person.platformUserId = "101010";
        person.userDisplayHandle = "Bbob520";

        return person;
    }

    public static List<Slice> provideMockSliceData(int size) {
        List<Slice> result = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            Slice slice = new Slice();

            slice.sliceMessage = "message";
            slice.totalComments = 5;
            slice.totalLikes = 5;
            slice.platformPostId = String.valueOf(5000 + i);
            slice.createdDate = System.currentTimeMillis();
            slice.platformType = PlatformType.NULL;
            slice.platformAuthorName = "Billy Bob";
            slice.postLinkUrl = "www.google.com";
            slice.platformAuthorHandle = "Bbob520";
            slice.platformPostId = String.valueOf(10000 + i);
            slice.polledDate = System.currentTimeMillis();
            slice.platformAuthorId = "101010";
            slice.postExtraData = "";

            result.add(slice);
        }

        return result;
    }
}
