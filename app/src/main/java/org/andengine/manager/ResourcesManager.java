package org.andengine.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.graphics.Typeface;

import org.andengine.GameActivity;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.base.BaseScene;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;

/**
 * Created by Lukas on 15.05.2017.
 */

public class ResourcesManager {

    private static final ResourcesManager INSTANCE = new ResourcesManager();

    public boolean prepared = false;
    public int screenWidth, screenHeight, sideLength;
    public float screenRatio;

    public Engine engine;
    public EngineOptions engineOptions;
    public GameActivity activity;
    public BoundCamera camera;
    public VertexBufferObjectManager vbom;

    public Font standardFont;
    public Font smallFont;
    public Font worldNumberFont;
    public Font bigFont;

    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------

    //Menu TextureRegions
    public ITextureRegion splash_region;
    public ITextureRegion menu_background_region;
    public ITextureRegion play_region;
    public ITextureRegion play_multi_region;
    public ITextureRegion world_region;
    public ITextureRegion loud_region;
    public ITextureRegion psst_region;
    public ITextureRegion help_region;
    public ITextureRegion know_region;
    public ITextureRegion ball_fall;
    public ITextureRegion military;
    public ITextureRegion play_coin_region;
    public ITextureRegion shopping_region;

    //Help Region
    public ITextureRegion help_background_region;

    public SpriteBackground spriteBackground;

    // Texture
    public BuildableBitmapTextureAtlas gameTextureAtlas = null;
    public BuildableBitmapTextureAtlas chooseLevelTextureAtlas = null;
    public BuildableBitmapTextureAtlas multiTextureAtlas = null;
    public BuildableBitmapTextureAtlas menuTextureAtlas = null;
    public BuildableBitmapTextureAtlas shopTextureAtlas = null;
    public BuildableBitmapTextureAtlas helpTextureAtlas = null;
    public BitmapTextureAtlas splashTextureAtlas = null;
    public BuildableBitmapTextureAtlas world0TextureAtlas = null;
    public BuildableBitmapTextureAtlas world1TextureAtlas = null;
    public BuildableBitmapTextureAtlas world2TextureAtlas = null;
    public BuildableBitmapTextureAtlas world3TextureAtlas = null;
    public BuildableBitmapTextureAtlas world4TextureAtlas = null;
    public BuildableBitmapTextureAtlas world5TextureAtlas = null;
    public BuildableBitmapTextureAtlas world6TextureAtlas = null;
    public BuildableBitmapTextureAtlas world7TextureAtlas = null;
    public BuildableBitmapTextureAtlas world8TextureAtlas = null;
    public BuildableBitmapTextureAtlas highScoreAtlas = null;

    // Game Texture Regions
    public ITextureRegion cracky_stone_region;
    public ITextureRegion thorny_stone_region;
    public ITextureRegion cannonball_region;
    public ITextureRegion board_region;
    public ITextureRegion coin_region;
    public ITextureRegion lume_region;
    public ITextureRegion cannons_n_region;
    public ITextureRegion cannons_e_region;
    public ITextureRegion cannons_s_region;
    public ITextureRegion cannons_w_region;
    public ITextureRegion replay_region;
    public ITextureRegion finish_region;

    //Signs Texture Regions
    public ITextureRegion shoot_normal_region;
    public ITextureRegion shoot_diagonal_region;
    public ITextureRegion move_normal_region;
    public ITextureRegion move_diagonal_region;
    public ITextureRegion cracky_mirror_sign_region;
    public ITextureRegion snail_sign_region;
    public ITextureRegion no_snail_sign_region;

    //Overlay Regions
    public ITextureRegion outer_overlay1_region;
    public ITextureRegion inner_overlay1_region;

    //World 0 TextureRegions
    public ITextureRegion kimmelnitz_region;
    public ITextureRegion kimmelnitz_ko_region;
    public ITextureRegion punch_region;
    public ITextureRegion background_world0_region;

    //World 1 TextureRegions
    public ITextureRegion background_world1_region;

    //World 2 TextureRegions
    public ITextureRegion background_world2_region;

    //World3 TextureRegions
    public ITextureRegion background_world3_region;
    public ITextureRegion arrow_region;
    public ITextureRegion arrow_yellow_region;

    //World4 TextureRegions
    public ITextureRegion background_world4_region;
    public ITextureRegion cracky_mirror;

