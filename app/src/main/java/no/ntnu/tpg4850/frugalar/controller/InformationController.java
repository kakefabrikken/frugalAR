package no.ntnu.tpg4850.frugalar.controller;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

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
    private boolean qrInFocus;
    private Point[] qrFocusBounds;
    private boolean qrTextShown;

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
        String s = "";
        for(QRCode qr: recentQRCodes) {
            s += qr.id + " ";
        }

        this.view.show3DToast(s);
    }

    public void trigger() {
        QRCode selected = null;
        int[] reticuleBounds = view.getGraphicsviewDimensions();
        int width = reticuleBounds[0];
        int height = reticuleBounds[1];

        Point midPoint = new Point(width, height);
        Double leastDistance = -1.0;
        if (storage.getAll().size() > 0) {
            for (QRCode qr : storage.getAll()) {
                Double dist = getDistanceToQR(midPoint, qr);
                if (dist < leastDistance && dist > -1.0) {
                    leastDistance = dist;
                    selected = qr;
                }

            }
        }
        else {
            view.show3DToast("Storage empty. No QR code detected");
        }

        if (selected != null) {
            view.show3DToast(selected.toString());
        }
    }

    private Double getDistanceToQR(Point aim, QRCode qr) {
        /*
            bounds are received counter clock-wise
            4---3
            |   |
            |   |
            1---2
        */

        //Point[] bounds = qr.getBounds();
        int aimX = aim.x;
        int aimY = aim.y;

        Point qrMidPoint = qr.getMidpoint();
        int qrX = qrMidPoint.x;
        int qrY = qrMidPoint.y;

        Double xdelta = (double)(qrX - aimX);
        Double ydelta = (double)(qrY - aimY);

        return Math.sqrt( Math.pow(xdelta,2) + Math.pow(ydelta,2 ) );
    }

}
