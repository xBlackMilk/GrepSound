package com.grepsound.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.grepsound.R;

public class TwoTextCounter extends FrameLayout {
    LayoutInflater inflater = null;
    TextView counter, label;
    private int borderWidth = 4;
    private int viewWidth;
    private int viewHeight;

    private static int bgColor;
    private static int counterColor;
    private static int labelColor;

    public TwoTextCounter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    public TwoTextCounter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwoTextCounter);


        bgColor = a.getColor(R.styleable.TwoTextCounter_backgroundColor, Color.WHITE);
        counterColor = a.getColor(R.styleable.TwoTextCounter_counterColor, Color.BLACK);
        labelColor = a.getColor(R.styleable.TwoTextCounter_labelColor, Color.BLACK);

        a.recycle();
        initViews();
    }

    public TwoTextCounter(Context context) {
        super(context);
        initViews();
    }

    void initViews() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.counter_with_label, this, true);
        counter = (TextView) findViewById(R.id.counter);
        label = (TextView) findViewById(R.id.label);

    }

    public void setCounter(int value) {
        counter.setText(""+value);
    }

    public void setLabel(String value) {
        label.setText(value);
    }

    /**
     * Animate the counter value from 0 to the given @param value
     * @param value final value
     * @param duration duration of animation in ms
     */
    public void setCounterWithAnimation(int value, int duration) {
        counter.setText("0");
    }

    public static Bitmap getBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();

        path.addCircle(
                (float)(width / 2)
                , (float)(height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);

        Paint test = new Paint();
        test.setDither(true);
        test.setAntiAlias(true);
        test.setStyle(Paint.Style.STROKE);       // set to STOKE
        test.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        test.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        test.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
        canvas.drawBitmap(bitmap, 0, 0, test);
        return outputBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec, widthMeasureSpec);

        viewWidth = width;
        viewHeight = height;

        setMeasuredDimension(width, height);

        Bitmap b = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        b.eraseColor(bgColor);
        counter.setTextColor(counterColor);
        label.setTextColor(labelColor);
        b = getBitmapClippedCircle(b);
        BitmapDrawable dr = new BitmapDrawable(getResources(), b);
        setBackground(dr);

    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        }
        else {
            // Measure the text
            result = viewWidth;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight, int measureSpecWidth) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        }
        else {
            // Measure the text (beware: ascent is a negative number)
            result = viewHeight;
        }

        return result;
    }
}
