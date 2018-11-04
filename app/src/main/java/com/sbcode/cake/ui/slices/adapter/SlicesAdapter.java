package com.sbcode.cake.ui.slices.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.data.room.models.Datum;
import com.sbcode.cake.data.room.models.ExtraData;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.room.models.Update;
import com.sbcode.cake.ui.slices.SlicesActivity;
import com.sbcode.cake.utils.AppUtils;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Adapts Slice objects to RecyclerView items in the main Slice layout.
 */
public class SlicesAdapter extends RecyclerView.Adapter<SlicesAdapter.SliceViewHolder>
        implements SlicesViewPositionChanged {

    private static final int AD_PLACEMENT_FACTOR = 13;

    private final MutableLiveData<List<Slice>> mSlices;
    private final SlicesDiffCallback mDiffCallback;
    private final CakePlatformManager mCakePlatformManager;
    private final SlicesIndicators mSlicesIndicators;
    private final View.OnClickListener mSliceOnClickListener;
    private final JsonAdapter<ExtraData> mExtraDataJsonAdapter;
    private final SlicesLoaded mSlicesLoadedCallback;
    private final int mTagKeyPlatform, mTagKeyPosition;

    private List<Update> mUpdates;
    private RecyclerView mRecyclerView;

    class SliceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.slice_date)
        TextView sliceDate;
        @BindView(R.id.slice_content_message)
        TextView sliceMessage;
        @BindView(R.id.slice_platform_image)
        ImageView platformImage;
        @BindView(R.id.slice_status_likes)
        TextView sliceLikes;
        @BindView(R.id.slice_status_comments)
        TextView sliceComments;
        @BindView(R.id.slice_content_images_container)
        LinearLayout mImagesContainerLayout;
        @BindView(R.id.slice_status_likes_updates)
        TextView sliceLikesUpdates;
        @BindView(R.id.slice_status_comments_updates)
        TextView sliceCommentsUpdates;

        SliceViewHolder(View itemView, boolean isAdView) {
            super(itemView);
            if (!isAdView) {
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public SlicesAdapter(CakePlatformManager cakePlatformManager,
                         SlicesLoaded slicesLoadedCallback) {
        mSlices = new MutableLiveData<>();
        mSlices.setValue(new ArrayList<>());
        mDiffCallback = new SlicesDiffCallback();

        mSlicesIndicators = new SlicesIndicators(30);
        mSlicesIndicators.triggerOnce(true);

        this.mCakePlatformManager = cakePlatformManager;

        mTagKeyPlatform = R.integer.tag_key_platform;
        mTagKeyPosition = R.integer.tag_key_position;

        mExtraDataJsonAdapter = new Moshi.Builder().build().adapter(ExtraData.class);

        this.mSliceOnClickListener = v -> {
            if (mSlices.getValue() != null) {
                Slice slice = mSlices.getValue().get((Integer) v.getTag(mTagKeyPosition));

                ((SlicesActivity) v.getContext()).handleSliceClicked(slice);
            }
        };

        this.mSlicesLoadedCallback = slicesLoadedCallback;
    }

    @Override
    public void onViewPositionChanged(View view, int position) {
        if (!isAdPosition(position)) {
            int realPosition = getRealIndexPosition(position);
            PlatformType platformType = PlatformType.valueOf((String) view.getTag(mTagKeyPlatform));
            mSlicesIndicators.checkIndicators(platformType, realPosition);
        }
    }

    @NonNull
    @Override
    public SliceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Resources res = parent.getResources();

        if (viewType == 0) {
            return new SliceViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_slice, parent, false), false);
        } else {
            /* Build ad container frame layout */
            FrameLayout adContainer = new FrameLayout(parent.getContext());
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int stdMarginPadding = (int) res.getDimension(R.dimen.standard_margin_padding);
            frameParams.setMargins(0, stdMarginPadding, 0, 0);
            adContainer.setLayoutParams(frameParams);

            /* Build ad view */
            AdView adView = new AdView(parent.getContext());
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(parent.getResources().getString(R.string.admob_app_unit_id));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            adView.setLayoutParams(params);
            adView.getLayoutParams();

            adContainer.addView(adView);

            /* Get AdRequest and add to view */
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            AdRequest request = adRequestBuilder.build();
            adView.loadAd(request);

            return new SliceViewHolder(adContainer, true);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SliceViewHolder holder, int position) {
        assert mSlices.getValue() != null;

        if (holder.getItemViewType() == 0) {

            int realPosition = getRealIndexPosition(position);

            Slice slice = mSlices.getValue().get(realPosition);

            holder.sliceMessage.setText(slice.sliceMessage);

            long[] difference = arrayOfNowTimeDifference(slice.createdDate);

            String readableTimeDifference = buildReadableTimeDifference(
                    holder
                            .itemView
                            .getContext()
                            .getResources(),
                    difference);

            holder.sliceDate.setText(readableTimeDifference);

            Platform platform = mCakePlatformManager.get(slice.platformType);

            if (platform != null) {
                Drawable drawable = ContextCompat.getDrawable(
                        holder.itemView.getContext(),
                        platform.getBadgeImageResourceId());

                holder.platformImage.setImageDrawable(drawable);


                String totalLikes =
                        String.format(
                                holder
                                        .itemView
                                        .getContext()
                                        .getString(
                                                platform.getLikesFormattedMessageResourceId()),
                                slice.totalLikes);
                String totalComments =
                        String.format(
                                holder
                                        .itemView
                                        .getContext()
                                        .getString(
                                                platform.getCommentsFormattedMessageResourceId()),
                                slice.totalComments);

                holder.sliceLikes.setText(totalLikes);
                holder.sliceComments.setText(totalComments);
            }

            holder.itemView.setTag(mTagKeyPlatform, slice.platformType.name());
            holder.itemView.setTag(mTagKeyPosition,
                    getRealIndexPosition(holder.getAdapterPosition()));

            holder.itemView.setOnClickListener(mSliceOnClickListener);

            if (slice.sliceMessage == null || slice.sliceMessage.length() == 0) {
                holder.sliceMessage.setVisibility(View.GONE);
            } else {
                holder.sliceMessage.setVisibility(View.VISIBLE);
            }

            try {
                holder.mImagesContainerLayout.removeAllViews();

                if (slice.postExtraData == null) {
                    return;
                }

                ExtraData extraData = mExtraDataJsonAdapter.fromJson(slice.postExtraData);

                if (extraData != null && extraData.data.size() > 0) {

                    holder.mImagesContainerLayout.setVisibility(View.VISIBLE);

                    Datum datum;
                    for (int i = 0; i < extraData.data.size(); ++i) {
                        datum = extraData.data.get(i);

                        if (datum.type.equals(ExtraData.TYPE_MEDIA)) {

                            ImageView imageView = new ImageView(holder.itemView.getContext());

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                            if (!(i >= extraData.data.size() - 1)) {
                                params.setMargins(0, 0, 0, 8);
                            }

                            imageView.setLayoutParams(params);
                            imageView.setAdjustViewBounds(true);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            AppUtils.seamlessLoadFromUrlToContainer(imageView, datum.value, imageView);

                            holder.mImagesContainerLayout.addView(imageView);
                        }
                    }
                } else {
                    holder.mImagesContainerLayout.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                Timber.d("Error retrieving extra data from slice at position %s",
                        realPosition);
                e.printStackTrace();
            }

            if (mUpdates != null) {
                holder.sliceLikesUpdates.setVisibility(View.GONE);
                holder.sliceCommentsUpdates.setVisibility(View.GONE);
                for (Update update : mUpdates) {
                    if (update.platformPostId.equals(slice.platformPostId) &&
                            update.platformType == slice.platformType) {
                        if (update.likesAdded > 0) {
                            holder.sliceLikesUpdates.setVisibility(View.VISIBLE);
                            holder.sliceLikesUpdates.setText(
                                    String.format("(+%s)", update.likesAdded)
                            );
                        }
                        if (update.commentsAdded > 0) {
                            holder.sliceCommentsUpdates.setVisibility(View.VISIBLE);
                            holder.sliceCommentsUpdates.setText(
                                    String.format("(+%s)", update.commentsAdded)
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdPosition(position)) {
            return 1;
        }

        return 0;
    }

    private int getRealIndexPosition(int position) {
        return position - (position / AD_PLACEMENT_FACTOR);
    }

    private boolean isAdPosition(int position) {
        return position != 0 && position % AD_PLACEMENT_FACTOR == 0;
    }

    private String buildReadableTimeDifference(Resources resources, long[] difference) {

        long current;

        if ((current = difference[0]) > 0) {

            return current + (current < 2 ? resources.getString(R.string.time_day_ago) :
                    resources.getString(R.string.time_days_ago));

        } else if ((current = difference[1]) > 0) {

            return current + (current < 2 ? resources.getString(R.string.time_hour_ago) :
                    resources.getString(R.string.time_hours_ago));

        } else if ((current = difference[2]) > 0) {

            return current + (current < 2 ? resources.getString(R.string.time_minute_ago) :
                    resources.getString(R.string.time_minutes_ago));

        } else {

            return resources.getString(R.string.time_moments_ago);
        }
    }

    @Override
    public int getItemCount() {
        if (mSlices.getValue() == null)
            return 0;

        return mSlices.getValue().size();
    }

    public SlicesIndicators getIndicators() {
        return mSlicesIndicators;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setLiveSliceData(LifecycleOwner lifecycleOwner, LiveData<List<Slice>> data) {
        data.observe(lifecycleOwner, slices -> {
            if (mSlices.getValue() != null) {
                if (slices != null && slices.size() > 0) {
                    mSlicesLoadedCallback.onSlicesLoaded();
                }

                int oldSize = mSlices.getValue().size();

                mDiffCallback.setLists(mSlices.getValue(), slices);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(mDiffCallback);

                mSlices.setValue(slices);

                diffResult.dispatchUpdatesTo(this);

                if (slices != null && mRecyclerView != null &&
                        mRecyclerView.getChildCount() > 0 &&
                        oldSize < slices.size()) {
                    mRecyclerView.smoothScrollToPosition(0);
                }

                if (slices != null && oldSize != slices.size()) {
                    mSlicesIndicators.setData(mSlices.getValue());
                } else if (slices == null) {
                    mSlicesIndicators.setData(new ArrayList<>());
                }
            }
        });
    }

    public void setLiveUpdatesData(LifecycleOwner lifecycleOwner, LiveData<List<Update>> data) {
        data.observe(lifecycleOwner, updates -> mUpdates = updates);
    }

    private static long[] arrayOfNowTimeDifference(long subtrahend) {
        long currentTime = System.currentTimeMillis();

        long difference = currentTime - subtrahend;

        long minutes = (difference / (1000 * 60)) % 60;

        long hours = (difference / (1000 * 60 * 60)) % 60;

        long days = (difference / (1000 * 60 * 60 * 24));

        return new long[]{days, hours, minutes};
    }
}
