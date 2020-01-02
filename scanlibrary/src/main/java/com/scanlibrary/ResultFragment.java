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

    private class BWButtonClickListener implements OnClickListener {
        private BWButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultFragment resultFragment = ResultFragment.this;
            resultFragment.showProgressDialog(resultFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        ResultFragment.this.transformed = ((ScanActivity) ResultFragment.this.getActivity()).getBWBitmap(ResultFragment.this.original);
                    } catch (final OutOfMemoryError e) {
                        ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ResultFragment.this.transformed = ResultFragment.this.original;
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

    private class DoneButtonClickListener implements OnClickListener {
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
                        data.putExtra(ScanConstants.SCANNED_RESULT, Utils.getUri(ResultFragment.this.getActivity(), bitmap));
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

    private class GrayButtonClickListener implements OnClickListener {
        private GrayButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultFragment resultFragment = ResultFragment.this;
            resultFragment.showProgressDialog(resultFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        ResultFragment.this.transformed = ((ScanActivity) ResultFragment.this.getActivity()).getGrayBitmap(ResultFragment.this.original);
                    } catch (final OutOfMemoryError e) {
                        ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ResultFragment.this.transformed = ResultFragment.this.original;
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

    private class MagicColorButtonClickListener implements OnClickListener {
        private MagicColorButtonClickListener() {
        }

        public void onClick(final View v) {
            ResultFragment resultFragment = ResultFragment.this;
            resultFragment.showProgressDialog(resultFragment.getResources().getString(R.string.applying_filter));
            AsyncTask.execute(new Runnable() {
                public void run() {
                    try {
                        ResultFragment.this.transformed = ((ScanActivity) ResultFragment.this.getActivity()).getMagicColorBitmap(ResultFragment.this.original);
                    } catch (final OutOfMemoryError e) {
                        ResultFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ResultFragment.this.transformed = ResultFragment.this.original;
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

    private class OriginalButtonClickListener implements OnClickListener {
        private OriginalButtonClickListener() {
        }

        public void onClick(View v) {
            try {
                ResultFragment.this.showProgressDialog(ResultFragment.this.getResources().getString(R.string.applying_filter));
                ResultFragment.this.transformed = ResultFragment.this.original;
                ResultFragment.this.scannedImageView.setImageBitmap(ResultFragment.this.original);
                ResultFragment.this.dismissDialog();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                ResultFragment.this.dismissDialog();
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.result_layout, null);
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

    /* JADX WARNING: type inference failed for: r1v0, types: [java.lang.String, java.lang.String[]] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r1v0, types: [java.lang.String, java.lang.String[]]
  assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY]]
  uses: [java.lang.String, java.lang.String[]]
  mth insns count: 13
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 1 */
    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            this.original = Utils.getBitmap(getActivity(), uri);
            getActivity().getContentResolver().delete(uri, null, null);
            return this.original;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri getUri() {
        return (Uri) getArguments().getParcelable(ScanConstants.SCANNED_RESULT);
    }

    public void setScannedImage(Bitmap scannedImage) {
        this.scannedImageView.setImageBitmap(scannedImage);
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
