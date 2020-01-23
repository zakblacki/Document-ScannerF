package com.scanlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {
    private Utils() {
    }

    public static Uri getUri(Context context, Bitmap bitmap) {
        bitmap.compress(CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(Media.insertImage(context.getContentResolver(), bitmap, "Title", null));
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        return Media.getBitmap(context.getContentResolver(), uri);
    }

}
