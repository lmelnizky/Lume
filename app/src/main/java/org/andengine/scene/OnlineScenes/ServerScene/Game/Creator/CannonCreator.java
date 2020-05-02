package org.andengine.scene.OnlineScenes.ServerScene.Game.Creator;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.manager.ResourcesManager;
import org.andengine.object.Circle;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.scene.OnlineScenes.ServerScene.Game.MultiplayerGameScene;
import org.andengine.scene.OnlineScenes.ServerScene.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class CannonCreator extends Creator {
    //const
    private final float speed = 1f;
    private int direction;
    private float sideLength;
    private String playerId;

    private ResourcesManager resourcesManager = ResourcesManager.getInstance();
    private MultiplayerGameScene scene = MultiplayerGameScene.getInstance();
    private Sprite playerSprite;

    public CannonCreator(String room, int direction, String playerId) {
        super(room);
        this.direction = direction;
        this.playerId = playerId;
        this.sideLength = resourcesManager.sideLength;
    }

    @Override
    public Sprite createSprite() {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        for (Player p : scene.getMultiplayer().getPlayers()) if (p.getId().equals(playerId)) playerSprite = p.getSprite();
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        final Sprite cannonball;
//        Ball ball;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        switch (direction) {
            case 1: //up to down
                x = playerSprite.getX();
                y = playerSprite.getY() - playerSprite.getHeight();
                yVel = -speed;
                break;
            case 2: //right to left
                x = playerSprite.getX() - playerSprite.getWidth();
                y = playerSprite.getY();
                xVel = -speed;
                break;
            case 3: //down to up
                x = playerSprite.getX();
                y = playerSprite.getY() + playerSprite.getHeight();
                yVel = speed;
                break;
            case 4: //left to right
                x = playerSprite.getX() + playerSprite.getWidth();
                y = playerSprite.getY();
                xVel = speed;
                break;
        }

        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, resourcesManager.vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                for (Sprite crackyStone : scene.crackyStones) {
                    final Circle cannonCircle, stoneCircle;
                    cannonCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
                    stoneCircle = new Circle(crackyStone.getX(), crackyStone.getY(), crackyStone.getWidth() / 2);

                    if (cannonCircle.collision(stoneCircle)) {
                        scene.crackyStonesToRemove.add(crackyStone);
                        scene.stonesToRemove.add(crackyStone);
                        scene.cannonBallsToRemove.add(this);
                        final Sprite cannonBalltoRemove = this;

                        resourcesManager.engine.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                if (scene.cannonBallsToRemove.size()+scene.crackyStonesToRemove.size() > 0) {
                                    if (scene.cannonBallsToRemove.size() > 0) {
                                        cannonBalltoRemove.detachSelf();
                                        cannonBalltoRemove.dispose();
                                    }

                                    scene.cannonBallsToRemove.clear();
                                    for (Sprite sprite : scene.crackyStonesToRemove) {
                                        sprite.detachSelf();
                                        sprite.dispose();
                                    }
                                    scene.stonesToRemove.clear();
                                    scene.crackyStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                scene.stones.removeAll(scene.stonesToRemove);
                scene.crackyStones.removeAll(scene.crackyStonesToRemove);

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > resourcesManager.camera.getWidth()+sideLength || this.getY() > resourcesManager.camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        scene.secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(scene.physicsWorld, cannonball, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
//        ball = new Ball(cannonball, "cannonball");
//        body.setUserData(ball);
        body.setLinearVelocity(xVel, yVel);
        scene.physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
        return cannonball;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("direction", direction);
            jsonObject.put("room", room);
            jsonObject.put("playerId", playerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static CannonCreator getCreatorFromJSON(JSONObject jsonObject) {
        try {
            return new CannonCreator(jsonObject.getString("room"), jsonObject.getInt("direction"), jsonObject.getString("playerId"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
