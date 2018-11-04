package com.sbcode.cake.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.ui.reporting.StatusReporter;
import com.sbcode.cake.ui.slices.CakeActivity;

import java.util.HashSet;
import java.util.Iterator;

import javax.inject.Inject;

/**
 * A Fragment wrapper for the new posts dialog that extends from DialogFragment.
 */
public class PostDialogFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = "post_dialog_fragment";

    @Inject
    CakePlatformManager mPlatformManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        CakeApplication.getInstance().component().inject(this);

        return buildSelectPlatformDialog((CakeActivity) getActivity());
    }

    private Dialog buildSelectPlatformDialog(CakeActivity activity) {
        HashSet<Platform> platformsSignedIn = mPlatformManager.getPlatformsSignedIn();

        /* If not signed in to any platforms, report to user and exit */
        if (platformsSignedIn.size() == 0) {
            CakeApplication.getInstance().component().getStatusReporter()
                    .enqueueReportDispatchImmediate(
                            new StatusReporter.StatusReport(StatusReporter.ReportType.PLATFORM,
                                    PlatformType.NULL, getString(R.string.status_reporter_not_signed_in)
                            ));
            this.dismiss();
        }

        /* Iterate over platforms signed in and ask the user to select a platform */
        Iterator<Platform> platformIterator = platformsSignedIn.iterator();
        CharSequence[] charSequences = new CharSequence[platformsSignedIn.size()];

        for (int i = 0; i < platformsSignedIn.size(); ++i) {
            charSequences[i] =
                    convertAllCapsToCapWord(platformIterator
                            .next()
                            .getPlatformType()
                            .name()
                    );
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.text_select_platform);
        builder.setItems(charSequences, (dialogInterface, i) -> {
                    Dialog selectActionDialog = buildSelectActionDialog(activity,
                            PlatformType.valueOf(
                                    convertCapWordToAllCaps(charSequences[i]).toString())
                    );
                    selectActionDialog.show();
                }
        );

        builder.setNegativeButton(getString(R.string.text_cancel), null);

        return builder.create();
    }

    private Dialog buildSelectActionDialog(CakeActivity activity, PlatformType platformType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.text_select_action);

        /* Get the available actions for the platform */

        CharSequence[] actions = getActions(platformType);

        /* Get the list of formatted actions with the word capitalized */
        String platformVerb = CakeApplication.getInstance().getString(
                mPlatformManager.get(platformType).getPlatformVerbResourceId()
        );
        CharSequence[] actionsCapWord = new CharSequence[actions.length];
        for (int i = 0; i < actions.length; ++i) {
            actionsCapWord[i] = platformVerb + convertAllCapsToCapWord(actions[i].toString());
        }

        builder.setItems(actionsCapWord, (dialogInterface, i) ->
                performAction(activity, platformType, PostAction.valueOf(actions[i].toString())));

        return builder.create();
    }

    private CharSequence[] getActions(PlatformType platformType) {
        switch (platformType) {
            case FACEBOOK:
                return new CharSequence[]{PostAction.MESSAGE.name(), PostAction.IMAGE.name()};
            case TWITTER:
                return new CharSequence[]{PostAction.MESSAGE.name(), PostAction.IMAGE.name()};
            default:
                return new CharSequence[0];
        }
    }

    private void performAction(CakeActivity activity, PlatformType platformType,
                               PostAction postAction) {
        switch (postAction) {
            case MESSAGE:
                mPlatformManager
                        .get(platformType)
                        .getNewPostHandler()
                        .handleNewPostMessage(activity);
                break;
            case IMAGE:
                activity.setActivityResultMetaPlatform(platformType);
                mPlatformManager
                        .get(platformType)
                        .getNewPostHandler()
                        .initChooseNewPostImage(activity);
                break;
        }
    }

    private CharSequence convertAllCapsToCapWord(CharSequence charSequence) {
        StringBuilder builder = new StringBuilder().append(charSequence.charAt(0));
        for (int i = 1; i < charSequence.length(); ++i) {
            builder.append(Character.toLowerCase(charSequence.charAt(i)));
        }

        return builder.toString();
    }

    private CharSequence convertCapWordToAllCaps(CharSequence charSequence) {
        StringBuilder builder = new StringBuilder().append(charSequence.charAt(0));
        for (int i = 1; i < charSequence.length(); ++i) {
            builder.append(Character.toUpperCase(charSequence.charAt(i)));
        }

        return builder.toString();
    }

    private enum PostAction {
        MESSAGE, IMAGE
    }
}
