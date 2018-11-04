package com.sbcode.cake.ui.slices;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.data.room.CakeDatabase;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.data.room.models.Update;

import java.util.List;

public class SlicesViewModel extends ViewModel {

    private CakeDatabase mCakeDatabase;
    private CakePlatformManager mCakePlatformManager;
    private LiveData<List<Slice>> mSlicesLiveData;

    public SlicesViewModel() {

    }

    public void setCakeDatabase(CakeDatabase database) {
        this.mCakeDatabase = database;
        mSlicesLiveData = database.sliceDao().queryAllForLiveData();
    }

    public void setPlatformManager(CakePlatformManager platformManager) {
        this.mCakePlatformManager = platformManager;
    }

    public CakePlatformManager getPlatformManager() {
        return mCakePlatformManager;
    }

    public LiveData<List<Slice>> getSlices() {
        return mSlicesLiveData;
    }

    public LiveData<List<Update>> getUpdates() {
        return mCakeDatabase.updatesDao().queryAllByConsumedForLiveData(false);
    }
}
