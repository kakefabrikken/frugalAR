package no.ntnu.tpg4850.frugalar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.view.View;
import android.content.Context;

/**
 * Created by Kristian on 16.03.2015.
 */
public class PointView extends View {

    private static final String TAG = "DrawPoint";
    private ShapeDrawable drawable;
    Point p = new Point();
    Paint paint = new Paint();

    public PointView(Context context) {
        super(context);
        paint.setColor(Color.RED);

        int x= 1;//trying to hack oval into point
        int y = 1;
        int width = 1;
        int height = 1;

        drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(0xff8A0707);
        drawable.setBounds(x, y, x+width, y+height );
    }

    protected void onDraw(Canvas canvas) {
        drawable.draw(canvas);
    }



    //public DrawView()
}
