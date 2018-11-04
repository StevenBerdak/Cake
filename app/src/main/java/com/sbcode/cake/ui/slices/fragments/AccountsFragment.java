package com.sbcode.cake.ui.slices.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.transition.Slide;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.ui.slices.SlicesActivity;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.sbcode.cake.data.platforms.abstracts.AbstractPlatformAccount.ACCOUNTS_BROADCAST_ACTION;
import static com.sbcode.cake.data.platforms.abstracts.AbstractPlatformAccount.KEY_PLATFORM;

/**
 * Fragment for the 'Accounts' screen.
 */
@SuppressWarnings("WeakerAccess")
public class AccountsFragment extends CakeFragment {

    public static final String FRAGMENT_TAG = "accounts_fragment";

    @BindView(R.id.sign_in_button_facebook)
    ConstraintLayout mFacebookSignInButton;
    @BindView(R.id.sign_in_button_twitter)
    ConstraintLayout mTwitterSignInButton;
    @BindView(R.id.sign_in_button_facebook_tv)
    TextView mFacebookSignInButtonTv;
    @BindView(R.id.sign_in_button_twitter_tv)
    TextView mTwitterSignInButtonTv;

    @Inject
    CakePlatformManager mCakePlatformManager;

    AccountsBroadcastReceiver mAccountsBroadcastReceiver;

    private Tracker mTracker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mTracker = CakeApplication.getInstance().getDefaultTracker();
        mTracker.setScreenName(getString(R.string.screen_name_accounts_activity_fragment));

        if (getActivity() != null) {
            ((CakeApplication) getActivity().getApplication())
                    .component()
                    .inject(this);
        } else {
            Timber.w("Activity is null");
        }

        mAccountsBroadcastReceiver = new AccountsBroadcastReceiver();

        mCakePlatformManager.get(PlatformType.FACEBOOK)
                .initSignInButton(getActivity(), mFacebookSignInButton);

        mCakePlatformManager.get(PlatformType.TWITTER)
                .initSignInButton(getActivity(), mTwitterSignInButton);
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        for (Platform socialMediaPlatform : mCakePlatformManager.getPlatforms()) {
            PlatformType platform = socialMediaPlatform.getPlatformType();
            setSignInButtonText(platform);
        }

        if (getActivity() != null)
            getActivity().registerReceiver(mAccountsBroadcastReceiver,
                    mAccountsBroadcastReceiver.getIntentFilter());

        updateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity() != null)
            getActivity().unregisterReceiver(mAccountsBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.v("onActivityResult : requestCode = %s", requestCode);

        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            CallbackManager.Factory.create().onActivityResult(requestCode, resultCode, data);
            mCakePlatformManager.get(PlatformType.FACEBOOK)
                    .getRepository()
                    .queryInitial(getContext());
        } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            new TwitterAuthClient().onActivityResult(requestCode, resultCode, data);
            mCakePlatformManager.get(PlatformType.TWITTER)
                    .getRepository()
                    .queryInitial(getContext());
        }
    }

    /**
     * Sets the text on the sign in button for sign in/sign out
     *
     * @param type The type of PlatformType that applies
     */
    private void setSignInButtonText(PlatformType type) {
        TextView signInTextView = getButtonsSignInTextView(type);
        boolean isSignedIn = mCakePlatformManager.get(type).getAccount().isSignedIn();

        if (signInTextView != null) {
            if (isSignedIn) {
                signInTextView.setText(
                        getString(R.string.sign_in_button_text,
                        getString(R.string.sign_out),
                        getString(PlatformType.getTitleResource(type))));
            } else {
                signInTextView.setText(
                        getString(R.string.sign_in_button_text,
                        getString(R.string.sign_in),
                        getString(PlatformType.getTitleResource(type))));
            }
        } else {
            Timber.e("Invalid platform specified for TextView");
        }
    }

    private TextView getButtonsSignInTextView(@NonNull PlatformType platformType) {
        switch (platformType) {
            case FACEBOOK:
                return mFacebookSignInButtonTv;
            case TWITTER:
                return mTwitterSignInButtonTv;
            default:
                return null;
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition enterTransition() {
        return new Slide();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition exitTransition() {
        return new Slide();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected int transitionDuration() {
        return 300;
    }

    @Override
    public void updateOptionsMenu() {
        if (getActivity() == null) {
            return;
        }

        SlicesActivity slicesActivity = (SlicesActivity) getActivity();
        slicesActivity.setAppBarState(FRAGMENT_TAG, false);
    }

    //Used to detect log-outs.
    public class AccountsBroadcastReceiver extends BroadcastReceiver {

        private final IntentFilter mIntentFilter;

        AccountsBroadcastReceiver() {
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(ACCOUNTS_BROADCAST_ACTION);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            PlatformType platformType = PlatformType.valueOf(intent.getStringExtra(KEY_PLATFORM));
            setSignInButtonText(platformType);

            boolean isSignedIn = mCakePlatformManager.get(platformType).getAccount().isSignedIn();

            Timber.d("Account status broadcast received. Signed in = %s", isSignedIn);

            if (!isSignedIn) {
                mCakePlatformManager.get(platformType).getRepository().deleteAllData(getContext());
            }
        }

        public IntentFilter getIntentFilter() {
            return mIntentFilter;
        }
    }
}
