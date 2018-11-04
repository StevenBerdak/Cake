package com.sbcode.cake.data.platforms.implementations.twitter.handlers;

import android.support.annotation.NonNull;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractApiRequestHandler;
import com.sbcode.cake.data.platforms.callbacks.ApiResponseReceived;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.UsersRequestMapper;
import com.sbcode.cake.ui.reporting.StatusReporter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles requests made on the Twitter Android kit.
 */
public class TwitterApiRequestHandler extends AbstractApiRequestHandler {

    @Override
    public void handlePostsRequest(@NonNull PostsRequestMapper input,
                                   @NonNull ApiResponseReceived apiResponseReceived) {

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        try {

            Long userId = input.getUserId() != null ? Long.valueOf(input.getUserId()) : null;
            Long beforeId = input.getBefore() > 0 ? input.getBefore() : null;
            Long afterId = input.getAfter() > 0 ? input.getAfter() : null;

            if (input.getUserId() == null) {
                CakeApplication cakeApplication = CakeApplication.getInstance();
                cakeApplication.component()
                        .getStatusReporter()
                        .enqueueReportDispatchImmediate(
                                new StatusReporter.StatusReport(
                                        StatusReporter.ReportType.PLATFORM,
                                        PlatformType.FACEBOOK,
                                        cakeApplication
                                                .getString(R.string.reporter_error_user_account,
                                                        cakeApplication.getString(
                                                                PlatformType.getTitleResource(
                                                                        PlatformType.TWITTER)
                                                        )
                                                )
                                ));

                return;
            }

            List<Tweet> results = statusesService.userTimeline(
                    userId,
                    null,
                    input.getLimit(),
                    afterId,
                    beforeId,
                    null,
                    null,
                    null,
                    null
            ).execute().body();

            if (results != null) {
                List<Object> objects = new ArrayList<>(results);
                apiResponseReceived.onApiResponse(objects);
            } else {
                throw new NullPointerException();
            }

        } catch (IOException e) {
            e.printStackTrace();
            CakeApplication
                    .getInstance()
                    .component()
                    .getStatusReporter()
                    .enqueueReportDispatchImmediate(
                            new StatusReporter.StatusReport(
                                    StatusReporter.ReportType.IO,
                                    PlatformType.TWITTER,
                                    CakeApplication
                                            .getInstance()
                                            .getString(R.string.reporter_error_downloading_user_data,
                                                    CakeApplication
                                                            .getInstance()
                                                            .getString(
                                                                    PlatformType.getTitleResource(
                                                                            PlatformType.TWITTER)
                                                            )
                                            )
                            ));
        }
    }

    @Override
    public void handleUserRequest(@NonNull UsersRequestMapper input,
                                  @NonNull ApiResponseReceived apiResponseReceived) {

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        if (input.getUserId() == null) {
            CakeApplication
                    .getInstance()
                    .component()
                    .getStatusReporter()
                    .enqueueReportDispatchImmediate(
                            new StatusReporter.StatusReport(
                                    StatusReporter.ReportType.PLATFORM,
                                    PlatformType.TWITTER,
                                    CakeApplication.getInstance()
                                            .getString(R.string.reporter_error_user_account,
                                                    CakeApplication
                                                            .getInstance()
                                                            .getString(
                                                                    PlatformType.getTitleResource(
                                                                            PlatformType.TWITTER)
                                                            )
                                            )
                            ));

            return;
        }

        try {
            List<Tweet> results = statusesService.userTimeline(
                    Long.valueOf(input.getUserId()),
                    null,
                    1,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ).execute().body();

            if (results != null) {
                List<Object> objects = new ArrayList<>(results);
                apiResponseReceived.onApiResponse(objects);
            } else {
                throw new NullPointerException();
            }

        } catch (IOException e) {
            e.printStackTrace();
            CakeApplication
                    .getInstance()
                    .component()
                    .getStatusReporter()
                    .enqueueReport(
                            new StatusReporter.StatusReport(
                                    StatusReporter.ReportType.IO,
                                    PlatformType.TWITTER,
                                    CakeApplication
                                            .getInstance()
                                            .getString(R.string.reporter_error_downloading_user_data,
                                                    CakeApplication
                                                            .getInstance()
                                                            .getString(
                                                                    PlatformType.getTitleResource(
                                                                            PlatformType.TWITTER)
                                                            )
                                            )
                            ));
        }
    }

    @Override
    public void handleUsersMultiRequest(@NonNull List<UsersRequestMapper> inputList,
                                        @NonNull ApiResponseReceived apiResponseReceived) {

        if (inputList.get(0).getUserId() == null) {
            CakeApplication
                    .getInstance()
                    .component()
                    .getStatusReporter()
                    .enqueueReportDispatchImmediate(
                            new StatusReporter.StatusReport(
                                    StatusReporter.ReportType.PLATFORM,
                                    PlatformType.FACEBOOK,
                                    CakeApplication.getInstance()
                                            .getString(R.string.reporter_error_user_account,
                                                    CakeApplication
                                                            .getInstance()
                                                            .getString(
                                                                    PlatformType.getTitleResource(
                                                                            PlatformType.TWITTER)
                                                            )
                                            )
                            ));

            return;
        }

        for (UsersRequestMapper mapper : inputList) {
            handleUserRequest(mapper, apiResponseReceived);
        }
    }
}
