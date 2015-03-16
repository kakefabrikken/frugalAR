package no.ntnu.tpg4850.frugalar.scanner;

import android.hardware.Camera;
import android.util.Log;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import no.ntnu.tpg4850.frugalar.CardboardOverlayView;

public class QRScanner implements Camera.PreviewCallback {

    private static final String TAG = "QRScanner";
    private ImageScanner mScanner;
    private CardboardOverlayView mOverlayView;
    private Camera mCamera;

    public QRScanner(CardboardOverlayView mOverlayView) {
        this.mOverlayView = mOverlayView; //TEMP WAY of showing data in text hud
        setupScanner();
    }

    public void setCamera(Camera c) {
        this.mCamera = c;
    }

    private void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        for(BarcodeFormat format : BarcodeFormat.ALL_FORMATS) {
            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
        }
    }

    @Override
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
            mOverlayView.show3DToast("QR");
            //Log.i(TAG, "ROCOGNIZED!");
            SymbolSet syms = mScanner.getResults();
            Log.i(TAG, syms.toString());
            for (Symbol sym : syms) {
                Log.i(TAG, "barcode result " + sym.getData());
                for(int i: sym.getBounds()) {
                    Log.i(TAG, i + " pos");
                }
                Log.i(TAG, "barcode position" + sym.getBounds());
                Log.i(TAG, "barcode position" + sym.getOrientation());
                Log.i(TAG, "barcode position" + sym.toString());
                //barcodeScanned = true;
            }
        }
        //mCamera.setOneShotPreviewCallback(this);

    }
}