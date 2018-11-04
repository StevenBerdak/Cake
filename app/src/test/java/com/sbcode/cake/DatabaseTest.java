package com.sbcode.cake;

import android.arch.persistence.room.Room;
import android.content.Intent;

import com.sbcode.cake.data.MockDataProvider;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.room.CakeDatabase;
import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.room.services.DatabaseMaintenanceJobIntentService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(application = CakeTestApplication.class)
public class DatabaseTest {

    private static final int DB_SLICE_INSERT_SIZE = 5000;

    private static CakeTestApplication mCakeTestApplication;

    public DatabaseTest() {
        mCakeTestApplication = (CakeTestApplication) RuntimeEnvironment.application;
    }

    @Test
    public void testDatabase() {
        CakeDatabase cakeDatabase = Room
                .inMemoryDatabaseBuilder(mCakeTestApplication, CakeDatabase.class)
                .allowMainThreadQueries()
                .build();

        testDatabaseInsertPerson(cakeDatabase);

        testDatabaseInsertSlices(cakeDatabase);

        testDatabaseMaintenanceService(cakeDatabase);

        cakeDatabase.close();
    }

    public void testDatabaseInsertPerson(CakeDatabase cakeDatabase) {
        cakeDatabase.personsDao().insert(MockDataProvider.provideMockPerson());

        List<Person> persons =
                cakeDatabase.personsDao().loadPlatformUsersAsList(PlatformType.NULL.name());

        assertTrue(persons != null);

        assertTrue(persons.size() > 0);

        System.out.println("Persons in database = " + persons.size());
    }

    public void testDatabaseInsertSlices(CakeDatabase cakeDatabase) {
        cakeDatabase.sliceDao()
                .insert(MockDataProvider.provideMockSliceData(DB_SLICE_INSERT_SIZE));

        List<Slice> slices =
                cakeDatabase.sliceDao().queryAllForList();

        assertTrue(slices != null);

        assertTrue(slices.size() > 0);

        System.out.println("Slices in database = " + slices.size());
    }

    public void testDatabaseMaintenanceService(CakeDatabase cakeDatabase) {
        DatabaseMaintenanceJobIntentService service =
                Robolectric.buildService(DatabaseMaintenanceJobIntentService.class).get();
        service.provideDatabase(cakeDatabase);

        CountDownLatch latch = new CountDownLatch(1);

        service.getCallbackManager().addCallback(latch::countDown);

        service.manualStartService(new Intent());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Slice> slicesAfterMaintenance =
                cakeDatabase
                        .sliceDao()
                        .queryAllByPlatformForList(
                                PlatformType.NULL);

        assertTrue(slicesAfterMaintenance.size() < DB_SLICE_INSERT_SIZE);

        System.out.println("Slices in database = " + slicesAfterMaintenance.size());
    }
}
