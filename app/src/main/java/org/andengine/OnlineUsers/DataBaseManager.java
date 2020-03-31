package org.andengine.OnlineUsers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataBaseManager {
    private static DataBaseManager REFERENCE = new DataBaseManager();
    private DatabaseReference basePath = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userPath = FirebaseDatabase.getInstance().getReference().child("Users");

    private DataBaseManager(){

    }
    public static DataBaseManager getInstance(){
        return REFERENCE;
    }

    public DatabaseReference getBasePath() {
        return basePath;
    }

    public DatabaseReference getUserPath() {
        return userPath;
    }
}
