package com.sbcode.cake.data.platforms.interfaces;

public interface ApiRequestHandler<PostsInputType, UserInputType,
        ResponseCallback extends ResponseReceived>
        extends RequestHandler<PostsInputType, UserInputType, ResponseCallback> {
}
