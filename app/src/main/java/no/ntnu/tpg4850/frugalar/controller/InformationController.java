package no.ntnu.tpg4850.frugalar.controller;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;

import no.ntnu.tpg4850.frugalar.CardboardOverlayView;
import no.ntnu.tpg4850.frugalar.network.ValveService;
import no.ntnu.tpg4850.frugalar.scanner.QRCode;
import no.ntnu.tpg4850.frugalar.scanner.QRScanner;
import no.ntnu.tpg4850.frugalar.scanner.QRStorage;

/**
 * Created by Olav on 25.03.2015.
 */
public class InformationController implements Camera.PreviewCallback {

    public static final long MAX_LOOK_AT_TIME = 1500; // measured in milliseconds
    public final static String TAG = "CONTROLLER";
    private Camera camera;
    private QRStorage storage;
    private QRScanner scanner;
    private ValveService network;
    private CardboardOverlayView view;
    private boolean qrInFocus;
    private Point[] qrFocusBounds;
    private boolean qrTextShown;
    private QRCode qrFocus;
    private Point midPoint;
    private long qrFocusStartTimer;



    public InformationController(CardboardOverlayView mOverlayView) {
        this.view = mOverlayView;
        this.storage = new QRStorage(10, 1000);
        this.scanner = new QRScanner(this.storage);
        this.network = new ValveService();
        qrInFocus = false;
        midPoint = getMidPoint();
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
            if (qrInFocus(qr,midPoint)) {
                displayQrFocusInformation(qr);
            }
            if(!qr.isData()) {
                this.network.get(qr.id, qr);
            }
        }

        //this.view.show3DToast(s);
    }

    //private

    public void trigger() {
        Log.i("Entering trigger", "Now what");
        QRCode selected = null;
        /*
        int[] reticuleBounds = view.getGraphicsviewDimensions();
        int width = reticuleBounds[0];
        int height = reticuleBounds[1];

        Point midPoint = new Point((int)(width/2.0), (int)(height/2.0));
        */
        Double leastDistance = -1.0;
        if (storage.getAll() != null) {//storage.getAll().size() > 0) {
            Log.i("storage nonempty", "we have action");
            for (QRCode qr : storage.getAll()) {
                Log.i("QR code" , "" + storage.getAll().size());
                if (qrInFocus(qr, midPoint)) {
                    Log.i("qrFocus", "KAAAAAAAAAAAAAAAAAAAAAAHN");
                    view.show3DToast("WE HAVE DA QR\n" + qr.toString());
                }
            }
        }
        else {
            Log.i("storage empty", "no action");
            view.show3DToast("Storage empty. No QR code detected");
        }

        if (selected != null) {
            view.show3DToast(selected.toString());
        }
    }

    // sets the graphics model qr code data if the qr code has been in focus for at least MAX_LOOK_AT_TIME milliseconds
    private void displayQrFocusInformation(QRCode qr) {
        // there is a qr code in focus. This previously set qr code and the currently observed qr code are the same
        if (qrFocus != null && qrFocus.getId().equals(qr.getId())) {
            // haven't looked long enough at the qr code
            if (System.currentTimeMillis() - qrFocusStartTimer < MAX_LOOK_AT_TIME) {}
            else {
                ArrayList<QRCode> arr = new ArrayList<QRCode>(1);
                arr.add(qrFocus);
                view.setCurrentQrData(arr);
            }
        }
        else {
            qrFocus = qr;
            qrFocusStartTimer = System.currentTimeMillis();
        }

        /*
        if (qrFocus == null) { // we have no qr set in focus from before
            qrFocus = qr;
            qrFocusStartTimer = System.currentTimeMillis();
        }
        else if (qrFocus.getId().equals(qr.getId())) { // previously set qr code and currently observed qr code are the same
            // haven't looked long enough at the qr code
            if (System.currentTimeMillis() - qrFocusStartTimer < MAX_LOOK_AT_TIME) {}
            else {
                ArrayList<QRCode> arr = new ArrayList<QRCode>(1);
                arr.add(qrFocus);
                view.setCurrentQrData(arr);
            }
        }
        else {
            // qr focus and currently observed qr code are not the same, so reset qr focus and the timer
            qrFocus = qr;
            qrFocusStartTimer = System.currentTimeMillis();
        }
        */
    }

    private Point getMidPoint() {
        int[] reticuleBounds = view.getGraphicsviewDimensions();
        int width = reticuleBounds[0];
        int height = reticuleBounds[1];

        return new Point((int)(width/2.0), (int)(height/2.0));
    }

    private boolean qrInFocus(QRCode qr , Point midpoint) {
        /*
            bounds are received counter clock-wise
            4-----------3
            |     .     |
            | midpoint  |
            1-----------2
        */
        Point[] bounds = qr.getBounds();
        Point p1 = bounds[0];
        Point p3 = bounds[2];
        /*
                     ---p3
            |   .        |
            |            |
            p1--
            Checking if midpoint is contained by the points p1 and p3
         */
        if (p1.x <= midpoint.x && p1.y <= midpoint.y && p3.x >= midpoint.x && p3.y >= midpoint.y) {
            return true;
        }
        else {
            return false;
        }
    }

    private Double getDistanceToQR(Point aim, QRCode qr) {


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
