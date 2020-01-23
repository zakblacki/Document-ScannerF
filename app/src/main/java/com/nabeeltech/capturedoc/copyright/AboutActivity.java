package com.nabeeltech.capturedoc.copyright;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.nabeeltech.capturedoc.R;
import com.nabeeltech.capturedoc.settings.Constants;

public class AboutActivity extends AppCompatActivity {
    private LinearLayout mLayoutPortrait;
    private LinearLayout mLayoutLandscape;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mLayoutPortrait.setVisibility(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ? View.VISIBLE : View.GONE);
        mLayoutLandscape.setVisibility(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
//        setSupportActionBar(toolbar);

        String aboutStr = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
                "<HTML>\n" +
                "<HEAD>\n" +
                "<META HTTP-EQUIV=\"CONTENT-TYPE\" CONTENT=\"text/html; charset=us-ascii\">\n" +
                "<TITLE></TITLE>\n" +
                "<STYLE TYPE=\"text/css\">\n" +
                "\t<!--\n" +
                "\t\t@page { margin: 0.5in }\n" +
                "\t\tP { margin-bottom: 0.08in }\n" +
                "\t\tA:link { so-language: zxx }\n" +
                "\t-->\n" +
                "\t</STYLE>\n" +
                "</HEAD>\n" +
                "<BODY LANG=\"en-US\" DIR=\"LTR\" STYLE=\"border: none; padding: 0in\">\n" +
                "<P STYLE=\"margin-top: 0.07in; margin-bottom: 0.07in\"><FONT SIZE=2>\n" +
                "<p>When the user opens the camera, the software can guide them to take the image at the right angle. Access to the camera torch is enabled. Finally, the image taken is processed and cleaned up with eVRS and ready for process OCR or storage.</p>\n" +
                "<p>Full Open Source</p>\n" +
                "<h3>About information:</h3>\n" +
                "<ol style=\"list-style: none; font-size: 14px; line-height: 32px; font-weight: bold;\">\n" +
                "<li style=\"clear: both;\">MobileImage – \n " +
                "Document Capture from CaptureDoc.</li>\n" +
                "<li style=\"clear: both;\">Version: " + Constants.VERSION + "</li>\n" +
                "<li style=\"clear: both;\"><a href=\"mailto:aarab@capturedoc.com\">aarab@capturedoc.com</a></li>\n" +
                "<li style=\"clear: both;\"><a title=\"CaptureDoc\" href=\"https://capturedoc.com\">Capturedoc</a></li>\n" +
                "</ol>\n" +
                "</FONT></P>\n" +
                "</BODY>\n" + "</HTML>";

        WebView aboutWebViewPortrait  = (WebView) findViewById(R.id.about_text_portrait);
        aboutWebViewPortrait.loadData(aboutStr, "text/html; charset=utf-8", "UTF-8" );

        WebView aboutWebViewLandscape = (WebView) findViewById(R.id.about_text_landscape);
        aboutWebViewLandscape.loadData(aboutStr, "text/html; charset=utf-8", "UTF-8" );

        mLayoutPortrait = (LinearLayout) findViewById(R.id.about_portrait_layout);
        mLayoutPortrait.setVisibility(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? View.VISIBLE : View.GONE);

        mLayoutLandscape = (LinearLayout) findViewById(R.id.about_landscape_layout);
        mLayoutLandscape.setVisibility(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? View.VISIBLE : View.GONE);

        // these 2 lines enable back button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