    //World5 TextureRegions
    public ITextureRegion background_world5_region;

    //World6 Texture Regions
    public ITextureRegion background_world6_region;

    //World7 Texture Regions
    public ITextureRegion background_world7_region;

    //World8 Texture Regions
    public ITextureRegion background_world8_region;
    public ITextureRegion lamporghina_sign_region;
    public ITextureRegion lamporghina_region;
    public ITextureRegion helmet_region;

    //ChooseLevel TextureRegions
    public ITextureRegion chooseLevel_region;
    public ITextureRegion change_page;
    public ITextureRegion video_show_region;

    //Multi TextureRegions
    public ITextureRegion background_multi_region;
    public ITextureRegion board_region4;
    public ITextureRegion grume_region;
    public ITextureRegion firebeam_horizontal;
    public ITextureRegion firebeam_vertical;
    public ITextureRegion bomb_normal_region;
    public ITextureRegion bomb_red_region;
    public ITextureRegion heart_region;
    public ITextureRegion bomb_sign00_region;
    public ITextureRegion bomb_sign13_region;
    public ITextureRegion bomb_sign23_region;
    public ITextureRegion bomb_sign_red_region;
    public ITextureRegion finger_luser;
    public ITextureRegion finger_watch;
    public ITextureRegion finger_middle;

    //UploadUser Graphics
    public ITextureRegion upload_background_region;
    public ITextureRegion confirm_region;
    public ITextureRegion inputtext_region;

    //Shop Graphics
    public ITextureRegion background_shop_region;
    public ITextureRegion personal_region;

    //Bluetooth and Multiplayer
    public BluetoothSocket bluetoothSocket;
    public BluetoothDevice bluetoothDevice;
    public String player;

    //Sound and Music
    public Music backgroundMusic;
    public Sound luserSound;
    public Sound easySound;
    public Sound belchSound1;
    public Sound belchSound2;
    public Sound belchSound3;

    //METHODS

    public void loadMenuResources() {
        loadMenuGraphics();
        loadMenuAudio();
    }

    public void loadGameResources(int world) {
        loadGameGraphics();
        loadWorldGraphics(world);
        loadGameAudio();
    }

    public void loadMultiResources() {
        loadGameGraphics();
        loadWorldGraphics(0);
        loadMultiGraphics();
        loadGameAudio();
    }

    public void loadLevelResources(int worlds) {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/level/");
        chooseLevelTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);

        if (worlds == 1) {
            //activity.loadRewardedVideoAd();
            chooseLevel_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(chooseLevelTextureAtlas, activity, "chooseLevel1.png");
            change_page = BitmapTextureAtlasTextureRegionFactory.createFromAsset(chooseLevelTextureAtlas, activity, "finger_e.png");
        } else if (worlds == 2) {
            chooseLevel_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(chooseLevelTextureAtlas, activity, "chooseLevel2.png");
            change_page = BitmapTextureAtlasTextureRegionFactory.createFromAsset(chooseLevelTextureAtlas, activity, "finger_w.png");
        }
        video_show_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(chooseLevelTextureAtlas, activity, "video_show.png");

