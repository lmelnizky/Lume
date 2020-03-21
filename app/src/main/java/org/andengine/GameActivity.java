package org.andengine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
//import com.tappx.sdk.android.Tappx;
//import com.tappx.sdk.android.TappxAdError;
//import com.tappx.sdk.android.TappxInterstitial;
//import com.tappx.sdk.android.TappxInterstitialListener;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.File;
import java.io.IOException;

import me.drakeet.support.toast.ToastCompat;

public class GameActivity extends BaseGameActivity implements RewardedVideoAdListener {

    private static final int REQUEST_ENABLE_BT = 7;
    private static final String GAME = "LUME";
    private static final String CURRENTWORLD = "CURRENTWORLD";
    private static final String PLAYED_GAMES = "PLAYED_GAMES";
    private static final String SHOWED_LEVEL_HINTS = "SHOWED_LEVEL_HINTS";
    private static final String LOUDVISIBLE = "LOUDVISIBLE";
    private static final String TUTORIALMULTI = "TUTORIALMULTI";
    private static final String FIRSTSLOWMOWORLD = "FIRSTSLOWMOWORLD";
    private static final String FIRSTSLOWMOMENU = "FIRSTSLOWMOMENU";

    private BoundCamera camera;
    private EngineOptions engineOptions;

    private  boolean showText = true;
    private boolean firstSlowMoWorld = true;
    private boolean firstSlowMoMenu = true;

    private int cameraWidth; //1280
    private int cameraHeight; //720
    private int playedGames = 4;
    private int showedLevelHints;
    private int worldToStart, levelToStart;

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;

    //ADS
    RelativeLayout relativeLayout = null;
    FrameLayout.LayoutParams relativeLayoutLayoutParams = null;
    android.widget.RelativeLayout.LayoutParams surfaceViewLayoutParams = null;
    FrameLayout frameLayout = null;
    RelativeLayout.LayoutParams params = null;

    private AdView adView;
    private InterstitialAd mMultiInterstitialAd, mSingleInterstitialAd;
    private RewardedVideoAd mAd;
    //public TappxInterstitial tappxInterstitial;



