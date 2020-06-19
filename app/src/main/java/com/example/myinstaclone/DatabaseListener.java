package com.example.myinstaclone;

import java.util.List;

public interface DatabaseListener {

    static enum DataType {USERS, FEEDS}

    void onQueryUsersResult(List<UserModel> result);

    void onQueryFeedsResult(List<FeedModel> result);
}
