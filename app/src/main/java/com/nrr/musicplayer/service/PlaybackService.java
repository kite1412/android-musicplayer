package com.nrr.musicplayer.service;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.nrr.musicplayer.util.Log;

public class PlaybackService extends MediaSessionService {
    private MediaSession session = null;
    private Handler playbackPositionHandler;
    private long playbackPosition = 0;
    private final int LISTEN_PLAYBACK_POSITION_CODE = 1;
    private final long LISTEN_PLAYBACK_POSITION_INTERVAL = 1000L;
    public long getPlaybackPosition() { return playbackPosition; }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return session;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        session = new MediaSession.Builder(this, player).build();
        playbackPositionHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == LISTEN_PLAYBACK_POSITION_CODE) {
                    listenPlaybackCurrentPosition();
                    playbackPositionHandler.sendEmptyMessageDelayed(
                            LISTEN_PLAYBACK_POSITION_CODE,
                            LISTEN_PLAYBACK_POSITION_INTERVAL
                    );
                }
            }
        };
        playbackPositionHandler.sendEmptyMessage(LISTEN_PLAYBACK_POSITION_CODE);
    }

    private void listenPlaybackCurrentPosition() {
        playbackPosition = session.getPlayer().getCurrentPosition();
//        Log.INSTANCE.d(String.valueOf(playbackPosition));
    }

    @Override
    public void onDestroy() {
        session.getPlayer().release();
        session.release();
        session = null;
        super.onDestroy();
    }
}