    public void toastOnUiThread(final String msg, final int length) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT == 25) {
                    ToastCompat.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                            .setBadTokenListener(toast -> {

                            }).show();
                } else {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createTypingText(final String s, Text tv, boolean finish) {
        showText = true;
        tv.setText("");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                if (!finish) {
                    for (int i = 0; i < s.length(); i++) {
                        int finalI = i;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                tv.setText(tv.getText()+String.valueOf(s.charAt(finalI)));
                                if (s.charAt(finalI) != ' ') {
                                    try {
                                        Thread.sleep(30);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setShowText(false);
                        }
                    }, 1000);
                } else {
                    tv.setText(s);
                }
            }
        });
    }

    public boolean isShowText() {
        return this.showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public void unlockWorld(int world) {
        editor.putInt(CURRENTWORLD, world);
        editor.commit();

        String message = (world == 9) ? "You are a real LUME lad" : "You have unlocked World " + String.valueOf(world);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                alert.setTitle("Congratulations Man!");
                alert.setMessage(message);
                alert.setIcon(R.drawable.finger_watch);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                alert.show();
            }
        });
    }

    public boolean isStartVideo() {
//        boolean startVideo = false;
//        this.toastOnUiThread(String.valueOf(pref.getInt(PLAYED_GAMES, 0)));
//        if (pref.getInt(PLAYED_GAMES, 0)%10 == 0 && pref.getInt(PLAYED_GAMES, 0) != 0) {
//            startVideo = true;
//        } else {
//            playedGames++;
//        }
//        editor.putInt(PLAYED_GAMES, playedGames);
//        editor.commit();
//        return startVideo;
        return false;
    }

    public int getCurrentWorld() {
        return pref.getInt(CURRENTWORLD, 0);
    }

    public boolean isLoudVisible() {
        return pref.getBoolean(LOUDVISIBLE, true);
    }

    public void setLoudVisible(boolean loudvisible) {
        editor.putBoolean(LOUDVISIBLE, loudvisible);
        editor.commit();
    }

    public boolean isMultiTutorialSeen() {
        return pref.getBoolean(TUTORIALMULTI, false);
    }

    public void setMultiTutorialSeen(boolean seen) {
        editor.putBoolean(TUTORIALMULTI, seen);
        editor.commit();
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(int playedGames) {
        this.playedGames = playedGames;
    }

    public int getShowedLevelHints() {
        return pref.getInt(SHOWED_LEVEL_HINTS, 0);
    }

    public void setShowedLevelHints(int showedLevelHints) {
        this.showedLevelHints = showedLevelHints;
    }

    public boolean isFirstSlowMoWorld() {
        return pref.getBoolean(FIRSTSLOWMOWORLD, true);
    }

    public void setFirstslowmoworld(boolean firstSlowMoWorld) {
        this.firstSlowMoWorld = firstSlowMoWorld;
    }

    public boolean isFirstSlowMoMenu() {
        return pref.getBoolean(FIRSTSLOWMOMENU, true);
    }

    public void setFirstslowmoMenu(boolean firstSlowMoMenu) {
        this.firstSlowMoMenu = firstSlowMoMenu;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //Tappx ADS
        //Tappx ADS

//        tappxInterstitial = new TappxInterstitial(this.getApplicationContext(), "pub-50236-android-4229");
//        tappxInterstitial.setAutoShowWhenReady(true);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tappxInterstitial.loadAd();
//            }
//        });
//        tappxInterstitial.setListener(new TappxInterstitialListener() {
//            @Override
//            public void onInterstitialLoaded(TappxInterstitial tappxInterstitial) {
//            }
//
//            @Override
//            public void onInterstitialLoadFailed(TappxInterstitial tappxInterstitial, TappxAdError tappxAdError) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tappxInterstitial.loadAd();
//                    }
//                });
//            }
//
//            @Override
//            public void onInterstitialShown(TappxInterstitial tappxInterstitial) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tappxInterstitial.loadAd();
//                    }
//                });
//            }
//
//            @Override
//            public void onInterstitialClicked(TappxInterstitial tappxInterstitial) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tappxInterstitial.loadAd();
//                    }
//                });
//            }
//
//            @Override
//            public void onInterstitialDismissed(TappxInterstitial tappxInterstitial) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tappxInterstitial.loadAd();
//                    }
//                });
//            }
//        });
    }

    @Override protected void onSetContentView() {

        //Tappx.getPrivacyManager(this.getApplicationContext()).setAutoPrivacyDisclaimerEnabled(true);


        relativeLayout = new RelativeLayout(this);
        relativeLayoutLayoutParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

        mRenderSurfaceView = new RenderSurfaceView(getApplicationContext());
        mRenderSurfaceView.setRenderer(mEngine, this);


        surfaceViewLayoutParams = new RelativeLayout.LayoutParams(BaseGameActivity.createSurfaceViewLayoutParams());
        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        relativeLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);

        frameLayout = new FrameLayout(this);

        //initialize mobileads and adViews
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4787870052129424~3644680594");

        adView = new AdView(this);

        this.adView.setAdSize(AdSize.SMART_BANNER);
        this.adView.setAdUnitId("ca-app-pub-4787870052129424/4872354475"); //already from AdMob

        mMultiInterstitialAd = new InterstitialAd(this);
        mMultiInterstitialAd.setAdUnitId("ca-app-pub-4787870052129424/3730022561"); //already from AdMob
        mMultiInterstitialAd.loadAd(new AdRequest.Builder().build());
        mMultiInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mMultiInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        mSingleInterstitialAd = new InterstitialAd(this);
        mSingleInterstitialAd.setAdUnitId("ca-app-pub-4787870052129424/2094173543"); //id from admob
        mSingleInterstitialAd.loadAd(new AdRequest.Builder()
               // .addTestDevice("E8F7D3C5F811FFE1D5AEB61BB219CECC")
                .build());
        mSingleInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mSingleInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        this.loadRewardedVideoAd();


//        adView.refreshDrawableState();
//        adView.setVisibility(View.VISIBLE);

            //loading setAdVisibility
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("E8F7D3C5F811FFE1D5AEB61BB219CECC")
                    .build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }
            });
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        }


        frameLayout.addView(adView);
        relativeLayout.addView(frameLayout, params);


        this.setContentView(relativeLayout, relativeLayoutLayoutParams);
    }

    public void loadRewardedVideoAd() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAd.loadAd("ca-app-pub-4787870052129424/4739684035", new AdRequest.Builder()
                        //.addTestDevice("E8F7D3C5F811FFE1D5AEB61BB219CECC")
                        .build());
                //ad unit ca-app-pub-4787870052129424/4739684035
                //test ca-app-pub-3940256099942544/5224354917
            }
        });
    }

    public void setAdVisibility() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world0Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world1Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world2Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world3Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world4Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world5Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world6Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world7Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().world8Scene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().loadingScene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().helpScene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().multiScene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().highscoreScene) {
                    adView.setVisibility(View.GONE);
                } else {
                    adView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void showSingleInterstitial() {
        if (playedGames == 5) { //show google ad
            playedGames = 0;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSingleInterstitialAd.isLoaded()) {
                        mSingleInterstitialAd.show();
                    }
                }
            });
        } else if (playedGames == 4 || playedGames == 1) { //show tappx ad
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //if (tappxInterstitial.isReady()) tappxInterstitial.show();
                }
            });
            playedGames++;
        } else {
            playedGames++;
        }
    }

    public void showLevelHint() {
        if (playedGames == 3 && getShowedLevelHints() < 4) {
            showedLevelHints++;
            editor.putInt(SHOWED_LEVEL_HINTS, showedLevelHints);
            editor.commit();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String message = (getShowedLevelHints() == 1 || getShowedLevelHints() == 3) ?
                            "You have to train in Levels Menu (World Symbol in Main Menu)! " +
                            "You can also see a Walkthrough Video there if you are helpless!" :
                            "You have to train in Levels Menu (World Symbol in Main Menu)!";
                    AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                    alert.setTitle("Hey Man!");
                    alert.setMessage(message);
                    alert.setIcon(R.drawable.kimmelnitz);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //nothing to do
                        }
                    });

                    alert.show();
                }
            });
        }
    }

    public void showSlowMoHintMenu() {
        if (isFirstSlowMoMenu() && getCurrentWorld() > 0) {
            firstSlowMoMenu = false;
            editor.putBoolean(FIRSTSLOWMOMENU, firstSlowMoMenu);
            editor.commit();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = "You can now play in Slow Motion Mode in Levels Menu (World Symbol) " +
                            "by simply single tapping (not swiping) on the right half!";
                    AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                    alert.setTitle("Hey Man!");
                    alert.setMessage(message);
                    alert.setIcon(R.drawable.kimmelnitz);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //nothing to do
                        }
                    });

                    alert.show();
                }
            });
        }
    }

    public void showSlowMoHintWorld() {
        if (isFirstSlowMoWorld()) {
            firstSlowMoWorld = false;
            firstSlowMoMenu = false;
            editor.putBoolean(FIRSTSLOWMOWORLD, firstSlowMoWorld);
            editor.putBoolean(FIRSTSLOWMOMENU, firstSlowMoMenu);
            editor.commit();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = "You can play in Slow Motion Mode here! " +
                            "Just tap on the right half (not swiping) and slow down time!";
                    AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                    alert.setTitle("Hey Man!");
                    alert.setMessage(message);
                    alert.setIcon(R.drawable.kimmelnitz);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //nothing to do
                        }
                    });

                    alert.show();
                }
            });
        }
    }

    public void showHighHint() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "This Mode will be available soon!";
                AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                alert.setTitle("Hey Man!");
                alert.setMessage(message);
                alert.setIcon(R.drawable.kimmelnitz);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //nothing to do
                    }
                });

                alert.show();
            }
        });
    }

    public void showMultiInterstitial() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMultiInterstitialAd.isLoaded()) {
                    mMultiInterstitialAd.show();
                }
            }
        });
    }

    public void showRewarded(int worldToStart, int levelToStart) {
        this.worldToStart = worldToStart;
        this.levelToStart = levelToStart;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAd.isLoaded()) {
                    mAd.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                    alert.setTitle("Sorry, Man!");
                    alert.setMessage("Ad video could not load");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

                    alert.show();
                }
            }
        });
    }

    public void resetMainMenuResources() {
        ResourcesManager.getInstance().unloadCurrentScene(SceneManager.getInstance().getCurrentScene());
        ResourcesManager.getInstance().menuTextureAtlas.clearTextureAtlasSources();
        ResourcesManager.getInstance().menuTextureAtlas = null;
        SceneManager.getInstance().menuScene.detachSelf();
        SceneManager.getInstance().menuScene.disposeScene();
        SceneManager.getInstance().menuScene = null;

        SceneManager.getInstance().createMenuScene();
    }

    @Override
    public void onGameCreated() {
        super.onGameCreated();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEngine.start();
        mAd.resume(this);
    }

    @Override
    protected void onPause() {
        mEngine.stop();
        mAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAd.destroy(this);
        //if (tappxInterstitial != null) tappxInterstitial.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SceneManager.getInstance().getCurrentScene() != null) {
                SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
            } else {
                System.exit(0);
            }
        }
        return false;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        if (!ResourcesManager.getInstance().prepared) {
            DisplayMetrics displayM = this.getResources().getDisplayMetrics();
            float pxWidth = displayM.widthPixels;
            float pxHeight = displayM.heightPixels;

            pref = getSharedPreferences(GAME, 0);
            editor = pref.edit();

            //change width and height because we have landscape
            cameraWidth = 1280;
            cameraHeight = 720; //using absolute values!!



            camera = new BoundCamera(0, 0, cameraWidth, cameraHeight);
            engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(cameraWidth, cameraHeight), this.camera);
            engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
            engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);

            engineOptions.getTouchOptions().setNeedsMultiTouch(true);
            showedLevelHints = pref.getInt(SHOWED_LEVEL_HINTS, 0);
