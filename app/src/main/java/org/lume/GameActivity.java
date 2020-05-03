package org.lume;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import org.lume.engine.Engine;
import org.lume.engine.LimitedFPSEngine;
import org.lume.engine.camera.BoundCamera;
import org.lume.engine.handler.timer.ITimerCallback;
import org.lume.engine.handler.timer.TimerHandler;
import org.lume.engine.options.EngineOptions;
import org.lume.engine.options.ScreenOrientation;
import org.lume.engine.options.WakeLockOptions;
import org.lume.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.lume.entity.scene.Scene;
import org.lume.entity.text.Text;
import org.lume.manager.ResourcesManager;
import org.lume.manager.SceneManager;
import org.lume.opengl.view.RenderSurfaceView;
import org.lume.scene.ShopScene;
import org.lume.ui.activity.BaseGameActivity;

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
    private static final String IS_NAME_ONLINE = "IS_NAME_ONLINE"; //boolean
    private static final String WANTS_ONLINE = "WANTS_ONLINE"; //boolean
    private static final String NAME_ONLINE = "NAME_ONLINE"; //TODO String online name
    private static final String ID_ONLINE = "ID_ONLINE"; //TODO String online name
    private static final String COINS = "COINS";
    private static final String HIGHSCORE = "HIGHSCORE";
    private static final String PLAYER = "PLAYER";
    private static final String IS_SLOWMO = "IS_SLOWMO";

    private BoundCamera camera;
    private EngineOptions engineOptions;

    private  boolean showText = true;
    private boolean firstSlowMoWorld = true;
    private boolean firstSlowMoMenu = true;

    public final static float CAMERA_WIDTH = 1280;
    public final static float CAMERA_HEIGHT = 720;
    private int playedGames = 4;
    private int showedLevelHints;
    private int rewardedType;

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
    private RewardedVideoAd mWalkthroughAd, mCoinsAd;
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

    public boolean checkHighscore(int score) {
        int currentHighscore = pref.getInt(HIGHSCORE, 0);
        if (score > currentHighscore) {
            editor.putInt(HIGHSCORE, score);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    public int getCurrentHighscore() {
        return pref.getInt(HIGHSCORE, 0);
    }

    public void addBeersos(int beersos) {
        int currentBeersos = pref.getInt(COINS, 0);
        int newBeersos = currentBeersos + beersos;
        editor.putInt(COINS, newBeersos);
        editor.commit();
    }

    public int getCurrentBeersos() {
        int beersos = pref.getInt(COINS, 0);
        return beersos;
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

    public boolean isWantOnline() {
        return pref.getBoolean(WANTS_ONLINE, true);
    }

    public void setWantsOnline(boolean wantsOnline) {
        editor.putBoolean(WANTS_ONLINE, wantsOnline);
        editor.commit();
    }

    public boolean isNameOnline() {
        return pref.getBoolean(IS_NAME_ONLINE, false);
    }

    public void setNameOnline(boolean isOnline) {
        editor.putBoolean(IS_NAME_ONLINE, isOnline);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(NAME_ONLINE, "");
    }

    public void setUserName(String userName) {
        editor.putString(NAME_ONLINE, userName);
        editor.commit();
    }

    public int getUserID() {
        return pref.getInt(ID_ONLINE, 0);
    }

    public void setUserID(int userID) {
        editor.putInt(ID_ONLINE, userID);
        editor.commit();
    }

    public int getCurrentPlayer() {
        return pref.getInt(PLAYER, 0);
    }

    public void setPlayer(int player) {
        editor.putInt(PLAYER, player);
        editor.commit();
    }

    public boolean isLoudVisible() {
        return pref.getBoolean(LOUDVISIBLE, true);
    }

    public void setLoudVisible(boolean loudvisible) {
        editor.putBoolean(LOUDVISIBLE, loudvisible);
        editor.commit();
    }

    public boolean isSlowMotion() {
        return pref.getBoolean(IS_SLOWMO, false);
    }

    public void setSlowMotion(boolean slowMo) {
        editor.putBoolean(IS_SLOWMO, slowMo);
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
                .addTestDevice("E8F7D3C5F811FFE1D5AEB61BB219CECC")
                .build());
        mSingleInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mSingleInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        mWalkthroughAd = MobileAds.getRewardedVideoAdInstance(this);
        mWalkthroughAd.setRewardedVideoAdListener(this);

        mCoinsAd = MobileAds.getRewardedVideoAdInstance(this);
        mCoinsAd.setRewardedVideoAdListener(this);
        this.loadRewardedVideoAd();



//        adView.refreshDrawableState();
//        adView.setVisibility(View.VISIBLE);

            //loading setAdVisibility
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("E8F7D3C5F811FFE1D5AEB61BB219CECC")
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
                mWalkthroughAd.loadAd("ca-app-pub-4787870052129424/4739684035", new AdRequest.Builder()
                        .addTestDevice("E8F7D3C5F811FFE1D5AEB61BB219CECC")
                        .build());
                mCoinsAd.loadAd("ca-app-pub-4787870052129424/7199859943", new AdRequest.Builder()
                        .addTestDevice("E8F7D3C5F811FFE1D5AEB61BB219CECC")
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
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().highscoreScene ||
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().skillGameScene) {
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
        } else {
            playedGames++;
        }
    }

    public void showLevelHint() {
        if (playedGames == 3 && getShowedLevelHints() < 4 && isFirstSlowMoMenu()) {
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
                            "by simply single tapping (not swiping) on the right half or setting the symbol in" +
                            " Levels menu to SlowMotion!";
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

    public void showCoinHint(int player, int coins, ShopScene scene) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "Do you want to unlock this player?";
                AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                alert.setTitle("Hey!");
                alert.setMessage(message);
                alert.setIcon(R.drawable.kimmelnitz);
                alert.setPositiveButton("Good deal!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        setPlayer(player);
                        addBeersos(-coins);
                        scene.updateChosenRect();
                    }
                });
                alert.setNegativeButton("No, too expensive", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

    public void showRewarded(int rewardedType) {
        this.rewardedType = rewardedType;
        if (rewardedType == 0) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWalkthroughAd.isLoaded()) {
                        mWalkthroughAd.show();
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
        } else if (rewardedType == 1) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCoinsAd.isLoaded()) {
                        mCoinsAd.show();
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
        mWalkthroughAd.resume(this);
        mCoinsAd.resume(this);
    }

    @Override
    protected void onPause() {
        mEngine.stop();
        mWalkthroughAd.pause(this);
        mCoinsAd.pause(this);
        ResourcesManager.getInstance().unloadGameAudio();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWalkthroughAd.destroy(this);
        mCoinsAd.destroy(this);
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
            // using absolute values!!



            camera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
            engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);
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
        /*
        if (SceneManager.getInstance().getCurrentScene() == null) {
            SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
        } else {
        }*/
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
        /*SurfaceView mBackgroundView = new PreviewView(this);
        this.addContentView(mBackgroundView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                public void onTimePassed(final TimerHandler pTimerHandler) {
                    mEngine.unregisterUpdateHandler(pTimerHandler);
                    SceneManager.getInstance().createMenuScene();
                }
            }));
        pOnPopulateSceneCallback.onPopulateSceneFinished();*/

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
        if (rewardedType == 1) {
            toastOnUiThread("Received 100 coins!");
            addBeersos(100);
            this.loadRewardedVideoAd();
        } else if (rewardedType == 0) {
            toastOnUiThread("Showing Walkthrough Video");

            showWalkthroughVideo();
            this.loadRewardedVideoAd(); //load next video

            playedGames = 0;
            editor.putInt(PLAYED_GAMES, playedGames);
            editor.commit();
        }
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
                alert.setTitle("Hey, Lad!");
                alert.setMessage("Do you want to see the Walkthrough Video?");
                alert.setIcon(R.drawable.kimmelnitz);
                alert.setCancelable(true);

                alert.setPositiveButton(
                        "Yes, I am helpless",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showRewarded(0);
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
