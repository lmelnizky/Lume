package org.andengine.scene.OnlineScenes.ServerScene;

import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.andengine.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
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
    public String userName;
    //actions
    private UserActions userActions;
    private GameActions gameActions;
    //String list of events
    protected final String connect = Socket.EVENT_CONNECT;
    protected final String disconnect = Socket.EVENT_CONNECT;
    protected final String getIdFromServer = "socketID";
    protected final String getAllUsers = "GetAllUsers";
    protected final String newUserConnected = "newPlayer";
    protected final String userDisconnected = "playerDisconnected";
    protected final String request = "request";
    protected final String createPlayer = "createPlayer";
    protected final String answerRequest = "answerRequest";
    protected final String createGameRoom = "createGameRoom";
    protected final String joinRoom = "joinRoom";

    protected final String playerMoved = "PlayerMoved";
    protected final String loadBall = "AddBall";
    protected final String loadCoin = "AddCoin";
    //constructor
    public Server(GameActions gameActions, UserActions userActions, String username) {
        //initialize variables
        this.userActions = userActions;
        this.gameActions = gameActions;
        this.userName = username;
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

        }).on(getIdFromServer, args -> {
            id = ServerDataFactory.getIdFromData(args);
            socket.emit(createPlayer, ServerDataFactory.getCreatePlayerData(userName, id));
        }).on(disconnect, args -> {
            userActions.disconnect();
        }).on(getAllUsers, args ->{
            userActions.allUsers(ServerDataFactory.getPLayersFromData(args));
        }).on(newUserConnected, args ->{
            userActions.newUser(ServerDataFactory.getPlayerFromData(args));
        }).on(request, args ->{
            userActions.getRequest(ServerDataFactory.getRequestFromData(args));
        }).on(answerRequest, args ->{
            userActions.answerRequest((Boolean) ServerDataFactory.getAnswerFromRequestData()[0], (String) ServerDataFactory.getAnswerFromRequestData()[1]);
        }).on(createGameRoom, args ->{
            gameActions.createdGame((String[])ServerDataFactory.getStartGameFromData(args)[0],(String) ServerDataFactory.getStartGameFromData(args)[1]);
        }).on(joinRoom, args ->{
            socket.emit(joinRoom, args[0]);
            gameActions.startGame();
        }).on(playerMoved, args ->{
            gameActions.playerMoved(MoveCreator.getCreatorFromJson((JSONObject) args[0]));
        }).on(loadBall, args ->{
            gameActions.loadBall(BallCreator.getCreatorFromJson((JSONObject) args[0]));
        }).on(loadCoin, args ->{
            gameActions.loadCoin(CoinCreator.getCreatorFromJson((JSONObject) args[0]));
        }).on(userDisconnected, args ->{
            gameActions.opponentDisconnected();//TODO
        });
    }
    public void sendRequest(String toPlayerID){
        if(!socket.connected()) throw new RuntimeException("method requestGame is called in buildTime.. it needs to be called event based! Or Client cannot connect to server");
        socket.emit(request, ServerDataFactory.getRequestData(toPlayerID, id));
    }
    public void sendAnswer(boolean angenommen, String toID){
        socket.emit(answerRequest, ServerDataFactory.getAnswerRequestData(angenommen, toID, id));
    }
    public void createGameRoom(String[] IDs, String room){ //TODO for generating a room on the server, you have to create your own link.. so you have to create your own random link
        socket.emit(createGameRoom, ServerDataFactory.getCreateGameData(IDs, room));
    }
    //these are methods, which can called by the referee
    public void emitBall(BallCreator creator){socket.emit(loadBall, creator.getJSON());}
    public void emitCoin(CoinCreator creator){socket.emit(loadCoin, creator.getJSON());}
    //this method is called when a player moved
    public void emitMove(MoveCreator creator){socket.emit(playerMoved, creator.getJSON());}
    //createPlayer and save the username on the server
    public void createPlayer(String username){
        try {socket.emit(createPlayer, new JSONObject("{\"name\":" + "\"" +  username + "\"" + "}"));}
        catch (JSONException e) {e.printStackTrace();}
    }
    //getter
    public String getOnlineServerUrl() {return onlineServerUrl;}
    public UserActions getUserActions() {return userActions;}
    public GameActions getGameActions() {return gameActions;}
    public Socket getSocket() {return socket;}
}
