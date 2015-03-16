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
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import com.google.vrtoolkit.cardboard.*;
import javax.microedition.khronos.egl.EGLConfig;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.text.TextUtils;
import net.sourceforge.zbar.Config;


/**
 * Cardboard application. Will display a camera preview for each eye.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer, OnFrameAvailableListener,  Camera.PreviewCallback {

    private static final String TAG = "MainActivity";

    private Camera myCamera = null;
    private ByteBuffer indexBuffer;    // Buffer for index-array
    private int texture;
    private CardboardOverlayView mOverlayView;
    private CardboardView cardboardView;
    private SurfaceTexture surface;
    //private float[] mView;
    //private float[] mCamera;
    private CameraEyeTransformer cameraPreviewTransformer;
    private ImageScanner mScanner;


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
            myCamera.setPreviewCallback(this);
            myCamera.startPreview();
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
        super.onCreate(savedInstanceState);

        this.cameraPreviewTransformer = new CameraEyeTransformer();
        setContentView(R.layout.common_ui);
        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        //mCamera = new float[16];
        //mView = new float[16];
        mOverlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        mOverlayView.show3DToast("FrugalAR");
        setupScanner();
    }

    public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        for(BarcodeFormat format : BarcodeFormat.ALL_FORMATS) {
            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        this.releaseCamera();
    }

    private void releaseCamera() {
        if (myCamera != null) {
            myCamera.stopPreview();
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
        float[] mtx = new float[16];
        cameraPreviewTransformer.clearGL();
        surface.updateTexImage();
        surface.getTransformMatrix(mtx);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture arg0) {
        this.cardboardView.requestRender();
    }

    /**
     * Draws a frame for an eye. The transformation for that eye (from the myCamera) is passed in as
     * a parameter.
     * @param transform The transformations to apply to render this eye.
     */
    @Override
    public void onDrawEye(EyeTransform transform) {
        cameraPreviewTransformer.drawEye(texture);
        //Matrix.multiplyMM(mView, 0, transform.getEyeView(), 0, mCamera, 0);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.i(TAG, "ON PREVIEW FRAME");
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;
        Image barcode = new Image(width, height, "Y800");
        barcode.setData(data);

        int result = mScanner.scanImage(barcode);

        if (result != 0) {
            //releaseCamera();
            //mOverlayView.show3DToast("QR");
            mOverlayView.showPoint();
            //Log.i(TAG, "ROCOGNIZED!");

            /*SymbolSet syms = mScanner.getResults();
            Result rawResult = new Result();
            for (Symbol sym : syms) {
                String symData = sym.getData();
                if (!TextUtils.isEmpty(symData)) {
                    rawResult.setContents(symData);
                    rawResult.setBarcodeFormat(BarcodeFormat.getFormatById(sym.getType()));
                    Log.i(TAG, symData);
                    break;

                }



            }
            */
            myCamera.setOneShotPreviewCallback(this);
        } else {
            myCamera.setOneShotPreviewCallback(this);
        }

    }
    @Override
    public void onCardboardTrigger() {
        //When magnetic button has been triggered
        Log.i(TAG, "onCardboardTrigger");
    }
}