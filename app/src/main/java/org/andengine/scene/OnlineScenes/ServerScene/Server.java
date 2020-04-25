package org.andengine.scene.OnlineScenes.ServerScene;

import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.Creator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.GameActions;
import org.andengine.scene.OnlineScenes.ServerScene.Users.UserActions;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Server {
    //variables
    private Socket socket;
    private final String onlineServerUrl = "https://lumegameserver.herokuapp.com/";
    private final String localServerUrl = "localhost:8080";
    public String id;
    //actions
    private UserActions userActions;
    private GameActions gameActions;
    //String list of events
    protected final String connect = Socket.EVENT_CONNECT;
    protected final String disconnect = Socket.EVENT_CONNECT;
    protected final String localSocketID = "socketID";
    protected final String playerMoved = "PlayerMoved";
    protected final String loadBall = "AddBall";
    protected final String loadCoin = "AddCoin";
    protected final String getAllUsers = "GetAllUsers";
    protected final String newUserConnected = "newPlayer";
    protected final String userDisconnected = "playerDisconnected";
    protected final String request = "request";
    //constructor
    public Server(GameActions gameActions, UserActions userActions) {
        //initialize variables
        this.userActions = userActions;
        this.gameActions = gameActions;
        //create Server
        connectWithServer();
    }
    //methods
    private void connectWithServer() {
        try {
            socket = IO.socket(localServerUrl);
            setUpEvents();
            socket.connect();
        } catch (URISyntaxException e) {e.printStackTrace();/*cannot connect to server!?*/}
    }
    private void setUpEvents() {
        //connect to server
        socket.on(connect, args -> {

        }).on(localSocketID, args -> {
            //will called after connection and gives the socketID
            JSONObject data = (JSONObject) args[0];
            try {id = data.getString("id"); userActions.socketID(id);}
            catch (JSONException e) {e.printStackTrace();}
        }).on(disconnect, args -> {
            userActions.disconnect();
        }).on(getAllUsers, args ->{
            userActions.allUsers(null); //TODO
        }).on(newUserConnected, args ->{
            userActions.newUser(null);
        }).on(request, args ->{
            JSONObject object = (JSONObject) args[0];
            try {
                userActions.getRequest(object.getString("id"), object.getString("name"));
            } catch (JSONException e) {e.printStackTrace();}
        }).on(playerMoved, args ->{
            gameActions.playerMoved(null, null); //TODO
        }).on(loadBall, args ->{
            gameActions.loadBall(null);//TODO
        }).on(loadCoin, args ->{
            gameActions.loadCoin(null); //TODO
        });
    }
    public void requestGame(String playerID){
        if(!socket.connected()) throw new RuntimeException("method requestGame is called in buildTime.. it needs to be called event based! Or Client cannot connect to server");
        JSONObject o = new JSONObject();
        try {
            o.put("sendToID", playerID);
            o.put("fromID", id);
        } catch (JSONException e) {e.printStackTrace();}
        socket.emit(request, o);
    }
    public void emitBall(Creator creator){socket.emit(loadBall, creator.getJSON());}
    public void emitCoin(Creator creator){socket.emit(loadCoin, creator.getJSON());}
    public void emitMove(Creator creator){socket.emit(playerMoved, creator.getJSON());}
    //getter
    public String getOnlineServerUrl() {return onlineServerUrl;}
    public UserActions getUserActions() {return userActions;}
    public GameActions getGameActions() {return gameActions;}
    public Socket getSocket() {return socket;}
}
