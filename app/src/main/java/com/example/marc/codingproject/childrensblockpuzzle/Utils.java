package com.example.marc.codingproject.childrensblockpuzzle;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class Utils {
    private Utils() {
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }





}
