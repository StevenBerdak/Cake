package com.sbcode.cake.data.webservices;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.platforms.abstracts.AbstractApiRequestHandler;
import com.sbcode.cake.data.platforms.callbacks.ApiResponseReceived;
import com.sbcode.cake.data.platforms.interfaces.ApiPostsRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.ApiRequestHandler;
import com.sbcode.cake.data.platforms.interfaces.ApiResponseMapper;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.RequestHandler;
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
 * API based request.
 */
public class ApiRequestWebService extends AbstractRequestWebService {

    private static final String PLATFORM_TYPE = "platform_type";
    private static final String REQUEST_MAPPER = "request_mapper_serialized";
    public static final int JOB_ID = 529198605;

    @SuppressWarnings("WeakerAccess")
    @Inject
    CakeDatabase mCakeDatabase;

    @SuppressWarnings("WeakerAccess")
    @Inject
    CakePlatformManager mCakePlatformManager;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        ((CakeApplication) getApplicationContext()).component().inject(this);

        Timber.d("intent == null ?? %s", intent.getStringExtra(PLATFORM_TYPE) == null);

        String platformType = intent.getStringExtra(PLATFORM_TYPE);

        Timber.d("Platform type = %s", platformType);

        ApiPostsRequestMapper requestMapper = (ApiPostsRequestMapper)
                intent.getSerializableExtra(REQUEST_MAPPER);

        Timber.i("Query requested : Platform Type, Query Type : %s", platformType);

        ApiRequestHandler<PostsRequestMapper, UsersRequestMapper, ApiResponseReceived> handler =
                (AbstractApiRequestHandler) getRequestHandler(PlatformType.valueOf(platformType));

        if (handler == null) {
            Timber.e("Handler not found for %s", platformType);
            return;
        }

        try {

            handler.handlePostsRequest(requestMapper, (List<Object> data) -> {
                ApiResponseMapper responseMapper = (ApiResponseMapper)
                        getResponseMapper(PlatformType.valueOf(platformType));

                List<Slice> slicesResult = responseMapper.mapSlices(data);

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

                    handler.handleUserRequest(usersRequestMapper, personResponse -> {
                        Person person = responseMapper.mapPerson(personResponse.get(0));

                        if (person.platformUserId.length() > 0) {
                            Long personItemId = mCakeDatabase.personsDao().insert(person);

                            Timber.d("Persons inserted into database: %s",
                                    personItemId.toString());
                        }
                    });

                } else if (unknownPersonsResult.size() > 1) {

                    //Add multiple persons to the database
                    List<UsersRequestMapper> userRequestMappers = new ArrayList<>();

                    for (int i = 0; i < unknownPersonsResult.size(); ++i) {
                        UsersRequestMapper usersRequestMapper = mCakePlatformManager
                                .get(PlatformType.valueOf(platformType))
                                .getUserRequestMapper();

                        usersRequestMapper.setUserId(unknownPersonsResult.get(i));

                        userRequestMappers.add(usersRequestMapper);
                    }

                    handler.handleUsersMultiRequest(userRequestMappers, personResponse -> {
                        Person person = responseMapper.mapPerson(personResponse);

                        if (person.platformUserId.length() > 0) {
                            Long personItemId = mCakeDatabase.personsDao().insert(person);

                            Timber.d("Persons inserted into database: %s",
                                    personItemId.toString());
                        }
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


    public static Intent intentBuilder(String platformType, PostsRequestMapper postsRequestMapper) {
        Intent result = new Intent();
        result.putExtra(PLATFORM_TYPE, platformType);
        result.putExtra(REQUEST_MAPPER, postsRequestMapper);
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

    @Override
    public RequestHandler getRequestHandler(PlatformType platformType) {
        return getPlatformManager().get(platformType).getRequestHandler();
    }
}
