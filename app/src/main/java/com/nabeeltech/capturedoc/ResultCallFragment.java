package com.nabeeltech.capturedoc;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.scanlibrary.Utils;
import com.scanlibrary.ProgressDialogFragment;

public class ResultCallFragment extends Fragment {
    private static ProgressDialogFragment progressDialogFragment;
    private Button MagicColorButton;
    private Button bwButton;
    private Button doneButton;
    private Button grayModeButton;
    /* access modifiers changed from: private */
    public Bitmap original;
    private Button originalButton;
    /* access modifiers changed from: private */
    public ImageView scannedImageView;
    /* access modifiers changed from: private */
    public Bitmap transformed;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.result_layout, (ViewGroup) null);
        init();
        return this.view;
    }

    private void init() {
        this.scannedImageView = (ImageView) this.view.findViewById(R.id.scannedImage);
        this.originalButton = (Button) this.view.findViewById(R.id.original);
        this.originalButton.setOnClickListener(new OriginalButtonClickListener());
        this.MagicColorButton = (Button) this.view.findViewById(R.id.magicColor);
        this.MagicColorButton.setOnClickListener(new MagicColorButtonClickListener());
        this.grayModeButton = (Button) this.view.findViewById(R.id.grayMode);
        this.grayModeButton.setOnClickListener(new GrayButtonClickListener());
        this.bwButton = (Button) this.view.findViewById(R.id.BWMode);
        this.bwButton.setOnClickListener(new BWButtonClickListener());
        byte[] byteArray = getArguments().getByteArray("scannedResult");
        this.original = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        setScannedImage(this.original);
        this.doneButton = (Button) this.view.findViewById(R.id.doneButton);
        this.doneButton.setOnClickListener(new DoneButtonClickListener());
    }

    public void setScannedImage(Bitmap scannedImage) {
        this.scannedImageView.setImageBitmap(scannedImage);
    }

    private class DoneButtonClickListener implements View.OnClickListener {
        private DoneButtonClickListener() {
        }

        public void onClick(View v) {
            ResultCallFragment resultCallFragment = ResultCallFragment.this;
            resultCallFragment.showProgressDialog(resultCallFragment.getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Intent data = new Intent();
                        Bitmap bitmap = ResultCallFragment.this.transformed;
                        if (bitmap == null) {
                            bitmap = ResultCallFragment.this.original;
                        }
                        data.putExtra("scannedResult", Utils.getUri(ResultCallFragment.this.getActivity(), bitmap));
                        ResultCallFragment.this.getActivity().setResult(-1, data);
                        ResultCallFragment.this.original.recycle();
                        System.gc();
                        ResultCallFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ResultCallFragment.this.dismissDialog();
                                ResultCallFragment.this.getActivity().finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Result", "Exception " + e.getMessage());
                    }
                }
            });
        }
    }

    private class BWButtonClickListener implements View.OnClickListener {
        private BWButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultCallFragment resultCallFragment = ResultCallFragment.this;
            resultCallFragment.showProgressDialog(resultCallFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Bitmap unused = ResultCallFragment.this.transformed = ((ScanCallActivity) ResultCallFragment.this.getActivity()).getBWBitmap(ResultCallFragment.this.original);
                    } catch (OutOfMemoryError e) {
                        ResultCallFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Bitmap unused = ResultCallFragment.this.transformed = ResultCallFragment.this.original;
                                ResultCallFragment.this.scannedImageView.setImageBitmap(ResultCallFragment.this.original);
                                e.printStackTrace();
                                ResultCallFragment.this.dismissDialog();
                                BWButtonClickListener.this.onClick(v);
                            }
                        });
                    }
                    ResultCallFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ResultCallFragment.this.scannedImageView.setImageBitmap(ResultCallFragment.this.transformed);
                            ResultCallFragment.this.dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class MagicColorButtonClickListener implements View.OnClickListener {
        private MagicColorButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultCallFragment resultCallFragment = ResultCallFragment.this;
            resultCallFragment.showProgressDialog(resultCallFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Bitmap unused = ResultCallFragment.this.transformed = ((ScanCallActivity) ResultCallFragment.this.getActivity()).getMagicColorBitmap(ResultCallFragment.this.original);
                    } catch (OutOfMemoryError e) {
                        ResultCallFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Bitmap unused = ResultCallFragment.this.transformed = ResultCallFragment.this.original;
                                ResultCallFragment.this.scannedImageView.setImageBitmap(ResultCallFragment.this.original);
                                e.printStackTrace();
                                ResultCallFragment.this.dismissDialog();
                                MagicColorButtonClickListener.this.onClick(v);
                            }
                        });
                    }
                    ResultCallFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ResultCallFragment.this.scannedImageView.setImageBitmap(ResultCallFragment.this.transformed);
                            ResultCallFragment.this.dismissDialog();
                        }
                    });
                }
            });
        }
    }

    private class OriginalButtonClickListener implements View.OnClickListener {
        private OriginalButtonClickListener() {
        }

        public void onClick(View v) {
            try {
                ResultCallFragment.this.showProgressDialog(ResultCallFragment.this.getResources().getString(R.string.applying_filter));
                Bitmap unused = ResultCallFragment.this.transformed = ResultCallFragment.this.original;
                ResultCallFragment.this.scannedImageView.setImageBitmap(ResultCallFragment.this.original);
                ResultCallFragment.this.dismissDialog();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                ResultCallFragment.this.dismissDialog();
            }
        }
    }

    private class GrayButtonClickListener implements View.OnClickListener {
        private GrayButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultCallFragment resultCallFragment = ResultCallFragment.this;
            resultCallFragment.showProgressDialog(resultCallFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Bitmap unused = ResultCallFragment.this.transformed = ((ScanCallActivity) ResultCallFragment.this.getActivity()).getGrayBitmap(ResultCallFragment.this.original);
                    } catch (OutOfMemoryError e) {
                        ResultCallFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Bitmap unused = ResultCallFragment.this.transformed = ResultCallFragment.this.original;
                                ResultCallFragment.this.scannedImageView.setImageBitmap(ResultCallFragment.this.original);
                                e.printStackTrace();
                                ResultCallFragment.this.dismissDialog();
                                GrayButtonClickListener.this.onClick(v);
                            }
                        });
                    }
                    ResultCallFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ResultCallFragment.this.scannedImageView.setImageBitmap(ResultCallFragment.this.transformed);
                            ResultCallFragment.this.dismissDialog();
                        }
                    });
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(getFragmentManager(), ProgressDialogFragment.class.toString());
    }

    /* access modifiers changed from: protected */
    public synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }
}