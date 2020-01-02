package com.nabeeltech.capturedoc;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.adityaarora.liveedgedetection.activity.ScanActivity;
import com.adityaarora.liveedgedetection.constants.ScanConstants;
import com.adityaarora.liveedgedetection.util.ScanUtils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.chrisbanes.photoview.PhotoView;
import com.scanlibrary.ResultFragment;
import com.scanlibrary.ScanFragment;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class ScanCallActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = ScanCallActivity.class.getSimpleName();
    private ImageView scannedImageView;
    private Bitmap baseBitmap;

    Button enhance;

    String mText;

//    QuadrilateralSelectionImageView mSelectionImageView;

    Button mButtonScan;

    Bitmap mBitmap;
    Bitmap mResult;

    MaterialDialog mResultDialog;

    private static final int MAX_HEIGHT = 500;

    private int PICK_IMAGE_REQUEST = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_edge);

        scannedImageView = findViewById(R.id.scanned_image2);

        enhance = findViewById(R.id.enhance_image);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        startScan();


        enhance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanCallActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });




/*
        //mSelectionImageView = (QuadrilateralSelectionImageView) findViewById(R.id.polygonView2);
        mButtonScan = (Button) findViewById(R.id.buttonScan);

        mResultDialog = new MaterialDialog.Builder(this)
                .title("Scan Result")
                .positiveText("Save")
                .negativeText("Cancel")
                .customView(R.layout.dialog_document_scan_result, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        // TODO Saving
                        mResult = null;
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        mResult = null;
                    }
                })
                .build();

        mButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PointF> points = mSelectionImageView.getPoints();

                if (mBitmap != null) {
                    Mat orig = new Mat();
                    org.opencv.android.Utils.bitmapToMat(mBitmap, orig);

                    Mat transformed = perspectiveTransform(orig, points);
                    mResult = applyThreshold(transformed);

                    if (mResultDialog.getCustomView() != null) {
                        PhotoView photoView = (PhotoView) mResultDialog.getCustomView().findViewById(R.id.imageView);
                        photoView.setImageBitmap(mResult);
                        mResultDialog.show();
                    }

                    orig.release();
                    transformed.release();
                }
            }
        });*/



    }

    public void startScan() {
        Intent intent = new Intent(ScanCallActivity.this, ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
/*
        //new test
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mSelectionImageView.setImageBitmap(getResizedBitmap(mBitmap, MAX_HEIGHT));
                List<PointF> points = findPoints();
                mSelectionImageView.setPoints(points);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


        if (requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(null != data && null != data.getExtras()) {
                    try {
                        String filePath = data.getExtras().getString(com.adityaarora.liveedgedetection.constants.ScanConstants.SCANNED_RESULT);
                        Bitmap baseBitmap = ScanUtils.decodeBitmapFromFile(filePath, com.adityaarora.liveedgedetection.constants.ScanConstants.IMAGE_NAME);
                        scannedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        scannedImageView.setImageBitmap(baseBitmap);
//                        enhance.setVisibility(View.VISIBLE);
                        Log.d(TAG,"File Path: "+filePath + ScanConstants.IMAGE_NAME);
                    }catch(Exception ex)
                    {
                        Log.e("I shouldn't be here", Objects.requireNonNull(ex.getMessage()));
                    }
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }

/*

    @Override
    public void onResume() {
        super.onResume();

    }



//     * Resize a given bitmap to scale using the given height
//     *
//     * @return The resized bitmap

    private Bitmap getResizedBitmap(Bitmap bitmap, int maxHeight) {
        double ratio = bitmap.getHeight() / (double) maxHeight;
        int width = (int) (bitmap.getWidth() / ratio);
        return Bitmap.createScaledBitmap(bitmap, width, maxHeight, false);
    }


//     * Attempt to find the four corner points for the largest contour in the image.
//     *
//     * @return A list of points, or null if a valid rectangle cannot be found.

    private List<PointF> findPoints() {
        List<PointF> result = null;

        Mat image = new Mat();
        Mat orig = new Mat();
        org.opencv.android.Utils.bitmapToMat(getResizedBitmap(mBitmap, MAX_HEIGHT), image);
        org.opencv.android.Utils.bitmapToMat(mBitmap, orig);

        Mat edges = edgeDetection(image);
        MatOfPoint2f largest = findLargestContour(edges);

        if (largest != null) {
            Point[] points = sortPoints(largest.toArray());
            result = new ArrayList<>();
            result.add(new PointF(Double.valueOf(points[0].x).floatValue(), Double.valueOf(points[0].y).floatValue()));
            result.add(new PointF(Double.valueOf(points[1].x).floatValue(), Double.valueOf(points[1].y).floatValue()));
            result.add(new PointF(Double.valueOf(points[2].x).floatValue(), Double.valueOf(points[2].y).floatValue()));
            result.add(new PointF(Double.valueOf(points[3].x).floatValue(), Double.valueOf(points[3].y).floatValue()));
            largest.release();
        } else {
            Timber.d("Can't find rectangle!");
        }

        edges.release();
        image.release();
        orig.release();

        return result;
    }


//     * Detect the edges in the given Mat
//     * @param src A valid Mat object
//     * @return A Mat processed to find edges

    private Mat edgeDetection(Mat src) {
        Mat edges = new Mat();
        Imgproc.cvtColor(src, edges, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(edges, edges, new Size(5, 5), 0);
        Imgproc.Canny(edges, edges, 75, 200);
        return edges;
    }


//     * Find the largest 4 point contour in the given Mat.
//     *
//     * @param src A valid Mat
//     * @return The largest contour as a Mat

    private MatOfPoint2f findLargestContour(Mat src) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // Get the 5 largest contours
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                double area1 = Imgproc.contourArea(o1);
                double area2 = Imgproc.contourArea(o2);
                return (int) (area2 - area1);
            }
        });
        if (contours.size() > 5) contours.subList(4, contours.size() - 1).clear();

        MatOfPoint2f largest = null;
        for (MatOfPoint contour : contours) {
            MatOfPoint2f approx = new MatOfPoint2f();
            MatOfPoint2f c = new MatOfPoint2f();
            contour.convertTo(c, CvType.CV_32FC2);
            Imgproc.approxPolyDP(c, approx, Imgproc.arcLength(c, true) * 0.02, true);

            if (approx.total() == 4 && Imgproc.contourArea(contour) > 150) {
                // the contour has 4 points, it's valid
                largest = approx;
                break;
            }
        }

        return largest;
    }


//     * Transform the coordinates on the given Mat to correct the perspective.
//     *
//     * @param src A valid Mat
//     * @param points A list of coordinates from the given Mat to adjust the perspective
//     * @return A perspective transformed Mat

    private Mat perspectiveTransform(Mat src, List<PointF> points) {
        Point point1 = new Point(points.get(0).x, points.get(0).y);
        Point point2 = new Point(points.get(1).x, points.get(1).y);
        Point point3 = new Point(points.get(2).x, points.get(2).y);
        Point point4 = new Point(points.get(3).x, points.get(3).y);
        Point[] pts = {point1, point2, point3, point4};
        return fourPointTransform(src, sortPoints(pts));
    }


//     * Apply a threshold to give the "scanned" look
//     *
//     * NOTE:
//     * See the following link for more info http://docs.opencv.org/3.1.0/d7/d4d/tutorial_py_thresholding.html#gsc.tab=0
//     * @param src A valid Mat
//     * @return The processed Bitmap

    private Bitmap applyThreshold(Mat src) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);

        // Some other approaches
//        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 15);
//        Imgproc.threshold(src, src, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        Imgproc.GaussianBlur(src, src, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        Bitmap bm = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(src, bm);

        return bm;
    }


//     * Sort the points
//     *
//     * The order of the points after sorting:
//     * 0------->1
//     * ^        |
//     * |        v
//     * 3<-------2
//     *
//     * NOTE:
//     * Based off of http://www.pyimagesearch.com/2014/08/25/4-point-opencv-getperspective-transform-example/
//     *
//     * @param src The points to sort
//     * @return An array of sorted points

    private Point[] sortPoints(Point[] src) {
        ArrayList<Point> srcPoints = new ArrayList<>(Arrays.asList(src));
        Point[] result = {null, null, null, null};

        Comparator<Point> sumComparator = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y + lhs.x).compareTo(rhs.y + rhs.x);
            }
        };
        Comparator<Point> differenceComparator = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y - lhs.x).compareTo(rhs.y - rhs.x);
            }
        };

        result[0] = Collections.min(srcPoints, sumComparator);        // Upper left has the minimal sum
        result[2] = Collections.max(srcPoints, sumComparator);        // Lower right has the maximal sum
        result[1] = Collections.min(srcPoints, differenceComparator); // Upper right has the minimal difference
        result[3] = Collections.max(srcPoints, differenceComparator); // Lower left has the maximal difference

        return result;
    }


//     * NOTE:
//     * Based off of http://www.pyimagesearch.com/2014/08/25/4-point-opencv-getperspective-transform-example/
//     *
//     * @param src
//     * @param pts
//     * @return

    private Mat fourPointTransform(Mat src, Point[] pts) {
        double ratio = src.size().height / (double) MAX_HEIGHT;

        Point ul = pts[0];
        Point ur = pts[1];
        Point lr = pts[2];
        Point ll = pts[3];

        double widthA = Math.sqrt(Math.pow(lr.x - ll.x, 2) + Math.pow(lr.y - ll.y, 2));
        double widthB = Math.sqrt(Math.pow(ur.x - ul.x, 2) + Math.pow(ur.y - ul.y, 2));
        double maxWidth = Math.max(widthA, widthB) * ratio;

        double heightA = Math.sqrt(Math.pow(ur.x - lr.x, 2) + Math.pow(ur.y - lr.y, 2));
        double heightB = Math.sqrt(Math.pow(ul.x - ll.x, 2) + Math.pow(ul.y - ll.y, 2));
        double maxHeight = Math.max(heightA, heightB) * ratio;

        Mat resultMat = new Mat(Double.valueOf(maxHeight).intValue(), Double.valueOf(maxWidth).intValue(), CvType.CV_8UC4);

        Mat srcMat = new Mat(4, 1, CvType.CV_32FC2);
        Mat dstMat = new Mat(4, 1, CvType.CV_32FC2);
        srcMat.put(0, 0, ul.x * ratio, ul.y * ratio, ur.x * ratio, ur.y * ratio, lr.x * ratio, lr.y * ratio, ll.x * ratio, ll.y * ratio);
        dstMat.put(0, 0, 0.0, 0.0, maxWidth, 0.0, maxWidth, maxHeight, 0.0, maxHeight);

        Mat M = Imgproc.getPerspectiveTransform(srcMat, dstMat);
        Imgproc.warpPerspective(src, resultMat, M, resultMat.size());

        srcMat.release();
        dstMat.release();
        M.release();

        return resultMat;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.top_navigation_share, menu);

        return super.onCreateOptionsMenu(menu);
    }


    //handle actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        int id = item.getItemId();

        if (id == R.id.action_share_scan) {

            Drawable myDrawable = scannedImageView.getDrawable();
            if(myDrawable == null) {

                Toast.makeText(ScanCallActivity.this, "Load Image First...", Toast.LENGTH_SHORT).show();

            } else {
                Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();
                try {
                    Uri file;
                    File file2 = new File(getExternalCacheDir(), "myImage.png");
                    FileOutputStream fOut = new FileOutputStream(file2);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
                    fOut.flush();
                    fOut.close();
                    file2.setReadable(true, false);
                    Intent intentx = new Intent("android.intent.action.SEND");
                    intentx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT < 21) {
                        file = Uri.fromFile(file2);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(getPackageName());
                        sb.append(".provider");
                        file = FileProvider.getUriForFile(this, sb.toString(), file2);
                    }
                    Intent intx = Intent.createChooser(intentx, "Share Image");
                    for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(intx, PackageManager.GET_META_DATA)) {
                        grantUriPermission(resolveInfo.activityInfo.packageName, file, FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intentx.putExtra("android.intent.extra.STREAM", file);
                    intentx.setType("image/png");
                    startActivity(intx);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ScanCallActivity.this, "File not found", Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }


}
