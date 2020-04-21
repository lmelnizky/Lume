package org.andengine.manager;

import org.andengine.base.BaseScene;
import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.scene.HelpScene;
import org.andengine.scene.HighscoreScene;
import org.andengine.scene.MultiScene;
import org.andengine.scene.ShopScene;
import org.andengine.scene.SkillMenu;
import org.andengine.scene.Worlds1to4Scene;
import org.andengine.scene.LoadingScene;
import org.andengine.scene.MainMenuScene;
import org.andengine.scene.SplashScene;
import org.andengine.scene.Worlds5to8Scene;
import org.andengine.scene.skillscenes.Skill11;
import org.andengine.scene.worlds.World0;
import org.andengine.scene.worlds.World1;
import org.andengine.scene.worlds.World2;
import org.andengine.scene.worlds.World3;
import org.andengine.scene.worlds.World4;
import org.andengine.scene.worlds.World5;
import org.andengine.scene.worlds.World6;
import org.andengine.scene.worlds.World7;
import org.andengine.scene.worlds.World8;
import org.andengine.ui.IGameInterface;

/**
 * Created by Lukas on 15.05.2017.
 */

public class SceneManager {

    public BaseScene splashScene;
    public MainMenuScene menuScene;
    public BaseScene gameScene;
    public BaseScene multiScene;
    public BaseScene highscoreScene;
    public BaseScene shopScene;
    public BaseScene skillGameScene;
    public BaseScene skillMenuScene;
    public BaseScene helpScene;
    public BaseScene loadingScene;
    public BaseScene worlds1to4Scene;
    public BaseScene worlds5to8Scene;

    public BaseScene world0Scene;
    public BaseScene world1Scene;
    public BaseScene world2Scene;
    public BaseScene world3Scene;
    public BaseScene world4Scene;
    public BaseScene world5Scene;
    public BaseScene world6Scene;
    public BaseScene world7Scene;
    public BaseScene world8Scene;
//    private BaseScene level2Scene;

    private static final SceneManager INSTANCE = new SceneManager();

    private SceneType currentSceneType = SceneType.SCENE_SPLASH;

    private BaseScene currentScene;

    private Engine engine = ResourcesManager.getInstance().engine;

//    public enum SceneType {
//        SCENE_SPLASH,
//        SCENE_MENU,
//        SCENE_LOADING,
//        SCENE_LEVELS,
//        SCENE_MULTI,
//        SCENE_W1,
//        SCENE_W2,
//    }

    public void setScene(BaseScene scene) {
        engine.setScene(scene);
        currentScene = scene;
        currentSceneType = scene.getSceneType();

        //call setContentView in Activity to show ads
        ResourcesManager.getInstance().activity.setAdVisibility();
    }

//    public void setScene(SceneType sceneType) {
//        switch (sceneType) {
//            case SCENE_MENU:
//                setScene(menuScene);
//                break;
//            case SCENE_GAME:
//                setScene(gameScene);
//                break;
//            case SCENE_SPLASH:
//                setScene(splashScene);
//                break;
//            case SCENE_LOADING:
//                setScene(loadingScene);
//                break;
//            default:
//                break;
//        }
//    }

    public void createSplashScene(IGameInterface.OnCreateSceneCallback pOnCreateSceneCallback) {
        ResourcesManager.getInstance().loadSplashScreen();
        splashScene = new SplashScene();
        currentScene = splashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }

//    private void disposeSplashScene() {
//        ResourcesManager.getInstance().unloadSplashScreen();
//        splashScene.disposeScene();
//        splashScene = null;
//    }

    public void createMenuScene() {
        if (currentScene != null && currentScene == menuScene) { //menuscene can also be tbe current scene
            ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        }
        ResourcesManager.getInstance().loadMenuResources();
        loadingScene = new LoadingScene();
        menuScene = new MainMenuScene();
        SceneManager.getInstance().setScene(menuScene);
    }

    public void loadHelpScene(final Engine mEngine) {
        ResourcesManager.getInstance().loadHelpBackground();
        helpScene = new HelpScene();
        setScene(helpScene);
    }

