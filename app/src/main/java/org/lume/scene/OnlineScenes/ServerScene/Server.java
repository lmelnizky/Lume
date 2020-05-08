package org.lume.scene.OnlineScenes.ServerScene;

import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.BallCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CannonCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.CoinCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.Creator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.LoseLifeCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.Creator.MoveCreator;
import org.lume.scene.OnlineScenes.ServerScene.Game.GameActions;
import org.lume.scene.OnlineScenes.ServerScene.Users.UserActions;
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
    protected final String loadCannon = "AddCannon";
    protected final String loseLife = "loseLife";
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
            socket = IO.socket(onlineServerUrl);
            setUpEvents();
            socket.connect();
        } catch (URISyntaxException e) {e.printStackTrace();/*cannot connect to server!?*/}
    }
    private void setUpEvents() {
        //connect to server
        socket.on(connect, args -> {

        }).on(getIdFromServer, args -> {
            id = ServerDataFactory.getIdFromData(args);
            userActions.socketID(id);
            socket.emit(createPlayer, ServerDataFactory.getCreatePlayerData(userName, id));
        }).on(disconnect, args -> {
            userActions.disconnect();
        }).on(getAllUsers, args ->{
            userActions.allUsers(ServerDataFactory.getPLayersFromData(args));
        }).on(newUserConnected, args ->{
            userActions.newUser(ServerDataFactory.getPlayerFromData(args));
        }).on(request, args ->{
            userActions.getRequest(ServerDataFactory.getRequestFromData(args)[0], ServerDataFactory.getRequestFromData(args)[1]);
        }).on(answerRequest, args ->{
            userActions.getAnswerRequest((Boolean) ServerDataFactory.getAnswerFromRequestData(args)[0], (String) ServerDataFactory.getAnswerFromRequestData(args)[1],(String) ServerDataFactory.getAnswerFromRequestData(args)[2]);
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
        }).on(loadCannon, args ->{
            gameActions.loadCanon(CannonCreator.getCreatorFromJSON((JSONObject) args[0]));
        }).on(loseLife, args ->{
            gameActions.lostLife(LoseLifeCreator.getPlayerIdFromJSON((JSONObject) args[0]));
        }).on(userDisconnected, args ->{
            gameActions.opponentDisconnected();//TODO
            userActions.userDisconnected(ServerDataFactory.getIdFromData(args));
        });
    }
    public void sendRequest(String toPlayerID){
        if(!socket.connected()) throw new RuntimeException("method requestGame is called in buildTime.. it needs to be called event based! Or Client cannot connect to server");
        socket.emit(request, ServerDataFactory.getRequestData(toPlayerID, id));
    }
    public void sendAnswer(boolean angenommen, String toID, String room){
        socket.emit(answerRequest, ServerDataFactory.getAnswerRequestData(angenommen, toID, id, room));
    }
    public void createGameRoom(String[] IDs, String room){
        socket.emit(createGameRoom, ServerDataFactory.getCreateGameData(IDs, room));
    }
    //these are methods, which can called by the referee
    public void emit(Creator creator){
        if(creator instanceof CoinCreator)  socket.emit(loadCoin, creator.getJSON());
        if(creator instanceof MoveCreator)  socket.emit(playerMoved, creator.getJSON());
        if(creator instanceof BallCreator)  socket.emit(loadBall, creator.getJSON());
        if(creator instanceof CannonCreator)socket.emit(loadCannon,creator.getJSON());
        if(creator instanceof LoseLifeCreator)socket.emit(loseLife,creator.getJSON());
    }
    public void loseLifeEmit(String ID){
        try {socket.emit(loseLife, new JSONObject("{\"ID\":" + "\"" +  ID + "\"" + "}"));}
        catch (JSONException e){e.printStackTrace();}
    }
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
