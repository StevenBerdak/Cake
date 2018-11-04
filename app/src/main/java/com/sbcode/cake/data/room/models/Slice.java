package com.sbcode.cake.data.room.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.logic.indicators.IndicatorComparable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@SuppressWarnings("WeakerAccess")
@Entity(tableName = Slice.TABLE_NAME,
        indices = {
                @Index(value = {Slice.COLUMN_PLATFORM_TYPE, Slice.COLUMN_PLATFORM_POST_ID},
                        unique = true),
                @Index(value = {Slice.COLUMN_PLATFORM_TYPE, Slice.COLUMN_PLATFORM_AUTHOR_ID}),
                @Index(value = {Slice.COLUMN_PLATFORM_TYPE})
        },
        foreignKeys = @ForeignKey(
                entity = Person.class,
                parentColumns = {Person.COLUMN_PLATFORM_TYPE, Person.COLUMN_PLATFORM_USER_ID},
                childColumns = {Slice.COLUMN_PLATFORM_TYPE, Slice.COLUMN_PLATFORM_AUTHOR_ID},
                onDelete = CASCADE))
public class Slice implements Comparable<Slice>, IndicatorComparable {

    public static final String TABLE_NAME = "slices";

    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_PLATFORM_TYPE = "platform_type";
    public static final String COLUMN_PLATFORM_AUTHOR_ID = "platform_author_id";
    public static final String COLUMN_PLATFORM_AUTHOR_NAME = "platform_author_name";
    public static final String COLUMN_PLATFORM_AUTHOR_HANDLE = "platform_author_handle";
    public static final String COLUMN_PLATFORM_POST_ID = "platform_post_id";
    public static final String COLUMN_POLLED_DATE = "polled_date";
    public static final String COLUMN_CREATED_DATE = "created_date";
    public static final String COLUMN_TOTAL_LIKES = "total_likes";
    public static final String COLUMN_TOTAL_COMMENTS = "total_comments";
    public static final String COLUMN_SLICE_MESSAGE = "slice_message";
    public static final String COLUMN_POST_LINK_URL = "post_link_url";
    public static final String COLUMN_SLICE_EXTRA_DATA = "slice_extra_data";

    @ColumnInfo(name = COLUMN_UID)
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = COLUMN_PLATFORM_TYPE)
    public PlatformType platformType;
    @ColumnInfo(name = COLUMN_PLATFORM_AUTHOR_ID)
    public String platformAuthorId;
    @ColumnInfo(name = COLUMN_PLATFORM_AUTHOR_NAME)
    public String platformAuthorName;
    @ColumnInfo(name = COLUMN_PLATFORM_AUTHOR_HANDLE)
    public String platformAuthorHandle;
    @ColumnInfo(name = COLUMN_PLATFORM_POST_ID)
    public String platformPostId;
    @ColumnInfo(name = COLUMN_POLLED_DATE)
    public Long polledDate;
    @ColumnInfo(name = COLUMN_CREATED_DATE)
    public Long createdDate;
    @ColumnInfo(name = COLUMN_TOTAL_LIKES)
    public Integer totalLikes;
    @ColumnInfo(name = COLUMN_TOTAL_COMMENTS)
    public Integer totalComments;
    @ColumnInfo(name = COLUMN_SLICE_MESSAGE)
    public String sliceMessage;
    @ColumnInfo(name = COLUMN_POST_LINK_URL)
    public String postLinkUrl;
    @ColumnInfo(name = COLUMN_SLICE_EXTRA_DATA)
    public String postExtraData;

    @Override
    public int compareTo(@NonNull Slice slice) {
        if (this.createdDate > slice.createdDate)
            return 1;
        else if (this.createdDate < slice.createdDate)
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return String.valueOf(uid);
    }

    @Override
    public int getCompareValue() {
        return createdDate.hashCode();
    }
}
