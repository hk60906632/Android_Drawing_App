package com.example.android_drawing_app;

public class Rectangle_shape {
        float side_length_1;
        float side_length_2;
        float centerX;
        float centerY;
        float topLeftX;
        float topLeftY;
        float bottomRightX;
        float bottomRightY;
        int color;

        public Rectangle_shape(float side_length_1, float centerX, float centerY, int color) {
            //this class store all the rectangle information for each rectangle drawn on the screen,
            // so all the rectangle can be redrawn onto the
            //screen by the onDraw() android function
            this.side_length_1 = side_length_1;
            this.side_length_2 = side_length_1/2;
            this.centerX = centerX;
            this.centerY = centerY;
            this.color = color;
            this.topLeftX = centerX - (side_length_1 / 2);
            this.topLeftY = centerY + (side_length_1 / 4);
            this.bottomRightX = centerX + (side_length_1 / 2);
            this.bottomRightY = centerY - (side_length_1 / 4);
        }
}
