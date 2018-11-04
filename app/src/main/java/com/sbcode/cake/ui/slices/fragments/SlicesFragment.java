package com.sbcode.cake.ui.slices.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.platforms.manager.PlatformManager;
import com.sbcode.cake.data.platforms.utils.PlatformUtils;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.room.services.DatabaseQueryNewPostsService;
import com.sbcode.cake.data.room.services.DatabaseRefreshDataService;
import com.sbcode.cake.injection.components.DaggerSlicesFragmentComponent;
import com.sbcode.cake.injection.components.modules.SlicesFragmentModule;
import com.sbcode.cake.logic.indicators.ListRangeIndicators;
import com.sbcode.cake.ui.slices.SlicesActivity;
import com.sbcode.cake.ui.slices.SlicesViewModel;
import com.sbcode.cake.ui.slices.adapter.SlicesAdapter;
import com.sbcode.cake.ui.slices.adapter.SlicesLoaded;

import java.util.HashSet;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Fragment for the 'Slices' screen.
 */
@SuppressWarnings("WeakerAccess")
public class SlicesFragment extends CakeFragment implements SlicesLoaded {

    public static final String FRAGMENT_TAG = "slices_fragment";
    public static final String SIS_BUNDLE_LIST_STATE = "rv_position";

    @BindView(R.id.status_message_accounts_button)
    Button mAccountsButton;
    @BindView(R.id.slice_status_messages)
    NestedScrollView mStatusMessagesLayout;
    @BindView(R.id.slices_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.slices_rv)
    RecyclerView mRecyclerView;

    @Inject
    SlicesAdapter mSlicesAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    SlicesViewModel mSlicesViewModel;

    private ListRangeIndicators.ListRangeIndicatorsCallback<Slice> mSliceIndicatorsCallback;
    private Handler mHandler;
    private Tracker mTracker;

    private Parcelable mRvLayoutManagerState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Timber.d("onViewCreated");

        mTracker = CakeApplication.getInstance().getDefaultTracker();
        mTracker.setScreenName(getString(R.string.screen_name_slices_activity_fragment));

        mHandler = new Handler(Looper.getMainLooper());


        if (getActivity() != null) {
            DaggerSlicesFragmentComponent
                    .builder()
                    .slicesFragmentModule(new SlicesFragmentModule(this))
                    .slicesActivityModule(((SlicesActivity) getActivity()).getSlicesActivityModule())
                    .build()
                    .inject(this);
        }

        mSlicesViewModel
                .getSlices()
                .observe(this, slices -> mSwipeRefreshLayout.setRefreshing(false));

        initViews();

        mSlicesAdapter.setLiveUpdatesData(this, mSlicesViewModel.getUpdates());
        mSlicesAdapter.setLiveSliceData(this, mSlicesViewModel.getSlices());

