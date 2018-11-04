package com.sbcode.cake.data.room.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.sbcode.cake.data.PlatformType;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@SuppressWarnings("WeakerAccess")
@Entity(tableName = Update.TABLE_NAME,
        indices = {
                @Index(value = {Update.COLUMN_PLATFORM_TYPE}),
                @Index(value = {Update.COLUMN_PLATFORM_TYPE, Update.COLUMN_PLATFORM_POST_ID})
        },
        foreignKeys = @ForeignKey(
                entity = Slice.class,
                parentColumns = {Slice.COLUMN_PLATFORM_TYPE, Slice.COLUMN_PLATFORM_POST_ID},
                childColumns = {Update.COLUMN_PLATFORM_TYPE, Update.COLUMN_PLATFORM_POST_ID},
                onDelete = CASCADE))
public class Update implements Parcelable {

    public static final String TABLE_NAME = "updates";

    public static final String COLUMN_UPDATE_CREATED_DATE = "update_created_date";
    public static final String COLUMN_PLATFORM_TYPE = "column_platform_type";
    public static final String COLUMN_PLATFORM_POST_ID = "column_platform_post_id";
    public static final String COLUMN_LIKES_ADDED = "likes_added";
    public static final String COLUMN_COMMENTS_ADDED = "comments_added";
    public static final String COLUMN_IS_CONSUMED = "is_consumed";

    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = COLUMN_UPDATE_CREATED_DATE)
    public Long createdDate;
    @ColumnInfo(name = COLUMN_PLATFORM_TYPE)
    public PlatformType platformType;
    @ColumnInfo(name = COLUMN_PLATFORM_POST_ID)
    public String platformPostId;
    @ColumnInfo(name = COLUMN_LIKES_ADDED)
    public Integer likesAdded;
    @ColumnInfo(name = COLUMN_COMMENTS_ADDED)
    public Integer commentsAdded;
    @ColumnInfo(name = COLUMN_IS_CONSUMED)
    public boolean isConsumed;

    public Update() {}

    protected Update(Parcel in) {
        uid = in.readInt();
        if (in.readByte() == 0) {
            createdDate = null;
        } else {
            createdDate = in.readLong();
        }
        platformType = PlatformType.valueOf(in.readString());
        platformPostId = in.readString();
        if (in.readByte() == 0) {
            likesAdded = null;
        } else {
            likesAdded = in.readInt();
        }
        if (in.readByte() == 0) {
            commentsAdded = null;
        } else {
            commentsAdded = in.readInt();
        }
        isConsumed = in.readByte() != 0;
    }

    public static final Creator<Update> CREATOR = new Creator<Update>() {
        @Override
        public Update createFromParcel(Parcel in) {
            return new Update(in);
        }

        @Override
        public Update[] newArray(int size) {
            return new Update[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        if (createdDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(createdDate);
        }
        dest.writeString(platformType.name());
        dest.writeString(platformPostId);
        if (likesAdded == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(likesAdded);
        }
        if (commentsAdded == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(commentsAdded);
        }
        dest.writeByte((byte) (isConsumed ? 1 : 0));
    }
}
