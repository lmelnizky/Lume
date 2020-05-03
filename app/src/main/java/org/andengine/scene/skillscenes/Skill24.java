package org.andengine.scene.skillscenes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.base.SkillScene;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.TickerText;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.object.Ball;
import org.andengine.object.Circle;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import java.util.Date;

public class Skill24 extends SkillScene {
    public Skill24() {
        super(8);
    }

    @Override
    public void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(),
                camera.getWidth(), camera.getHeight(), resourcesManager.background_world0_region, vbom));
        this.setBackground(spriteBackground);
    }

    @Override
    public void createHalves() {

        shootLeft = new Rectangle(camera.getCenterX() - resourcesManager.screenWidth / 4, camera.getCenterY(),
                resourcesManager.screenWidth / 2, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    shootX1 = x;
                    shootY1 = y;
                    return true;
                } else if (touchEvent.isActionUp()) {
                    shootX2 = x;
                    shootY2 = y;
                    float deltaX = shootX2 - shootX1;
                    float deltaY = shootY2 - shootY1;
                    //checks if it was a swipe
//                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
//                        //checks if it was horizontal or vertical
//                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal swipe
//                            if (deltaX > 0) { //left to right
//                                createCannonball(4);
//                            } else { //right to left
//                                createCannonball(2);
//                            }
//                        } else { //vertical swipe
//                            if (deltaY > 0) { //up to down
//                                createCannonball(3);
//                            } else { //down to up
//                                createCannonball(1);
//                            }
//                        }
//                    }
                    if (deltaX > SWIPE_MIN_DISTANCE || deltaY > SWIPE_MIN_DISTANCE
                            || deltaX < -SWIPE_MIN_DISTANCE || deltaY < -SWIPE_MIN_DISTANCE) {
                        createCannonball(deltaX, deltaY);
                    }


                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        swipeRight = new Rectangle(camera.getCenterX() + resourcesManager.screenWidth / 4, camera.getCenterY(),
                resourcesManager.screenWidth / 2, resourcesManager.screenHeight, vbom) {
            public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
                if (touchEvent.isActionDown()) {
                    swipeX1 = x;
                    swipeY1 = y;
                    return true;
                } else if (touchEvent.isActionUp()) {
                    swipeX2 = x;
                    swipeY2 = y;
                    float deltaX = swipeX2 - swipeX1;
                    float deltaY = swipeY2 - swipeY1;
                    //checks if it was a swipe and not a tap
                    if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE || Math.abs(deltaY) > SWIPE_MIN_DISTANCE) {
                        //horizontal or vertical check
                        if (Math.abs(deltaX) > Math.abs(deltaY)) { //horizontal
                            if (deltaX > 0) { //left to right
                                movePlayer('R');
                            } else { //right to left
                                movePlayer('L');
                            }
                        } else { //vertical
                            if (deltaY > 0) { //up to down
                                movePlayer('U');
                            } else { //down to up
                                movePlayer('D');
                            }
                        }
                    } else { //TAP - show slowMotion
//                        if (cameFromLevelsScene) {
//                            slowMotion = !slowMotion;
//                            setSlowMotionMode(); //sets values of current moving stones
//                            snailSign.setVisible(slowMotion);
//                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }

            ;
        };

        shootLeft.setColor(Color.GREEN);
        swipeRight.setColor(Color.RED);
        shootLeft.setAlpha(0.2f);
        swipeRight.setAlpha(0.2f);
        this.registerTouchArea(shootLeft);
        this.registerTouchArea(swipeRight);
        secondLayer.attachChild(shootLeft);
        secondLayer.attachChild(swipeRight);
    }

    private void createCannonball(float deltaX, float deltaY) {
        ITextureRegion textureRegion = resourcesManager.cannonball_region;
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        FIXTURE_DEF.filter.categoryBits = CATEGORY_CANNONBALL;
        FIXTURE_DEF.filter.maskBits = MASK_CANNONBALL;
        final Sprite cannonball;
        Ball ball;
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float speed = sideLength/5;

        float factor, vectorLength;
        float dirVectorX, dirVectorY;

        vectorLength = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        factor = (float) 0.6f*sideLength/vectorLength;
        dirVectorX = deltaX*factor;
        dirVectorY = deltaY*factor;

        x = lumeSprite.getX();
        y = lumeSprite.getY();
        xVel = dirVectorX;
        yVel = dirVectorY;

        cannonball = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom) {
            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                for (Sprite crackyStone : crackyStones) {
                    final Circle cannonCircle, stoneCircle;
                    cannonCircle = new Circle(this.getX(), this.getY(), this.getWidth() / 2);
                    stoneCircle = new Circle(crackyStone.getX(), crackyStone.getY(), crackyStone.getWidth() / 2);

                    if (cannonCircle.collision(stoneCircle)) {
                        addToScore(1);
                        crackyStonesToRemove.add(crackyStone);
                        stonesToRemove.add(crackyStone);
                        cannonBallsToRemove.add(this);
                        final Sprite cannonBalltoRemove = this;
                        engine.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                if (cannonBallsToRemove.size()+crackyStonesToRemove.size() > 0) {
                                    if (cannonBallsToRemove.size() > 0) {
                                        final PhysicsConnector physicsConnector =
                                                physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(cannonBalltoRemove);
                                        if (physicsConnector != null) {
                                            physicsWorld.unregisterPhysicsConnector(physicsConnector);
                                            Ball ball = (Ball) cannonBalltoRemove.getUserData();
                                            Body body = ball.getBody();
                                            body.setActive(false);
                                            physicsWorld.destroyBody(body);
                                            cannonBalltoRemove.detachSelf();
                                            cannonBalltoRemove.dispose();
                                        }
                                    }
                                    cannonBallsToRemove.clear();
                                    for (Sprite sprite : crackyStonesToRemove) {
                                        final PhysicsConnector physicsConnector =
                                                physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(sprite);
                                        if (physicsConnector != null) {
                                            physicsWorld.unregisterPhysicsConnector(physicsConnector);
                                            Ball ball = (Ball) sprite.getUserData();
                                            Body body = ball.getBody();
                                            body.setActive(false);
                                            physicsWorld.destroyBody(body);
                                            sprite.detachSelf();
                                            sprite.dispose();
                                        }
                                    }
                                    stonesToRemove.clear();
                                    crackyStonesToRemove.clear();
                                }
                            }
                        });
                    }
                }
                crackyStones.removeAll(crackyStonesToRemove);
                stones.removeAll(stonesToRemove);
