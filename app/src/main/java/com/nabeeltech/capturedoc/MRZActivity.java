package com.nabeeltech.capturedoc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.nabeeltech.capturedoc.R;

import org.jmrtd.lds.icao.MRZInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mrz.reader.CaptureActivity;

import static mrz.reader.CaptureActivity.MRZ_RESULT;

public class MRZActivity extends AppCompatActivity {

    Button btnScan;
    private TextView tvMRZ;
    private TextView tvDoc;
    private TextView tvLast;
    private TextView tvFirst;
    private TextView tvPassportNo;
    private TextView tvCountry;
    private TextView tvSex;
    private TextView tvDOB;
    private TextView tvDOE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrz);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        init();
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                Intent intent = new Intent(MRZActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    private void clear() {
        tvMRZ.setText(null);
        tvDoc.setText(null);
        tvLast.setText(null);
        tvFirst.setText(null);
        tvPassportNo.setText(null);
        tvCountry.setText(null);
        tvSex.setText(null);
        tvDOB.setText(null);
        tvDOE.setText(null);
    }

    private void init() {
        btnScan = findViewById(R.id.btn_scan);
        tvMRZ = findViewById(R.id.tv_mrz);
        tvDoc = findViewById(R.id.tv_document);
        tvLast = findViewById(R.id.tv_lastname);
        tvFirst = findViewById(R.id.tv_firstname);
        tvPassportNo = findViewById(R.id.tv_passport_no);
        tvCountry = findViewById(R.id.tv_country);
        tvSex = findViewById(R.id.tv_sex);
        tvDOB = findViewById(R.id.tv_dob);
        tvDOE = findViewById(R.id.tv_doe);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            MRZInfo mrzInfo = new MRZInfo(data.getStringExtra(MRZ_RESULT));
            updateResult(mrzInfo);
            btnScan.setVisibility(View.GONE);

        }
    }

    private void updateResult(MRZInfo mrzInfo) {
        tvMRZ.setText(mrzInfo.toString());
        tvDoc.setText(mrzInfo.getDocumentType() == 3 ? "PASSPORT" : "OTHER");
        tvLast.setText(mrzInfo.getPrimaryIdentifier());
        tvFirst.setText(mrzInfo.getSecondaryIdentifier().replace("<", ""));
        tvPassportNo.setText(mrzInfo.getDocumentNumber());
        tvCountry.setText(mrzInfo.getNationality());
        tvSex.setText(mrzInfo.getGender().toInt() == 1 ? "MALE" : "FEMALE");
        tvDOB.setText(parseDate(mrzInfo.getDateOfBirth()));
        tvDOE.setText(parseDate(mrzInfo.getDateOfExpiry()));
    }

    private String parseDate(String input) {
        @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyMMdd");
        @SuppressLint("SimpleDateFormat") DateFormat outputDateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date inputDate;
        try {
            inputDate = inputDateFormat.parse(input);
            return outputDateFormat.format(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