    public void loadWorld0Scene(final Engine mEngine, final int level) {
                    ResourcesManager.getInstance().unloadCurrentScene(currentScene);
    setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(0);
        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
        public void onTimePassed(final TimerHandler pTimerHandler) {
            mEngine.unregisterUpdateHandler(pTimerHandler);
            if (level == 0) {
                world0Scene = new World0(); //selected from MainMenuScene
            } else {
                world0Scene = new World0(level); //selected from Worlds1to4Scene
            }
            setScene(world0Scene);
        }
    }));
}

    public void loadWorld1Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(1);
        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world1Scene = new World1(); //selected from MainMenuScene
                } else {
                    world1Scene = new World1(level); //selected from Worlds1to4Scene
                }
                setScene(world1Scene);
            }
        }));
    }

    public void loadWorld2Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(2);

        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world2Scene = new World2(); //selected from MainMenuScene
                } else {
                    world2Scene = new World2(level); //selected from Worlds1to4Scene
                }
                setScene(world2Scene);
            }
        }));
    }

    public void loadWorld3Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(3);

        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world3Scene = new World3(); //selected from MainMenuScene
                } else {
                    world3Scene = new World3(level); //selected from Worlds1to4Scene
                }
                setScene(world3Scene);
            }
        }));
    }

    public void loadWorld4Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(4);

        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world4Scene = new World4(); //selected from MainMenuScene
                } else {
                    world4Scene = new World4(level); //selected from Worlds1to4Scene
                }
                setScene(world4Scene);
            }
        }));
    }

    public void loadWorld5Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(5);

        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world5Scene = new World5(); //selected from MainMenuScene
                } else {
                    world5Scene = new World5(level); //selected from Worlds1to4Scene
                }
                setScene(world5Scene);
            }
        }));
    }

    public void loadWorld6Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(6);

        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world6Scene = new World6(); //selected from MainMenuScene
                } else {
                    world6Scene = new World6(level); //selected from Worlds1to4Scene
                }
                setScene(world6Scene);
            }
        }));
    }

    public void loadWorld7Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(7);

        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world7Scene = new World7(); //selected from MainMenuScene
                } else {
                    world7Scene = new World7(level); //selected from Worlds1to4Scene
                }
                setScene(world7Scene);
            }
        }));
    }

    public void loadWorld8Scene(final Engine mEngine, final int level) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadGameResources(8);

        mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                if (level == 0) {
                    world8Scene = new World8(); //selected from MainMenuScene
                } else {
                    world8Scene = new World8(level); //selected from Worlds5to8Scene
                }
                setScene(world8Scene);
            }
        }));
    }

    public void loadWorlds1to4Scene(final Engine mEngine) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadLevelResources(1);

        mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                worlds1to4Scene = new Worlds1to4Scene();
                setScene(worlds1to4Scene);
            }
        }));
    }

    public void loadWorlds5to8Scene(final Engine mEngine) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        setScene(loadingScene);
        ResourcesManager.getInstance().loadLevelResources(2);

        mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                worlds5to8Scene = new Worlds5to8Scene();
                setScene(worlds5to8Scene);
            }
        }));
    }

    public void loadHighscoreScene(final Engine mEngine) {
        loadingScene = new LoadingScene();
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadHighscoreResources();
                highscoreScene = new HighscoreScene();
                setScene(highscoreScene);
            }
        }));
    }

    public void loadShopScene(final Engine mEngine) {
        loadingScene = new LoadingScene();
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadShopResources();
                shopScene = new ShopScene();
                setScene(shopScene);
            }
        }));
    }

    public void loadSkillGameScene(final Engine mEngine) {
        loadingScene = new LoadingScene();
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadSkillResources();
                skillGameScene = new Skill11();
                setScene(skillGameScene);
            }
        }));
    }

    public void loadSkillMenuScene(final Engine mEngine) {
        BaseScene currentScene = getCurrentScene();
        loadingScene = new LoadingScene();
        setScene(loadingScene);
        if (currentScene.getSceneType() == SceneType.SCENE_MENU) ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadSkillMenuResources();
                skillMenuScene = new SkillMenu();
                setScene(skillMenuScene);
            }
        }));
    }

    public void loadMultiScene(final Engine mEngine) {
        loadingScene = new LoadingScene();
        setScene(loadingScene);
        ResourcesManager.getInstance().unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadMultiResources();
                multiScene = new MultiScene();
                setScene(multiScene);
            }
        }));
    }

    public void loadMenuScene(final Engine mEngine) {
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        currentScene.disposeScene();
        setScene(loadingScene);
//        gameScene.disposeScene();
//        ResourcesManager.getInstance().unloadGameTextures();
        ResourcesManager.getInstance().unloadCurrentScene(currentScene);
        ResourcesManager.getInstance().bluetoothSocket = null;
        ResourcesManager.getInstance().bluetoothDevice = null;
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.getInstance().loadMenuTextures();
                setScene(menuScene);
                menuScene.updateWorldText();
                menuScene.updateCoinText();
            }
        }));
    }


    public static SceneManager getInstance() {
        return INSTANCE;
    }

    public SceneType getCurrentSceneType() {
        return currentSceneType;
    }

    public BaseScene getCurrentScene() {
        return currentScene;
    }

}
