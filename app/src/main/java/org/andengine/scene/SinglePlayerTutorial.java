package org.andengine.scene;

import android.util.Log;

import org.andengine.base.BaseScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.manager.SceneType;
import org.andengine.util.adt.align.HorizontalAlign;

public class SinglePlayerTutorial extends BaseScene implements IOnMenuItemClickListener {
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------

    private MenuScene menuChildScene;

    private final int MENU_PLAY = 0;
    private final int MENU_MULTI = 1;
    private final int MENU_LEVELS = 2;

    private Text worldText;

    //CONSTRUCTOR
    public SinglePlayerTutorial() {
        updateWorldText();
    }


    //---------------------------------------------
    // METHODS FROM SUPERCLASS
    //---------------------------------------------

    @Override
    public void createScene() {
        Log.i("ANDENG", "MainMenuScene CreateScene");
        createBackground();
        createWorldText();
        createMenuChildScene();
    }

    @Override
    public void onBackKeyPressed() {
        System.exit(0);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_MENU;
    }


    @Override
    public void disposeScene() {
        // TODO Auto-generated method stub
    }

    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
        switch (pMenuItem.getID()) {
            case MENU_PLAY:
                //Load Game Scene!
                switch (activity.getCurrentWorld()) {
                    case 0:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(0, 0);
                        } else {
                            SceneManager.getInstance().loadWorld0Scene(engine, 0);
                        }
                        break;
                    case 1:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(1, 0);
                        } else {
                            SceneManager.getInstance().loadWorld1Scene(engine, 0);
                        }
                        break;
                    case 2:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(2, 0);
                        } else {
                            SceneManager.getInstance().loadWorld2Scene(engine, 0);
                        }
                        break;
                    case 3:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(3, 0);
                        } else {
                            SceneManager.getInstance().loadWorld3Scene(engine, 0);
                        }
                        break;
                    case 4:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(4, 0);
                        } else {
                            SceneManager.getInstance().loadWorld4Scene(engine, 0);
                        }
                        break;
                    case 5:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(5, 0);
                        } else {
                            SceneManager.getInstance().loadWorld5Scene(engine, 0);
                        }
                        break;
                    case 6:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(6, 0);
                        } else {
                            SceneManager.getInstance().loadWorld6Scene(engine, 0);
                        }
                        break;
                    case 7:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(7, 0);
                        } else {
                            SceneManager.getInstance().loadWorld7Scene(engine, 0);
                        }
                        break;
                    case 8:
                        if (ResourcesManager.getInstance().activity.isStartVideo()) {
                            ResourcesManager.getInstance().activity.showRewarded(8, 0);
                        } else {
                            SceneManager.getInstance().loadWorld8Scene(engine, 0);
                        }
                        break;
                }
//                disposeWorldText();
                return true;
            case MENU_MULTI:
                //Intent intent2 = new Intent(activity.getApplicationContext(), MultiplayerExample.class);
                //activity.startActivity(intent2);
                SceneManager.getInstance().loadMultiScene(engine);
                return true;
            case MENU_LEVELS:
                if (activity.getCurrentWorld() <= 4) {
                    SceneManager.getInstance().loadWorlds1to4Scene(engine);
                } else {
                    SceneManager.getInstance().loadWorlds5to8Scene(engine);
                }
//                disposeWorldText();
                return true;
            default:
                return false;
        }
    }

    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    private void createBackground() {
        SpriteBackground spriteBackground = new SpriteBackground(new Sprite(camera.getCenterX(), camera.getCenterY(), camera.getWidth(), camera.getHeight(), resourcesManager.menu_background_region, vbom));
        this.setBackground(spriteBackground);
    }

    private void createWorldText() {
        if (worldText == null) {
            worldText = new Text(80, camera.getHeight()-45, resourcesManager.smallFont, "W0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
            worldText.setText("W" + String.valueOf(activity.getCurrentWorld()));
            int color = android.graphics.Color.parseColor("#808080");
            worldText.setColor(color);
            this.attachChild(worldText);
        } else {
            this.updateWorldText();
        }
    }

    public void updateWorldText() {
        if (worldText != null) {
            worldText.setText("W" + String.valueOf(activity.getCurrentWorld()));
        }
    }

    private void disposeWorldText() {
        worldText.detachSelf();
        worldText.dispose();
    }

    private void createMenuChildScene() {
        menuChildScene = new MenuScene(camera);
        menuChildScene.setPosition(0, 0);

        final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, 60, 60, resourcesManager.play_region, vbom), 1.2f, 1);
        final IMenuItem levelsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEVELS, 60, 60, resourcesManager.world_region, vbom), 1.2f, 1);
        final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MULTI, 60, 60, resourcesManager.play_multi_region, vbom), 1.2f, 1);
        final IMenuItem singleTextMenuItem = new TextMenuItem(9, resourcesManager.smallFont, "Single Player", vbom);
        final IMenuItem multiTextMenuItem = new TextMenuItem(9, resourcesManager.smallFont, "Multi Player", vbom);

        menuChildScene.addMenuItem(playMenuItem);
        menuChildScene.addMenuItem(levelsMenuItem);
        menuChildScene.addMenuItem(optionsMenuItem);
        menuChildScene.addMenuItem(singleTextMenuItem);
        menuChildScene.addMenuItem(multiTextMenuItem);

        menuChildScene.buildAnimations();
        menuChildScene.setBackgroundEnabled(false);

//        playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 10);
        playMenuItem.setPosition(camera.getCenterX()-50, camera.getCenterY());
        levelsMenuItem.setPosition(camera.getCenterX()+50, camera.getCenterY());
        optionsMenuItem.setPosition(camera.getCenterX(), playMenuItem.getY() - 150);
        singleTextMenuItem.setPosition(camera.getCenterX(), playMenuItem.getY() + 50);
        multiTextMenuItem.setPosition(camera.getCenterX(), optionsMenuItem.getY() + 50);

        menuChildScene.setOnMenuItemClickListener(this);

        setChildScene(menuChildScene);
    }
}