        try {
            this.chooseLevelTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.chooseLevelTextureAtlas.load();
        } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }
    }

    private void loadMenuGraphics() {
        if (menuTextureAtlas == null) {
            BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
            menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 4096, 4096, TextureOptions.BILINEAR);
            menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
            play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
            play_multi_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play_multi.png");
            world_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "world.png");
            loud_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "loud.png");
            psst_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "psst.png");
            help_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "help.png");
            know_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "know.png");
            ball_fall = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "ball_fall.png");
            military = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "military.png");
            play_coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play_coin.png");
            coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "coin.png");
            shopping_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "shopping.png");
            upload_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "upload_background.png");
            confirm_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "confirm.png");
            inputtext_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "inputtext.png");


            try {
                this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                this.menuTextureAtlas.load();
            } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                Debug.e(e);
            }
        } else {
            loadMenuTextures();
        }
    }

    public void loadHelpBackground() {
        helpTextureAtlas = null;
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
        helpTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        help_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(helpTextureAtlas, activity, "help_background.png");
        try {
            this.helpTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.helpTextureAtlas.load();
        } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }
    }

    public void loadHighscoreResources() {
        loadGameGraphics();
        loadHighscoreGraphics();
        loadGameAudio();
    }

    public void loadShopResources() {
        if (shopTextureAtlas == null) {
            BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game");
            shopTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
            background_shop_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "background_shop.png");
            lume_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "lume.png");
            lamporghina_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "lamporghina.png");
            grume_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "grume.png");
            personal_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "personal.png");
            try {
                this.shopTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                this.shopTextureAtlas.load();
            } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                Debug.e(e);
            }
        } else {
            shopTextureAtlas.load();
        }

    }

    private void unloadHighscoreGraphics() {
        highScoreAtlas.unload();
    }

    private void unloadShopGraphics() {
        shopTextureAtlas.unload();
    }

    private void loadMenuAudio() {

    }

    private void loadFonts() {
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final ITexture bigFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final ITexture veryBigFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        standardFont = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  32f, true, org.andengine.util.adt.color.Color.BLACK_ABGR_PACKED_INT);
        standardFont.load();

        smallFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "lumefont.otf", 55, true, Color.WHITE, 2, Color.BLACK);
        smallFont.load();
        worldNumberFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), veryBigFontTexture, activity.getAssets(), "lumefont.otf", 100, true, Color.WHITE, 2, Color.BLACK);
        worldNumberFont.load();
        bigFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), bigFontTexture, activity.getAssets(), "lumefont.otf", 80, true, Color.WHITE, 2, Color.BLACK);
        bigFont.load();
    }

