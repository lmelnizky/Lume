package org.andengine.object;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.entity.sprite.Sprite;

/**
 * Created by Lukas on 22.05.2017.
 */

public class Ball {
    private int direction;
    private Vector2 gravity;
    private Body body;
    private Sprite sprite;
    private boolean thorny;
    private boolean overHalf;
    private float speed;

    public Ball () {

    }

    public Ball(int direction, Vector2 gravity, Body body, Sprite sprite, boolean thorny, boolean overHalf, float speed) {
        this.direction = direction;
        this.sprite = sprite;
        this.thorny = thorny;
        this.gravity = gravity;
        this.body = body;
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Vector2 getGravity() {
        return gravity;
    }

    public void setGravity(Vector2 gravity) {
        this.gravity = gravity;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public boolean isOverHalf() {
        return overHalf;
    }

    public void setOverHalf(boolean overHalf) {
        this.overHalf = overHalf;
    }

    public boolean isThorny() {
        return thorny;
    }

    public void setThorny(boolean thorny) {
        this.thorny = thorny;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
