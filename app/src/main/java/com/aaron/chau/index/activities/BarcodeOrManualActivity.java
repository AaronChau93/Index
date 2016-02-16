package com.aaron.chau.index.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.chau.index.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.FocusingProcessor;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarcodeOrManualActivity extends AppCompatActivity {
    private static final String TAG = BarcodeOrManualActivity.class.getName();
    private SurfaceView myCameraView;
    private TextView myBarcodeInfo;
    private CameraSource myCameraSource;
    private boolean myBarcodeFoundFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_or_manual);

        myBarcodeFoundFlag = false;

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getApplicationContext()).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
        if(!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
            return;
        }

        myCameraView = (SurfaceView) findViewById(R.id.camera_view);
        myBarcodeInfo = (TextView) findViewById(R.id.code_info);

        // Create Camera Source
        myCameraSource =  new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f).build();

        myCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    myCameraSource.start(myCameraView.getHolder());
                } catch (IOException ie) {
                    Log.e(TAG, ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                myCameraSource.stop();
            }
        });

        ((Button) findViewById(R.id.addManually)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
    }

    private void addNewItem() {
        addNewItem("");
    }
    private void addNewItem(final String theBarcode) {
        Intent intent = new Intent(this, AddItemActivity.class);
        if (!theBarcode.isEmpty())
            intent.putExtra("barcode", theBarcode);
        startActivity(intent);
    }

    private class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
        @Override
        public Tracker<Barcode> create(Barcode barcode) {
            return new MyBarcodeTracker();
        }
    }

    private class MyBarcodeTracker extends Tracker<Barcode> {
        @Override
        public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode barcode) {
            // Access detected barcode values
            final SparseArray<Barcode> barcodes = detectionResults.getDetectedItems();

            if (barcodes.size() != 0 && !myBarcodeFoundFlag) {
                myBarcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                    public void run() {
                        myBarcodeFoundFlag = true;
                        myBarcodeInfo.setText(    // Update the TextView
                                barcodes.valueAt(0).displayValue
                        );
                        addNewItem(barcodes.valueAt(0).displayValue);
                    }
                });
            }
        }
    }
}
