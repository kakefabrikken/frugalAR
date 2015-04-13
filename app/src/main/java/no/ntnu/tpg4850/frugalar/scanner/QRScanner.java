package no.ntnu.tpg4850.frugalar.scanner;

import android.util.Log;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import java.util.ArrayList;


public class QRScanner {

    private static final String TAG = "QRScanner";

    private ImageScanner mScanner;
    private QRStorage storage;
    //TODO: Interpolation between detections, and trail off behavior. IE. QR object with bounds and
    //TODO:id kept for a fixed period of time, and removed if not renewed by a new sighting. Can also use accelorometer to improve this behavior.

    public QRScanner(QRStorage storage) {
        this.storage = storage;
        setupScanner();
    }

    /**
     * Sets up a scanner using the zbar libary. The scanner is configured to look for
     * formats defined in BarcodeFormat.ALL_FORMATS
     */
    private void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        for(BarcodeFormat format : BarcodeFormat.QR_CODE_FORMAT) {
            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
        }
    }

    /**
     * scanImage, use the zbar scanner to extract qr bounds and content from the image. QRCode objects
     * are made from bounds, date and content of found qr codes, and then stored in QRStorage.
     * @param data - Image data
     * @param width - Width of image
     * @param height - Height of image
     * @return ArrayList of found QRCodes in the image.
     */
    public ArrayList<QRCode> scanImage(byte[] data, int width, int height) {
        Image barcode = new Image(width, height, "Y800");
        barcode.setData(data);
        ArrayList<QRCode> detectedList = new ArrayList<QRCode>();
        int result = mScanner.scanImage(barcode);
        if (result != 0) {
            SymbolSet syms = mScanner.getResults();
            Log.i(TAG, syms.toString());
            for (Symbol sym : syms) {
                Log.i("abcd", "barcode result " + sym.getData());
                QRCode q = new QRCode(sym.getData(), sym.getBounds());
                Log.i(TAG, this.storage.size()+ "");
                this.storage.Store(q);
                detectedList.add(q);

                //TODO: async storage update every so and so ms
            }
        }
        Log.i("abcd",detectedList.size() + "");
        return detectedList;
    }
}
