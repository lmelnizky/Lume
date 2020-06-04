package org.lume;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

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
    private static final String IS_NAME_ONLINE = "IS_NAME_ONLINE"; //boolean
    private static final String WANTS_ONLINE = "WANTS_ONLINE"; //boolean
    private static final String NAME_ONLINE = "NAME_ONLINE"; //TODO String online name
    private static final String ID_ONLINE = "ID_ONLINE"; //TODO String online name
    private static final String COINS = "COINS";
    private static final String HIGHSCORE = "HIGHSCORE";
    private static final String PLAYER = "PLAYER";
    private static final String LAMPORGHINA_UNLOCKED = "LAMPORGHINA_UNLOCKED";
    private static final String GRUME_UNLOCKED = "GRUME_UNLOCKED";
    private static final String IS_SLOWMO = "IS_SLOWMO";

    private static final int CAMERA_REQUEST = 1888;
    private static final int WRITE_REQUEST = 1988;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PIC_CROP = 200;
    private ImageView photoImageView;
    private Button buttonCrop, photoButton;
    private ImageButton biggerButton, smallerButton, okButton;
    Bitmap photo, icon;
    String mCurrentPhotoPath;
    private boolean imageViewInitialised = false;
    private int fingersUsed, firstFingerX, firstFingerY, secondFingerX, secondFingerY, secondFingerXFinal, secondFingerYFinal;
    private int posX, posY, diameter;
    private int bmpWidth, bmpHeight, viewWidth, viewHeight, wFactor, hFactor;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int screenHeight = displayMetrics.heightPixels;
    private int screenWidth = displayMetrics.widthPixels;

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
        switch (player) {
            case 0:
                //Lume is always unlocked
                break;
            case 1:
                editor.putBoolean(LAMPORGHINA_UNLOCKED, true);
                editor.commit();
                break;
            case 2:
                editor.putBoolean(GRUME_UNLOCKED, true);
                editor.commit();
                break;
        }
        editor.putInt(PLAYER, player);
        editor.commit();
    }

    public boolean isPlayerUnlocked(int player) {
        boolean isUnlocked = false;
        switch (player) {
            case 0:
                isUnlocked = true;
                break;
            case 1:
                isUnlocked = pref.getBoolean(LAMPORGHINA_UNLOCKED, false);
                 break;
            case 2:
                isUnlocked = pref.getBoolean(GRUME_UNLOCKED, false);
                break;
        }
        return isUnlocked;
    }

    public boolean isLamporghinaUnlocked() {
        return pref.getBoolean(LAMPORGHINA_UNLOCKED, false);
    }

    public boolean isGrumeUnlocked() {
        return pref.getBoolean(GRUME_UNLOCKED, false);
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

        //TODO THIS IS TEST
        setContentView(R.layout.activity_main);
        this.photoImageView = (ImageView)this.findViewById(R.id.imageView1);
        buttonCrop = findViewById(R.id.button2);
        buttonCrop.setVisibility(View.GONE);
        photoButton = (Button) this.findViewById(R.id.button1);
        biggerButton = findViewById(R.id.bigger);
        biggerButton.setVisibility(View.GONE);
//        biggerButton.getLayoutParams().height = 20;
//        biggerButton.getLayoutParams().width = 20;
        smallerButton = findViewById(R.id.smaller);
        smallerButton.setVisibility(View.GONE);
        okButton = findViewById(R.id.ok);
        okButton.setVisibility(View.GONE);
//        smallerButton.getLayoutParams().height = photoButton.getHeight();
//        smallerButton.getLayoutParams().width = photoButton.getHeight();
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    } else {
                        //ask for permission
                        if (Build.VERSION.SDK_INT >= 23) {
                            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            if (!hasPermissions(GameActivity.this.getApplicationContext(), PERMISSIONS)) {
                                ActivityCompat.requestPermissions(GameActivity.this, PERMISSIONS, WRITE_REQUEST);
                            } else {
                                toastOnUiThread("writing granted", 0);
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (photoFile != null) {
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                            }
                        } else {
                            toastOnUiThread("Permission denied", 0);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                //ask for permission
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), PERMISSIONS, WRITE_REQUEST);
                    } else {
                        toastOnUiThread("Permission denied", 0);
                    }
                } else {
                    toastOnUiThread("Permission denied", 0);
                }

                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == WRITE_REQUEST) {
            Toast.makeText(this, "writing granted", Toast.LENGTH_SHORT).show();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                photo = null;
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                photoImageView.setImageBitmap(photo);
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                //Bitmap circlePhoto = this.getCroppedBitmap(photo);
                //Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        //R.drawable.lume_scheme);
                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(icon, 120, 120, false);
                //Bitmap overlayedPhoto = this.overlay(circlePhoto, scaledBitmap);


                File fdelete = new File(mCurrentPhotoPath);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + mCurrentPhotoPath);
                    } else {
                        System.out.println("file not Deleted :" + mCurrentPhotoPath);
                    }
                }

                photoButton.setText(R.string.fat);
                biggerButton.getLayoutParams().height = photoButton.getHeight();
                biggerButton.getLayoutParams().width = photoButton.getHeight();
                biggerButton.requestLayout();
                smallerButton.getLayoutParams().height = photoButton.getHeight();
                smallerButton.getLayoutParams().width = photoButton.getHeight();
                smallerButton.requestLayout();


                icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.lume_scheme);
                photoImageView.setImageBitmap(icon);
                Bitmap overlayedPhoto = this.overlay(photo, icon);
                photoImageView.setImageBitmap(overlayedPhoto);
                viewWidth = photoImageView.getWidth();
                viewHeight = photoImageView.getHeight();
                GameActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                screenHeight = displayMetrics.heightPixels;
                screenWidth = displayMetrics.widthPixels;
                //toastOnUiThread("Screenw and Screenh: " + screenWidth + ", " + screenHeight, 0);
                wFactor = bmpWidth/viewWidth;
                hFactor = bmpHeight/viewHeight;
                photoImageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int trueViewWidth = bmpWidth/hFactor; //scaled because imageView is on complete width
                        int deadSpaceX = (screenWidth-trueViewWidth)/2; //this is the imageView space on left side
                        int deadSpaceY = (screenHeight-trueViewWidth)/2; //this is the imageView space on left side
                        if (event.getAction() == MotionEvent.ACTION_DOWN){
                            if (fingersUsed < 2) fingersUsed++;
                            //toastOnUiThread("OnActionDown, " + fingersUsed, 0);
                            if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) toastOnUiThread("Action_Pointer_Down", 1);
                            if (fingersUsed == 1) firstFingerX = (int) (event.getX()-deadSpaceX)*hFactor;
                            if (fingersUsed == 1) firstFingerY = (int) (event.getY())*hFactor;
                            if (fingersUsed == 2) secondFingerX = (int) (event.getX()-deadSpaceX)*hFactor;
                            if (fingersUsed == 2) secondFingerY = (int) (event.getY())*hFactor;
                            return true;
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            fingersUsed--;
                            if (fingersUsed < 0) fingersUsed = 0;
                            //toastOnUiThread("OnActionUp, " + fingersUsed, 0);
                            if (fingersUsed == 0) { //set position
                                int x, y;
                                x = (int)(event.getX()-deadSpaceX)*hFactor;
                                y = (int) event.getY()*hFactor;
                                Bitmap newBitmap = overlay(photo, icon, x, y, diameter);
                                photoImageView.setImageBitmap(newBitmap);
                            } else if (fingersUsed == 1) { //set size
                                secondFingerXFinal = (int) (event.getX()-deadSpaceX)*hFactor;
                                secondFingerYFinal = (int) (event.getY())*hFactor;
                                int diffX1 = Math.abs(secondFingerX-firstFingerX);
                                int diffY1 = Math.abs(secondFingerY - firstFingerY);
                                int diffX2 = Math.abs(secondFingerXFinal-secondFingerX);
                                int diffY2 = Math.abs(secondFingerYFinal-secondFingerY);
                                int diffX = diffX2-diffX1;
                                int diffY = diffY2-diffY1;
                                diameter += Math.max(diffX, diffY);
                                Bitmap newBitmap = overlay(photo, icon, posX, posY, diameter);
                                photoImageView.setImageBitmap(newBitmap);
                            }

                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            int x, y;
                            x = (int)(event.getX()-deadSpaceX)*hFactor;
                            y = (int) event.getY()*hFactor;
                            Bitmap newBitmap = overlay(photo, icon, x, y, diameter);
                            photoImageView.setImageBitmap(newBitmap);
