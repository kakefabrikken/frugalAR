package no.ntnu.tpg4850.frugalar.scanner;

import android.graphics.Point;
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
    //TODO:Handle more than 1 qr at the same time.
    //TODO: Interpolation between detections, and trail off behavior. IE. QR object with bounds and
    //TODO:id kept for a fixed period of time, and removed if not renewed by a new sighting. Can also use accelorometer to improve this behavior.
    private int[] qrCodeBounds = null;
    private String qrId = null;

    public QRScanner(CardboardOverlayView mOverlayView) {
        this.mOverlayView = mOverlayView; //TODO: TEMP WAY of showing data in text hud
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

    public Point getMidpoint(int id) {
        //TODO: Only 1 qr code can be kept so id irrelevant. Use 0 as value.
        int[] v = this.qrCodeBounds;
        if(v != null && v.length>=3) {
            Point p = new Point((v[0]+v[2])/2, (v[1]+v[3])/2);
            return p;
        }
        return null;
    }

    public Point[] getBounds(int id) {
        int[] v = this.qrCodeBounds;
        int corners = 4;
        Point[] p = new Point[corners];
        if(v != null && v.length>=3) {
            p[0] = new Point(v[0], v[1]);
            p[1] = new Point(v[0]+v[2], v[1]);
            p[2] = new Point(v[0]+v[2], v[1]+ v[3]);
            p[3] = new Point(v[0], v[1]+ v[3]);
        }
        return p;
    }

    public int[] getBoundsRect(int id) {
        return this.qrCodeBounds;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;
        Image barcode = new Image(width, height, "Y800");
        barcode.setData(data);

        int result = mScanner.scanImage(barcode);
        if (result != 0) {
            mOverlayView.show3DToast("QR");
            SymbolSet syms = mScanner.getResults();
            Log.i(TAG, syms.toString());
            for (Symbol sym : syms) {
                //TODO: How to handle more than 1 qr in the image.
                //Currently assume that only one qr code is detected for each preview frame.
                Log.i(TAG, "barcode result " + sym.getData());
                this.qrId = sym.getData();
                this.qrCodeBounds = sym.getBounds();
                for(int i = 0; i<this.qrCodeBounds.length; i++) {
                    Log.i(TAG, this.qrCodeBounds[i] + "");
                }
            }
        }

    }
}
