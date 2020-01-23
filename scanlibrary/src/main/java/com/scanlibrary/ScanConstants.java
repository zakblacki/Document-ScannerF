package com.scanlibrary;

import android.os.Environment;


public class ScanConstants {
    public static final String IMAGE_BASE_PATH_EXTRA = "ImageBasePath";
    public static final String IMAGE_PATH;
    public static final int OPEN_CAMERA = 4;
    public static final String OPEN_INTENT_PREFERENCE = "selectContent";
    public static final int OPEN_MEDIA = 5;
    public static final int PICKFILE_REQUEST_CODE = 1;
    public static final String SCANNED_RESULT = "scannedResult";
    public static final String SELECTED_BITMAP = "selectedBitmap";
    public static final int START_CAMERA_REQUEST_CODE = 2;

    static {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory().getPath());
        stringBuilder.append("/scanSample");
        IMAGE_PATH = stringBuilder.toString();
    }
}
