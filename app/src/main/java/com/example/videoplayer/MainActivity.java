package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * Ejemplo de reproductor multimedia con MediaPlayer y controles
 * de reproducción personalizados
 * @author Rafa
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private EditText editText;
    private TextView logTextView;
    private ImageButton ibPlay, ibStop, ibPause, ibLog;
    private MediaPlayer mediaPlayer;
    private boolean pause,stop;
    private String path;
    private int savePos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        editText = findViewById(R.id.path);
        editText.setText("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");
        //editText.setText("android.resource://"+
        //        getPackageName() + "/raw/"+R.raw.calamardo);
        logTextView = findViewById(R.id.Log);
        ibPlay = findViewById(R.id.play);
        ibPlay.setEnabled(false);
        ibPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ibPlay.setEnabled(false);
                if (mediaPlayer != null) {
                    if (pause) {
                        pause = false;
                        mediaPlayer.start();
                    } else {
                        playVideo();
                    }
                }
            }
        });
        ibPause = findViewById(R.id.pause);
        ibPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mediaPlayer != null && !stop && !pause) {
                    pause = true;
                    mediaPlayer.pause();
                    ibPlay.setEnabled(true);
                }
            }
        });
        ibStop = findViewById(R.id.stop);
        ibStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mediaPlayer != null && !pause && !stop) {
                    pause = false;
                    stop = true;
                    ibPlay.setEnabled(true);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            }
        });
        ibLog = findViewById(R.id.logButton);
        ibLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (logTextView.getVisibility() == TextView.VISIBLE) {
                    logTextView.setVisibility(TextView.INVISIBLE);
                } else {
                    logTextView.setVisibility(TextView.VISIBLE);
                }
            }
        });
        log("");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && !pause && !stop) {
            mediaPlayer.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !pause && !stop) {
            mediaPlayer.start();
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mediaPlayer != null) {
            int pos = mediaPlayer.getCurrentPosition();
            outState.putString("ruta", path);
            outState.putInt("posicion", pos);
        }
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            path = savedInstanceState.getString("ruta");
            savePos = savedInstanceState.getInt("posicion");
        }
    }
    /**
     * Reproducción del vídeo
     */
    private void playVideo() {
        try {
            pause = false;
            stop = false;
            path = editText.getText().toString();
            mediaPlayer = new MediaPlayer();
            //vídeo en streaming
            mediaPlayer.setDataSource("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");

            //vídeo almacenado en la carpeta raw
            //mediaPlayer.setDataSource(this,
            //        Uri.parse("android.resource://"+
            //                getPackageName() + "/raw/"+R.raw.calamardo));
            mediaPlayer.setDisplay(surfaceHolder);
            //mediaPlayer.prepare();
            mediaPlayer.prepareAsync(); //Para streaming
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.seekTo(savePos);
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
        }
    }
    /**
     * Registras los mensajes de cambio de estado del reproductor
     * @param s Mensaje
     */
    private void log(String s) {
        logTextView.append(s + " |\t");
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        log("onBufferingUpdate percent:" + percent);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        log("onCompletion called");
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        log("onPrepared called");
        int mVideoWidth = mediaPlayer.getVideoWidth();
        int mVideoHeight = mediaPlayer.getVideoHeight();
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
            mediaPlayer.start();
        }
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        log("surfaceCreated called");
        playVideo();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        log("surfaceChanged called");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        log("surfaceDestroyed called");
    }
}