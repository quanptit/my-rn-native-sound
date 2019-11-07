package com.my.rn.sound.RNSound;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Build;
import android.widget.Toast;

import com.baseLibs.BaseApplication;
import com.baseLibs.utils.BaseUtils;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

public class RNSoundModule extends ReactContextBaseJavaModule {
    Map<Integer, MyMediaPlayer> playerPool = new HashMap<>();
    ReactApplicationContext context;
    final static Object NULL = null;

    public RNSoundModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
    }

    @Override
    public String getName() {
        return "RNSound";
    }

    @ReactMethod
    public void prepare(final String fileName, final Integer key, final boolean isFromResourceDir, final Callback callback) {
        MyMediaPlayer player = new MyMediaPlayer();
        player.setStateChangeListener(new MyMediaPlayer.IStateChange() {
            @Override
            public void playerStateChange(int oldState, int state) {
                WritableMap writableMap = Arguments.createMap();
                writableMap.putString("state", MyMediaPlayer.getStateStr(state));
                writableMap.putString("oldState", MyMediaPlayer.getStateStr(oldState));
                BaseUtils.sendEvent(RNSoundModule.this.context, "PlayerStateChange", writableMap);
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                WritableMap props = Arguments.createMap();
                props.putDouble("duration", mp.getDuration() * 0.001);
                callback.invoke(NULL, props);
            }
        });
        try {
            if (isFromResourceDir) {
                AssetFileDescriptor descriptor = context.getAssets().openFd(fileName);
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
            } else
                player.setDataSource(fileName);
            this.playerPool.put(key, player);
            player.prepareAsync();
        } catch (Exception ex) {
            WritableMap e = Arguments.createMap();
            e.putInt("code", -1);
            e.putString("message", "resource not found: " + ex.getMessage());
            callback.invoke(e);
        }
    }

    @ReactMethod
    public void play(final Integer key, final Callback callback) {
        MyMediaPlayer player = this.playerPool.get(key);
        if (player == null) {
            callback.invoke(false);
            return;
        }
        if (player.isPlaying()) {
            return;
        }
        player.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!mp.isLooping()) {
                    callback.invoke(true);
                }
            }
        });
        player.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                callback.invoke(false);
                return true;
            }
        });
        player.start();
    }

    @ReactMethod
    public void pause(final Integer key) {
        MyMediaPlayer player = this.playerPool.get(key);
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    @ReactMethod
    public void stop(final Integer key) {
        MediaPlayer player = this.playerPool.get(key);
        if (player != null && player.isPlaying()) {
            player.pause();
            player.seekTo(0);
        }
    }

    @ReactMethod
    public void release(final Integer key, Callback callback) {
        MediaPlayer player = this.playerPool.get(key);
        if (player != null) {
            player.release();
            this.playerPool.remove(key);
        }
        callback.invoke(true);
    }

    @ReactMethod
    public void setVolume(final Integer key, final Float left, final Float right) {
        MediaPlayer player = this.playerPool.get(key);
        if (player != null) {
            player.setVolume(left, right);
        }
    }

    @ReactMethod
    public void setLooping(final Integer key, final Boolean looping) {
        MediaPlayer player = this.playerPool.get(key);
        if (player != null) {
            player.setLooping(looping);
        }
    }

    @ReactMethod
    public void setCurrentTime(final Integer key, final Float sec) {
        MediaPlayer player = this.playerPool.get(key);
        if (player != null) {
            player.seekTo((int) Math.round(sec * 1000));
        }
    }

    @ReactMethod
    public void getCurrentTime(final Integer key, final Callback callback) {
        MediaPlayer player = this.playerPool.get(key);
        if (player == null) {
            callback.invoke(true, NULL);
            return;
        }

        callback.invoke(NULL, player.getCurrentPosition() * .001);
    }

    @ReactMethod
    public void setSpeed(final Integer key, final Float value) {
        MediaPlayer player = this.playerPool.get(key);
        try {
            if (player != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    player.setPlaybackParams(player.getPlaybackParams().setSpeed(value));
                } else {
                    BaseApplication.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BaseApplication.getAppContext(), "Speed feature is only supported from Android 6.0 and above", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ReactMethod
    public void enable(final Boolean enabled) {
        // no op
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("IsAndroid", true);
        constants.put("IsEnableSpeed", Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        return constants;
    }

}
