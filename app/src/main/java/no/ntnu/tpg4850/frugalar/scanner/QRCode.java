package no.ntnu.tpg4850.frugalar.scanner;

import java.util.Date;

/**
 * Created by Olav on 18.03.2015.
 */
public class QRCode {
    public int id;
    public Date previouslySeen;

    public QRCode(int id) {
        this.id = id;
        this.previouslySeen = new Date();
    }

    public void updateDate() {
        this.previouslySeen = new Date();
    }
}
