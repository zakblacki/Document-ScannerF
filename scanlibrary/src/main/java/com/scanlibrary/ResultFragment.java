package com.scanlibrary;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import java.io.IOException;

public class ResultFragment extends Fragment {
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
        setScannedImage(getBitmap());
        this.doneButton = (Button) this.view.findViewById(R.id.doneButton);
        this.doneButton.setOnClickListener(new DoneButtonClickListener());
    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            this.original = Utils.getBitmap(getActivity(), uri);
            getActivity().getContentResolver().delete(uri, (String) null, (String[]) null);
            return this.original;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri getUri() {
        return (Uri) getArguments().getParcelable("scannedResult");
    }

    public void setScannedImage(Bitmap scannedImage) {
        this.scannedImageView.setImageBitmap(scannedImage);
    }

    private class DoneButtonClickListener implements View.OnClickListener {
        private DoneButtonClickListener() {
        }

        public void onClick(View v) {
            ResultFragment resultFragment = ResultFragment.this;
            resultFragment.showProgressDialog(resultFragment.getResources().getString(R.string.loading));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Intent data = new Intent();
                        Bitmap bitmap = ResultFragment.this.transformed;
                        if (bitmap == null) {
                            bitmap = ResultFragment.this.original;
                        }
                        data.putExtra("scannedResult", Utils.getUri(ResultFragment.this.getActivity(), bitmap));
                        ResultFragment.this.getActivity().setResult(-1, data);
                        ResultFragment.this.original.recycle();
                        System.gc();
                        ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ResultFragment.this.dismissDialog();
                                ResultFragment.this.getActivity().finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class BWButtonClickListener implements View.OnClickListener {
        private BWButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultFragment resultFragment = ResultFragment.this;
            resultFragment.showProgressDialog(resultFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Bitmap unused = ResultFragment.this.transformed = ((ScanActivity) ResultFragment.this.getActivity()).getBWBitmap(ResultFragment.this.original);
                    } catch (final OutOfMemoryError e) {
                        ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Bitmap unused = ResultFragment.this.transformed = ResultFragment.this.original;
                                ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.original);
                                e.printStackTrace();
                                ResultFragment.this.dismissDialog();
                                BWButtonClickListener.this.onClick(v);
                            }
                        });
                    }
                    ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.transformed);
                            ResultFragment.this.dismissDialog();
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
            ResultFragment resultFragment = ResultFragment.this;
            resultFragment.showProgressDialog(resultFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Bitmap unused = ResultFragment.this.transformed = ((ScanActivity) ResultFragment.this.getActivity()).getMagicColorBitmap(ResultFragment.this.original);
                    } catch (final OutOfMemoryError e) {
                        ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Bitmap unused = ResultFragment.this.transformed = ResultFragment.this.original;
                                ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.original);
                                e.printStackTrace();
                                ResultFragment.this.dismissDialog();
                                MagicColorButtonClickListener.this.onClick(v);
                            }
                        });
                    }
                    ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.transformed);
                            ResultFragment.this.dismissDialog();
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
                ResultFragment.this.showProgressDialog(ResultFragment.this.getResources().getString(R.string.applying_filter));
                Bitmap unused = ResultFragment.this.transformed = ResultFragment.this.original;
                ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.original);
                ResultFragment.this.dismissDialog();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                ResultFragment.this.dismissDialog();
            }
        }
    }

    private class GrayButtonClickListener implements View.OnClickListener {
        private GrayButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultFragment resultFragment = ResultFragment.this;
            resultFragment.showProgressDialog(resultFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        Bitmap unused = ResultFragment.this.transformed = ((ScanActivity) ResultFragment.this.getActivity()).getGrayBitmap(ResultFragment.this.original);
                    } catch (final OutOfMemoryError e) {
                        ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Bitmap unused = ResultFragment.this.transformed = ResultFragment.this.original;
                                ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.original);
                                e.printStackTrace();
                                ResultFragment.this.dismissDialog();
                                GrayButtonClickListener.this.onClick(v);
                            }
                        });
                    }
                    ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.transformed);
                            ResultFragment.this.dismissDialog();
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