//                        } else if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
//                            //toastOnUiThread("ActionPointerDown", 0);
//                            secondFingerX = (int) (event.getX()-deadSpaceX)*hFactor;
//                            secondFingerY = (int) (event.getY())*hFactor;
//                        } else if (event.getAction() == MotionEvent.ACTION_POINTER_UP) {
//                            //toastOnUiThread("ActionPointerUp", 0);
//                            secondFingerXFinal = (int) (event.getX()-deadSpaceX)*hFactor;
//                            secondFingerYFinal = (int) (event.getY())*hFactor;
//                            int diffX1 = Math.abs(secondFingerX-firstFingerX);
//                            int diffY1 = Math.abs(secondFingerY - firstFingerY);
//                            int diffX2 = Math.abs(secondFingerXFinal-secondFingerX);
//                            int diffY2 = Math.abs(secondFingerYFinal-secondFingerY);
//                            int diffX = diffX2-diffX1;
//                            int diffY = diffY2-diffY1;
//                            diameter += Math.max(diffX, diffY);
//                            Bitmap newBitmap = overlay(photo, icon, posX, posY, diameter);
//                            photoImageView.setImageBitmap(newBitmap);
                        }
                        return false;
                    }
                });
                imageViewInitialised = true;
                buttonCrop.setVisibility(View.VISIBLE);
                biggerButton.setVisibility(View.VISIBLE);
                smallerButton.setVisibility(View.VISIBLE);
                buttonCrop.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        toastOnUiThread("Cropping", 0);
                        //cropping
                        Bitmap cropBitmap = getCroppedBitmap(photo, icon, posX, posY, diameter);
                        photoImageView.setImageBitmap(cropBitmap);

                        okButton.setVisibility(View.VISIBLE);
                        okButton.getLayoutParams().height = photoButton.getHeight();
                        okButton.getLayoutParams().width = photoButton.getHeight();
                        okButton.requestLayout();
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                diameter += 30;
//                                Bitmap cropBitmap = overlay(photo, icon, posX, posY, diameter);
//                                photoImageView.setImageBitmap(cropBitmap);
                            }
                        });
                    }
                });
                biggerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        diameter += 30;
                        Bitmap cropBitmap = overlay(photo, icon, posX, posY, diameter);
                        photoImageView.setImageBitmap(cropBitmap);
                    }
                });
                smallerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        diameter -= 30;
                        Bitmap cropBitmap = overlay(photo, icon, posX, posY, diameter);
                        photoImageView.setImageBitmap(cropBitmap);
                    }
                });
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imageViewInitialised) {

        }
        return super.onTouchEvent(event);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public File createImageFile() throws IOException {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "NAME_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public Bitmap getCroppedBitmap(Bitmap photo, Bitmap scheme, int x, int y, int diameter) {
        Bitmap output = Bitmap.createBitmap(photo.getWidth(),
                photo.getHeight(), photo.getConfig());
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, photo.getWidth(), photo.getHeight());
        final Rect rectScheme = new Rect(x-diameter/2, y-diameter/2, x+diameter/2,
                y+diameter/2);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(x, y,
                diameter/2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(photo, null, rect, paint);
        canvas.drawBitmap(scheme, null, rectScheme, null);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        int width = bmp1.getWidth()/4;
//        toastOnUiThread("bmp1 w, h: " + bmp1.getWidth() + ", " + bmp1.getHeight() +
//                "bmp2 w, h: " + bmp2.getWidth() + ", " + bmp2.getHeight() +
//                "bmp2 w, h: " + photoImageView.getWidth() + ", " + photoImageView.getHeight());
        Rect src = new Rect(0, 0, bmp1.getWidth(), bmp1.getHeight());
        Rect dest = new Rect(bmp1.getWidth()/2-width/2, bmp1.getHeight()/2-width/2,
                bmp1.getWidth()/2+width/2, bmp1.getHeight()/2+width/2);
        this.posX = bmp1.getWidth()/2;
        this.posY = bmp1.getHeight()/2;
        this.bmpWidth = bmp1.getWidth();
        this.bmpHeight = bmp1.getHeight();
        this.diameter = width;
        canvas.drawBitmap(bmp2, null, dest, null);
        return bmOverlay;
    }

    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2, int x, int y, int diameter) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        int width = bmp1.getWidth()/4;
//        toastOnUiThread("bmp1 w, h: " + bmp1.getWidth() + ", " + bmp1.getHeight() +
//                "bmp2 w, h: " + bmp2.getWidth() + ", " + bmp2.getHeight() +
//                "bmp2 w, h: " + photoImageView.getWidth() + ", " + photoImageView.getHeight());
        Rect src = new Rect(0, 0, bmp1.getWidth(), bmp1.getHeight());
        Rect dest = new Rect(x-diameter/2, y-diameter/2,
                x+diameter/2, y+diameter/2);
        this.posX = x;
        this.posY = y;
        this.diameter = diameter;
        //bmp2 = setBitmapPosition(bmp2, bmp1.getWidth()/2, bmp1.getHeight()/2);
        canvas.drawBitmap(bmp2, null, dest, null);
        return bmOverlay;
    }

    public Bitmap setBitmapSize(Bitmap bitmap, int width) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        Rect dest = new Rect(2, 2, 2+width, 2+width);
        canvas.drawBitmap(bitmap, null, dest, null);
        return newBitmap;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
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
            //adView.loadAd(adRequest);
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
                        SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().onlineGameScene ||
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
                        if (!isPlayerUnlocked(player)) addBeersos(-coins);
                        setPlayer(player);
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
