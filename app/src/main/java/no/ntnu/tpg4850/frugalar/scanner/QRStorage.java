package no.ntnu.tpg4850.frugalar.scanner;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Olav on 18.03.2015.
 */
public class QRStorage {
    private ArrayList<QRCode> storage = new ArrayList<QRCode>();
    private int timeLimit = 1000;

    public QRStorage(int maxLim, int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean Store(QRCode code) {
        boolean isNew = QRStorage.containsId(this.storage, code.id);
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

    public QRCode get(int id) {
        int idx = QRStorage.getIndexOfId(this.storage, id);
        if(idx>-1) {
            return this.storage.get(idx);
        }
        return null;
    }

    public void updateAll() {
        //TODO: Should probabily be done async.
        long now = (new Date()).getTime();
        for(int i = 0; i<this.storage.size(); i++) {
            long diff = now - this.storage.get(i).previouslySeen.getTime();
            if(diff > this.timeLimit) {
                this.storage.remove(i);
            }
        }
    }
    private static boolean containsId(ArrayList<QRCode> list, int id) {
        for(QRCode c: list) {
            if (c.id == id) {
                return true;
            }
        }
        return false;
    }

    private static int getIndexOfId(ArrayList<QRCode> list, int id) {
        for(int i = 0; i<list.size(); i++) {
            if(list.get(i).id == id) {
                return i;
            }
        }
        return -1;
    }
}
