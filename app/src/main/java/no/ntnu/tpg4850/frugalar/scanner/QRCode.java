package no.ntnu.tpg4850.frugalar.scanner;

import java.util.Date;

/**
 * Created by Olav on 18.03.2015.
 */
public class QRCode {
    public int id;
    public Date previouslySeen;
    public int[] bounds;

    public QRCode(int id, int[] bounds) {
        this.id = id;
        this.bounds = bounds;
        this.previouslySeen = new Date();
    }

    public void updateDate() {
        this.previouslySeen = new Date();
    }

    public String toString() {
        return "ID: " + this.id + " Last seen: " + this.previouslySeen;
    }
}
