/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.ntnu.tpg4850.frugalar;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import com.google.vrtoolkit.cardboard.*;
import javax.microedition.khronos.egl.EGLConfig;
import java.io.IOException;
import java.nio.ByteBuffer;

import no.ntnu.tpg4850.frugalar.controller.InformationController;
import no.ntnu.tpg4850.frugalar.scanner.QRScanner;


/**
 * Cardboard application. Will display a camera preview for each eye.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer, OnFrameAvailableListener {

    public static final String TAG = "MainActivity";

    private Camera myCamera = null;
    private ByteBuffer indexBuffer;    // Buffer for index-array
    private int texture;
    private CardboardOverlayView mOverlayView;
    private CardboardView cardboardView;
    private SurfaceTexture surface;
    //private float[] mView;
    //private float[] mCamera;
    private CameraEyeTransformer cameraPreviewTransformer;
    private QRScanner qr;
    private InformationController controller;


    public void startCamera(int texture) {
        surface = new SurfaceTexture(texture);
        surface.setOnFrameAvailableListener(this);

        try {
            myCamera = Camera.open();
        }
        catch (Exception e) {
            //ADDED try catch to isolate problem.
            Log.w("MainActivity","CAMERA SERVICE FAILED, OTHER PROCESS HAS LOCKED CAMERA");
        }
        try {

            myCamera.setPreviewTexture(surface);
            myCamera.setPreviewCallback(controller);
            myCamera.startPreview();
            this.controller.setCamera(myCamera);
        }
        catch (IOException ioe) {
            Log.w("MainActivity","CAMERA LAUNCH FAILED");
        }
    }

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Oncreate");
        super.onCreate(savedInstanceState);

        this.cameraPreviewTransformer = new CameraEyeTransformer();
        setContentView(R.layout.common_ui);
        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        mOverlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        mOverlayView.show3DToast("FrugalAR");
        this.controller = new InformationController(this.mOverlayView);

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        this.releaseCamera();
    }

    private void releaseCamera() {
        Log.i(TAG, "RELEASE");
        if (myCamera != null) {
            myCamera.stopPreview();
            myCamera.setPreviewCallback(null);
            myCamera.release();
            myCamera = null;
        }
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    /**
     * Creates the buffers we use to store information about the 3D world. OpenGL doesn't use Java
     * arrays, but rather needs data in a format it can understand. Hence we use ByteBuffers.
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        cameraPreviewTransformer.createSurface();

        texture = CameraEyeTransformer.createTexture();
        startCamera(texture);
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        Log.i(TAG, "NEW FRAME");
        float[] mtx = new float[16];
        cameraPreviewTransformer.clearGL();
        surface.updateTexImage();
        surface.getTransformMatrix(mtx);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture arg0) {
        Log.i(TAG, "ONFRAMEAVAILABLE");
        this.cardboardView.requestRender();
    }

    /**
     * Draws a frame for an eye. The transformation for that eye (from the myCamera) is passed in as
     * a parameter.
     * @param transform The transformations to apply to render this eye.
     */
    @Override
    public void onDrawEye(EyeTransform transform) {
        Log.i(TAG, "DRAW EYE");
        cameraPreviewTransformer.drawEye(texture);
        //Matrix.multiplyMM(mView, 0, transform.getEyeView(), 0, mCamera, 0);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onCardboardTrigger() {
        //When magnetic button has been triggered
        Log.i(TAG, "onCardboardTrigger");
        controller.trigger();
    }
}