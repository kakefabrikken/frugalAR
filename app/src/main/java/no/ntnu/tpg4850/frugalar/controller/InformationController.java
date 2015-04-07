package no.ntnu.tpg4850.frugalar.controller;

import android.hardware.Camera;
import java.util.ArrayList;
import no.ntnu.tpg4850.frugalar.CardboardOverlayView;
import no.ntnu.tpg4850.frugalar.network.ValveService;
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
    private ValveService network;
    private CardboardOverlayView view;

    public InformationController(CardboardOverlayView mOverlayView) {
        this.view = mOverlayView;
        this.storage = new QRStorage(10, 1000);
        this.scanner = new QRScanner(this.storage);
        this.network = new ValveService();
    }

    public void setCamera(Camera cam) {
        this.camera = cam;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        /*
        On previewFrame is called each time a new preview frame is available from the camera.
        The preview frame is feed to the qr scanner and the resulting qr codes.
         */
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        ArrayList<QRCode> QRInFocus = scanner.scanImage(data, size.width, size.height); //QR codes found for this specific image
        this.storage.updateAll();
        ArrayList<QRCode> recentQRCodes = this.storage.getAll();
        this.view.setCurrentQrData(recentQRCodes);
        //String s = "";
        for(QRCode qr: recentQRCodes) {
            //s += qr.id + " ";
            if(qr.test == false) {
                this.network.get(qr.id, qr);
                qr.test = true;
            }
        }

        //this.view.show3DToast(s);
    }



}
