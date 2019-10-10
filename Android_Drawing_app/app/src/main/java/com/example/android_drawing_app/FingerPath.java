package com.example.android_drawing_app;

import android.graphics.Path;

public class FingerPath {

    int color;
    boolean blur;
    int strokeWidth;
    Path path;

    public FingerPath(int color, boolean blur, int strokeWidth, Path path) {
        //this class store all the line information for each line drawn on the screen,
        // so all the line can be redrawn onto the
        //screen by the onDraw() android function
        this.color = color;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
