package com.nabeeltech.capturedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.nabeeltech.capturedoc.settings.SettingsActivity;
import com.nabeeltech.capturedoc.copyright.AboutActivity;
import com.nabeeltech.capturedoc.copyright.LicenseAgreementActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


public class OCRActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String[] cameraPermission;
    String[] storagePermission;

    FloatingActionButton mSaveTextBtn, mSavePdfBtn, mSavePdfImage;

    EditText mResultEt;

    String mText;

    Uri image_uri;

    ImageView mPreviewIV;
    //PDFView mPerviewPdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        mSaveTextBtn = (FloatingActionButton) findViewById(R.id.saveBtn);
        mSavePdfBtn = (FloatingActionButton) findViewById(R.id.pdfBtn);
        mSavePdfImage = (FloatingActionButton) findViewById(R.id.pdfImage);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //scannedImageView = findViewById(R.id.imageScanned);

        //Edit Text Result
        mResultEt = findViewById(R.id.resultText);

        //Image Preview
        mPreviewIV = findViewById(R.id.imagePreview);

        //issue here
//        mPerviewPdf = (PDFView)findViewById(R.id.imagePreview);


        //camera Permission
        cameraPermission = new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        //storage Permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        mSaveTextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mText = mResultEt.getText().toString().trim();
                if(mText.isEmpty()){
                    Toast.makeText(OCRActivity.this, "Load Image First...", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions,WRITE_EXTERNAL_STORAGE_CODE);
                        }else {
                            saveToTxtFile(mText);
                        }
                    }else {
                        saveToTxtFile(mText);
                    }
                }

            }
        });

        mSavePdfBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mText = mResultEt.getText().toString().trim();
                if (mText.isEmpty()) {
                    Toast.makeText(OCRActivity.this, "Load Image First...", Toast.LENGTH_SHORT).show();
                }   else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED){
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions,WRITE_EXTERNAL_STORAGE_CODE);
                        }else {
                            savePdf();
                        }
                    }else {
                        savePdf();
                    }
                }

            }
        });


    }

    private void savePdf() {

        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = new String(getExternalFilesDir(null).getAbsolutePath()) + "/" + mFileName + ".pdf";
        try {
            //create instance pdfwriter class
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            mDoc.open();
            //get text from edittext
            String mText = mResultEt.getText().toString();
            //add author document optionnel
            mDoc.addAuthor("CaptureDoc");
            //add paragraph to the document
            mDoc.add(new Paragraph(mText));
            //you can add more feature here

            //close document
            mDoc.close();
            //show message that file is saved
            Toast.makeText(this, mFileName +".pdf\nis saved to\n"+ mFilePath, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


//    private Menu globalMenuItem;

    //action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.top_navigation_ocr, menu);
//        globalMenuItem = menu;

        return super.onCreateOptionsMenu(menu);
    }


    //handle actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivityForResult(myIntent, 0);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }


        int id = item.getItemId();

        if (id == R.id.action_share_ocr){

            Drawable myDrawable = mPreviewIV.getDrawable();
            if(myDrawable == null) {

                Toast.makeText(OCRActivity.this, "Load Image First...", Toast.LENGTH_SHORT).show();

            } else {
                Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();
                try {
                    Uri fileUri;
                    File file = new File(getExternalCacheDir(), "myImage.png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    Intent intentx = new Intent("android.intent.action.SEND");
                    intentx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT < 21) {
                        fileUri = Uri.fromFile(file);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(getPackageName());
                        stringBuilder.append(".provider");
                        fileUri = FileProvider.getUriForFile(this, stringBuilder.toString(), file);
                    }
                    Intent chooser = Intent.createChooser(intentx, "Share File");
                    for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(chooser, PackageManager.GET_META_DATA)) {
                        grantUriPermission(resolveInfo.activityInfo.packageName, fileUri, FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intentx.putExtra("android.intent.extra.STREAM", fileUri);
                    intentx.setType("image/png");
                    startActivity(chooser);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(OCRActivity.this, "File not found", Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }

        }
        if (id == R.id.action_pick3){
            showImageImportDialog();
        }
        if (id == R.id.action_settings3) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_about3) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_license3) {
            Intent intent = new Intent(this, LicenseAgreementActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showImageImportDialog() {
        //items to display in dialog
        String[] items = {"Camera", " Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, (dialog1, which) -> {
            if(which == 0){
                //Camera option clicked
                if(!checkCameraPermission()){
                    //camera permission not allowed, request it
                    requestCameraPermission();
                }else{
                    //permission allowed, take picture
                    pickCamera();
                }
            }
            if(which == 1){
                //Gallery option clicked
                if(!checkStoragePermission()){
                    //Storage permission not allowed, request it
                    requestStoragePermission();
                }else {
                    pickGallery();
                }
            }
        });
        dialog.create().show(); //show dialog
    }

    public void pickGallery(){
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    public void pickCamera(){
        //intent to take Image from camera, it will also be save to storage to get hgih quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text"); //description
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result2;
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        pickCamera();
                    }else {
                        Toast.makeText( this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:

                if(grantResults.length >0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickGallery();
                    }else {
                        Toast.makeText( this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case WRITE_EXTERNAL_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    //permission granted save data
                    saveToTxtFile(mText);
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage Permission required to store", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case IMAGE_PICK_GALLERY_CODE: {
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    //permission granted save data
                    savePdf();
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage Permission required to store", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    //handle image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                    //got image from gallery now crop it

                    if (data != null) {
                        CropImage.activity(data.getData())
                                .setGuidelines(CropImageView.Guidelines.ON) // enable image guidelines
                                .start(this);
                    }
                }
                if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                    //got image from camera now crop it
                    CropImage.activity(image_uri)
                            .setGuidelines(CropImageView.Guidelines.ON) // enable image guidelines
                            .start(this);
                }
            }
            //get cropped image
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri(); //get image uri

                    //set image to image view
                    mPreviewIV.setImageURI(resultUri);
                    //mPreviewIV.setImageBitmap(bitmap);

                    //get drawable bitmap for text recongnition
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIV.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                    //pdf image
                    PdfDocument pdfDocument = new PdfDocument();
                    PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder
                            (bitmap.getWidth(),bitmap.getHeight(),1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pi);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#FFFFFFFF"));
                    canvas.drawPaint(paint);

                    bitmap = Bitmap.createScaledBitmap
                            (bitmap, bitmap.getWidth(), bitmap.getHeight(),true);
                    paint.setColor(Color.BLUE);
                    canvas.drawBitmap(bitmap,0,0,null);
                    pdfDocument.finishPage(page);
                    //save bitmap image
                    File root = new File(Environment.getExternalStorageDirectory(), "PDF Folder 12");
                    if(!root.exists()){
                        root.mkdirs();
                    }
                    File file = new File(root, "pictur.pdf");
                    try
                    {
                      FileOutputStream fileOutputStream = new FileOutputStream(file);
                      pdfDocument.writeTo(fileOutputStream);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    pdfDocument.close();



                    if (!recognizer.isOperational()) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = recognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();
                        //get text from sb until there is no text
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock myItem = items.valueAt(i);
                            sb.append(myItem.getValue());
                            sb.append("\n");
                        }
                        //set text to edit
                        mResultEt.setText(sb.toString());
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //if there is any error show it
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();


                }
            }
        }




    private void saveToTxtFile(String mText){
        //get current time for file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        try{
            //path tp storage
            File path = new File(getExternalFilesDir(null).getAbsolutePath());;
            //create folder named "My file"
            File dir = new File( path + "/My Files/");
            dir.mkdirs();
            //file name
            String fileName = "MyFile_" + timestamp + ".txt";

            File file = new File (dir, fileName);

            //used to store characater in file
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(mText);
            bw.close();

            //show file name and path where file saved
            Toast.makeText(this, fileName+"is saved to\n" +dir, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            //if anything goes wrong
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

/*    public void openPdf(View v){
        File root = new File(Environment.getExternalStorageDirectory(), "PDF Folder 12");
        File file = new File(root, "pictur.pdf");

        mPerviewPdf.fromFile(file)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();

    }*/



    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }
}
