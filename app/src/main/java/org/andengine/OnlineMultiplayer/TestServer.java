package org.andengine.OnlineMultiplayer;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TestServer {
    private Socket socket;
    private final String serverUrl = "http://localhost:8080";

    public TestServer() {
        connectWithServer();
        events();
    }

    private void connectWithServer() {
        try {
            System.out.println("TestServer" + "   " + "start Connection with the server");
            socket = IO.socket(serverUrl);
            System.out.println("TestServer" + "   " + "socket is instance");
            socket.connect();
            System.out.println("TestServer" + "   " + "connection done");
        } catch (URISyntaxException e) {
            System.out.println(e.toString());
            e.printStackTrace();//cannot connect to server!?
        }
    }

    private void events() {
        socket.on(Socket.EVENT_CONNECT, args -> System.out.println("SocketIO Connected")).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    System.out.println("id = " + id);
                } catch (JSONException e) {
                    System.out.println("error");
                    e.printStackTrace();
                }
            }
        });
    }
}
