package org.andengine.OnlineUsers;

import java.util.LinkedList;

public interface UsernameLoaderManager {
    void startLoadingNames();
    void finishLoadingNames(LinkedList<String> userNames);
}
