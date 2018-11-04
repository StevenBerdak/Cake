package com.sbcode.cake.data.platforms.interfaces;

public interface PostsRequestMapper extends RequestMapper {

    void postsBefore(long value);

    void postsAfter(long value);

    void setLimit(int limit);

    long getBefore();

    long getAfter();

    int getLimit();
}
