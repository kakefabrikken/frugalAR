package no.ntnu.tpg4850.frugalar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
                    canvas.drawCircle(this.viewMargin + p.x*0.5f,p.y, 15, paint);

                }
                paint.setColor(Color.BLACK);
                Point mid = c.getMidpoint();
                //TODO:Get real size and scale text according to distance. Faux 3D effect
                paint.setTextSize(48f);
                canvas.drawText(c.toDisplay(), mid.x, mid.y, paint);

            }
        }
        this.invalidate();
    }
}

