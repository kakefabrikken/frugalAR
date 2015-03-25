package no.ntnu.tpg4850.frugalar.scanner;

import android.graphics.Point;
import java.util.Date;

/**
 * Created by Olav on 18.03.2015.
 */
public class QRCode {
    public String id;
    public Date previouslySeen;
    public int[] bounds;

    public QRCode(String id, int[] bounds) {
        this.id = id;
        this.bounds = bounds;
        this.previouslySeen = new Date();
    }

    public Point getMidpoint(int id) {
        //TODO: Only 1 qr code can be kept so id irrelevant. Use 0 as value.
        int[] v = this.bounds;
        if(v != null && v.length>=3) {
            Point p = new Point((v[0]+v[2])/2, (v[1]+v[3])/2);
            return p;
        }
        return null;
    }
    public int[] getBoundsRect(int id) {
        return this.bounds;
    }

    public Point[] getBounds(int id) {
        int[] v = this.bounds;
        int corners = 4;
        Point[] p = new Point[corners];
        if(v != null && v.length>=3) {
            p[0] = new Point(v[0], v[1]);
            p[1] = new Point(v[0]+v[2], v[1]);
            p[2] = new Point(v[0]+v[2], v[1]+ v[3]);
            p[3] = new Point(v[0], v[1]+ v[3]);
        }
        return p;
    }

    public void updateDate() {
        this.previouslySeen = new Date();
    }

    public String toString() {
        return "ID: " + this.id + " Last seen: " + this.previouslySeen;
    }
}
