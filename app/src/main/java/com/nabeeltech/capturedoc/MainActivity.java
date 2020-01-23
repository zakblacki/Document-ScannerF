 package com.nabeeltech.capturedoc;

 import android.Manifest;
 import android.annotation.SuppressLint;
 import android.app.Activity;
 import android.app.Dialog;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.content.pm.PackageManager;
 import android.content.pm.ResolveInfo;
 import android.graphics.Bitmap;
 import android.graphics.Color;
 import android.graphics.drawable.BitmapDrawable;
 import android.graphics.drawable.Drawable;
 import android.net.Uri;
 import android.os.Build;
 import android.os.Bundle;
 import android.os.Handler;
 import android.preference.PreferenceManager;
 import android.provider.MediaStore;
 import android.provider.Settings;
 import android.view.Gravity;
 import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.Button;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.annotation.RequiresApi;
 import androidx.appcompat.app.ActionBar;
 import androidx.appcompat.app.ActionBarDrawerToggle;
 import androidx.appcompat.app.AlertDialog;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.core.content.FileProvider;
 import androidx.drawerlayout.widget.DrawerLayout;

 import com.adityaarora.liveedgedetection.view.TouchImageView;
 import com.google.android.material.floatingactionbutton.FloatingActionButton;
 import com.google.android.material.navigation.NavigationView;
 import com.karumi.dexter.Dexter;
 import com.karumi.dexter.MultiplePermissionsReport;
 import com.karumi.dexter.PermissionToken;
 import com.karumi.dexter.listener.DexterError;
 import com.karumi.dexter.listener.PermissionRequest;
 import com.karumi.dexter.listener.PermissionRequestErrorListener;
 import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
 import com.nabeeltech.capturedoc.settings.Constants;
 import com.nabeeltech.capturedoc.settings.SettingsActivity;
 import com.nabeeltech.capturedoc.settings.SettingsHelperClass;
 import com.nabeeltech.capturedoc.copyright.AboutActivity;
 import com.nabeeltech.capturedoc.copyright.FeedbackActivity;
 import com.nabeeltech.capturedoc.copyright.LicenseAgreementActivity;
 import com.rbddevs.splashy.Splashy;
 import com.scanlibrary.ScanActivity;
 import com.scanlibrary.ScanConstants;
 import com.scanlibrary.ScanFragment;

 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.List;

 import static android.app.PendingIntent.getActivity;
 import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


 public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



     TouchImageView scannedImageView;
     DrawerLayout drawerLayout;
     Toolbar toolbar;
     NavigationView navigationView;
     FloatingActionButton fab;


     private ActionBarDrawerToggle drawerListener;
     private boolean mLimitExceeded = false;
     private static boolean splashLoaded = false;

     private static final int MY_CAMERA_REQUEST_CODE = 100;
     private static final String TAG = "MainActivity";
     private static final int OPEN_THING = 99;
     private static final int PERMISSION_CODE = 1000;

     Uri image_uri;
     String mText;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         //launch splash
         if (!splashLoaded) {
             new Handler().postDelayed(new Runnable() {
                 public void run() {
                    SplashScreen();
                 }
             }, 0);

             splashLoaded = true;
         }

         //ask run time permissions for camera and read and write file
         checkPermissions();
         init();
         setSupportActionBar(toolbar);
         ActionBar actionBar = getSupportActionBar();
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         navigationView.setNavigationItemSelectedListener(this);

         drawerListener = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
         drawerLayout.addDrawerListener(drawerListener);
         drawerListener.syncState();

     }

     public void init() {
         navigationView = (NavigationView) findViewById(R.id.navigationView);
         toolbar = findViewById(R.id.toolbar);
         drawerLayout = findViewById(R.id.drawerLayout);
         scannedImageView = findViewById(R.id.scanned_image);
         fab = findViewById(R.id.fab_capture);
         fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent myIntent3 = new Intent(MainActivity.this, ScanCallActivity.class);
                 startActivity(myIntent3);
             }
         });

     }

     public void SplashScreen(){
         new Splashy(this)
                 .setLogo(R.drawable.splashy)
                 .setTitle("CaptureDoc")
                 .setSubTitle("Scan Document made easy")
                 .setProgressColor(R.color.black)
                 .setFullScreen(true)
                 .setTitleColor(R.color.black)
                 .showTitle(true)
                 .showProgress(true)
                 .setAnimation(Splashy.Animation.GLOW_LOGO_TITLE, 2500)
                 .show();
     }

     //***********************************************open Gallery ***************************************************************
     public void openGallery() {

         int REQUEST_CODE = 100;
         int preference = ScanConstants.OPEN_MEDIA;
         Intent intent = new Intent(this, ScanActivity.class);
         intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
         startActivityForResult(intent, OPEN_THING);
     }

     //***********************************************open camera ***************************************************************
     public void openCamera() {

//         int REQUEST_CODE = 99;
         int preference = ScanConstants.OPEN_CAMERA;
         Intent intent = new Intent(this, ScanActivity.class);

         intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
         startActivityForResult(intent, OPEN_THING);

     }

     //***********************************************on activity result for camera and gallery ***************************************************************
     @SuppressLint("RestrictedApi")
     @RequiresApi(api = Build.VERSION_CODES.Q)
     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == Activity.RESULT_OK && data != null) {
             if (requestCode == OPEN_THING) {
                 Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                 Bitmap bitmap = null;
                 try {
                     bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                     getContentResolver().delete(uri, null, null);
                     scannedImageView.setImageBitmap(bitmap);

                     FileOutputStream outputStream = null;
                     File sdCard = new File(getExternalFilesDir(null).getAbsolutePath());
                     File directory = new File (sdCard.getAbsolutePath() +"/Scan Documents");
                     directory.mkdir();

                     String filename = String.format("d.jpg", System.currentTimeMillis());
                     File outFile = new File(directory, filename);

                     Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

                     Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                     intent.setData(Uri.fromFile(outFile));
                     sendBroadcast(intent);
                     fab.hide();

                     try{
                         outputStream = new FileOutputStream(outFile);
                         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                         outputStream.flush();
                         outputStream.close();

                     }catch (FileNotFoundException e)
                     {
                         e.printStackTrace();
                     }
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
     }


     //***********************************************permission code ***************************************************************
     private void checkPermissions()
     {
         Dexter.withActivity(this)
                 .withPermissions(
                         Manifest.permission.CAMERA,
                         Manifest.permission.READ_EXTERNAL_STORAGE,
                         Manifest.permission.WRITE_EXTERNAL_STORAGE
                 ).withListener(new MultiplePermissionsListener() {
             @Override public void onPermissionsChecked(MultiplePermissionsReport report)
             {
                 // check if all permissions are granted
                 if (report.areAllPermissionsGranted()) {

                     // do you work now
                 }

                 // check for permanent denial of any permission
                 if (report.isAnyPermissionPermanentlyDenied()) {
                     // permission is denied permenantly, navigate user to app settings
                     showSettingsDialog();
                 }
             }
             @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
             {
                 token.continuePermissionRequest();
             }
         }).withErrorListener(new PermissionRequestErrorListener() {
             @Override
             public void onError(DexterError error) {
                 Toast.makeText(MainActivity.this, "Error Occured!!!", Toast.LENGTH_SHORT).show();
             }
         })
                 .onSameThread()
                 .check();
     }

     private void showSettingsDialog()
     {
         AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
         builder.setTitle("Need Camera and Storage Permissions");
         builder.setMessage("This app needs permissions to use this feature you can grant them in app settings");
         builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
                 openSettings();
             }
         });
         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
             }
         });
         builder.show();
     }

     private void openSettings()
     {
         Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
         Uri uri = Uri.fromParts("package", getPackageName(), null);
         intent.setData(uri);
         startActivityForResult(intent, 101);
     }


     //***********************************************finish permissions code ***************************************************************


     //popup for manual scan
     public void openDialog() {
         AlertDialog alertDialog = new AlertDialog.Builder(this).create();

         // Set Custom Title
         TextView title = new TextView(this);
         // Title Properties
         title.setText(R.string.select_image);
         title.setPadding(10, 10, 10, 10);   // Set Position
         title.setGravity(Gravity.CENTER);
         title.setTextColor(Color.BLACK);
         title.setTextSize(20);
         alertDialog.setCustomTitle(title);

         // Set Button
         // you can more buttons

         alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Manual", (dialog, which) -> {
             // Perform Action on Button

                 openCamera();

         });

         alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Gallery", (dialog, which) -> {
             // Perform Action on Button
             openGallery();
         });

         new Dialog(getApplicationContext());
         alertDialog.show();

         // Set Properties for OK Button
         final Button manualBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
         LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) manualBT.getLayoutParams();
         neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
         manualBT.setPadding(50, 10, 10, 10);   // Set Position
         manualBT.setTextColor(Color.BLUE);
         manualBT.setLayoutParams(neutralBtnLP);

         final Button galleryBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
         LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) galleryBT.getLayoutParams();
         negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
         galleryBT.setTextColor(Color.RED);
         galleryBT.setLayoutParams(negBtnLP);

     }


     //action bar menu
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         //inflate menu
         getMenuInflater().inflate(R.menu.top_navigation_main, menu);
         menu.findItem(R.id.action_settings2).setEnabled(!mLimitExceeded);

         return super.onCreateOptionsMenu(menu);
     }


     @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
         menu.findItem(R.id.action_settings2).setEnabled(!mLimitExceeded);
         return super.onPrepareOptionsMenu(menu);
     }
     //handle actionbar item clicks
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

         if (id == R.id.action_settings2) {
             Intent intent = new Intent(this, SettingsActivity.class);
             startActivity(intent);
         } else if (id == R.id.action_about2) {
             Intent intent = new Intent(this, AboutActivity.class);
             startActivity(intent);
         } else if (id == R.id.action_license2) {
             Intent intent = new Intent(this, LicenseAgreementActivity.class);
             startActivity(intent);
         } else if (id == R.id.action_share) {

             Drawable myDrawable = scannedImageView.getDrawable();
             if(myDrawable == null) {

                 Toast.makeText(MainActivity.this, "Load Image First...", Toast.LENGTH_SHORT).show();

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
                     Toast.makeText(MainActivity.this, "File not found", Toast.LENGTH_SHORT).show();
                 } catch (Exception e2) {
                     e2.printStackTrace();
                 }

             }

         }

         if(drawerListener.onOptionsItemSelected(item))
         {
             return true;
         }

         return super.onOptionsItemSelected(item);
     }


     @Override
     protected void onStart() {
         super.onStart();

         boolean check = true;
         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         if (prefs.contains("pref_key_shots_count") || prefs.contains("pref_key_last_usage_date")) {
             Date currentDate = new Date();
             long currentTime = currentDate.getTime();
             long lastUsage = prefs.getLong("pref_key_last_usage_date", -1);
             Date lastUsageDate = new Date(lastUsage);

             if (lastUsage != -1 && ((currentTime - lastUsage) >  60 * 60 * 24 * 31 * 1000L)) {
                 // ok
                 SharedPreferences.Editor editor = prefs.edit();
                 editor.putInt("pref_key_shots_count", 0);
                 editor.apply();
             } else {
                 String currentMonthStr   = new SimpleDateFormat("MM").format(currentDate);
                 String lastUsageMonthStr = new SimpleDateFormat("MM").format(lastUsageDate);

                 if (currentMonthStr.equals(lastUsageMonthStr)) {
                     if (prefs.getInt("pref_key_shots_count", -1) >= Constants.MONTHLY_LIMIT) check = false;
                 } else {
                     SharedPreferences.Editor editor = prefs.edit();
                     editor.putInt("pref_key_shots_count", 0);
                     editor.apply();
                 }
             }
         } else {
             SettingsHelperClass.initRestrictionPropertiesFields(prefs);
         }

         mLimitExceeded = !check;
     }

     @Override
     public boolean onNavigationItemSelected(MenuItem menuItem) {

         menuItem.setChecked(true);

         switch (menuItem.getItemId()) {
             case R.id.navHome:
                 Intent intenty = new Intent(this, MainActivity.class);
                 startActivity(intenty);
                 break;

             case R.id.action_scan:
                 openDialog();
                 break;
             case R.id.action_ocr:
                 Intent intent = new Intent(this, OCRActivity.class);
                 startActivity(intent);
                 break;
             case R.id.action_mrz:
                 Intent intent3 = new Intent(this, MRZActivity.class);
                 startActivity(intent3);
                 break;
             case R.id.navFeedback:
                 Intent intent4 = new Intent(this, FeedbackActivity.class);
                 startActivity(intent4);
                 break;
         }

         return true;
     }

 }