//    private void unloadFonts() {
//        smallFont.unload();
//        bigFont.unload();
//        worldNumberFont.unload();
//    }

    private void loadGameGraphics() {
        if (gameTextureAtlas == null) {
            BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
            gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 4096, 4096, TextureOptions.BILINEAR);

            cracky_stone_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "cracky_stone.png");
            thorny_stone_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "thorny_stone.png");
            cannonball_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "cannonball.png");
            board_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "board.png");
            coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png");
            lume_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "lume.png");
            cannons_n_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "cannons_n.png");
            cannons_e_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "cannons_e.png");
            cannons_s_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "cannons_s.png");
            cannons_w_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "cannons_w.png");
            replay_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "replay.png");
            finish_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "finish.png");
            finger_luser = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "finger_luser.png");
            finger_watch = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "finger_watch.png");
            snail_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "snail_sign.png");
            no_snail_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "no_snail_sign.png");

            //signs
            shoot_normal_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "shoot_normal_sign.png");
            shoot_diagonal_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "shoot_diagonal_sign.png");
            move_normal_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "move_normal_sign.png");
            move_diagonal_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "move_diagonal_sign.png");

            try {
                this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                this.gameTextureAtlas.load();
            } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                Debug.e(e);
            }
        } else {
            this.gameTextureAtlas.load();
        }
    }

    private void loadWorldGraphics(int world) {
        switch (world) {
            case 0:
                if (world0TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world0TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
                    background_world0_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world0TextureAtlas, activity, "background_world1.png");
                    kimmelnitz_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world0TextureAtlas, activity, "kimmelnitz.png");
                    kimmelnitz_ko_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world0TextureAtlas, activity, "kimmelnitz_ko.png");
                    punch_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world0TextureAtlas, activity, "punch.png");
                    try {
                        this.world0TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world0TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world0TextureAtlas.load();
                }
                break;
            case 1:
                if (world1TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world1TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
                    background_world1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world1TextureAtlas, activity, "background_world1.png");
                    try {
                        this.world1TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world1TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world1TextureAtlas.load();
                }
                break;
            case 2:
                if (world2TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world2TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
                    background_world2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world2TextureAtlas, activity, "background_world2.png");
                    try {
                        this.world2TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world2TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world2TextureAtlas.load();
                }
                break;
            case 3:
                if (world3TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world3TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
                    background_world3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world3TextureAtlas, activity, "background_world3.png");
                    arrow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world3TextureAtlas, activity, "arrow.png");
                    arrow_yellow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world3TextureAtlas, activity, "arrow_yellow.png");
                    try {
                        this.world3TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world3TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world3TextureAtlas.load();
                }
                break;
            case 4:
                if (world4TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world4TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
                    background_world4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world4TextureAtlas, activity, "background_world4.png");
                    cracky_mirror = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world4TextureAtlas, activity, "cracky_mirror.png");
                    cracky_mirror_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world4TextureAtlas, activity, "cracky_mirror_sign.png");
                    try {
                        this.world4TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world4TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world4TextureAtlas.load();
                }
                break;
            case 5:
                if (world5TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world5TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
                    background_world5_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world5TextureAtlas, activity, "background_world5.png");
                    try {
                        this.world5TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world5TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world5TextureAtlas.load();
                }
                break;
            case 6:
                if (world6TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world6TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
                    background_world6_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world6TextureAtlas, activity, "background_world6.png");
                    try {
                        this.world6TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world6TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world6TextureAtlas.load();
                }
                break;
            case 7:
                if (world7TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world7TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
                    background_world7_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world7TextureAtlas, activity, "background_world7.png");
                    arrow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world7TextureAtlas, activity, "arrow.png");
                    arrow_yellow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world7TextureAtlas, activity, "arrow_yellow.png");
                    try {
                        this.world7TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world7TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world7TextureAtlas.load();
                }
                break;
            case 8:
                if (world8TextureAtlas == null) {
                    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
                    world8TextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);

                    background_world8_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world8TextureAtlas, activity, "background_world8.png");
                    lamporghina_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world8TextureAtlas, activity, "lamporghina_sign.png");
                    lamporghina_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world8TextureAtlas, activity, "lamporghina.png");
                    helmet_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(world8TextureAtlas, activity, "helmet_sign.png");
                    try {
                        this.world8TextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                        this.world8TextureAtlas.load();
                    } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                        Debug.e(e);
                    }
                } else {
                    world8TextureAtlas.load();
                }
                break;
        }
    }

    private void loadHighscoreGraphics() {
        if (highScoreAtlas == null) {
            BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
            highScoreAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
            background_world0_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "background_world1.png");
            inner_overlay1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "inner_overlay1.png");
            outer_overlay1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "outer_overlay1.png");
            arrow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "arrow.png");
            arrow_yellow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "arrow_yellow.png");
            cracky_mirror_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "cracky_mirror_sign.png");
            cracky_mirror = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "cracky_mirror.png");
            lamporghina_sign_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "lamporghina_sign.png");
            lamporghina_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "lamporghina.png");
            helmet_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highScoreAtlas, activity, "helmet_sign.png");
            try {
                this.highScoreAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
                this.highScoreAtlas.load();
            } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
                Debug.e(e);
            }
        } else {
            highScoreAtlas.load();
        }
    }

    private void unloadWorldGraphics(int world) {
        switch (world) {
            case 0:
                this.world0TextureAtlas.unload();
                break;
            case 1:
                this.world1TextureAtlas.unload();
                break;
            case 2:
                this.world2TextureAtlas.unload();
                break;
            case 3:
                this.world3TextureAtlas.unload();
                break;
            case 4:
                this.world4TextureAtlas.unload();
                break;
            case 5:
                this.world5TextureAtlas.unload();
                break;
            case 6:
                this.world6TextureAtlas.unload();
                break;
        }
    }

    private void unloadMultiGraphics() {
        this.multiTextureAtlas.unload();
    }

    private void loadMultiGraphics() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
        multiTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 4096, 4096, TextureOptions.BILINEAR);

        background_multi_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "background_multi.png");
        board_region4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "board4.png");
        grume_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "grume.png");
        bomb_normal_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "bomb_normal.png");
        bomb_red_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "bomb_red.png");
        bomb_sign00_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "bomb_sign00.png");
        bomb_sign13_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "bomb_sign13.png");
        bomb_sign23_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "bomb_sign23.png");
        bomb_sign_red_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "bomb_sign_red.png");
        heart_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "heart.png");
        firebeam_horizontal = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "firebeam_horizontal.png");
        firebeam_vertical = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "firebeam_vertical.png");
        finger_middle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(multiTextureAtlas, activity, "finger.png");

        try {
            this.multiTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.multiTextureAtlas.load();
        } catch (final ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }
    }

    private void loadGameAudio() {
        try
        {
            backgroundMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(),
                    activity.getApplicationContext(), "gfx/sound/back.wav");
            luserSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(),
                    activity.getApplicationContext(), "gfx/sound/luser.wav");
            easySound = SoundFactory.createSoundFromAsset(engine.getSoundManager(),
                    activity.getApplicationContext(), "gfx/sound/too_easy.wav");
            belchSound1 = SoundFactory.createSoundFromAsset(engine.getSoundManager(),
                    activity.getApplicationContext(), "gfx/sound/belch_1.wav");
            belchSound2 = SoundFactory.createSoundFromAsset(engine.getSoundManager(),
                    activity.getApplicationContext(), "gfx/sound/belch_2.wav");
            belchSound3 = SoundFactory.createSoundFromAsset(engine.getSoundManager(),
                    activity.getApplicationContext(), "gfx/sound/belch_3.wav");
            belchSound1.setVolume(0.4f);
            belchSound2.setVolume(0.4f);
            belchSound3.setVolume(0.4f);

            if (!activity.isLoudVisible()) {
                backgroundMusic.setVolume(0f);
                luserSound.setVolume(0f);
                easySound.setVolume(0f);
                belchSound1.setVolume(0f);
                belchSound2.setVolume(0f);
                belchSound3.setVolume(0f);
            } else {
                backgroundMusic.setVolume(1f);
                luserSound.setVolume(1f);
                easySound.setVolume(1f);
                belchSound1.setVolume(1f);
                belchSound2.setVolume(1f);
                belchSound3.setVolume(1f);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void unloadGameAudio() {
        backgroundMusic.release();
        luserSound.release();
        easySound.release();
        belchSound1.release();
        belchSound2.release();
        belchSound3.release();

        backgroundMusic = null;
        luserSound = null;
        easySound = null;
        belchSound1 = null;
        belchSound2 = null;
        belchSound3 = null;
    }



    public void loadSplashScreen() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        loadFonts();
        splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
        splashTextureAtlas.load();
    }

    public void unloadCurrentScene(BaseScene scene) {
        switch (scene.getSceneType()) {
            case SCENE_MENU:
                this.unloadMenuTextures();
                break;
            case SCENE_GAME:
                this.unloadGameTextures();
                break;
            case SCENE_HIGHSCORE:
                this.unloadGameTextures();
                this.unloadHighscoreGraphics();
                this.unloadGameAudio();
                break;
            case SCENE_LEVELS:
                this.unloadLevelTextures();
                break;
            case SCENE_SHOP:
                this.unloadShopGraphics();
                break;
            case SCENE_SPLASH:
                this.unloadSplashScreen();
                break;
            case SCENE_MULTI:
                this.unloadGameTextures();
                this.unloadWorldGraphics(0);
                this.unloadMultiGraphics();
                this.unloadGameAudio();
                break;
            case SCENE_WORLD0:
                this.unloadGameTextures();
                this.unloadWorldGraphics(0);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD1:
                this.unloadGameTextures();
                this.unloadWorldGraphics(1);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD2:
                this.unloadGameTextures();
                this.unloadWorldGraphics(2);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD3:
                this.unloadGameTextures();
                this.unloadWorldGraphics(3);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD4:
                this.unloadGameTextures();
                this.unloadWorldGraphics(4);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD5:
                this.unloadGameTextures();
                this.unloadWorldGraphics(5);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD6:
                this.unloadGameTextures();
                this.unloadWorldGraphics(6);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD7:
                this.unloadGameTextures();
                this.unloadWorldGraphics(7);
                this.unloadGameAudio();
                break;
            case SCENE_WORLD8:
                this.unloadGameTextures();
                this.unloadWorldGraphics(8);
                this.unloadGameAudio();
                break;
        }
    }

    public void unloadSplashScreen() {
        splashTextureAtlas.unload();
        splash_region = null;
    }

    public void unloadMenuTextures() {
        menuTextureAtlas.unload();
    }

    public void unloadGameTextures() {
        gameTextureAtlas.unload();
    }

    public void unloadLevelTextures() {
        chooseLevelTextureAtlas.unload();
        //unloadMenuFonts();
    }

    public void loadMenuTextures() {
        menuTextureAtlas.load();
    }


    public static void prepareManager(Engine engine, EngineOptions engineOptions, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom) {
        getInstance().engine = engine;
        getInstance().engineOptions = engineOptions;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
        getInstance().screenWidth = (int) camera.getWidth();
        getInstance().screenHeight = (int) camera.getHeight();
        getInstance().sideLength = (int) camera.getHeight()/9;
        getInstance().screenRatio = camera.getWidth()/camera.getHeight();
        getInstance().prepared = true;
    }

    public static ResourcesManager getInstance() {
        return INSTANCE;
    }

}
