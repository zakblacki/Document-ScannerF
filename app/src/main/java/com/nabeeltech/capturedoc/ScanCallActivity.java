package com.nabeeltech.capturedoc;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.adityaarora.liveedgedetection.activity.ScanActivity;
import com.adityaarora.liveedgedetection.util.ScanUtils;
import com.adityaarora.liveedgedetection.view.TouchImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.scanlibrary.IScanner;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScanCallActivity extends AppCompatActivity {
    private static final int MAX_HEIGHT = 500;
    private static final int REQUEST_CODE = 101;
    private static final String TAG = ScanCallActivity.class.getSimpleName();
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap baseBitmap;
    Button enhance;
    private String filePath;
    private Uri fileUri;
    FrameLayout frameLayout;
    Bitmap mBitmap;
    Button mButtonScan;
    Bitmap mResult;
    //MaterialDialog mResultDialog;
    String mText;
    private IScanner scanner;
    TouchImageView touchImageView;

    public native Bitmap getBWBitmap(Bitmap bitmap);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native float[] getPoints(Bitmap bitmap);

    public native Bitmap getScannedBitmap(Bitmap bitmap, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8);

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Scanner");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_edge);
        this.touchImageView = (TouchImageView) findViewById(R.id.scanned_image2);
        this.enhance = (Button) findViewById(R.id.enhance_image);
        this.frameLayout = (FrameLayout) findViewById(R.id.container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        openCamera();
        this.enhance.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ScanCallActivity.this.baseBitmap.compress(CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ScanCallActivity.this.frameLayout.setVisibility(View.VISIBLE);
                ResultCallFragment fragment = new ResultCallFragment();
                Bundle bundle = new Bundle();
                bundle.putByteArray("scannedResult", byteArray);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = ScanCallActivity.this.getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.container, fragment);
                fragmentTransaction.addToBackStack(ResultCallFragment.class.toString());
                fragmentTransaction.commit();
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        Log.e("resumt", "yes");
    }

    public void openCamera()
    {
        startActivityForResult(new Intent(this, ScanActivity.class), 101);
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


    private Uri createImageFile(String path) {
        clearTempImages();
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(path, com.adityaarora.liveedgedetection.constants.ScanConstants.IMAGE_NAME);
        Log.e("Abs Path", "is " + file.getAbsolutePath());
        if (Build.VERSION.SDK_INT < 21) {
            this.fileUri = Uri.fromFile(file);
        } else {
            this.fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        }
        return this.fileUri;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onActivityResult");
        stringBuilder.append(resultCode);
        Log.e("", stringBuilder.toString());
        if (resultCode != -1) {
            Log.e("Not", "Ok");
            finish();
        } else if (requestCode == 2) {
            try {
                this.touchImageView.setImageBitmap(getBitmap(this.fileUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101) {
            try {
                this.filePath = data.getExtras().getString("scannedResult");
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("is ");
                stringBuilder2.append(this.filePath);
                Log.e("Path", stringBuilder2.toString());
                this.baseBitmap = ScanUtils.decodeBitmapFromFile(this.filePath, com.adityaarora.liveedgedetection.constants.ScanConstants.IMAGE_NAME);
                this.touchImageView.setImageBitmap(this.baseBitmap);

                ocrBitmap();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        Options options = new Options();
        options.inSampleSize = 3;
        return BitmapFactory.decodeFileDescriptor(getContentResolver().openAssetFileDescriptor(selectedimg, "r").getFileDescriptor(), null, options);
    }

    public void startScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 4);
        startActivityForResult(intent, 99);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_navigation_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            if (item.getItemId() == R.id.action_share_scan) {
                Drawable myDrawable = this.touchImageView.getDrawable();
                if (myDrawable == null) {
                    Toast.makeText(this, "Load Image First...", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();
                    try {
                        Uri fileUri;
                        File file = new File(getExternalCacheDir(), "myImage.png");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(CompressFormat.PNG, 80, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (VERSION.SDK_INT < 21) {
                            fileUri = Uri.fromFile(file);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(getPackageName());
                            stringBuilder.append(".provider");
                            fileUri = FileProvider.getUriForFile(this, stringBuilder.toString(), file);
                        }
                        intent.putExtra("android.intent.extra.STREAM", fileUri);
                        intent.setType("image/png");
                        startActivity(Intent.createChooser(intent, "Share Image Via"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    private void ocrBitmap() {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()){
            Toast.makeText(this, "Dependencies Not Found", Toast.LENGTH_SHORT).show();
        } else {

            Bitmap bitmapTemp = Bitmap.createBitmap(baseBitmap.getWidth(),baseBitmap.getHeight(),Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmapTemp);
            ColorMatrix ma = new ColorMatrix();
            ma.setSaturation(0);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(ma));
            canvas.drawBitmap(baseBitmap, 0, 0, paint);

            Frame frame = new Frame.Builder().setBitmap(baseBitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);

            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.WHITE);
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setStrokeWidth(4.0f);

            if (textBlockSparseArray.size() != 0){
                for (int i=0;i<textBlockSparseArray.size();i++){
                    TextBlock item = textBlockSparseArray.valueAt(i);

                    rectPaint.setColor(Color.BLACK);
                    rectPaint.setTextSize(560);
                    //canvas.drawText("My Text",0,0,rectPaint);

                    RectF rectF = new RectF(item.getBoundingBox());
                    canvas.drawRect(rectF,rectPaint);

                    touchImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    touchImageView.setImageBitmap(bitmapTemp);
                }
            }
        }
    }

}