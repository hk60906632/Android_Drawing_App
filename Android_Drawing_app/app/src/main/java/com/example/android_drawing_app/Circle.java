package com.example.android_drawing_app;



public class Circle {
    //this class store all the circle information for each circle drawn on the screen, so all the circle can be redrawn onto the
    //screen by the onDraw() android function
    float radius;
    float centerX;
    float centerY;
    int color;

    public Circle (float centerX,float centerY ,float radius, int color){
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
        this.color = color;
    }

}
