package com.example.george.bookmarker.activity.camera;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.example.george.bookmarker.R;

import java.io.IOException;

/**
 * Androidのカメラを使用してバーコードデータを読み取り値を返すActivity
 */
public class CameraActivity extends AppCompatActivity {

    private static final float FPS = 10f;

    private CameraSource cameraSource = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new HolderCallBack());

        BarcodeProcessorFactory factory = new BarcodeProcessorFactory();

        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.EAN_13).build();
        detector.setProcessor(new MultiProcessor.Builder<>(factory).build());

        CameraSource.Builder cameraSourceBuilder = new CameraSource.Builder(getApplicationContext(),detector);
        cameraSourceBuilder.setFacing(CameraSource.CAMERA_FACING_BACK);
        cameraSourceBuilder.setRequestedFps(FPS);
        cameraSourceBuilder.setAutoFocusEnabled(true);
        cameraSource = cameraSourceBuilder.build();

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(new CameraOverlayView(getApplicationContext()),params);

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(cameraSource != null){
            cameraSource.stop();
            cameraSource.release();
            cameraSource = null;
        }
    }

    private class HolderCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try{
                cameraSource.start(surfaceHolder);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if(cameraSource != null) {
                cameraSource.stop();
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private class BarcodeProcessorFactory implements MultiProcessor.Factory<Barcode>{
        @Override
        public Tracker<Barcode> create(Barcode barcode){
            return new BarcodeTracker();
        }
    }

    private class BarcodeTracker extends Tracker<Barcode>{
        @Override
        public void onNewItem(int id, Barcode barcode) {
            Rect targetRect = new Rect(0, 0, cameraSource.getPreviewSize().getWidth(), cameraSource.getPreviewSize().getHeight());
            targetRect.offset(0, cameraSource.getPreviewSize().getHeight() / 2);

            if(targetRect.contains(barcode.getBoundingBox()) && !barcode.rawValue.contains("1920")){
                Intent intent = new Intent();
                intent.putExtra("barcode",barcode.rawValue);
                setResult(RESULT_OK,intent);
                finish();
            }

            /*
            Intent intent = new Intent();
            intent.putExtra("barcode", barcode.rawValue);
            setResult(RESULT_OK, intent);
            finish();
            */
        }
    }
}
