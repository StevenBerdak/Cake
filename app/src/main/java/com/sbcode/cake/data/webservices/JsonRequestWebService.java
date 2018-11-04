package com.sbcode.cake.data.webservices;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.platforms.abstracts.AbstractJsonRequestHandler;
import com.sbcode.cake.data.platforms.callbacks.JsonResponseReceived;
import com.sbcode.cake.data.platforms.interfaces.JsonRequestHandler;
import com.sbcode.cake.data.platforms.interfaces.JsonResponseMapper;
import com.sbcode.cake.data.platforms.interfaces.UsersRequestMapper;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.data.platforms.manager.PlatformManager;
import com.sbcode.cake.data.room.CakeDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Service that performs the web request to retrieve the data and insert into the database for an
 * Json based request.
 */
public class JsonRequestWebService extends AbstractRequestWebService {

    private static final String QUERY_STRING = "query_string";
    private static final String PLATFORM_TYPE = "platform_type";
    public static final int JOB_ID = 529198601;

    @SuppressWarnings("WeakerAccess")
    @Inject
    CakeDatabase mCakeDatabase;

    @SuppressWarnings("WeakerAccess")
    @Inject
    CakePlatformManager mCakePlatformManager;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Timber.d("JsonRequestWebService started");

        ((CakeApplication) getApplicationContext()).component().inject(this);

        String platformType = intent.getStringExtra(PLATFORM_TYPE);
        String queryString = intent.getStringExtra(QUERY_STRING);

        Timber.d("Query requested : Platform Type, Query String : %s, %s",
                platformType, queryString);

        JsonRequestHandler<String, String, JsonResponseReceived> handler =
                (AbstractJsonRequestHandler) getRequestHandler(PlatformType.valueOf(platformType));

        if (handler == null) {
            Timber.e("Handler not found for %s", platformType);
            return;
        }

        try {

            handler.handlePostsRequest(queryString, jsonResponse -> {
                JsonResponseMapper responseMapper = (JsonResponseMapper)
                        getResponseMapper(PlatformType.valueOf(platformType));

                List<Slice> slicesResult = responseMapper.mapSlices(jsonResponse);

                if (slicesResult.size() == 0) {
                    Timber.d("The handler returned 0 results for %s platform",
                            platformType);
                    return;
                }

                /* Compile list of unknown persons */

                List<String> unknownPersonsResult = compileUnknownPersonsIdList(slicesResult,
                        PlatformType.valueOf(platformType), mCakeDatabase.personsDao()
                                .loadPlatformUsersAsList(platformType));

                /* Insert Persons into the database if unknown */

                if (unknownPersonsResult.size() == 1) {
                    UsersRequestMapper usersRequestMapper = mCakePlatformManager
                            .get(PlatformType
                                    .valueOf(platformType))
                            .getUserRequestMapper();

                    //Add single person to the database.
                    usersRequestMapper.setUserId(unknownPersonsResult.get(0));

                    handler.handleUserRequest(usersRequestMapper.toString(), personResponse -> {
                        Person person = responseMapper.mapPerson(personResponse);

                        Long personItemId = mCakeDatabase.personsDao().insert(person);

                        Timber.d("Persons inserted into database: %s",
                                personItemId.toString());

                    });

                } else if (unknownPersonsResult.size() > 1) {

                    //Add multiple persons to the database
                    List<String> userRequestQueryStrings = new ArrayList<>();

                    for (int i = 0; i < unknownPersonsResult.size(); ++i) {
                        UsersRequestMapper usersRequestMapper = mCakePlatformManager
                                .get(PlatformType.valueOf(platformType))
                                .getUserRequestMapper();

                        usersRequestMapper.setUserId(unknownPersonsResult.get(i));

                        userRequestQueryStrings.add(usersRequestMapper.toString());
                    }

                    handler.handleUsersMultiRequest(userRequestQueryStrings, personResponse -> {
                        Person person = responseMapper.mapPerson(personResponse);

                        Long personItemId = mCakeDatabase.personsDao().insert(person);

                        Timber.d("Persons inserted into database: %s",
                                personItemId.toString());

                    });
                }

                insertIntoDatabase(PlatformType.valueOf(platformType), slicesResult);
            });

        } catch (Exception e) {
            Timber.e("Error retrieving data from web service.");
            e.printStackTrace();
        }

        //Provide sleep to prevent excessive api calls in a short amount of time.
        timeout(500);
    }

    public static Intent intentBuilder(String platformType, String queryString) {
        Intent result = new Intent();
        result.putExtra(PLATFORM_TYPE, platformType);
        result.putExtra(QUERY_STRING, queryString);
        return result;
    }

    @Override
    public PlatformManager getPlatformManager() {
        return mCakePlatformManager;
    }

    @Override
    public CakeDatabase getDatabase() {
        return mCakeDatabase;
    }
}
