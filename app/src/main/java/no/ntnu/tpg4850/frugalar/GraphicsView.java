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

    private void drawReticule(Canvas c) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);

        int w = this.getWidth();
        int h = this.getHeight();

        // the following floats are offsets for establishing how far the
        // lines are to be from the center and how long they are to be
        // left and upper line
        float startOffset_Lower = 0.85f;
        float endOffset_Lower = 0.9f;
        //right and lower line
        float startOffset_Higher = 1.1f;
        float endOffset_Higher = 1.15f;

        // upper left corner is 0,0 so height is "at the bottom"
        // and width is to the right
        // by dividing them by 2 we get their axis midpoint, from which
        // we can write lines that are close to the middle
        // thereby building the reticule

        //first horizontal line
        float h1_x_0 = (float)(w/2.0f * startOffset_Lower);
        float h1_y_0 = (float)(h/2.0f);
        float h1_x_1 = (float)(w/2.0f * endOffset_Lower);
        float h1_y_1 = (float)(h/2.0f);
        float[] first_horizontal = {h1_x_0,h1_y_0, h1_x_1,h1_y_1};

        //second horizontal line
        float h2_x_0 = (float)(w/2.0f * startOffset_Higher);
        float h2_y_0 = (float)(h/2.0f);
        float h2_x_1 = (float)(w/2.0f * endOffset_Higher);
        float h2_y_1 = (float)(h/2.0f);
        float[] second_horizontal = {h2_x_0,h2_y_0, h2_x_1,h2_y_1};

        //first vertical line
        float h3_x_0 = (float)(w/2.0f);
        float h3_y_0 = (float)(h/2.0f * startOffset_Lower);
        float h3_x_1 = (float)(w/2.0f);
        float h3_y_1 = (float)(h/2.0f * endOffset_Lower);
        float[] first_vertical = {h3_x_0,h3_y_0, h3_x_1,h3_y_1};

        //second vertical line
        float h4_x_0 = (float)(w/2.0f);
        float h4_y_0 = (float)(h/2.0f * startOffset_Higher);
        float h4_x_1 = (float)(w/2.0f);
        float h4_y_1 = (float)(h/2.0f * endOffset_Higher);
        float[] second_vertical = {h4_x_0,h4_y_0, h4_x_1,h4_y_1};

        //lines, clock-wise from left horizontal arrow
        drawLineHelper(first_horizontal, c, p);
        drawLineHelper(first_vertical, c,p);
        drawLineHelper(second_horizontal, c,p);
        drawLineHelper(second_vertical, c,p);
    }

    private void drawLineHelper(float[] line, Canvas c, Paint p) {
        c.drawLine(line[0],line[1],line[2],line[3], p);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawReticule(canvas);
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
}

