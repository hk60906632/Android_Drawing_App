package com.example.android_drawing_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PaintView extends View {
    //this is the class control all the drawing, this set up the bitmap and the canvas so the user can draw and see what they
    //have drawn

    private static final float TOUCH_TOLERANCE = 4;

    private drawing_mode touch_mode = drawing_mode.LINE;
    private int currentColor;
    private int backgroundColor;
    private int strokeWidth;
    private float userX;
    private float userY;
    private boolean blur;
    private Path userPath;
    private Paint userPaint; //The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
    private MaskFilter userBlur;
    private Bitmap userBitmap;
    private Canvas userCanvas;
    private Paint userBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int height;
    private int width;


    private int currentClColor;
    private float radius;
    private Paint userCirclePaint;
    private ArrayList<Circle> mCircles = new ArrayList<>();
    private SparseArray<Circle> mCirclePointer = new SparseArray<Circle>();

    private Paint userRectanglePaint;
    private ArrayList<Rectangle_shape> mRectangle = new ArrayList<>();
    private SparseArray<Rectangle_shape> mRectanglePointer = new SparseArray<Rectangle_shape>();




    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        userPaint = new Paint();
        userPaint.setAntiAlias(true);//smooths out the edges of what is being drawn, but is has no impact on the interior of the shape.
        userPaint.setDither(true);
        userPaint.setStyle(Paint.Style.STROKE);
        userPaint.setStrokeJoin(Paint.Join.ROUND);
        userPaint.setStrokeCap(Paint.Cap.ROUND);
        userPaint.setXfermode(null);
        userPaint.setAlpha(0xff);
        userBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);


        userCirclePaint = new Paint();
        userCirclePaint.setStrokeWidth(40);
        userCirclePaint.setStyle(Paint.Style.FILL);

        userRectanglePaint = new Paint();
        userRectanglePaint.setStrokeWidth(40);
        userRectanglePaint.setStyle(Paint.Style.FILL);
    }

    public void touch_mode_setter(drawing_mode mode){
        touch_mode = mode;
    }

    //################################Line Drawing function###############################################

    public void init_line(DisplayMetrics metrics){
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        userBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        userCanvas = new Canvas(userBitmap);

    }

    public void normal(){
        blur = false;
    }


    public void blur(){
        blur = true;
    }



    private void touchScreenStart(float x, float y){  //call when user touch the screen
        userPath = new Path();
        Log.d("StrokeWidth", Integer.toString(strokeWidth));
        FingerPath fp = new FingerPath(currentColor, blur, strokeWidth, userPath);
        paths.add(fp);

        userPath.reset();
        userPath.moveTo(x, y);
        userX = x;
        userY = y;
    }



    private void touchScreenMove(float x, float y){ //call when user move the finger on the screen
        float deltaX = Math.abs(x - userX);
        float deltaY = Math.abs(y - userY);

        if(deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE){
            userPath.quadTo(userX, userY, (x + userX)/2, (y + userY)/2);
            userX = x;
            userY = y;
        }
    }

    private void touchUp(){ //call when user move finger out of the screen
        userPath.lineTo(userX, userY);
    }




    //############################Circle function#######################################################

    private Circle obtainTouchedCircle(final float touchX, final float touchY){ //check if user finger point at a place with circle, if not touching a circle create one, if yes select it and move it
        Circle touchCircle = locateTouchCircle(touchX, touchY);

        if (null == touchCircle){
            touchCircle = new Circle(touchX, touchY, radius, currentClColor);

            Log.w("obtainTouchedCircle", "added circle");

            mCircles.add(touchCircle);
        }
        return touchCircle;
    }

    private Circle locateTouchCircle(final float touchX, final float touchY){ //find out which circle in the screen is touching
        Circle touched = null;

        for (Circle clc : mCircles){
            if((clc.centerX - touchX)*(clc.centerX - touchX) + (clc.centerY - touchY)*(clc.centerY - touchY) <= clc.radius*clc.radius){
                touched = clc;
                break;
            }
        }

        return touched;
    }



    //#################################rectangel functions###################################################
    private Rectangle_shape obtainTouchedRectangle(final float touchX, final float touchY){ //check user touch a rectangle or not, if no create a rectangle, if yes move it
        Rectangle_shape touchRectangle = locateTouchRectangle(touchX, touchY);

        if (null == touchRectangle){
            touchRectangle = new Rectangle_shape(radius, touchX, touchY, currentClColor);

            Log.w("obtainTouchedRectangle", "added rectangle");

            mRectangle.add(touchRectangle);
        }
        return touchRectangle;
    }

    private Rectangle_shape locateTouchRectangle(final float touchX, final float touchY){ //find out which rectangle in the screen is touching
        Rectangle_shape touched = null;

        for (Rectangle_shape rec : mRectangle){
            if(touchX > rec.topLeftX && touchX < rec.bottomRightX && touchY > rec.bottomRightY && touchY < rec.topLeftY){
                touched = rec;
                break;
            }
        }

        return touched;
    }

    //#################################android spinner functions#############################################

    public void set_color(String text){ //set shape, stroke color
        switch (text){
            case "Red":
                currentClColor = Color.RED;
                currentColor = Color.RED;
                userPaint.setColor(Color.RED);
                userCirclePaint.setColor(Color.RED);
                break;

            case "Green":
                currentClColor = ContextCompat.getColor(getContext(), R.color.green);
                currentColor = ContextCompat.getColor(getContext(), R.color.green);
                userPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));
                userCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.green));
                break;

            case "Blue":
                currentClColor = Color.BLUE;
                currentColor = Color.BLUE;
                userPaint.setColor(Color.BLUE);
                userCirclePaint.setColor(Color.BLUE);
                break;

            case "Purple":
                currentClColor = ContextCompat.getColor(getContext(),R.color.purple);
                currentColor = ContextCompat.getColor(getContext(),R.color.purple);
                userPaint.setColor(ContextCompat.getColor(getContext(),R.color.purple));
                userCirclePaint.setColor(ContextCompat.getColor(getContext(),R.color.purple));
                break;

            case "Orange":
                currentClColor = ContextCompat.getColor(getContext(),R.color.orange);
                currentColor = ContextCompat.getColor(getContext(),R.color.orange);
                userPaint.setColor(ContextCompat.getColor(getContext(),R.color.orange));
                userCirclePaint.setColor(ContextCompat.getColor(getContext(),R.color.orange));
                break;

            case "Yellow":
                currentClColor = ContextCompat.getColor(getContext(),R.color.yellow);
                currentColor = ContextCompat.getColor(getContext(),R.color.yellow);
                userPaint.setColor(ContextCompat.getColor(getContext(),R.color.yellow));
                userCirclePaint.setColor(ContextCompat.getColor(getContext(),R.color.yellow));
                break;

            case "Black":
                currentClColor = Color.BLACK;
                currentColor = Color.BLACK;
                userPaint.setColor(Color.BLACK);
                userCirclePaint.setColor(Color.BLACK);
                break;

            case "White":
                currentClColor = Color.WHITE;
                currentColor = Color.WHITE;
                userPaint.setColor(Color.WHITE);
                userCirclePaint.setColor(Color.WHITE);
                break;
        }

    }

    public void set_bg_color(String text){
        switch (text){
            case "Red":
                backgroundColor = Color.RED;
                userCanvas.drawColor(Color.RED);
                invalidate();
                break;

            case "Green":
                backgroundColor = ContextCompat.getColor(getContext(),R.color.green);
                userCanvas.drawColor(ContextCompat.getColor(getContext(),R.color.green));
                invalidate();
                break;

            case "Blue":
                backgroundColor = Color.BLUE;
                userCanvas.drawColor(Color.BLUE);
                invalidate();
                break;

            case "Purple":
                backgroundColor = ContextCompat.getColor(getContext(),R.color.purple);
                userCanvas.drawColor(ContextCompat.getColor(getContext(),R.color.purple));
                invalidate();
                break;

            case "Orange":
                backgroundColor = ContextCompat.getColor(getContext(),R.color.orange);
                userCanvas.drawColor(ContextCompat.getColor(getContext(),R.color.orange));
                invalidate();
                break;

            case "Yellow":
                backgroundColor = ContextCompat.getColor(getContext(),R.color.yellow);
                userCanvas.drawColor(ContextCompat.getColor(getContext(),R.color.yellow));
                invalidate();
                break;

            case "Black":
                backgroundColor = Color.BLACK;
                userCanvas.drawColor(Color.BLACK);
                invalidate();
                break;

            case "White":
                backgroundColor = Color.WHITE;
                userCanvas.drawColor(Color.WHITE);
                invalidate();
                break;
        }

    }

    public void set_shape_size(String text){
        switch (text){
            case "50f":
                radius = 50f;
                break;

            case "100f":
                radius = 100f;
                break;

            case "200f":
                radius = 200f;
                break;

            case "300f":
                radius = 300f;
                break;

            case "400f":
                radius = 400f;
                break;

            case "500f":
                radius = 500f;
                break;

            case "600f":
                radius = 600f;
                break;
        }
    }

    public void set_stroke_width(String text){
        switch (text){
            case "5":
                Log.d("set_stroke_width","5 is set");
                strokeWidth = 5;
                break;

            case "10":
                Log.d("set_stroke_width","10 is set");
                strokeWidth = 10;
                break;

            case "20":
                strokeWidth = 20;
                break;

            case "30":
                strokeWidth = 30;
                break;

            case "40":
                strokeWidth = 40;
                break;

            case "50":
                strokeWidth = 50;
                break;

            case "60":
                strokeWidth = 60;
                break;
        }
    }

    //#########################################Saving######################################################
    public void saveBitmap(){  //save the painting bitmap into a jpg in the pictures folder in any android device
        Toast save_warning;

        if (isExternalStorgeWritable()){
            saveImage(userBitmap);
            Log.d("saving", "saved");
        }
        else{
            save_warning = Toast.makeText(getContext(), "storage access denied", Toast.LENGTH_SHORT);
            save_warning.show();
            Log.d("saving", "save failed");
        }
    }


    private void saveImage(Bitmap finalBitmap){
        Toast save_warning;

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Log.d("saving", root);
        File myDir = new File(root + "/Kevin_drawing_app");
        if(!myDir.isDirectory()){
            myDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Drawing_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            save_warning = Toast.makeText(getContext(), "successfully saved", Toast.LENGTH_SHORT);
            save_warning.show();
        } catch (Exception e) {
            save_warning = Toast.makeText(getContext(), "saving failed", Toast.LENGTH_SHORT);
            save_warning.show();
            e.printStackTrace();
        }


    }

    public boolean isExternalStorgeWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        Log.d("writing probelm", "External storage not writable");
        return false;
    }

    //#########################################Android function############################################

    public void last_step(){  //for the top right corner return button
        drawing_mode current_using_mode = touch_mode;

        switch (current_using_mode){
            case LINE:
                if (paths.size() > 0){
                    paths.remove(paths.size()-1);
                    invalidate();
                }
                break;

            case CIRCLE:
                if (mCircles.size() > 0){
                    mCircles.remove(mCircles.size()-1);
                    invalidate();
                }
                break;

            case RECTANGLE:
                if (mRectangle.size() > 0){
                    mRectangle.remove(mRectangle.size()-1);
                    invalidate();
                }
                break;

            default:
                break;

        }
    }

    public void clear(){ //for the clear all function
        backgroundColor = Color.WHITE;
        paths.clear();
        mCircles.clear();
        mRectangle.clear();
        normal();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        userCanvas.drawColor(backgroundColor);


        for (FingerPath fp: paths){
            userPaint.setColor(fp.color);
            userPaint.setStrokeWidth(fp.strokeWidth);
            userPaint.setMaskFilter(null);

            if(fp.blur){
                userPaint.setMaskFilter(userBlur);
            }

            userCanvas.drawPath(fp.path, userPaint);
        }


        for (Circle clc: mCircles){
            userCirclePaint.setColor(clc.color);
            userCanvas.drawCircle(clc.centerX, clc.centerY, clc.radius, userCirclePaint);
        }

        for (Rectangle_shape rec: mRectangle){
            userRectanglePaint.setColor(rec.color);
            userCanvas.drawRect(rec.topLeftX, rec.topLeftY, rec.bottomRightX, rec.bottomRightY, userRectanglePaint);
        }

        canvas.drawBitmap(userBitmap, 0, 0, userBitmapPaint);
        canvas.restore();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float touchX;
        float touchY;
        int pointerId;
        int actionIndex = event.getActionIndex();
        Circle touchCircle;
        Rectangle_shape touchRectangle;

        if(touch_mode==drawing_mode.LINE){ //for line drawing
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    touchScreenStart(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchScreenMove(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        if(touch_mode==drawing_mode.CIRCLE){ //for circle drawing

            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    mCirclePointer.clear();
                    touchX = x;
                    touchY = y;
                    touchCircle = obtainTouchedCircle(touchX, touchY);
                    touchCircle.centerX = touchX;
                    touchCircle.centerY = touchY;
                    mCirclePointer.put(event.getPointerId(0), touchCircle);
                    invalidate();
                    break;

                //allow muti-touch to move and generate circle
                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.w("Action_point_down", "Circle Pointer down");
                    pointerId = event.getPointerId(actionIndex);

                    touchX = event.getX(actionIndex);
                    touchY = event.getY(actionIndex);

                    touchCircle = obtainTouchedCircle(touchX, touchY);
                    mCirclePointer.put(pointerId, touchCircle);
                    touchCircle.centerX = touchX;
                    touchCircle.centerY = touchY;
                    invalidate();

                case MotionEvent.ACTION_MOVE:
                    final int pointerCount = event.getPointerCount();

                    Log.w("moving", "moving");

                    for(actionIndex = 0; actionIndex < pointerCount; actionIndex++){
                        pointerId = event.getPointerId(actionIndex);

                        touchX = event.getX(actionIndex);
                        touchY = event.getY(actionIndex);

                        touchCircle = mCirclePointer.get(pointerId);
                        if(null != touchCircle){
                            touchCircle.centerX = touchX;
                            touchCircle.centerY = touchY;
                        }
                    }
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    mCirclePointer.clear();
                    invalidate();
                    break;

                //allow muti-touch to move and generate circle
                case MotionEvent.ACTION_POINTER_UP:
                    // not general pointer was up
                    pointerId = event.getPointerId(actionIndex);

                    mCirclePointer.remove(pointerId);
                    invalidate();
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        if (touch_mode==drawing_mode.RECTANGLE){ //for rectangle drawing

            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    mRectanglePointer.clear();
                    touchX = x;
                    touchY = y;
                    touchRectangle = obtainTouchedRectangle(touchX, touchY);
                    touchRectangle.centerX = touchX;
                    touchRectangle.centerY = touchY;
                    mRectanglePointer.put(event.getPointerId(0), touchRectangle);
                    invalidate();
                    break;

                //allow muti-touch to move and generate circle
                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.w("Action_point_down", "Rectangle Pointer down");
                    pointerId = event.getPointerId(actionIndex);

                    touchX = event.getX(actionIndex);
                    touchY = event.getY(actionIndex);

                    touchRectangle = mRectanglePointer.get(pointerId);
                    if(null != touchRectangle){
                        touchRectangle.centerX = touchX;
                        touchRectangle.centerY = touchY;
                    }
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    final int pointerCount = event.getPointerCount();

                    Log.w("moving", "moving");

                    for(actionIndex = 0; actionIndex < pointerCount; actionIndex++){
                        pointerId = event.getPointerId(actionIndex);

                        touchX = event.getX(actionIndex);
                        touchY = event.getY(actionIndex);

                        touchRectangle = mRectanglePointer.get(pointerId);
                        if(null != touchRectangle){
                            touchRectangle.centerX = touchX;
                            touchRectangle.centerY = touchY;

                            touchRectangle.topLeftX = touchX - (touchRectangle.side_length_1 / 2);
                            touchRectangle.topLeftY = touchY + (touchRectangle.side_length_1 / 4);
                            touchRectangle.bottomRightX = touchX + (touchRectangle.side_length_1 / 2);
                            touchRectangle.bottomRightY = touchY - (touchRectangle.side_length_1 / 4);
                        }
                    }
                    invalidate();
                    break;


                case MotionEvent.ACTION_UP:
                    mRectanglePointer.clear();
                    invalidate();
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    pointerId = event.getPointerId(actionIndex);

                    mRectanglePointer.remove(pointerId);
                    invalidate();
                    break;

                default:
                    // do nothing
                    break;
            }

        }



     return true;
    }
}
