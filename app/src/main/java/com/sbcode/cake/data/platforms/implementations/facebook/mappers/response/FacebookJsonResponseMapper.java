package com.sbcode.cake.data.platforms.implementations.facebook.mappers.response;

import android.net.Uri;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.implementations.facebook.models.post.AttachmentsDatum;
import com.sbcode.cake.data.platforms.implementations.facebook.models.post.Datum;
import com.sbcode.cake.data.platforms.implementations.facebook.models.post.FacebookPosts;
import com.sbcode.cake.data.platforms.implementations.facebook.models.post.Subattachments;
import com.sbcode.cake.data.platforms.implementations.facebook.models.post.SubattachmentsDatum;
import com.sbcode.cake.data.platforms.implementations.facebook.models.user.FacebookUser;
import com.sbcode.cake.data.platforms.interfaces.JsonResponseMapper;
import com.sbcode.cake.data.platforms.utils.FacebookUtils;
import com.sbcode.cake.data.room.models.ExtraData;
import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.ui.reporting.StatusReporter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Maps responses from the Facebook Graph API to Slice object.
 */
public class FacebookJsonResponseMapper implements JsonResponseMapper {

    private static final String URI_AUTHORITY_FACEBOOK_MOBILE = "m.facebook.com";
    private final Moshi mainMoshi = new Moshi.Builder().build();
    private final FacebookUtils utils = new FacebookUtils();

    @Override
    public List<Slice> mapSlices(String json) {
        try {
            JsonAdapter<FacebookPosts> postsAdapter = mainMoshi
                    .adapter(FacebookPosts.class);

            FacebookPosts posts = postsAdapter.fromJson(json);
            ArrayList<Slice> slices = new ArrayList<>();

            JsonAdapter<ExtraData> extraDataJsonAdapter = ExtraData.getJsonAdapter();

            if (posts == null) {
                Timber.e("Facebook posts object was null");
                return new ArrayList<>();
            }

            for (Datum post : posts.data) {
                Slice slice = new Slice();
                slice.platformType = PlatformType.FACEBOOK;
                slice.platformAuthorId = post.from.id;
                slice.platformAuthorName = post.from.name;
                slice.platformAuthorHandle = post.from.name;
                slice.platformPostId = post.id;
                slice.polledDate = System.currentTimeMillis();
                slice.createdDate = utils.getNormalDate(post.createdTime);
                slice.totalLikes = post.likes.summary.totalCount;
                slice.totalComments = post.comments.summary.totalCount;

                /* Set the url for the on click action of a view that is displaying this slice
                 * data
                 */

                Uri uri = Uri.parse(post.permalinkUrl);
                Uri formattedUri =
                        uri.buildUpon()
                                .encodedAuthority(URI_AUTHORITY_FACEBOOK_MOBILE)
                                .build();

                slice.postLinkUrl = formattedUri.toString();
                slice.sliceMessage = post.message;
                ExtraData extraData = new ExtraData();

                //Add all attached media to the extra data object.
                if (post.attachments != null &&
                        post.attachments.data != null)
                    for (AttachmentsDatum attachment : post.attachments.data)
                        if (attachment != null &&
                                attachment.media != null &&
                                attachment.media.image != null &&
                                attachment.media.image.src != null) {
                            extraData.add(ExtraData.TYPE_MEDIA, attachment.media.image.src);
                            Subattachments sub = attachment.subattachments;
                            if (sub != null)
                                for (SubattachmentsDatum subattachment : sub.data)
                                    if (subattachment != null &&
                                            subattachment.media != null &&
                                            subattachment.media.image != null &&
                                            subattachment.media.image.src != null)
                                        extraData.add(ExtraData.TYPE_MEDIA,
                                                subattachment.media.image.src);
                        }

                slice.postExtraData = extraDataJsonAdapter.toJson(extraData);
                slices.add(slice);
            }

            return slices;

        } catch (IOException e) {
            e.printStackTrace();
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
                                            .getString(R.string.reporter_error_building_slices,
                                                    PlatformType.FACEBOOK.name())
                            ));
            return new ArrayList<>();
        }
    }

    @Override
    public Person mapPerson(String json) {
        JsonAdapter<FacebookUser> userAdapter = mainMoshi.adapter(FacebookUser.class);

        try {
            FacebookUser user = userAdapter.fromJson(json);

            if (user == null) {
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
                                                                                PlatformType.FACEBOOK)))
                                ));
                return null;
            }

            Person person = new Person();

            person.platformType = PlatformType.FACEBOOK;
            person.platformUserId = user.id;
            person.userDisplayHandle = user.name;
            person.userImageUrl = user.picture.data.url;

            return person;
        } catch (IOException e) {
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
                                                                            PlatformType.FACEBOOK)))
                            ));
            return null;
        }
    }
}
