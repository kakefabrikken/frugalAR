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
import java.util.List;

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



    @Override
    public void onDraw(Canvas canvas) {
        drawReticule(canvas);
        //canvas.drawColor(Color.BLUE);
        //int w = this.getWidth();

        if(this.data != null) {
            for(QRCode q: this.data) {
                if(q.getBoundsRaw() != null) {
                    this.drawQRBoundingBox(canvas, q);
                    if(!q.isData()) {
                        drawIdAndStatus(canvas,q);
                    }
                    //TODO: Reticule integration
                    //q.getinFocus() &&
                    if (q.isData()){
                        this.drawPanel(canvas, q);
                    }
                }
            }
        }
        this.invalidate();

    }

    private void drawIdAndStatus(Canvas c, QRCode q) {
        Point p = q.getMidpoint();
        int padding = 20;

        paint.setTextSize(30f);
        paint.setColor(Color.rgb(64,64,64));
        c.drawText(q.id + "", p.x, p.y, paint);
        if(!q.networkFailed) {
            c.drawText("Retrieving data", p.x, p.y + 2*padding, paint);
        }
        else {
            c.drawText("Could not retrieve data", p.x, p.y + 2 * padding, paint);
        }
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
        c.drawLine(line[0], line[1], line[2], line[3], p);
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
        int padding = 20;

        paint.setColor(Color.argb(150, 42,54,59));
        paint.setTextSize(20.0f);
        //TODO: Scaling?
        int length = 520;
        int height = 350;

        p.x = (int) (( p.x)*0.65);
        p.y = (int) ((p.y)+50);
        Rect panelBounds = new Rect(p.x, p.y, p.x+length, p.y+height);
        c.drawRect(panelBounds, paint);
        Valve v = q.getValve();
        if(!v.error) {
            paint.setColor(Color.rgb(153,186,152));
        }
        else {
            paint.setColor(Color.rgb(232,74,95));
        }
        c.drawCircle(p.x +padding, p.y+padding/2, 7, paint);
        paint.setColor(Color.rgb(254,206,168));
        //TODO: Retrieve text from valve
        c.drawText(v.type + " " + v.id, p.x+(2*padding), p.y+padding, paint);
        c.drawText(v.valveStatus + "% open", p.x+(8*padding), p.y+padding, paint);
        c.drawText(v.flow + " flow", p.x+(14*padding), p.y+padding, paint);
        //c.drawText("Installed: " + df.format(v.installed) , p.x+(3*padding), p.y+padding, paint);
        this.drawMultiline(c, v.status, 50,p.x + padding, p.y+(2*padding));

        int MAX_LINES = 10;
        int line = 0;
        int history_y = p.y + (5*padding);
        for(int i = 0; i<v.history.size() && line< MAX_LINES; i++) {
            Date d = v.history.get(i).date;
            c.drawText(dfwt.format(d), p.x + padding, history_y, paint);
            int nr_lines = this.drawMultiline(c, v.history.get(i).message, 30, p.x + 220, history_y);
            line += nr_lines;
            history_y += nr_lines * (-paint.ascent() + paint.descent())*1.1;
        }
        int valve_y = p.y+ height - 2*padding;
        c.drawText("Open:" + v.turnsToOpen, p.x + padding, valve_y, paint);
        c.drawText("Close:" + v.turnsToClosed, p.x + 6*padding, valve_y, paint);
        c.drawText("T.:" + v.temperature, p.x + 12*padding, valve_y, paint);

        int end_y = p.y+ height - padding;
        if(v.workPermission) {
            paint.setColor(Color.rgb(153,186,152));
            c.drawText(v.workPermissionInfo, p.x + padding, end_y, paint);
        }
        else {
            paint.setColor(Color.rgb(232,74,95));
            c.drawText("No work permissions", p.x + padding, end_y, paint);
        }


    }

    public int drawMultiline(Canvas c, String s, int max_line, int x, int y) {
        List<String> text = GraphicsView.splitEqually(s, max_line);
        for (String line: text)
        {
            c.drawText(line, x, y, paint);
            y += -paint.ascent() + paint.descent();
        }
        return text.size();
    }

    public static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }
}

