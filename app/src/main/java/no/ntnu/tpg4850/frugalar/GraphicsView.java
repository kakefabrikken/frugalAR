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
            for(QRCode q: this.data) {
                if(q.getBoundsRaw() != null) {
                    this.drawQRBoundingBox(canvas, q);
                    //TODO: Reticule integration
                    this.drawPanel(canvas, q);
                }
                paint.setColor(Color.BLACK);
                Point mid = q.getMidpoint();
                paint.setTextSize(48f);
                canvas.drawText(q.id, mid.x, mid.y, paint);

            }
        }
        this.invalidate();
    }

    private void drawQRBoundingBox(Canvas c, QRCode q) {
        int[] b = q.getBoundsRaw();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);
        Rect rectangle = new Rect((int)((b[0]*0.65)-this.viewMargin), b[1], (int)((b[0]+b[2])*0.65), b[1]+b[3]);
        c.drawRect(rectangle, paint);
        paint.setStyle(Paint.Style.FILL);
    }
    public void drawPanel(Canvas c, QRCode q) {
        Point p = q.getMidpoint();
        paint.setColor(Color.argb(150, 42,54,59));
        paint.setTextSize(20.0f);
        //TODO: Scaling?
        int length = 400;
        int height = 250;
        int padding = 30;
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

