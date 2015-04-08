package no.ntnu.tpg4850.frugalar.scanner;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Olav on 18.03.2015.
 */
public class QRStorage {
    private ArrayList<QRCode> storage;
    private int timeLimit = 1000;

    public QRStorage(int maxLim, int timeLimit) {
        this.storage = new ArrayList<QRCode>();
        this.timeLimit = timeLimit;
    }

    /**
     * Store method will store only unique qrcodes. If an exisiting QR is sent as an argument,
     * the bounds and date are updated.
     * @param code - QRCode to be stored
     * @return If a new qr has been added or date for exisiting has been renewed.
     */
    public boolean Store(QRCode code) {

        boolean isNew = !QRStorage.containsId(this.storage, code.id);
        if(isNew) {
            storage.add(code);
        }
        else {
            QRCode q = storage.get(QRStorage.getIndexOfId(this.storage, code.id));
            q.updateDate();
            q.bounds = code.bounds;
        }
        return isNew;
    }

    public int size() {
        return this.storage.size();
    }

    public QRCode get(String id) {
        int idx = QRStorage.getIndexOfId(this.storage, id);
        if(idx>-1) {
            return this.storage.get(idx);
        }
        return null;
    }

    /**
     * UpdateAll removes any QRCode in storage that has been there for longer than a
     * timeLimit. The timelimit can be adjusted when initializing object.
     */
    public void updateAll() {
        //TODO: Should probably be done async.
        long now = (new Date()).getTime();
        for(int i = 0; i<this.storage.size(); i++) {
            long diff = now - this.storage.get(i).previouslySeen.getTime();
            if(diff > this.timeLimit) {
                this.storage.remove(i);
            }
        }
    }

    public ArrayList<QRCode> getAll() {
        return this.storage;
    }

    /**
     * Method to check whether a id is in storage or not
     * @param list - storage
     * @param id - Id or content of QR code
     * @return True if QRCode is in list, false otherwise
     */
    private static boolean containsId(ArrayList<QRCode> list, String id) {
        for(QRCode c: list) {
            if (c.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return index of the QRCode with id supplied as argument. If there are no matches, an index
     * of -1 is returned.
     * @param list
     * @param id
     * @return
     */
    private static int getIndexOfId(ArrayList<QRCode> list, String id) {
        for(int i = 0; i<list.size(); i++) {
            if(list.get(i).id.equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
