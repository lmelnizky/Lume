package org.andengine.OnlineUsers;

public enum World {
    WORLD0(0),
    WORLD1(1),
    WORLD2(2),
    WORLD3(3),
    WORLD4(4),
    WORLD5(5),
    WORLD6(6),
    WORLD7(7),
    WORLD8(8);

    public final int fId;

    World(int id) {
        this.fId = id;
    }

    public static World getWorld(int id) {
        for (World world : values()) {
            if (world.fId == id) {
                return world;
            }
        }
        throw new IllegalArgumentException("Invalid World id: " + id);
    }
}
