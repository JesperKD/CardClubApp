package com.example.cardclub;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class MyDragShadowBuilder extends View.DragShadowBuilder {

    // The drag shadow image, defined as a drawable object.
    private static Drawable shadow;

    /**
     * MyDragShadowBuilder Constructor
     * @param v view
     */
    public MyDragShadowBuilder(View v) {

        // Stores the View parameter.
        super(v);

        // Creates a draggable image that fills the Canvas provided by the system.
        shadow = new ColorDrawable(Color.LTGRAY);
    }

    // Defines a callback that sends the drag shadow dimensions and touch point
    // back to the system.

    /**
     * Defines a callback that sends the drag shadow dimensions and touch point back to the system.
     * @param size Point
     * @param touch Point
     */
    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {

        // Defines local variables
        int width, height;

        // Set the width of the shadow to half the width of the original View.
        width = getView().getWidth() / 2;

        // Set the height of the shadow to half the height of the original View.
        height = getView().getHeight() / 2;

        // fills the Canvas.
        shadow.setBounds(0, 0, width, height);

        // Set the size parameter's width and height values.
        size.set(width, height);

        // Set the touch point's position to be in the middle of the drag shadow.
        touch.set(width / 2, height / 2);
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system
    // constructs from the dimensions passed to onProvideShadowMetrics().

    /**
     * Defines a callback that draws the drag shadow in a Canvas that the system constructs from the dimensions passed to onProvideShadowMetrics().
     * @param canvas
     */
    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draw the ColorDrawable on the Canvas passed in from the system.
        shadow.draw(canvas);
    }
}