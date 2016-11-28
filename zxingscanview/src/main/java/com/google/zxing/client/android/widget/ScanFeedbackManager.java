package com.google.zxing.client.android.widget;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.google.zxing.client.android.util.Utils;

/**
 * 扫描反馈管理器
 * Created by Alex on 2016/11/28.
 */
@SuppressWarnings("all")
public class ScanFeedbackManager implements MediaPlayer.OnCompletionListener {


    private static final long TIME = 200;
    private final MediaPlayer player = new MediaPlayer();
    private final AudioManager audioManager;
    private final Vibrator vibrator;
    private final Context context;
    private String assetsFileName;
    private int rawId;
    private long mVibrateMilliseconds;
    private boolean forceAudio = false;
    private boolean forceVibrator = false;


    public ScanFeedbackManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        player.setOnCompletionListener(this);
        setVibrateMilliseconds(TIME);
    }

    /**
     * 设置音频Assets文件名
     *
     * @param fileName 文件名
     */
    public void setAudioAssetsFileName(String fileName) {
        assetsFileName = fileName;
    }

    /**
     * 设置音频资源ID
     *
     * @param id 资源ID
     */
    public void setAudioRawId(int id) {
        rawId = id;
    }

    /**
     * 设置震动时长
     *
     * @param milliseconds 时长
     */
    public void setVibrateMilliseconds(long milliseconds) {
        mVibrateMilliseconds = milliseconds;
    }

    /**
     * 强制响铃
     *
     * @param force 强制
     */
    public void setForceAudio(boolean force) {
        forceAudio = force;
    }

    /**
     * 强制震动
     *
     * @param force 强制
     */
    public void setForceVibrator(boolean force) {
        forceVibrator = force;
    }

    /**
     * 执行扫描反馈
     */
    public void performScanFeedback() {
        if (forceAudio && forceVibrator) {
            playSoundEffect();
            playVibrateEffect();
        } else if (forceAudio) {
            playSoundEffect();
        } else if (forceVibrator) {
            playVibrateEffect();
        } else {
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    playVibrateEffect();
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                        playSoundEffect();
                    }
                    playVibrateEffect();
                    break;
            }
        }
    }

    private void playSoundEffect() {
        AssetFileDescriptor fileDescriptor = getDataSource();
        try {
            player.reset();
            if (fileDescriptor != null) {
                player.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
            } else {
                Uri alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                player.setDataSource(context, alert);
            }
            player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            player.setVolume(getVolume(AudioManager.STREAM_NOTIFICATION),
                    getVolume(AudioManager.STREAM_NOTIFICATION));
            player.setLooping(false);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileDescriptor != null)
                    fileDescriptor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void playVibrateEffect() {
        if (!Utils.lacksPermission(context, Utils.PERMISSION_VIBRATE) && vibrator.hasVibrator())
            vibrator.vibrate(mVibrateMilliseconds);
    }

    private AssetFileDescriptor getDataSource() {
        if (assetsFileName != null) {
            try {
                return context.getAssets().openFd(assetsFileName);
            } catch (Exception e) {
                return null;
            }
        }
        if (rawId != 0) {
            try {
                return context.getResources().openRawResourceFd(rawId);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private float getVolume(int streamType) {
        int maxVolume = audioManager.getStreamMaxVolume(streamType);
        int curVolume = audioManager.getStreamVolume(streamType);
        return curVolume * 1f / maxVolume;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
    }

    /**
     * 销毁
     */
    public void release() {
        if (player.isPlaying())
            player.stop();
        player.release();
    }
}
