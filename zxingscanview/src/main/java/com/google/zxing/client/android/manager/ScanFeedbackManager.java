package com.google.zxing.client.android.manager;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.View;

import com.google.zxing.client.android.compat.Compat;

/**
 * 扫描反馈管理器
 * Created by Alex on 2016/11/28.
 */
@SuppressWarnings("all")
public class ScanFeedbackManager implements MediaPlayer.OnCompletionListener {

    public static final int MODE_AUTO = 0;//自动模式
    public static final int MODE_AUDIO_ONLY = 1;//铃声
    public static final int MODE_VIBRATOR_ONLY = 2;//震动（需要权限）
    public static final int MODE_AUDIO_VIBRATOR = 3;//铃声与震动
    public static final int DEFAUT_MILLISECONDS = 200;
    private final MediaPlayer player = new MediaPlayer();
    private final AudioManager audioManager;
    private final Vibrator vibrator;
    private final Context context;
    private String assetsFileName;
    private int rawId = View.NO_ID;
    private long mVibrateMilliseconds;
    private boolean isAudio = false;
    private boolean isVibrator = false;


    public ScanFeedbackManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        player.setOnCompletionListener(this);
        setVibrateMilliseconds(DEFAUT_MILLISECONDS);
    }

    /**
     * 设置音频Assets文件名
     *
     * @param fileName 文件名
     */
    public void setAudioAssetsFileName(String fileName) {
        if (fileName == null || fileName.length() <= 0)
            return;
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
     * 设置反馈模式
     *
     * @param mode 反馈模式
     */
    public void setMode(int mode) {
        switch (mode) {
            case MODE_AUDIO_VIBRATOR:
                isAudio = true;
                isVibrator = true;
                break;
            case MODE_AUDIO_ONLY:
                isAudio = true;
                isVibrator = false;
                break;
            case MODE_VIBRATOR_ONLY:
                isAudio = false;
                isVibrator = true;
                break;
            default:
            case MODE_AUTO:
                isAudio = false;
                isVibrator = false;
                break;
        }
    }

    /**
     * 执行扫描反馈
     */
    public void performScanFeedback() {
        if (isAudio && isVibrator) {
            playSoundEffect();
            playVibrateEffect();
        } else if (isAudio) {
            playSoundEffect();
        } else if (isVibrator) {
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
        if (Compat.checkSelfPermission(context, Manifest.permission.VIBRATE)
                == PackageManager.PERMISSION_DENIED)
            return;
        if (vibrator != null && vibrator.hasVibrator())
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
        if (rawId != View.NO_ID) {
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
