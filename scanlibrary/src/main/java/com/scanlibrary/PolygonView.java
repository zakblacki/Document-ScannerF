package com.scanlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolygonView extends FrameLayout {
    protected Context context;
    private ImageView midPointer12;
    private ImageView midPointer13;
    private ImageView midPointer24;
    private ImageView midPointer34;
    private Paint paint;
    private ImageView pointer1;
    private ImageView pointer2;
    private ImageView pointer3;
    private ImageView pointer4;
    private PolygonView polygonView;

    private class MidPointTouchListenerImpl implements OnTouchListener {
        PointF DownPT = new PointF();
        PointF StartPT = new PointF();
        private ImageView mainPointer1;
        private ImageView mainPointer2;

        public MidPointTouchListenerImpl(ImageView mainPointer1, ImageView mainPointer2) {
            this.mainPointer1 = mainPointer1;
            this.mainPointer2 = mainPointer2;
        }

        public boolean onTouch(View v, MotionEvent event) {
            int eid = event.getAction();
            if (eid == 0) {
                this.DownPT.x = event.getX();
                this.DownPT.y = event.getY();
                this.StartPT = new PointF(v.getX(), v.getY());
            } else if (eid == 1) {
                int color;
                PolygonView polygonView = PolygonView.this;
                if (polygonView.isValidShape(polygonView.getPoints())) {
                    color = PolygonView.this.getResources().getColor(R.color.blue);
                } else {
                    color = PolygonView.this.getResources().getColor(R.color.orange);
                }
                PolygonView.this.paint.setColor(color);
            } else if (eid == 2) {
                PointF mv = new PointF(event.getX() - this.DownPT.x, event.getY() - this.DownPT.y);
                ImageView imageView;
                if (Math.abs(this.mainPointer1.getX() - this.mainPointer2.getX()) > Math.abs(this.mainPointer1.getY() - this.mainPointer2.getY())) {
                    if ((this.mainPointer2.getY() + mv.y) + ((float) v.getHeight()) < ((float) PolygonView.this.polygonView.getHeight()) && this.mainPointer2.getY() + mv.y > 0.0f) {
                        v.setX((float) ((int) (this.StartPT.y + mv.y)));
                        this.StartPT = new PointF(v.getX(), v.getY());
                        imageView = this.mainPointer2;
                        imageView.setY((float) ((int) (imageView.getY() + mv.y)));
                    }
                    if ((this.mainPointer1.getY() + mv.y) + ((float) v.getHeight()) < ((float) PolygonView.this.polygonView.getHeight()) && this.mainPointer1.getY() + mv.y > 0.0f) {
                        v.setX((float) ((int) (this.StartPT.y + mv.y)));
                        this.StartPT = new PointF(v.getX(), v.getY());
                        imageView = this.mainPointer1;
                        imageView.setY((float) ((int) (imageView.getY() + mv.y)));
                    }
                } else {
                    if ((this.mainPointer2.getX() + mv.x) + ((float) v.getWidth()) < ((float) PolygonView.this.polygonView.getWidth()) && this.mainPointer2.getX() + mv.x > 0.0f) {
                        v.setX((float) ((int) (this.StartPT.x + mv.x)));
                        this.StartPT = new PointF(v.getX(), v.getY());
                        imageView = this.mainPointer2;
                        imageView.setX((float) ((int) (imageView.getX() + mv.x)));
                    }
                    if ((this.mainPointer1.getX() + mv.x) + ((float) v.getWidth()) < ((float) PolygonView.this.polygonView.getWidth()) && this.mainPointer1.getX() + mv.x > 0.0f) {
                        v.setX((float) ((int) (this.StartPT.x + mv.x)));
                        this.StartPT = new PointF(v.getX(), v.getY());
                        imageView = this.mainPointer1;
                        imageView.setX((float) ((int) (imageView.getX() + mv.x)));
                    }
                }
            }
            PolygonView.this.polygonView.invalidate();
            return true;
        }
    }

    private class TouchListenerImpl implements OnTouchListener {
        PointF DownPT;
        PointF StartPT;

        private TouchListenerImpl() {
            this.DownPT = new PointF();
            this.StartPT = new PointF();
        }

        public boolean onTouch(View v, MotionEvent event) {
            int eid = event.getAction();
            if (eid == 0) {
                this.DownPT.x = event.getX();
                this.DownPT.y = event.getY();
                this.StartPT = new PointF(v.getX(), v.getY());
            } else if (eid == 1) {
                int color;
                PolygonView polygonView = PolygonView.this;
                if (polygonView.isValidShape(polygonView.getPoints())) {
                    color = PolygonView.this.getResources().getColor(R.color.blue);
                } else {
                    color = PolygonView.this.getResources().getColor(R.color.orange);
                }
                PolygonView.this.paint.setColor(color);
            } else if (eid == 2) {
                PointF mv = new PointF(event.getX() - this.DownPT.x, event.getY() - this.DownPT.y);
                if ((this.StartPT.x + mv.x) + ((float) v.getWidth()) < ((float) PolygonView.this.polygonView.getWidth()) && (this.StartPT.y + mv.y) + ((float) v.getHeight()) < ((float) PolygonView.this.polygonView.getHeight()) && this.StartPT.x + mv.x > 0.0f && this.StartPT.y + mv.y > 0.0f) {
                    v.setX((float) ((int) (this.StartPT.x + mv.x)));
                    v.setY((float) ((int) (this.StartPT.y + mv.y)));
                    this.StartPT = new PointF(v.getX(), v.getY());
                }
            }
            PolygonView.this.polygonView.invalidate();
            return true;
        }
    }

    public PolygonView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        this.polygonView = this;
        this.pointer1 = getImageView(0, 0);
        this.pointer2 = getImageView(getWidth(), 0);
        this.pointer3 = getImageView(0, getHeight());
        this.pointer4 = getImageView(getWidth(), getHeight());
        this.midPointer13 = getImageView(0, getHeight() / 2);
        this.midPointer13.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer1, this.pointer3));
        this.midPointer12 = getImageView(0, getWidth() / 2);
        this.midPointer12.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer1, this.pointer2));
        this.midPointer34 = getImageView(0, getHeight() / 2);
        this.midPointer34.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer3, this.pointer4));
        this.midPointer24 = getImageView(0, getHeight() / 2);
        this.midPointer24.setOnTouchListener(new MidPointTouchListenerImpl(this.pointer2, this.pointer4));
        addView(this.pointer1);
        addView(this.pointer2);
        addView(this.midPointer13);
        addView(this.midPointer12);
        addView(this.midPointer34);
        addView(this.midPointer24);
        addView(this.pointer3);
        addView(this.pointer4);
        initPaint();
    }

    /* Access modifiers changed, original: protected */
    public void attachViewToParent(View child, int index, LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    private void initPaint() {
        this.paint = new Paint();
        this.paint.setColor(getResources().getColor(R.color.blue));
        this.paint.setStrokeWidth(2.0f);
        this.paint.setAntiAlias(true);
    }

    public Map<Integer, PointF> getPoints() {
        List<PointF> points = new ArrayList();
        points.add(new PointF(this.pointer1.getX(), this.pointer1.getY()));
        points.add(new PointF(this.pointer2.getX(), this.pointer2.getY()));
        points.add(new PointF(this.pointer3.getX(), this.pointer3.getY()));
        points.add(new PointF(this.pointer4.getX(), this.pointer4.getY()));
        return getOrderedPoints(points);
    }

    public Map<Integer, PointF> getOrderedPoints(List<PointF> points) {
        PointF centerPoint = new PointF();
        int size = points.size();
        for (PointF pointF : points) {
            centerPoint.x += pointF.x / ((float) size);
            centerPoint.y += pointF.y / ((float) size);
        }
        Map<Integer, PointF> orderedPoints = new HashMap();
        for (PointF pointF2 : points) {
            int index = -1;
            if (pointF2.x < centerPoint.x && pointF2.y < centerPoint.y) {
                index = 0;
            } else if (pointF2.x > centerPoint.x && pointF2.y < centerPoint.y) {
                index = 1;
            } else if (pointF2.x < centerPoint.x && pointF2.y > centerPoint.y) {
                index = 2;
            } else if (pointF2.x > centerPoint.x && pointF2.y > centerPoint.y) {
                index = 3;
            }
            orderedPoints.put(Integer.valueOf(index), pointF2);
        }
        return orderedPoints;
    }

    public void setPoints(Map<Integer, PointF> pointFMap) {
        if (pointFMap.size() == 4) {
            setPointsCoordinates(pointFMap);
        }
    }

    private void setPointsCoordinates(Map<Integer, PointF> pointFMap) {
        ImageView imageView = this.pointer1;
        Integer valueOf = Integer.valueOf(0);
        imageView.setX(((PointF) pointFMap.get(valueOf)).x);
        this.pointer1.setY(((PointF) pointFMap.get(valueOf)).y);
        imageView = this.pointer2;
        valueOf = Integer.valueOf(1);
        imageView.setX(((PointF) pointFMap.get(valueOf)).x);
        this.pointer2.setY(((PointF) pointFMap.get(valueOf)).y);
        imageView = this.pointer3;
        valueOf = Integer.valueOf(2);
        imageView.setX(((PointF) pointFMap.get(valueOf)).x);
        this.pointer3.setY(((PointF) pointFMap.get(valueOf)).y);
        imageView = this.pointer4;
        valueOf = Integer.valueOf(3);
        imageView.setX(((PointF) pointFMap.get(valueOf)).x);
        this.pointer4.setY(((PointF) pointFMap.get(valueOf)).y);
    }

    /* Access modifiers changed, original: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Canvas canvas2 = canvas;
        canvas2.drawLine(this.pointer1.getX() + ((float) (this.pointer1.getWidth() / 2)), this.pointer1.getY() + ((float) (this.pointer1.getHeight() / 2)), this.pointer3.getX() + ((float) (this.pointer3.getWidth() / 2)), this.pointer3.getY() + ((float) (this.pointer3.getHeight() / 2)), this.paint);
        canvas2.drawLine(this.pointer1.getX() + ((float) (this.pointer1.getWidth() / 2)), this.pointer1.getY() + ((float) (this.pointer1.getHeight() / 2)), this.pointer2.getX() + ((float) (this.pointer2.getWidth() / 2)), this.pointer2.getY() + ((float) (this.pointer2.getHeight() / 2)), this.paint);
        canvas2.drawLine(this.pointer2.getX() + ((float) (this.pointer2.getWidth() / 2)), this.pointer2.getY() + ((float) (this.pointer2.getHeight() / 2)), this.pointer4.getX() + ((float) (this.pointer4.getWidth() / 2)), this.pointer4.getY() + ((float) (this.pointer4.getHeight() / 2)), this.paint);
        canvas2.drawLine(this.pointer3.getX() + ((float) (this.pointer3.getWidth() / 2)), this.pointer3.getY() + ((float) (this.pointer3.getHeight() / 2)), this.pointer4.getX() + ((float) (this.pointer4.getWidth() / 2)), this.pointer4.getY() + ((float) (this.pointer4.getHeight() / 2)), this.paint);
        this.midPointer13.setX(this.pointer3.getX() - ((this.pointer3.getX() - this.pointer1.getX()) / 2.0f));
        this.midPointer13.setY(this.pointer3.getY() - ((this.pointer3.getY() - this.pointer1.getY()) / 2.0f));
        this.midPointer24.setX(this.pointer4.getX() - ((this.pointer4.getX() - this.pointer2.getX()) / 2.0f));
        this.midPointer24.setY(this.pointer4.getY() - ((this.pointer4.getY() - this.pointer2.getY()) / 2.0f));
        this.midPointer34.setX(this.pointer4.getX() - ((this.pointer4.getX() - this.pointer3.getX()) / 2.0f));
        this.midPointer34.setY(this.pointer4.getY() - ((this.pointer4.getY() - this.pointer3.getY()) / 2.0f));
        this.midPointer12.setX(this.pointer2.getX() - ((this.pointer2.getX() - this.pointer1.getX()) / 2.0f));
        this.midPointer12.setY(this.pointer2.getY() - ((this.pointer2.getY() - this.pointer1.getY()) / 2.0f));
    }

    private ImageView getImageView(int x, int y) {
        ImageView imageView = new ImageView(this.context);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        imageView.setImageResource(R.drawable.circle);
        imageView.setX((float) x);
        imageView.setY((float) y);
        imageView.setOnTouchListener(new TouchListenerImpl());
        return imageView;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public boolean isValidShape(Map<Integer, PointF> pointFMap) {
        return pointFMap.size() == 4;
    }
}