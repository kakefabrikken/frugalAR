package no.ntnu.tpg4850.frugalar.scanner;

import java.util.ArrayList;

/**
 * Created by Olav on 18.03.2015.
 */
public class QRStorage {
    private ArrayList<QRCode> storage = new ArrayList<QRCode>();

    public QRStorage(int maxLim) {

    }

    public boolean Store(QRCode code) {
        boolean isNew = QRStorage.containsId(this.storage, code.id);
        if(isNew) {
            storage.add(code);
        }
        else {

            storage.get(QRStorage.getIndexOfId(this.storage, code.id)).
        }
        return isNew;
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
            if(list[i].id == id) {
                return i;
            }
        }
        return -1;
    }
}
