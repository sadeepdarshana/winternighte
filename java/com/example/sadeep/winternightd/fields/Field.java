package com.example.sadeep.winternightd.fields;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Sadeep on 10/14/2016.
 */

/**
 * Field is the fundamental unit Notes are made of. A Note may contain 1 or more Fields.
 * A Field does one specific task.
 *      ex: an instance of the BulletedField contains one bullet and its content textbox.
 *
 * [not to be confused with java.lang.reflect.Field , java fields]
 */
public abstract class Field extends LinearLayout {


    public static final int classFieldType = 0;
    /**
     * fieldType    Class
     *
     *  0           Field                   (abstract)
     *  1           IndentedField           (abstract)
     *  2           SimpleIndentedField
     *  3           BulletedField
     *  4           CheckedField
     *  5           NumberedField
     */

    protected int fieldType;//see Field.init() for info

    private GestureDetector gestureDetector;

    private boolean isEditable;

    public Field(Context context) {
        super(context);
        init();
    }

    private void init() {

        fieldType = classFieldType;
        /**
         *  fieldType property is reassigned at the init() method of each derived Field.
         *  So because the init() method of the most derived class is executed at last, the classFieldType
         *  of the most derived class (the actual class of the Field) becomes the instance's fieldType
         *  that would be retrieved by getFieldType() method.
         */


        setOrientation(HORIZONTAL);

        setPadding(0,0,0,0);
        setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //because we want to detect Field's onSingleTapUp to clear (if any) Selections (we have our own text selection system)
        gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {}

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {}

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

    }


    //because we want to detect Field's onSingleTapUp to clear (if any) Selections (we have our own text selection system)
    //[this framework method detects touch events of the whole ViewGroup before they are dispatched to children.]
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        gestureDetector.onTouchEvent(ev);
        return false;
    }

    public int getFieldType(){
        return fieldType;
    }

    public void setIsEditable(boolean isEditable){
        this.isEditable = isEditable;
    }

    public boolean getIsEditable(){
        return isEditable;
    }
}
