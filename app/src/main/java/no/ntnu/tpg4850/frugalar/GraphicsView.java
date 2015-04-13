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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import no.ntnu.tpg4850.frugalar.network.Valve;
import no.ntnu.tpg4850.frugalar.scanner.QRCode;

public class GraphicsView extends View {

    Paint paint = new Paint();
    public ArrayList<QRCode> data;
    private float viewMargin = 0.0f;
    private int cameraWidth = 100;
    SimpleDateFormat dfwt = new SimpleDateFormat("dd-mm-yy hh:mm:ss");
    SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");

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
        //int w = this.getWidth();

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
        int length = 500;
        int height = 350;
        int padding = 20;
        p.x = (int) (( p.x)*0.65);
        p.y = (int) ((p.y));
        Rect panelBounds = new Rect(p.x, p.y, p.x+length, p.y+height);
        c.drawRect(panelBounds, paint);

        if(!q.isData()) {
            paint.setColor(Color.rgb(254,206,168));
            c.drawText("Data not retrieved yet", p.x+ (length/2)-2*padding, p.y+ (height/2), paint);
        }
        else {
            Valve v = q.getValve();
            if(!v.error) {
                paint.setColor(Color.rgb(153,186,152));
            }
            else {
                paint.setColor(Color.rgb(232,74,95));
            }
            c.drawCircle(p.x+padding, p.y+padding, 10, paint);
            paint.setColor(Color.rgb(254,206,168));
            //TODO: Retrieve text from valve
            c.drawText(v.valveStatus + "% open", p.x+(2*padding), p.y+padding, paint);
            //c.drawText("Installed: " + df.format(v.installed) , p.x+(3*padding), p.y+padding, paint);
            c.drawText(v.status, p.x + padding, p.y+(2*padding), paint);
            int MAX_HISTORY = 5;
            for(int i = 0; i<v.history.size() || i< MAX_HISTORY; i++) {
                Date d = v.history.get(i).date;
                c.drawText(dfwt.format(d), p.x + padding, p.y + (4*padding) + (i*padding), paint);
                c.drawText(v.history.get(i).message, p.x + 220, p.y + (4*padding) + (i*padding), paint);
            }
        }
    }
}

