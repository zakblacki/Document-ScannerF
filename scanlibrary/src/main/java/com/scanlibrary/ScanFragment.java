package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanFragment extends Fragment {
    private Bitmap original;
    private PolygonView polygonView;
    private ProgressDialogFragment progressDialogFragment;
    private Button scanButton;
    private IScanner scanner;
    private FrameLayout sourceFrame;
    private ImageView sourceImageView;
    private View view;

    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private Map<Integer, PointF> points;

        public ScanAsyncTask(Map<Integer, PointF> points) {
            this.points = points;
        }

        /* Access modifiers changed, original: protected */
        public void onPreExecute() {
            super.onPreExecute();
            ScanFragment scanFragment = ScanFragment.this;
            scanFragment.showProgressDialog(scanFragment.getString(R.string.scanning));
        }

        /* Access modifiers changed, original: protected|varargs */
        public Bitmap doInBackground(Void... params) {
            ScanFragment scanFragment = ScanFragment.this;
            Bitmap bitmap = scanFragment.getScannedBitmap(scanFragment.original, this.points);
            ScanFragment.this.scanner.onScanFinish(Utils.getUri(ScanFragment.this.getActivity(), bitmap));
            return bitmap;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            bitmap.recycle();
            ScanFragment.this.dismissDialog();
        }
    }

    private class ScanButtonClickListener implements OnClickListener {
        private ScanButtonClickListener(ScanFragment scanFragment, Object o) {
        }


        public void onClick(View v) {
            Map<Integer, PointF> points = ScanFragment.this.polygonView.getPoints();
            if (ScanFragment.this.isScanPointsValid(points)) {
                new ScanAsyncTask(points).execute(new Void[0]);
            } else {
                ScanFragment.this.showErrorDialog();
            }
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
        this.view = inflater.inflate(R.layout.scan_fragment_layout, null);
        init();
        return this.view;
    }

    private void init() {
        this.sourceImageView = (ImageView) this.view.findViewById(R.id.sourceImageView);
        this.scanButton = (Button) this.view.findViewById(R.id.scanButton);
        this.scanButton.setOnClickListener(new ScanButtonClickListener(this, null));
        this.sourceFrame = (FrameLayout) this.view.findViewById(R.id.sourceFrame);
        this.polygonView = (PolygonView) this.view.findViewById(R.id.polygonView);
        this.sourceFrame.post(new Runnable() {
            public void run() {
                ScanFragment scanFragment = ScanFragment.this;
                scanFragment.original = scanFragment.getBitmap();
                if (ScanFragment.this.original != null) {
                    scanFragment = ScanFragment.this;
                    scanFragment.setBitmap(scanFragment.original);
                }
            }
        });
    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            Bitmap bitmap = Utils.getBitmap(getActivity(), uri);
            getActivity().getContentResolver().delete(uri, null, null);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri getUri() {
        return (Uri) getArguments().getParcelable(ScanConstants.SELECTED_BITMAP);
    }

    private void setBitmap(Bitmap original) {
        this.sourceImageView.setImageBitmap(scaledBitmap(original, this.sourceFrame.getWidth(), this.sourceFrame.getHeight()));
        Bitmap tempBitmap = ((BitmapDrawable) this.sourceImageView.getDrawable()).getBitmap();
        this.polygonView.setPoints(getEdgePoints(tempBitmap));
        this.polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        LayoutParams layoutParams = new LayoutParams(tempBitmap.getWidth() + (padding * 2), tempBitmap.getHeight() + (padding * 2));
        layoutParams.gravity = 17;
        this.polygonView.setLayoutParams(layoutParams);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        return orderedValidEdgePoints(tempBitmap, getContourEdgePoints(tempBitmap));
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        float[] points = ((ScanActivity) getActivity()).getPoints(tempBitmap);
        float x1 = points[0];
        float x2 = points[1];
        float x3 = points[2];
        float x4 = points[3];
        float y1 = points[4];
        float y2 = points[5];
        float y3 = points[6];
        float y4 = points[7];
        List<PointF> pointFs = new ArrayList();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap();
        outlinePoints.put(Integer.valueOf(0), new PointF(0.0f, 0.0f));
        outlinePoints.put(Integer.valueOf(1), new PointF((float) tempBitmap.getWidth(), 0.0f));
        outlinePoints.put(Integer.valueOf(2), new PointF(0.0f, (float) tempBitmap.getHeight()));
        outlinePoints.put(Integer.valueOf(3), new PointF((float) tempBitmap.getWidth(), (float) tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = this.polygonView.getOrderedPoints(pointFs);
        if (this.polygonView.isValidShape(orderedPoints)) {
            return orderedPoints;
        }
        return getOutlinePoints(tempBitmap);
    }

    private void showErrorDialog() {
        new SingleButtonDialogFragment(R.string.ok, getString(R.string.cantCrop), "Error", true).show(getActivity().getFragmentManager(), SingleButtonDialogFragment.class.toString());
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight()), new RectF(0.0f, 0.0f, (float) width, (float) height), ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {
        Map<Integer, PointF> map = points;
        int width = original.getWidth();
        int height = original.getHeight();
        float xRatio = ((float) original.getWidth()) / ((float) this.sourceImageView.getWidth());
        float yRatio = ((float) original.getHeight()) / ((float) this.sourceImageView.getHeight());
        Integer valueOf = Integer.valueOf(0);
        float x1 = ((PointF) map.get(valueOf)).x * xRatio;
        Integer valueOf2 = Integer.valueOf(1);
        float x2 = ((PointF) map.get(valueOf2)).x * xRatio;
        Integer valueOf3 = Integer.valueOf(2);
        float f = ((PointF) map.get(valueOf3)).x * xRatio;
        Integer valueOf4 = Integer.valueOf(3);
        float x4 = ((PointF) map.get(valueOf4)).x * xRatio;
        float y1 = ((PointF) map.get(valueOf)).y * yRatio;
        float y2 = ((PointF) map.get(valueOf2)).y * yRatio;
        float y3 = ((PointF) map.get(valueOf3)).y * yRatio;
        float y4 = ((PointF) map.get(valueOf4)).y * yRatio;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("POints(");
        stringBuilder.append(x1);
        String str = ",";
        stringBuilder.append(str);
        stringBuilder.append(y1);
        String str2 = ")(";
        stringBuilder.append(str2);
        stringBuilder.append(x2);
        stringBuilder.append(str);
        stringBuilder.append(y2);
        stringBuilder.append(str2);
        stringBuilder.append(f);
        stringBuilder.append(str);
        stringBuilder.append(y3);
        stringBuilder.append(str2);
        stringBuilder.append(x4);
        stringBuilder.append(str);
        stringBuilder.append(y4);
        stringBuilder.append(")");
        Log.d("", stringBuilder.toString());
        float x3 = f;
        return ((ScanActivity) getActivity()).getScannedBitmap(original, x1, y1, x2, y2, f, y3, x4, y4);
    }

    /* Access modifiers changed, original: protected */
    public void showProgressDialog(String message) {
        this.progressDialogFragment = new ProgressDialogFragment(message);
        this.progressDialogFragment.show(getFragmentManager(), ProgressDialogFragment.class.toString());
    }

    /* Access modifiers changed, original: protected */
    public void dismissDialog() {
        this.progressDialogFragment.dismissAllowingStateLoss();
    }
}