//                crackyStonesToRemove.clear();

                if (this.getX() < -sideLength || this.getY() < - sideLength ||
                        this.getX() > camera.getWidth()+sideLength || this.getY() > camera.getWidth()+sideLength) {
                    this.detachSelf();
                    this.dispose();
                }
            }
        };
        secondLayer.attachChild(cannonball);
        final Body body = PhysicsFactory.createCircleBody(physicsWorld, cannonball, BodyDef.BodyType.KinematicBody, FIXTURE_DEF);
        body.setLinearVelocity(xVel, yVel);
        ball = new Ball();
        ball.setBody(body);
        cannonball.setUserData(ball);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(cannonball, body, true, false));
    }

    public Sprite addBall(final boolean thorny, int direction, int gravityDirection, int position, float speedFactor) {
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        float x = 0;
        float y = 0;
        float xVel = 0;
        float yVel = 0;
        float xSpeed = camera.getWidth()/16/10 * speedFactor;
        float ySpeed = sideLength/10 * speedFactor;

        final Sprite stone;
        ITextureRegion textureRegion = (thorny) ? resourcesManager.thorny_stone_region : resourcesManager.cracky_stone_region;
        Ball ball;

        if (thorny) {
            FIXTURE_DEF.filter.categoryBits = CATEGORY_THORNY;
            FIXTURE_DEF.filter.maskBits = MASK_THORNY;
        } else { //cracky
            FIXTURE_DEF.filter.categoryBits = CATEGORY_CRACKY;
            FIXTURE_DEF.filter.maskBits = MASK_CRACKY;
        }

        switch (direction) {
            case 1:
                x = camera.getCenterX() - sideLength + sideLength*position;
                y = camera.getHeight() - sideLength / 2;
                xVel = (level == 4) ? 0 : (gravityDirection-3)*xSpeed; //2 v 4
                yVel = -ySpeed;
                break;
            case 2:
                x = camera.getWidth() - sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = -xSpeed;
                yVel = (gravityDirection-2)*ySpeed;
                break;
            case 3:
                x = camera.getCenterX()-sideLength + sideLength*position;
                y = sideLength / 2;
                xVel = (level == 4) ? 0 : (gravityDirection-3)*xSpeed; //2 v 4
                yVel = ySpeed;
                break;
            case 4:
                x = sideLength / 2;
                y = camera.getCenterY()-sideLength + sideLength*position;
                xVel = xSpeed;
                yVel = (gravityDirection-2)*ySpeed;
                break;
        }

        final Vector2 gravity = getGravity(gravityDirection);

        stone = new Sprite(x, y, sideLength * 3 / 4, sideLength * 3 / 4, textureRegion, vbom);
        Body body = PhysicsFactory.createCircleBody(physicsWorld, stone, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
        body.setLinearVelocity(xVel, yVel);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(stone, body, true, false));
        ball = new Ball(direction, gravity, body, stone, thorny, false, speedFactor);
        stone.setUserData(ball);
        stones.add(stone);

        secondLayer.attachChild(stone);
        //animate Cannons
        animateCannon(direction, position);

        if (!thorny) {
            crackyStones.add(stone);
        }

        return stone;
    }

    @Override
    public void addShootandMoveSign() {
        shootSign = new Sprite(camera.getCenterX() - 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.shoot_diagonal_sign_region, vbom);
        moveSign = new Sprite(camera.getCenterX() + 3*sideLength, camera.getHeight()-35, sideLength*7/8, sideLength*7/8, resourcesManager.move_normal_sign_region, vbom);
    }

    //moves the player in X or Y direction
    public void movePlayer(char direction) {
        switch (direction) {
            case 'R':
                if (xPosLume < 3) {
                    xPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX() + sideLength, lumeSprite.getY());
                }
                break;
            case 'L':
                if (xPosLume > 1) {
                    xPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX() - sideLength, lumeSprite.getY());
                }
                break;
            case 'D':
                if (yPosLume > 1) {
                    yPosLume--;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() - sideLength);
                }
                break;
            case 'U':
                if (yPosLume < 3) {
                    yPosLume++;
                    lumeSprite.setPosition(lumeSprite.getX(), lumeSprite.getY() + sideLength);
                }
                break;
        }
        coinCheck();
    }

    @Override
    public void showText() {
        String tvText0 = "Shoot as many balls as you can! You get one point for every destroyed ball, and lose one for every one you cannot catch.";
        if (kimmelnitzText == null) {
            //sideLength*6.6f
            kimmelnitzText = new TickerText(sideLength*6.6f, camera.getHeight() / 6, resourcesManager.smallFont, tvText0,
                    new TickerText.TickerTextOptions(AutoWrap.WORDS, sideLength*13.2f, HorizontalAlign.CENTER,50), resourcesManager.vbom);
            secondLayer.attachChild(kimmelnitzText);
            kimmelnitzText.setAlpha(0.7f);
            activity.createTypingText(tvText0, kimmelnitzText, false);
        }
    }

    @Override
    public void createStones() {
        int direction = randomGenerator.nextInt(2)*2 + 2; //is 2 or 4
        long interval = (long) 1200;
        long age = (new Date()).getTime() - stoneTime;
        if (firstStonesInLevel) interval = 1000;
        if (age >= interval) {
            //if (firstStonesInLevel) createCoin();
            firstStonesInLevel = false;
            this.showStonesToScreen(direction, false);
            stoneTime = new Date().getTime();
        }
    }

    private void showStonesToScreen(int directionVariant, final boolean thornyFirst) {
        int randomRow = randomGenerator.nextInt(3); //values 0 to 2
        float ratio = resourcesManager.screenRatio;
        float factor = (directionVariant%2 == 0) ? ratio : 1f;
        int gravityDirection = getGravityDirection(directionVariant);
        addBall(false, directionVariant, gravityDirection, randomRow, 1.5f*factor);
    }

}