//            if (MultiTouch.isSupported(this)) {
//                if(MultiTouch.isSupportedDistinct(this)) {
//                    Toast.makeText(this, "MultiTouch detected --> Both controls will work properly!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
//            }

            return engineOptions;
        } else {
            return ResourcesManager.getInstance().engineOptions;
        }
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        if (!ResourcesManager.getInstance().prepared) {
            ResourcesManager.prepareManager(mEngine, engineOptions, this, camera, getVertexBufferObjectManager());
            pOnCreateResourcesCallback.onCreateResourcesFinished();
        }
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        if (SceneManager.getInstance().getCurrentScene() == null) {
            SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
        } else {
        }

    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
            mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                public void onTimePassed(final TimerHandler pTimerHandler) {
                    mEngine.unregisterUpdateHandler(pTimerHandler);
                    SceneManager.getInstance().createMenuScene();
                }
            }));
        pOnPopulateSceneCallback.onPopulateSceneFinished();    }

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, 60);
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        this.loadRewardedVideoAd(); //load next video
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        toastOnUiThread("Showing Walkthrough Video");

        showWalkthroughVideo();
        this.loadRewardedVideoAd(); //load next video

        playedGames = 0;
        editor.putInt(PLAYED_GAMES, playedGames);
        editor.commit();

        //this.resetMainMenuResources();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        this.loadRewardedVideoAd(); //load next video
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        this.loadRewardedVideoAd(); //load next video
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    public void showWalkthroughDialog() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                alert.setTitle("Hey, Luser!");
                alert.setMessage("Do you really want to see the Walkthrough Video?");
                alert.setIcon(R.drawable.kimmelnitz);
                alert.setCancelable(true);

                alert.setPositiveButton(
                        "Yes, I am helpless",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showRewarded(0,0);
                            }
                        });

                alert.setNegativeButton(
                        "No, i want to fight",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

               alert.show();
            }
        });
    }

    public void showWalkthroughVideo() {
        Intent videoIntent = new Intent(this, VideoActivity.class);
        startActivity(videoIntent);
    }
}
