package am.project.x.alarm;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

/**
 * 铃声管理器
 * Created by Alex on 2015/12/8.
 */
public class AMAudioManager {

    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private MediaPlayer player = new MediaPlayer();
    ;
    private AudioManager audioManager;
    private int streamType;


    public AMAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private float getVolume(int streamType) {
        int maxVolume = audioManager.getStreamMaxVolume(streamType);
        int curVolume = audioManager.getStreamVolume(streamType);
        return curVolume * 1f / maxVolume;
    }

    public void ring(Context context) {
        try {
            player.reset();
            Uri alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            player.setDataSource(context, alert);
            final Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    vibrator.vibrate(500);
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                        // 系统通知没有想起进行响铃操作
                        streamType = AudioManager.STREAM_NOTIFICATION;
                        player.setAudioStreamType(streamType);
                        player.setVolume(getVolume(streamType),
                                getVolume(streamType));
                        player.setLooping(false);
                        player.prepare();
                        player.start();
                        vibrator.vibrate(500);
                    }
                    break;
            }
        } catch (Exception e) {
            //LogUtils.e("TastyAudioManager", e.getMessage());
        }
    }

    public void setAlarm(Context context) {
        try {
            player.reset();
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd("mazing_sound_long.ogg");
            player.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            streamType = AudioManager.STREAM_RING;
            player.setAudioStreamType(streamType);
            player.setVolume(getVolume(streamType),
                    getVolume(streamType));
            player.setLooping(false);
            player.prepare();
        } catch (Exception e) {
            //LogUtils.e("TastyAudioManager", e.getMessage());
        }
    }

    public void startAlarm() {
        try {
            if (!player.isPlaying())
                player.start();
        } catch (Exception e) {
            //LogUtils.e("TastyAudioManager", e.getMessage());
        }
    }

    public void pauseAlarm() {
        try {
            if (player.isPlaying())
                player.pause();
        } catch (Exception e) {
            //LogUtils.e("TastyAudioManager", e.getMessage());
        }
    }

    public void release() {
        try {
            player.stop();
            player.release();
        } catch (Exception e) {
            //LogUtils.e("TastyAudioManager", e.getMessage());
        }
    }

    public void updateVolume() {
        try {
            player.setVolume(getVolume(streamType),
                    getVolume(streamType));
        } catch (Exception e) {
            //LogUtils.e("TastyAudioManager", e.getMessage());
        }
    }
}
