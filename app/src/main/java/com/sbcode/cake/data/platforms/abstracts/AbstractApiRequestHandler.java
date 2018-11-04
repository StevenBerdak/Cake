package com.sbcode.cake.data.platforms.abstracts;

import com.sbcode.cake.data.platforms.callbacks.ApiResponseReceived;
import com.sbcode.cake.data.platforms.interfaces.ApiRequestHandler;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.UsersRequestMapper;

public abstract class AbstractApiRequestHandler
        implements ApiRequestHandler<PostsRequestMapper, UsersRequestMapper, ApiResponseReceived> {
}
