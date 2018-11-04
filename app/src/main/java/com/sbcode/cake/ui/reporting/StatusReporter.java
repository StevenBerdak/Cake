package com.sbcode.cake.ui.reporting;

import android.support.annotation.NonNull;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.logic.callbacks.CallbackManager;

import java.util.ArrayDeque;

import javax.inject.Inject;

/**
 * Queues and reports statuses as requested.
 */
public class StatusReporter {

    private final ArrayDeque<StatusReport> mReportsQueue;
    private final CallbackManager<ReportsCallback> mCallbackManager;

    @Inject
    public StatusReporter() {
        mReportsQueue = new ArrayDeque<>();
        mCallbackManager = new CallbackManager<>();
    }

    public void enqueueReport(@NonNull StatusReport report) {
        for (StatusReport reportFromList : mReportsQueue) {
            if (report.reportType == reportFromList.reportType &&
                    report.platformType == reportFromList.platformType)
                return;
        }

        mReportsQueue.addLast(report);
    }

    public void enqueueReportDispatchImmediate(@NonNull StatusReport report) {
        for (StatusReport reportFromList : mReportsQueue) {
            if (report.reportType == reportFromList.reportType &&
                    report.platformType == reportFromList.platformType)
                return;
        }

        mReportsQueue.addFirst(report);
        checkReports();
    }

    public CallbackManager<ReportsCallback> getCallbackManager() {
        return mCallbackManager;
    }

    public StatusReport pullReport() {
        return mReportsQueue.pollFirst();
    }

    public void checkReports() {
        if (mReportsQueue.size() > 0) {
            for (ReportsCallback callback : mCallbackManager.getCallbacks()) {
                callback.onReportReady(this);
            }
        }
    }

    public boolean hasReportReady() {
        return !mReportsQueue.isEmpty();
    }

    public boolean hasNext() {
        return mReportsQueue.size() > 0;
    }

    public static class StatusReport {

        final ReportType reportType;
        final PlatformType platformType;
        final String message;

        public StatusReport(@NonNull final ReportType reportType,
                            @NonNull final PlatformType platformType,
                            @NonNull final String message) {
            this.reportType = reportType;
            this.platformType = platformType;
            this.message = message;
        }

        public ReportType getReportType() {
            return reportType;
        }

        public PlatformType getPlatformType() {
            return platformType;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum ReportType {
        IO, DATA, ACCOUNT, PLATFORM, REPOSITORY
    }

    public interface ReportsCallback extends CallbackManager.Callback {
        void onReportReady(StatusReporter statusReporter);
    }
}
