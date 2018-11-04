package com.sbcode.cake.data.platforms.interfaces;

public interface JsonRequestHandler<PostsInputType, UserInputType,
        ResponseCallback extends ResponseReceived>
        extends RequestHandler<PostsInputType, UserInputType, ResponseCallback> {
}
