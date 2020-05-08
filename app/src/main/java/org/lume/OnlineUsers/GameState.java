package org.lume.OnlineUsers;

public class GameState {
    //variables
    private int coins;
    private World world;
    private String name;
    //methods
    public static GameState getGameStateFromID(String ID){
        return null;
    }
    //constructors
    private GameState(){}

    public GameState(int coins, World world, String name) {
        this.coins = coins;
        this.world = world;
        this.name = name;
    }
    //getters and setters
    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
