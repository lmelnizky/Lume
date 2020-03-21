package org.andengine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoActivity extends YouTubeBaseActivity implements MediaPlayer.OnCompletionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);

        YouTubePlayerView view = (YouTubePlayerView) findViewById(R.id.videoView);
        String path = "http://www.youtube.com/watch?v=660u-mTgkg4";
        String videoId = "660u-mTgkg4";
        playVideo(videoId, view);
        //String path = "android.resource://" + getPackageName() + "/" + R.raw.walkthrough;

//        view.setVideoURI(Uri.parse(path));
//        view.setOnCompletionListener(this);
//        view.start();
    }

    public void playVideo(String videoId, YouTubePlayerView videoView) {
        //initialize youtube player view
        videoView.initialize("AIzaSyDiJ4exAy5iYT8Uzd8yY-uXhMYwATGZohw",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        finishActivity(0);
        finish();
    }
}
