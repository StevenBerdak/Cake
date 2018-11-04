package com.sbcode.cake.data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.sbcode.cake.data.room.dao.PersonsDao;
import com.sbcode.cake.data.room.dao.SlicesDao;
import com.sbcode.cake.data.room.dao.UpdatesDao;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.room.converters.PlatformTypeConverter;
import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Update;

@Database(version = 28, entities = {Slice.class, Person.class, Update.class},
        exportSchema = false)
@TypeConverters({PlatformTypeConverter.class})
public abstract class CakeDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "cake_database";

    public abstract SlicesDao sliceDao();
    public abstract PersonsDao personsDao();
    public abstract UpdatesDao updatesDao();
}
