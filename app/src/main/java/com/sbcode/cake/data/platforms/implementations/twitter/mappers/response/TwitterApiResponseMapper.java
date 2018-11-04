package com.sbcode.cake.data.platforms.implementations.twitter.mappers.response;

import android.support.annotation.NonNull;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.ApiResponseMapper;
import com.sbcode.cake.data.room.models.ExtraData;
import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.ui.reporting.StatusReporter;
import com.squareup.moshi.JsonAdapter;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * Maps a Twitter API response to slice objects.
 */
public class TwitterApiResponseMapper implements ApiResponseMapper {

    private static final String URI_AUTHORITY_TWITTER_MOBILE = "https://mobile.twitter.com";

    @Nullable
    @Override
    public List<Slice> mapSlices(@NonNull List<Object> items) {
        List<Tweet> tweets = new ArrayList<>();

        for (Object item : items) {
            tweets.add((Tweet) item);
        }

        try {
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);

            ArrayList<Slice> result = new ArrayList<>();

            for (Tweet tweet : tweets) {

                if (tweet == null) {
                    throw new NullPointerException();
                }

                Slice slice = new Slice();

                JsonAdapter<ExtraData> extraDataJsonAdapter = ExtraData.getJsonAdapter();

                slice.createdDate = simpleDateFormat.parse(tweet.createdAt).getTime();
                slice.platformAuthorId = tweet.user.idStr;
                slice.platformAuthorName = tweet.user.name;
                slice.platformAuthorHandle = tweet.user.screenName;
                slice.platformPostId = tweet.idStr;
                slice.platformType = PlatformType.TWITTER;
                slice.polledDate = System.currentTimeMillis();
                slice.totalLikes = tweet.favoriteCount;
                slice.totalComments = tweet.retweetCount;

                /* Set the url for the on click action of a view that is displaying this slice
                 * data
                 */

                slice.postLinkUrl = String.format("%s/%s/status/%s",
                        URI_AUTHORITY_TWITTER_MOBILE,
                        slice.platformAuthorHandle,
                        Long.toString(tweet.id));
                slice.sliceMessage = tweet.text;

                ExtraData extraData = new ExtraData();

                for (MediaEntity mediaEntity : tweet.entities.media) {
                    extraData.add(ExtraData.TYPE_MEDIA, mediaEntity.mediaUrlHttps);
                }

                slice.postExtraData = extraDataJsonAdapter.toJson(extraData);

                result.add(slice);
            }

            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            CakeApplication
                    .getInstance()
                    .component()
                    .getStatusReporter()
                    .enqueueReportDispatchImmediate(
                            new StatusReporter.StatusReport(
                                    StatusReporter.ReportType.PLATFORM,
                                    PlatformType.TWITTER,
                                    CakeApplication
                                            .getInstance()
                                            .getString(R.string.reporter_error_building_slices,
                                                    PlatformType.TWITTER.name())
                            ));
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public Person mapPerson(@NonNull Object item) {
        Tweet tweet = (Tweet) item;

        Person person = new Person();

        try {
            person.platformType = PlatformType.TWITTER;
            person.platformUserId = tweet.user.idStr;
            person.userDisplayHandle = tweet.user.name;
            person.userImageUrl = tweet.user.profileImageUrlHttps;
        } catch (NullPointerException e) {
            e.printStackTrace();
            CakeApplication
                    .getInstance()
                    .component()
                    .getStatusReporter()
                    .enqueueReport(
                            new StatusReporter.StatusReport(
                                    StatusReporter.ReportType.PLATFORM,
                                    PlatformType.FACEBOOK,
                                    CakeApplication
                                            .getInstance()
                                            .getString(
                                                    R.string.reporter_error_invalid_user_object,
                                                    CakeApplication
                                                            .getInstance()
                                                            .getString(PlatformType
                                                                    .getTitleResource(
                                                                            PlatformType.TWITTER)))
                            ));
            return new Person();
        }

        return person;
    }
}
