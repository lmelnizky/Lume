/*
Diese Klasse stellt deine User dar. Möchtest du neue User hinzufügen, dann musst du die static Methode createUser aufrufen.
Hast du einen User in deiner Firebase schon erstellt kannst du diesen Über den public Konstruktor aufrufen!

Bitte schreib in dieser Klasse noch Methoden für das speichern deines Users in Sharedprefs.

Du musst bitte unbeding die ID des Users in den Sharedprefs. speichern! Ansonsten kannst du nicht mehr auf den User zugreifen!


*/
package org.andengine.OnlineUsers;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.andengine.OnlineUsers.DataBaseManager;
import org.andengine.OnlineUsers.GameState;

import java.util.Random;

public class User {
    //variables
    private GameState gameState;
    private int iD;
    private DatabaseReference iDRef;
    //constructor

    public User(int iD) {
        this.iD = iD;
        setUpUserFromDatabase();
    }
    private User(){}

    //static variables need for setUpUserFromDataBase
    private static int coin;
    private static String name;
    private static World world;
    //methods
    private void setUpUserFromDatabase(){

        iDRef = DataBaseManager.getInstance().getUserPath().child("" + iD);
        iDRef.child("coin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coin = (int) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("User","cant red data:" + databaseError);
            }
        });
        iDRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("User","cant red data:" + databaseError);
            }
        });
        iDRef.child("world").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int worldint;
                worldint = (int) dataSnapshot.getValue();
                switch (worldint){
                    case 1:
                        world = World.WORLD1;
                        break;
                    case 2:
                        world = World.WORLD2;
                        break;
                    case 3:
                        world = World.WORLD3;
                        break;
                    case 4:
                        world = World.WORLD4;
                        break;
                    case 5:
                        world = World.WORLD5;
                        break;
                    case 6:
                        world = World.WORLD6;
                        break;
                    case 7:
                        world = World.WORLD7;
                        break;
                    case 8:
                        world = World.WORLD8;
                        break;
                    default:
                        world = World.WORLD1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("User","cant red data:" + databaseError);
            }
        });
    }
    public static User createUser(GameState gameState){
        int iD = newUserInDatabase(gameState);
        return new User(iD);
    }
    private static int newUserInDatabase(GameState gS){
        int iD = createNewId();
        DatabaseReference dF = DataBaseManager.getInstance().getUserPath().child("" + iD).push();
        dF.child("coin").push().setValue(gS.getCoins());
        dF.child("name").push().setValue(gS.getName());
        int value;
        switch (gS.getWorld()){
            case WORLD1:
                value = 1;
                break;
            case WORLD2:
                value = 2;
                break;
            case WORLD3:
                value = 3;
                break;
            case WORLD4:
                value = 4;
                break;
            case WORLD5:
                value = 5;
                break;
            case WORLD6:
                value = 6;
                break;
            case WORLD7:
                value = 7;
                break;
            case WORLD8:
                value = 8;
                break;
            default:
                value = 1;

        }
        dF.child("world").push().setValue(value);
        return iD;
    }

    private static int createNewId() {
        Random random = new Random();
        final int[] returnValue = {random.nextInt(10000)};
        DataBaseManager.getInstance().getUserPath().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("createNewId")) returnValue[0] = createNewId();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        return returnValue[0];
    }
    //getters and setters

    public GameState getGameState() {
        return gameState;
    }

    public int getiD() {
        return iD;
    }

    public DatabaseReference getiDRef() {
        return iDRef;
    }
}
