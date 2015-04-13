package no.ntnu.tpg4850.frugalar.scanner;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Date;

import no.ntnu.tpg4850.frugalar.network.Valve;

/**
 * Created by Olav on 18.03.2015.
 */
public class QRCode {
    public String id;
    public Date previouslySeen;
    public int[] bounds;
    public Valve valve;
    public boolean test = false;

    public QRCode(String id, int[] bounds) {
        this.id = id;
        this.bounds = bounds;
        this.previouslySeen = new Date();
    }

    public void setValve(Valve v) {
        this.valve = v;
    }

    public String getId() {
        return this.id;
    }

    public boolean isData() {
        if(this.valve != null) {
            return true;
        }
        return false;
    }

    public Valve getValve() {
        return this.valve;
    }

    public String toDisplay() {
        if(this.valve == null) {
            //TODO: Handle errors. Show em?
            return "No data yet for valve #" + this.id;
        }
        return this.valve.status;
    }
    public Point getMidpoint() {

        int[] v = this.bounds;
        if(v != null && v.length>=3) {
            Point p = new Point((v[0]+v[2])/2, (v[1]+v[3])/2);
            return p;
        }
        return null;
    }
    public int[] getBoundsRaw() {
        return this.bounds;
    }

    public Rect getBoundsRect() {
        int[] v = this.bounds;
        if(v != null && v.length>=3) {
            Rect rectangle = new Rect(v[0], v[1], v[0]+v[2], v[1]+v[3]);
            return rectangle;
        }
        return null;
    }
    public Point[] getBounds() {
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
