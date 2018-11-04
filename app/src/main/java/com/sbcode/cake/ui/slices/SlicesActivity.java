package com.sbcode.cake.ui.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.platforms.interfaces.NewPostHandler;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.injection.components.DaggerSlicesActivityComponent;
import com.sbcode.cake.injection.components.modules.CakeApplicationModule;
import com.sbcode.cake.injection.components.modules.SlicesActivityModule;
import com.sbcode.cake.injection.components.modules.StatusReporterModule;
import com.sbcode.cake.ui.dialog.PostDialogFragment;
import com.sbcode.cake.ui.reporting.StatusReporter;
import com.sbcode.cake.ui.slices.fragments.AboutFragment;
import com.sbcode.cake.ui.slices.fragments.AccountsFragment;
import com.sbcode.cake.ui.slices.fragments.SlicesFragment;
import com.sbcode.cake.ui.slices.fragments.WebViewFragment;
import com.sbcode.cake.utils.AppUtils;

import java.util.List;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Component;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class SlicesActivity extends CakeActivity {

    public static final String SIS_KEY_OPTIONS_MENU_VISIBLE = "sk_options_menu_vis";
    public static final String SIS_KEY_CURRENT_FRAGMENT = "sk_current_fragment";

    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar_logo)
    ImageView mAppBarLogo;
    @BindView(R.id.activity_slices_root_view)
    CoordinatorLayout mRootView;

    private FragmentManager mFragmentManager;
    private StatusReporter.ReportsCallback mReportsCallback;

    private SlicesActivityModule mSlicesActivityModule;
    private Menu mMenu;
    private boolean mOptionsItemsVisible;
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slices);
        ButterKnife.bind(this);

        mSlicesActivityModule = new SlicesActivityModule(this);


        DaggerSlicesActivityComponent
                .builder()
                .slicesActivityModule(mSlicesActivityModule)
                .build().inject(this);

        setSupportActionBar(mToolbar);

        mFragmentManager = getSupportFragmentManager();

        mReportsCallback = (statusReporter) -> {
            Timber.i("Reports callback triggered");
            AppUtils.summonSnackBarSelfClosingWithRunnable(
                    mRootView,
                    statusReporter.pullReport().getMessage(),
                    getString(R.string.ok),
                    statusReporter::checkReports);
        };

        if (mFragmentManager.findFragmentByTag(SlicesFragment.FRAGMENT_TAG) == null) {
            mFragmentManager.beginTransaction().add(R.id.slices_content_layout,
                    new SlicesFragment(), SlicesFragment.FRAGMENT_TAG).commit();
        }

        mFab.setOnClickListener(view -> {
            PostDialogFragment dialog = new PostDialogFragment();
            dialog.show(getSupportFragmentManager(), PostDialogFragment.FRAGMENT_TAG);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(SIS_KEY_OPTIONS_MENU_VISIBLE, mOptionsItemsVisible);
        outState.putString(SIS_KEY_CURRENT_FRAGMENT, mCurrentFragmentTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOptionsItemsVisible = savedInstanceState.getBoolean(SIS_KEY_OPTIONS_MENU_VISIBLE);
        mCurrentFragmentTag = savedInstanceState.getString(SIS_KEY_CURRENT_FRAGMENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CakeApplication) getApplication())
                .component()
                .getStatusReporter()
                .getCallbackManager()
                .addCallback(mReportsCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((CakeApplication) getApplication())
                .component()
                .getStatusReporter()
                .getCallbackManager()
                .removeCallback(mReportsCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_slices, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem accountsItem = menu.findItem(R.id.action_manage_accounts);
        View actionView = accountsItem.getActionView();
        actionView.setOnClickListener(view -> onOptionsItemSelected(accountsItem));
        ImageView imageView = actionView.findViewById(R.id.action_view_image_view);
        imageView.setImageResource(R.drawable.ic_person_outline_white_24dp);

        setAppBarState(mCurrentFragmentTag, mOptionsItemsVisible);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> setAppBarState(mCurrentFragmentTag, mOptionsItemsVisible));

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SlicesFragment slicesFragment = (SlicesFragment)
                getSupportFragmentManager().findFragmentByTag(SlicesFragment.FRAGMENT_TAG);

        if (slicesFragment != null && slicesFragment.isVisible()) {
            slicesFragment.updateOptionsMenu();
            slicesFragment.initViews();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_manage_accounts:
                loadFragment(AccountsFragment.FRAGMENT_TAG, null);
                return true;
            case R.id.action_about:
                loadFragment(AboutFragment.FRAGMENT_TAG, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void handleSliceClicked(Slice slice) {
        CakeApplication cakeApplication = (CakeApplication) getApplication();
        Platform platform = cakeApplication.component().getPlatformManager()
                .get(slice.platformType);

        Timber.d("Slice type, platform type = %s, %s", slice.platformType.name(),
                platform.getPlatformType().name());

        if (platform.nativeAppInstalled()) {
            Uri uri = platform.getNativePostUri(slice);
            Timber.d("Slice clicked uri = %s", uri.toString());
            Intent openNativeIntent = new Intent(Intent.ACTION_DEFAULT, uri);
            if (openNativeIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(openNativeIntent);
            }
            return;
        }

        String postLinkUrl = slice.postLinkUrl;
        Timber.d("Start web view for url %s", postLinkUrl);
        loadUrl(postLinkUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NewPostHandler.IMAGE_SELECT_REQUEST_CODE &&
                resultCode == RESULT_OK && data != null && data.getData() != null &&
                mActivityResultMetaPlatform != null) {

            Uri uri = data.getData();

            CakeApplication
                    .getInstance()
                    .component()
                    .getPlatformManager()
                    .get(mActivityResultMetaPlatform)
                    .getNewPostHandler().handleNewPostImage(SlicesActivity.this, uri);
        } else if (requestCode == NewPostHandler.IMAGE_SELECT_REQUEST_CODE) {
            Timber.e("Error receiving image from intent");
        } else {
            List<Fragment> activeFragments = getSupportFragmentManager().getFragments();
            for (Fragment f : activeFragments) {
                f.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void loadFragment(String fragmentTag, Bundle arguments) {
        Fragment fragment = null;

        switch (fragmentTag) {
            case AccountsFragment.FRAGMENT_TAG:
                fragment = new AccountsFragment();
                break;
            case AboutFragment.FRAGMENT_TAG:
                fragment = new AboutFragment();
                break;
            case WebViewFragment.FRAGMENT_TAG:
                fragment = new WebViewFragment();
                break;
        }

        if (fragment != null && mFragmentManager.findFragmentByTag(fragmentTag) == null) {
            if (arguments != null) {
                fragment.setArguments(arguments);
            }

            mFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.slices_content_layout, fragment, fragmentTag)
                    .commit();
        } else {
            Timber.e("Error loading fragment");
        }
    }

    public void setAppBarState(String fragmentTag, boolean showOptionsMenuItems) {
        mCurrentFragmentTag = fragmentTag;
        mOptionsItemsVisible = showOptionsMenuItems;

        switch (fragmentTag) {
            case SlicesFragment.FRAGMENT_TAG:
                mToolbar.setNavigationIcon(null);
                mToolbar.setNavigationOnClickListener(null);
                mToolbar.setTitle(getString(R.string.app_name));
                break;
            case AccountsFragment.FRAGMENT_TAG:
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar.setNavigationOnClickListener(v -> onBackPressed());
                mToolbar.setTitle(getString(R.string.manage_accounts));
                break;
            case AboutFragment.FRAGMENT_TAG:
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar.setNavigationOnClickListener(v -> onBackPressed());
                mToolbar.setTitle(getString(R.string.about_cake));
                break;
            case WebViewFragment.FRAGMENT_TAG:
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mToolbar.setNavigationOnClickListener(v -> onBackPressed());
                mToolbar.setTitle(R.string.app_name);
                break;
        }

        showOptionsMenuItems(showOptionsMenuItems);
    }

    private void showOptionsMenuItems(boolean showItems) {
        if (mMenu == null) {
            return;
        }

        int duration = 300;
        int start = showItems ? 0 : 1;
        int end = showItems ? 1 : 0;

        AlphaAnimation alphaAnimation = new AlphaAnimation(start, end);
        alphaAnimation.setDuration(duration);

        MenuItem accountsItem = mMenu.findItem(R.id.action_manage_accounts);
        MenuItem aboutItem = mMenu.findItem(R.id.action_about);

        if (accountsItem != null) {
            accountsItem.getActionView()
                    .findViewById(R.id.action_view_image_view)
                    .startAnimation(alphaAnimation);
        }

        if (accountsItem != null && aboutItem != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                accountsItem.setVisible(showItems);
                aboutItem.setVisible(showItems);
            }, duration);
        }
    }

    public static PendingIntent getPendingIntent(Context context) {
        Intent slicesActivityIntent = new Intent(context, SlicesActivity.class);
        return PendingIntent.getActivity(context, 0,
                slicesActivityIntent, 0);
    }

    @Override
    public void loadUrl(String url) {
        Uri postLinkUri = Uri.parse(url);

        postLinkUri = postLinkUri.buildUpon().scheme("https").build();

        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.ARGUMENT_BUNDLE_URL, postLinkUri.toString());

        loadFragment(WebViewFragment.FRAGMENT_TAG, bundle);
    }

    @Component(modules = {CakeApplicationModule.class, StatusReporterModule.class})
    @Singleton
    public interface CakeApplicationComponent {
        CakeApplication getContext();
    }

    public void onEmailLinkClick(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.steven_berdak_email));

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Timber.w("Package manager was null");
        }
    }

    public void onFreepikLinkClick(View view) {
        Uri uri = Uri.parse(getString(R.string.licensing_content_freepik_link));
        Intent freepikLinkIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (freepikLinkIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(freepikLinkIntent);
        } else {
            Timber.w("Package manager was null");
        }
    }

    public SlicesActivityModule getSlicesActivityModule() {
        return mSlicesActivityModule;
    }
}
