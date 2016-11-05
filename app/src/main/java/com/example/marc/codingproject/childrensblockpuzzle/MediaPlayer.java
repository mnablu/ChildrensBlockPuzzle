package com.example.marc.codingproject.childrensblockpuzzle;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import java.io.FileDescriptor;

public class MediaPlayer {
    public MediaPlayer() {

    }

    enum SoundType {
        ANIMAL("bark.mp3"),
        VEHICLE("car.mp3"),
        FLOWER("storm.mp3");

        private final String fileName;

        SoundType(String fileName) {
            this.fileName = fileName;
        }
    }

    private MediaPlayer player = null;
    private Context mContext;

    public MediaPlayer(Context context) {
        player = new MediaPlayer();
        this.mContext = context;
    }

    public void playSound(SoundType type) {
        try {
            AssetFileDescriptor afd = mContext.getAssets().openFd(type.fileName);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.start();
    }

    private void start() {
    }

    private void prepare() {
    }

    private void setDataSource(FileDescriptor fileDescriptor, long startOffset, long length) {
    }

    public void stop() {
        if (player.isPlaying()) {
            player.stop();
        }
    }

    private boolean isPlaying() {
        return isPlaying();
    }

}
