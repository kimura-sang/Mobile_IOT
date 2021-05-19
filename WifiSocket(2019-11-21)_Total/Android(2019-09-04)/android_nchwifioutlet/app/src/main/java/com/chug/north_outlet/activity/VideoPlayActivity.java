package com.chug.north_outlet.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.chug.north_outlet.R;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView videoInstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        videoInstruction = (VideoView) findViewById(R.id.video_instruction);
        playVideo();
    }

    private void playVideo(){
        videoInstruction.setVisibility(View.VISIBLE);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoInstruction);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video1);
        //Starting VideView By Setting MediaController and URI
        videoInstruction.setMediaController(mediaController);
        videoInstruction.setVideoURI(uri);
        videoInstruction.requestFocus();
        videoInstruction.start();

        videoInstruction.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoInstruction.setVisibility(View.GONE);
                finish();
            }
        });
    }
}
