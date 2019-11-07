package com.my.rn.sound.RNSound;

import android.media.MediaPlayer;

import java.io.IOException;

public class MyMediaPlayer extends MediaPlayer {
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_SUSPEND_UNSUPPORTED = 8;

    public static String getStateStr(int state) {
        switch (state) {
            case STATE_PREPARED:
            case STATE_PREPARING:
                return "buffering";
            case STATE_PLAYING:
                return "playing";
            case STATE_PAUSED:
                return "paused";
            case STATE_PLAYBACK_COMPLETED:
                return "complete";
            case STATE_SUSPEND_UNSUPPORTED:
                return "error";
            default:
                return "stopped";
        }
    }

    private OnCompletionListener onCompletionListener;
    private OnErrorListener onErrorListener;
    private OnPreparedListener onPreparedListener;
    private IStateChange iStateChange;
    private int mCurrentState = STATE_IDLE;
    private String currentPath;

    public MyMediaPlayer() {
        super();
        super.setOnCompletionListener(new OnCompletionListener() {
            @Override public void onCompletion(MediaPlayer mp) {
                try {
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    if (iStateChange != null) {
                        iStateChange.playerStateChange(mCurrentState, STATE_PLAYBACK_COMPLETED);
                    }
                    if (onCompletionListener != null)
                        onCompletionListener.onCompletion(mp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        super.setOnPreparedListener(new OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                try {
                    mCurrentState = STATE_PREPARED;
                    if (iStateChange != null) {
                        iStateChange.playerStateChange(mCurrentState, STATE_PREPARED);
                    }
                    if (onPreparedListener != null)
                        onPreparedListener.onPrepared(mp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        super.setOnErrorListener(new OnErrorListener() {
            @Override public boolean onError(MediaPlayer mp, int what, int extra) {
                try {
                    mCurrentState = STATE_SUSPEND_UNSUPPORTED;
                    if (iStateChange != null)
                        iStateChange.playerStateChange(mCurrentState, STATE_SUSPEND_UNSUPPORTED);
                    if (onErrorListener != null)
                        return onErrorListener.onError(mp, what, extra);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    public int getState() {return mCurrentState;}

    public String getCurrentPath() {return currentPath;}

    @Override public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();
        if (iStateChange != null)
            iStateChange.playerStateChange(mCurrentState, STATE_PREPARING);
        mCurrentState = STATE_PREPARING;
    }

    @Override public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        if (iStateChange != null)
            iStateChange.playerStateChange(mCurrentState, STATE_PREPARING);
        mCurrentState = STATE_PREPARING;
    }

    @Override public void reset() {
        super.reset();
        if (iStateChange != null)
            iStateChange.playerStateChange(mCurrentState, STATE_IDLE);
        mCurrentState = STATE_IDLE;
        currentPath = null;
    }

    @Override public void release() {
        super.release();
        if (iStateChange != null)
            iStateChange.playerStateChange(mCurrentState, STATE_IDLE);
        mCurrentState = STATE_IDLE;
        currentPath = null;
    }

    @Override public void pause() throws IllegalStateException {
        if (isInPlayState()) {
            super.pause();
            if (iStateChange != null)
                iStateChange.playerStateChange(mCurrentState, STATE_PAUSED);
            mCurrentState = STATE_PAUSED;
        }
    }

    @Override public void start() throws IllegalStateException {
        if (isInPlayState()) {
            super.start();
            if (iStateChange != null)
                iStateChange.playerStateChange(mCurrentState, STATE_PLAYING);
            mCurrentState = STATE_PLAYING;
        }
    }

    @Override public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        if (mCurrentState != STATE_SUSPEND_UNSUPPORTED && path.equals(currentPath)) {
            seekTo(0);
            return;
        }
        currentPath = path;
        super.setDataSource(path);
        if (iStateChange != null)
            iStateChange.playerStateChange(mCurrentState, STATE_IDLE);
        mCurrentState = STATE_IDLE;
    }

    public boolean isInPlayState() {
        return mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED
                || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PREPARED;
    }

    public void setStateChangeListener(IStateChange iStateChange) {
        this.iStateChange = iStateChange;
    }

    @Override public void setOnCompletionListener(OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    @Override public void setOnErrorListener(OnErrorListener listener) {
        this.onErrorListener = listener;
    }

    @Override public void setOnPreparedListener(OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    public interface IStateChange {
        void playerStateChange(int oldState, int state);
    }
}
