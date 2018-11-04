package com.sbcode.cake.data.platforms.implementations.facebook.handlers;

import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.HttpMethod;
import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractJsonRequestHandler;
import com.sbcode.cake.data.platforms.callbacks.JsonResponseReceived;
import com.sbcode.cake.ui.reporting.StatusReporter;

import java.io.IOException;
import java.util.List;

/**
 * Handles requests made on the Facebook Graph API.
 */
public class FacebookJsonRequestHandler extends AbstractJsonRequestHandler {

    @Override
    public void handlePostsRequest(@NonNull String input, @NonNull JsonResponseReceived callback) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null || accessToken.isExpired()) {
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
                                                                            PlatformType.FACEBOOK)
                                                            )
                                            )
                            ));
            return;
        }

        GraphRequest graphRequest = new GraphRequest(accessToken, input, null,
                HttpMethod.GET, response -> {
            try {
                int statusCode = response.getConnection().getResponseCode();

                if (statusCode == 200) {
                    callback.onJsonResponse(response.getRawResponse());
                } else {
                    CakeApplication cakeApplication = CakeApplication.getInstance();
                    cakeApplication.component()
                            .getStatusReporter()
                            .enqueueReport(
                                    new StatusReporter.StatusReport(
                                            StatusReporter.ReportType.PLATFORM,
                                            PlatformType.FACEBOOK,
                                            cakeApplication.getString(
                                                    R.string.reporter_error_downloading_user_data,
                                                    cakeApplication
                                                            .getString(
                                                                    PlatformType.getTitleResource(
                                                                            PlatformType.FACEBOOK)
                                                            )
                                            )
                                    ));
                }
            } catch (IOException e) {
                CakeApplication
                        .getInstance()
                        .component()
                        .getStatusReporter()
                        .enqueueReport(
                                new StatusReporter.StatusReport(
                                StatusReporter.ReportType.IO,
                                PlatformType.FACEBOOK,
                                CakeApplication
                                        .getInstance()
                                        .getString(R.string.reporter_error_downloading_user_data,
                                                CakeApplication
                                                        .getInstance()
                                                        .getString(
                                                                PlatformType.getTitleResource(
                                                                        PlatformType.FACEBOOK)
                                                        )
                                        )
                        ));
                e.printStackTrace();
            }
        });

        graphRequest.executeAndWait();
    }

    @Override
    public void handleUserRequest(@NonNull String input,
                                  @NonNull JsonResponseReceived jsonResponseReceived) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null || accessToken.isExpired()) {
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
                                                                            PlatformType.FACEBOOK)
                                                            )
                                            )
                            ));
            return;
        }

        GraphRequest graphRequest = new GraphRequest(accessToken, input, null,
                HttpMethod.GET, response ->
                jsonResponseReceived.onJsonResponse(response.getRawResponse()));

        graphRequest.executeAndWait();
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Override
    public void handleUsersMultiRequest(@NonNull List<String> input,
                                        @NonNull JsonResponseReceived callback) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null || accessToken.isExpired()) {
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
                                                                            PlatformType.FACEBOOK)
                                                            )
                                            )
                            ));
            return;
        }

        GraphRequestBatch batch = new GraphRequestBatch();

        for (String query : input) {
            batch.add(new GraphRequest(accessToken, query, null, HttpMethod.GET,
                    response -> callback.onJsonResponse(response.getRawResponse())));
        }

        batch.executeAndWait();
    }
}
