package com.sbcode.cake.data.platforms.interfaces;

import android.support.annotation.NonNull;

import java.util.List;

public interface RequestHandler<PostsInputType, UsersInputType,
        ResponseCallback extends ResponseReceived> {

    void handlePostsRequest(@NonNull PostsInputType input,
                            @NonNull ResponseCallback responseCallback);

    void handleUserRequest(@NonNull UsersInputType input,
                           @NonNull ResponseCallback responseCallback);

    void handleUsersMultiRequest(@NonNull List<UsersInputType> inputList,
                                 @NonNull ResponseCallback responseCallback);
}