        initUtilityViews();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecyclerView.getLayoutManager() != null) {
            Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(SIS_BUNDLE_LIST_STATE, listState);
            Timber.d("onSaveInstanceState : listState added to outState");
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && mRecyclerView != null) {
            Parcelable listState = savedInstanceState.getParcelable(SIS_BUNDLE_LIST_STATE);
            Timber.d("onViewStateRestored : sis not null, rv not null");
            if (listState != null) {
                mRvLayoutManagerState = listState;
            }
        }
    }

    public void initViews() {
        CakeApplication.getInstance().component().getStatusReporter().checkReports();

        HashSet<Platform> activePlatforms =
                mSlicesViewModel.getPlatformManager().getPlatformsSignedIn();

        if (activePlatforms.size() > 0) {

            mStatusMessagesLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mRecyclerView.requestFocus();

            /* Signed in to platforms. Set views */
            if (mRecyclerView.getAdapter() == null) {
                mRecyclerView.setAdapter(mSlicesAdapter);
            }

            /* If has not children and platforms are signed in, display loading indicator */
            if (mRecyclerView.getLayoutManager() != null &&
                    mRecyclerView.getLayoutManager().getChildCount() == 0) {
                mSwipeRefreshLayout.setRefreshing(true);
            }

            for (Platform platform : activePlatforms) {
                Intent extras = DatabaseQueryNewPostsService
                        .intentBuilder(platform.getPlatformType());
                DatabaseQueryNewPostsService.enqueueWork(
                        CakeApplication.getInstance().getApplicationContext(),
                        DatabaseQueryNewPostsService.class
                        , DatabaseQueryNewPostsService.JOB_ID, extras
                );
            }
        } else {

            /* Not signed in to any platforms. Set views */

            mRecyclerView.setAdapter(null);
            mStatusMessagesLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            mStatusMessagesLayout.requestFocus();
        }

        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int rvScrollPosition = 0;

            /**
             * Callback method to be invoked when the RecyclerView has been scrolled. This will be
             * called after the scroll has completed.
             * <p>
             * This callback will also be called if visible item range changes after a layout
             * calculation. In that case, dx and dy will be 0.
             *
             * @param recyclerView The RecyclerView which scrolled.
             * @param dx           The amount of horizontal scroll.
             * @param dy           The amount of vertical scroll.
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int nextPosition = getScrollPosition();
                if (nextPosition != rvScrollPosition) {
                    rvScrollPosition = nextPosition;
                    View view = mLayoutManager.findViewByPosition(rvScrollPosition);
                    if (view != null) {
                        mSlicesAdapter.onViewPositionChanged(view, rvScrollPosition);
                    }
                }
            }
        });

        mSliceIndicatorsCallback = new ListRangeIndicators.ListRangeIndicatorsCallback<Slice>() {
            @Override
            public void onListEmpty() {
                Timber.d("Indicator onListEmpty called");
            }

            @Override
            public void onBefore(Slice item) {
                if (item.platformType == PlatformType.NULL) {
                    return;
                }

                Timber.d("Indicator onBefore called for platform %s",
                        item.platformType.name());

                PlatformManager manager = mSlicesViewModel.getPlatformManager();
                PlatformUtils utils = manager.get(item.platformType).getUtils();

                Long before = utils.getIndicatorLong(item);

                manager.get(item.platformType).getRepository().queryBefore(getContext(), before);

                showLoadingIndicator();
            }

            @Override
            public void onAfter(Slice item) {
                if (item.platformType == PlatformType.NULL) {
                    return;
                }

                Timber.d("Indicator onAfter called for platform %s",
                        item.platformType.name());

                PlatformManager manager = mSlicesViewModel.getPlatformManager();
                PlatformUtils utils = manager.get(item.platformType).getUtils();

                Long after = utils.getIndicatorLong(item);

                manager.get(item.platformType).getRepository().queryAfter(getContext(), after);

                showLoadingIndicator();
            }
        };
    }

    @Override
    public void updateOptionsMenu() {
        if (getActivity() == null) {
            return;
        }

        SlicesActivity slicesActivity = (SlicesActivity) getActivity();
        slicesActivity.setAppBarState(FRAGMENT_TAG, true);
    }

    private void initUtilityViews() {
        mAccountsButton.setOnClickListener(
                v -> {
                    if (getActivity() != null)
                        ((SlicesActivity) getActivity())
                                .loadFragment(AccountsFragment.FRAGMENT_TAG, null);
                });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            HashSet<Platform> signedIn =
                    mSlicesViewModel.getPlatformManager().getPlatformsSignedIn();

            if (signedIn.size() == 0) {
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }

            for (Platform platform : signedIn) {

                Intent refreshPlatformDataIntent =
                        DatabaseRefreshDataService.intentBuilder(platform.getPlatformType());

                DatabaseRefreshDataService.enqueueWork(
                        mSwipeRefreshLayout.getContext(),
                        DatabaseRefreshDataService.class,
                        DatabaseRefreshDataService.JOB_ID,
                        refreshPlatformDataIntent
                );
            }

            mHandler.postDelayed(() ->
                    mSwipeRefreshLayout.setRefreshing(false), 3000);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSlicesAdapter
                .getIndicators()
                .getCallbackManager()
                .addCallback(mSliceIndicatorsCallback);

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        updateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSlicesAdapter
                .getIndicators()
                .getCallbackManager()
                .removeCallback(mSliceIndicatorsCallback);
    }

    private int getScrollPosition() {
        return mLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    public void showLoadingIndicator() {
        mSwipeRefreshLayout.setRefreshing(true);
        mHandler.postDelayed(() -> mSwipeRefreshLayout.setRefreshing(false), 3000);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition enterTransition() {
        return new Explode();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition exitTransition() {
        return new Explode();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected int transitionDuration() {
        return 300;
    }

    @Override
    public void onSlicesLoaded() {
        if (mRvLayoutManagerState != null && mRecyclerView.getLayoutManager() != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mRvLayoutManagerState);
            mRvLayoutManagerState = null;
        }
    }
}
