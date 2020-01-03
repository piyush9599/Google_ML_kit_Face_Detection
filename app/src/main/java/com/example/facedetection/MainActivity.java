package com.example.facedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.facedetection.helper.GraphicOverlay;
import com.example.facedetection.helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
   private Button facedetectionbutton;
    private GraphicOverlay graphicoverlay;
    private CameraView mCameraView;
    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facedetectionbutton = findViewById(R.id.detect_face_button);
        graphicoverlay =  findViewById(R.id.graphicoverlay);
        mCameraView = findViewById(R.id.camera_view);
        mAlertDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Please Wait, processing...")
                .setCancelable(false)
                .build();
        facedetectionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.start();
                mCameraView.captureImage();
                graphicoverlay.clear();
            }
        });
        mCameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                mAlertDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,mCameraView.getWidth(),mCameraView.getHeight(),false);
                mCameraView.stop();
                processfacedetection(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }
    private void processfacedetection(Bitmap bitmap){
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions );

    firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                @Override
                public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                  getfaceresults(firebaseVisionFaces);
                }
            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(MainActivity.this,"ERROR: "+  e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    });
    }
    private void getfaceresults(List<FirebaseVisionFace> firebaseVisionFaces){
        int counter = 0;
        for(FirebaseVisionFace face : firebaseVisionFaces){
            Rect rect = face.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicoverlay , rect);
            graphicoverlay.add(rectOverlay);
            counter =counter+1;
        }
        mAlertDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }
}
