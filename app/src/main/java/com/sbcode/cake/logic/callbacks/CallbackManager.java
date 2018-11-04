package com.sbcode.cake.logic.callbacks;

import java.util.ArrayList;

/**
 * Manages callbacks of a provided type that extends from the inner callback interface.
 * @param <CallbackType> A callback implementation to use with the current manager instance.
 */
public class CallbackManager<CallbackType extends CallbackManager.Callback> {

    private final ArrayList<CallbackType> mCallbacks;

    public CallbackManager() {
        mCallbacks = new ArrayList<>();
    }

    public void addCallback(CallbackType callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallback(CallbackType callback) {
        mCallbacks.remove(callback);
    }

    public ArrayList<CallbackType> getCallbacks() {
        return mCallbacks;
    }

    public interface Callback { }
}
