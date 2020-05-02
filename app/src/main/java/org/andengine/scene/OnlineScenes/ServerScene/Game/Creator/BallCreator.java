package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.manager.ResourcesManager;
import org.andengine.object.Ball;
import org.andengine.object.Circle;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.scene.OnlineScenes.ServerScene.Game.MultiplayerGameScene;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BallCreator extends Creator {
    //variables
    //constants
    private final float sideLength = ResourcesManager.getInstance().sideLength;
    //fields for JSON
    private boolean thorny;
    private float speed;
    private short direction;
    private short position;
    private int xVel, yVel; // you never use these. you create block scoped floats in your method
    //base stuff
    private BoundCamera camera;
    private MultiplayerGameScene gameScene;
    private Ball ball;
    private Body body;
    //constructor
    public BallCreator(String room, boolean thorny, short direction, short position) {
        super(room);
        this.thorny = thorny;
        this.direction = direction;
        this.position = position;
    }

    @Override
    public Sprite createSprite() {
        //ich versteh die methode nicht.. ist aber auch egal
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        camera = ResourcesManager.getInstance().camera;
        gameScene = MultiplayerGameScene.getInstance();
        Circle stoneCircle;
        ArrayList<Sprite> stones = gameScene.stones;
        ArrayList<Sprite> crackyStones = gameScene.crackyStones;

        final Sprite stone;
        ITextureRegion textureRegion = (thorny) ? ResourcesManager.getInstance().thorny_stone_region :
                ResourcesManager.getInstance().cracky_stone_region;
        Ball ball;
        PhysicsWorld physicsWorld = gameScene.physicsWorld;

        speed = (direction%2 == 0) ? ResourcesManager.getInstance().screenRatio : 1f;

        switch (direction) {
            case 1:
                x = ResourcesManager.getInstance().camera.getCenterX() - sideLength + sideLength*position;
                y = (float) (camera.getHeight() + sideLength/2);
                yVel = -speed;
                break;
            case 2:
                x = (float) (camera.getWidth() + sideLength/2);
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -speed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = -sideLength/2;
                yVel = speed;
                break;
            case 4:
                x = -sideLength/2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = speed;
                break;
        }

        //stoneCircle = new Circle(x, y, sideLength*3/8);
        stone = new Sprite(x, y, sideLength*3/4, sideLength*3/4,
                textureRegion, ResourcesManager.getInstance().vbom) {
//            @Override
//            protected void onManagedUpdate(float pSecondsElapsed) {
//                //TODO check collisions
//            }
        };
        body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
        if (thorny) {
        } else {
            crackyStones.add(stone);
        }
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, null, body, stone, thorny, false, speed);
        stone.setUserData(ball);
        stones.add(stone);
        return stone;
    }


    public int getxVel() {return this.xVel;}
    public int getyVel() {return this.yVel;}
    public float getSpeed() {return speed;}
    /*
    private boolean thorny;
    private float speed;
    private int direction;
    private int position;
    private int xVel, yVel;
    */
    @Override
    public JSONObject getJSON() {
        JSONObject returnValue = new JSONObject();
        try {
            returnValue.put("room", room);
            returnValue.put("thorny", thorny);
            //returnValue.put("speed",speed);
            returnValue.put("direction",direction);
            returnValue.put("position",position);
        }catch(JSONException e){ e.printStackTrace();}
        return returnValue; //TODO Martin Melnizky
    }
    public static BallCreator getCreatorFromJson(JSONObject o){
        try{return new BallCreator(o.getString("room"),  o.getBoolean("thorny"),(short) o.getInt("direction"),(short) o.getInt("position"));} catch(JSONException e){e.printStackTrace();}
        return null;
    }
}
