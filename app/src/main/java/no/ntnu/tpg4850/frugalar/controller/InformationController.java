package no.ntnu.tpg4850.frugalar.controller;

import android.hardware.Camera;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardActivity;

import java.util.ArrayList;

import no.ntnu.tpg4850.frugalar.CardboardOverlayView;
import no.ntnu.tpg4850.frugalar.scanner.QRCode;
import no.ntnu.tpg4850.frugalar.scanner.QRScanner;
import no.ntnu.tpg4850.frugalar.scanner.QRStorage;

/**
 * Created by Olav on 25.03.2015.
 */
public class InformationController implements Camera.PreviewCallback {

    public final static String TAG = "CONTROLLER";
    private Camera camera;
    private QRStorage storage;
    private QRScanner scanner;
    private CardboardOverlayView view;

    public InformationController(CardboardOverlayView mOverlayView) {
        this.view = mOverlayView;
        this.storage = new QRStorage(10, 1000);
        this.scanner = new QRScanner(this.storage);
    }

    public void setCamera(Camera cam) {
        this.camera = cam;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.i("MainActivity", "ONPREVIEW FRAME");
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        ArrayList<QRCode> l = scanner.scanImage(data, size.width, size.height);
        this.storage.updateAll();
        String s = "";
        for(QRCode qr: l) {
            s += qr.id + " ";
        }

        this.view.show3DToast(s);
    }



}
