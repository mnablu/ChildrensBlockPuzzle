package com.example.marc.codingproject.childrensblockpuzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.marc.codingproject.childrensblockpuzzle.db.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBoardActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_THEME = "exGameTheme";
    public static final int ANIMALS_THEME = 0;
    public static final int VEHICLES_THEME = 1;
    public static final int FLOWERS_THEME = 2;

    //the imageViews array {preview, topLeft, topRight, bottomLeft, bottomRight}
    private ImageView[] imageViews = {};

    //the nested bitmaps array in the same order as the ImageViews array
    private List<List<Bitmap>> bitmaps = new ArrayList<>(5);

    //array the controls which image is currently being displayed
    private int[] currentLoadedImage = new int[5];

    private WorkerThread worker;
    private DatabaseHelper database;

    private Button startButton;
    private MediaPlayer player;
    private int gameMode;
    private int numberTouches = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //initialize database
        database = new DatabaseHelper(this);
        startButton = (Button) findViewById(R.id.next_button);
        player = new MediaPlayer(this);

        this.imageViews = new ImageView[]{
                (ImageView) findViewById(R.id.preview),
                (ImageView) findViewById(R.id.topLeft),
                (ImageView) findViewById(R.id.topRight),
                (ImageView) findViewById(R.id.bottomLeft),
                (ImageView) findViewById(R.id.bottomRight)};

        gameMode = getIntent().getIntExtra(EXTRA_GAME_THEME, -1);
        if (gameMode != -1) {
            int length = 0;
            int[] drawables = new int[0];
            switch (gameMode) {
                case ANIMALS_THEME:
                    length = Constant.ANIMAL_ICONS.length;
                    drawables = Constant.ANIMAL_ICONS;
                    break;
                case VEHICLES_THEME:
                    length = Constant.VEHICLE_ICONS.length;
                    drawables = Constant.VEHICLE_ICONS;
                    break;
                case FLOWERS_THEME:
                    length = Constant.FLOWER_ICONS.length;
                    drawables = Constant.FLOWER_ICONS;
                    break;
            }

            worker = new WorkerThread(this);
            worker.start();
            worker.totalImages = length;
            for (int drawable : drawables) {
                worker.loadResource(drawable, new Handler());
            }
        }
    }

    public void next(View view) {
        for (ImageView imageView : this.imageViews) {
            imageView.setColorFilter(Color.argb(0, 0, 0, 0));
            imageView.setEnabled(true);
        }
        startButton.setEnabled(false);
        nextPuzzles();
    }

    public void nextImage(View view) {
        numberTouches++;
        switch (view.getId()) {
            case R.id.topLeft:
                nextImage(1);
                break;
            case R.id.topRight:
                nextImage(2);
                break;
            case R.id.bottomLeft:
                nextImage(3);
                break;
            case R.id.bottomRight:
                nextImage(4);
                break;
        }

        if (isComplete()) {
            for (ImageView imageView : imageViews) {
                imageView.setColorFilter(Color.argb(100, 0, 255, 0));
                imageView.setEnabled(false);
                startButton.setEnabled(true);
                switch (gameMode) {
                    case ANIMALS_THEME:
                        player.playSound(MediaPlayer.SoundType.ANIMAL);
                        break;
                    case VEHICLES_THEME:
                        player.playSound(MediaPlayer.SoundType.VEHICLE);
                        break;
                    case FLOWERS_THEME:
                        player.playSound(MediaPlayer.SoundType.FLOWER);
                        break;
                }
            }

            database.increaseTouch(((GlideBitmapDrawable) imageViews[0].getDrawable()).getBitmap(), numberTouches);
        }
}

    protected void addBitmap(int position, Bitmap bitmap) {
        ArrayList<Bitmap> list = new ArrayList<>();
        bitmaps.add(list);
        bitmaps.get(position).add(bitmap);
    }

    private void nextPuzzles() {
        numberTouches = 0;
        Random random = new Random();
        int size = bitmaps.get(0).size();
        int[] positions = {random.nextInt(size), random.nextInt(size), random.nextInt(size), random.nextInt(size), random.nextInt(size)};
        currentLoadedImage = positions;

        for (int i = 0; i < imageViews.length; i++) {
            Glide.with(GameBoardActivity.this)
                    .load(Utils.bitmapToByte(bitmaps.get(i).get(currentLoadedImage[i])))
                    .into(imageViews[i]);
        }
    }

    private void nextImage(int id) {
        if (currentLoadedImage[id] + 1 < bitmaps.get(id).size()) {
            currentLoadedImage[id]++;
        } else {
            currentLoadedImage[id] = 0;
        }

        Glide.with(GameBoardActivity.this)
                .load(Utils.bitmapToByte(bitmaps.get(id).get(currentLoadedImage[id])))
                .into(imageViews[id]);
    }

    private boolean isComplete() {
        if (currentLoadedImage[1] == currentLoadedImage[2] && currentLoadedImage[1] == currentLoadedImage[3]
                && currentLoadedImage[1] == currentLoadedImage[4] && currentLoadedImage[1] == currentLoadedImage[0]) {

            return true;
        }
        return false;
    }

    private class WorkerThread extends Thread {

        private Handler handler;
        private Context context;
        private Looper looper;
        public int totalImages = 0;
        private int loaded = 0;

        public WorkerThread(Context context) {
            this.context = context;
            looper = Looper.myLooper();
            handler = new Handler(looper);
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize += 2;
                }
            }
            return inSampleSize;
        }

        public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }

        @Override
        public void run() {
            Looper.prepare();
            Looper.loop();
        }

        public void loadResource(final int id, final Handler runner) {
            final boolean post = handler.post(new Runnable() {
                @Override
                public void run() {
                    final Bitmap original = decodeSampledBitmapFromResource(context.getResources(), id, 300, 300);
                    addBitmap(0, decodeSampledBitmapFromResource(context.getResources(), id, 50, 60));
                    addBitmap(1, Bitmap.createBitmap(original, 0, 0, original.getWidth() / 2, original.getHeight() / 2));
                    addBitmap(2, Bitmap.createBitmap(original, original.getWidth() / 2, 0, original.getWidth() / 2, original.getHeight() / 2));
                    addBitmap(3, Bitmap.createBitmap(original, 0, original.getHeight() / 2, original.getWidth() / 2, original.getHeight() / 2));
                    addBitmap(4, Bitmap.createBitmap(original, original.getWidth() / 2, original.getHeight() / 2, original.getWidth() / 2, original.getHeight() / 2));
                    loaded++;

                    runner.post(new Runnable() {
                        @Override
                        public void run() {
                            if (loaded == totalImages) {
                                //imageViewController.setInitialBitmaps();
                                nextPuzzles();
                            }
                        }
                    });
                }
            });
        }
    }
}
