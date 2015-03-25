package no.ntnu.tpg4850.frugalar.scanner;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.ArrayList;

import no.ntnu.tpg4850.frugalar.CardboardOverlayView;

public class QRScanner implements Camera.PreviewCallback {

    private static final String TAG = "QRScanner";

    private ImageScanner mScanner;
    private Camera mCamera;
    private QRStorage storage;
    //TODO: Interpolation between detections, and trail off behavior. IE. QR object with bounds and
    //TODO:id kept for a fixed period of time, and removed if not renewed by a new sighting. Can also use accelorometer to improve this behavior.
    private int[] qrCodeBounds = null;
    private String qrId = null;

    public QRScanner(QRStorage storage) {
        this.storage = storage;
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


    public ArrayList<QRCode> scanImage(byte[] data, int width, int height) {
        Image barcode = new Image(width, height, "Y800");
        barcode.setData(data);
        ArrayList<QRCode> detectedList = new ArrayList<QRCode>();
        int result = mScanner.scanImage(barcode);
        if (result != 0) {
            SymbolSet syms = mScanner.getResults();
            Log.i(TAG, syms.toString());
            for (Symbol sym : syms) {
                Log.i(TAG, "barcode result " + sym.getData());
                QRCode q = new QRCode(sym.getData(), sym.getBounds());
                Log.i(TAG, this.storage.size()+ "");
                this.storage.Store(q);
                detectedList.add(q);

                //TODO: async storage update every so and so ms

            }
        }
        //TODO: return new qrcodes
        return detectedList;
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
            SymbolSet syms = mScanner.getResults();
            Log.i(TAG, syms.toString());
            for (Symbol sym : syms) {
                Log.i(TAG, "barcode result " + sym.getData());
                QRCode q = new QRCode(sym.getData(), sym.getBounds());
                Log.i(TAG, this.storage.size()+ "");
                this.storage.Store(q);
                //TODO: async storage update every so and so ms

            }
        }
        this.storage.updateAll();

    }
}
