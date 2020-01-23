package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PickImageFragment extends Fragment {
    private Uri fileUri;
    private IScanner scanner;
    private View view;

    private class CameraButtonClickListener implements OnClickListener {
        private CameraButtonClickListener() {
        }

        public void onClick(View v) {
            PickImageFragment.this.openCamera();
        }
    }

    private class GalleryClickListener implements OnClickListener {
        private GalleryClickListener() {
        }

        public void onClick(View view) {
            PickImageFragment.this.openMediaContent();
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IScanner) {
            this.scanner = (IScanner) activity;
            return;
        }
        throw new ClassCastException("Activity must implement IScanner");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.pick_image_fragment, null);
        init();
        Log.e("Fragment", "yes");
        return this.view;
    }

    private void init() {
        ((ImageView) this.view.findViewById(R.id.cameraButton)).setOnClickListener(new CameraButtonClickListener());
        ((ImageView) this.view.findViewById(R.id.selectButton)).setOnClickListener(new GalleryClickListener());
        if (isIntentPreferenceSet()) {
            handleIntentPreference();
        } else {
            getActivity().finish();
        }
    }

    private void clearTempImages() {
        try {
            for (File f : new File(ScanConstants.IMAGE_PATH).listFiles()) {
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleIntentPreference() {
        int preference = getIntentPreference();
        if (preference == 4) {
            openCamera();
        } else if (preference == 5) {
            openMediaContent();
        }
    }

    private boolean isIntentPreferenceSet() {
        if (getArguments().getInt(ScanConstants.OPEN_INTENT_PREFERENCE, 0) != 0) {
            return true;
        }
        return false;
    }

    private int getIntentPreference() {
        return getArguments().getInt(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
    }

    public void openMediaContent() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void openCamera() {
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = createImageFile();
        boolean isDirectoryCreated = file.getParentFile().mkdirs();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("openCamera: isDirectoryCreated: ");
        stringBuilder.append(isDirectoryCreated);
        Log.e("", stringBuilder.toString());
        String str = "output";
        if (VERSION.SDK_INT >= 24) {
            Context tempFileUri = getActivity().getApplicationContext();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getActivity().getPackageName());
            stringBuilder2.append(".provider");
            cameraIntent.putExtra(str, FileProvider.getUriForFile(tempFileUri, stringBuilder2.toString(), file));
        } else {
            cameraIntent.putExtra(str, Uri.fromFile(file));
        }
        startActivityForResult(cameraIntent, 2);
    }

    private File createImageFile() {
        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String str = ScanConstants.IMAGE_PATH;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(timeStamp);
        stringBuilder.append(".jpg");
        File file = new File(str, stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("is ");
        stringBuilder2.append(file.getAbsolutePath());
        Log.e("Path", stringBuilder2.toString());
        if (VERSION.SDK_INT < 21) {
            this.fileUri = Uri.fromFile(file);
        } else {
            Activity activity = getActivity();
            stringBuilder = new StringBuilder();
            stringBuilder.append(getActivity().getPackageName());
            stringBuilder.append(".provider");
            this.fileUri = FileProvider.getUriForFile(activity, stringBuilder.toString(), file);
        }
        return file;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onActivityResult");
        stringBuilder.append(resultCode);
        Log.e("", stringBuilder.toString());
        Bitmap bitmap = null;
        if (resultCode != -1) {
            getActivity().finish();
        } else if (requestCode == 1) {
            try {
                bitmap = getBitmap(data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2) {
            try {
                bitmap = getBitmap(this.fileUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            postImagePick(bitmap);
        }
    }

    /* Access modifiers changed, original: protected */
    public void postImagePick(Bitmap bitmap) {
        Uri uri = Utils.getUri(getActivity(), bitmap);
        bitmap.recycle();
        this.scanner.onBitmapSelect(uri);
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        Options options = new Options();
        options.inSampleSize = 3;
        return BitmapFactory.decodeFileDescriptor(getActivity().getContentResolver().openAssetFileDescriptor(selectedimg, "r").getFileDescriptor(), null, options);
    }
}