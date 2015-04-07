package no.ntnu.tpg4850.frugalar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import no.ntnu.tpg4850.frugalar.scanner.QRCode;

public class GraphicsView extends View {

    Paint paint = new Paint();
    public ArrayList<QRCode> data;
    private float viewMargin = 0.0f;
    private int cameraWidth = 100;

    public GraphicsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);

    }

    public void setViewMargin(float m) {
        this.viewMargin = m;
    }
    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.BLUE);
        int w = this.getWidth();

        if(this.data != null) {
            for(QRCode c: this.data) {
                paint.setColor(Color.RED);
                Point[] rect = c.getBounds();
                for (Point p: rect) {
                    canvas.drawCircle(p.x*0.75f,p.y, 15, paint);

                }
                //canvas.drawCircle(center.x,center.y, 10, paint);
            }
        }
        this.invalidate();
    }

    public void drawPanel(Canvas c, QRCode q) {
        Point p = q.getMidpoint();
        paint.setColor(Color.argb(150, 153,184,152));
        //TODO: Scaling?
        int length = 200;
        int height = 150;
        int padding = 10;
        Rect panelBounds = new Rect(p.x, p.y, p.x+length, p.y+height);
        c.drawRect(panelBounds, paint);

        paint.setColor(Color.rgb(153,186,152));
        c.drawCircle(p.x+padding, p.y+padding, 10, paint);
        paint.setColor(Color.rgb(254,206,168));
        //TODO: Retrieve text from valve
        c.drawText("Updated: 13:07 07.04.15", p.x+(3*padding), p.y+padding, paint);
        c.drawText("State: Installed 21.11.14", p.x + padding, p.y+(3*padding), paint);
    }
}

