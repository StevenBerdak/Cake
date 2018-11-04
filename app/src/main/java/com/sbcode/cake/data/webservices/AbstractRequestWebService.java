package com.sbcode.cake.data.webservices;

import android.content.Intent;
import android.support.v4.app.JobIntentService;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.CakeNotifications;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.RequestHandler;
import com.sbcode.cake.data.platforms.interfaces.ResponseMapper;
import com.sbcode.cake.data.room.models.Person;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.room.models.Update;
import com.sbcode.cake.widget.CakeWidgetProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

public abstract class AbstractRequestWebService extends JobIntentService implements RequestService {

    ResponseMapper getResponseMapper(PlatformType platformType) {
        return getPlatformManager().get(platformType).getResponseMapper();
    }

    List<String> compileUnknownPersonsIdList(List<Slice> slices, PlatformType platform,
                                             List<Person> personsInDb) {

        List<String> platformIds = new ArrayList<>();

        for (Person person : personsInDb) {
            if (person.platformType == platform)
                platformIds.add(person.platformUserId);
        }

        Iterator<Slice> sliceIterator = slices.iterator();

        ArrayList<String> result = new ArrayList<>();

        Slice slice;
        while (sliceIterator.hasNext()) {
            slice = sliceIterator.next();
            if (!platformIds.contains(slice.platformAuthorId) &&
                    !result.contains(slice.platformAuthorId)) {
                result.add(slice.platformAuthorId);
            }
        }

        return result;
    }

    protected void insertIntoDatabase(PlatformType platformType, List<Slice> sliceList) {

        if (sliceList.size() == 0) {
            Timber.d("List size is 0, no slices were inserted into the database");
            return;
        }

        // Get list before insertion for handling updates.
        List<Slice> beforeInsertionSlices = getDatabase().sliceDao()
                .queryAllByPlatformForList(platformType);

        // Insert slices into the database.
        getDatabase().sliceDao().insert(sliceList);

        //Get list after insertion for handling updates.
        List<Slice> afterInsertionSlices = getDatabase().sliceDao()
                .queryAllByPlatformForList(platformType);

        // If before insertion or after insertion either list is null or has a size of 0
        // then no update should be added, return early.
        if (beforeInsertionSlices == null || beforeInsertionSlices.size() == 0 ||
                afterInsertionSlices == null || afterInsertionSlices.size() == 0) {
            Timber.d("Before or after lists was empty, no transactions to record");
            return;
        }

        //Map for slices using platform type concatenate with post id as key.
        HashMap<String, Slice> beforeInsertionSlicesMap = new HashMap<>();

        // Add all slices before insertion
        for (Slice beforeSlice : beforeInsertionSlices) {
            beforeInsertionSlicesMap.put(beforeSlice.platformType.name() + beforeSlice.platformPostId, beforeSlice);
        }

        //  Array list for all updates identified.
        ArrayList<Update> updateResults = new ArrayList<>();

        Slice mapSlice;
        for (Slice afterSlice : afterInsertionSlices) {
            // If the after insertion slice is contained in the before insertion map then check its
            // contents.
            if ((mapSlice = beforeInsertionSlicesMap.get(afterSlice.platformType.name() +
                    afterSlice.platformPostId)) != null) {
                if (!mapSlice.totalLikes.equals(afterSlice.totalLikes) ||
                        !mapSlice.totalComments.equals(afterSlice.totalComments)) {
                    int newLikes = afterSlice.totalLikes - mapSlice.totalLikes;
                    newLikes = newLikes > 0 ? newLikes : 0;
                    int newComments = afterSlice.totalComments - mapSlice.totalComments;
                    newComments = newComments > 0 ? newComments : 0;

                    if ((newLikes ^ newComments) == 0) {
                        continue;
                    }

                    Update update = new Update();

                    update.createdDate = System.currentTimeMillis();
                    update.platformPostId = afterSlice.platformPostId;
                    update.isConsumed = false;
                    update.platformType = afterSlice.platformType;
                    update.likesAdded = newLikes;
                    update.commentsAdded = newComments;

                    updateResults.add(update);
                }
            }
        }

        int newLikes = 0, newComments = 0;

        if (updateResults.size() > 0) {
            Timber.d("Updates inserted, count = %s", updateResults.size());
            getDatabase().updatesDao().insert(updateResults);

            for (Update update : updateResults) {
                newLikes += update.likesAdded;
                newComments += update.commentsAdded;
            }
        }

        /* Send broadcast to update widget */
        Intent intent = CakeWidgetProvider.intentBuilder(newLikes, newComments);
        sendBroadcast(intent);

        /* Send notification (if required) */
        if (!CakeApplication.isInForeground()) {
            CakeNotifications.sendNewLikesCommentsNotification(this, newLikes, newComments);
        }
    }

    protected RequestHandler getRequestHandler(PlatformType platformType) {
        return getPlatformManager().get(platformType).getRequestHandler();
    }

    protected void timeout(long millis) {
        long timeoutEndDate = System.currentTimeMillis() + millis;

        long counter = 0;

        while (System.currentTimeMillis() < timeoutEndDate) {
            counter++;
        }

        Timber.d("Timeout completed at system time = %s, counter ticks =  %s",
                timeoutEndDate, counter);
    }
}
