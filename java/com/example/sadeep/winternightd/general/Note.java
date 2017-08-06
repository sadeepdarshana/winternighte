package com.example.sadeep.winternightd.general;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.factories.FieldsFactory;
import com.example.sadeep.winternightd.fields.Field;
import com.example.sadeep.winternightd.fields.SimpleIndentedField;

/**
 * Created by Sadeep on 10/18/2016.
 */

/**
 * A single note in WN
 */
public class Note extends LinearLayout {


    private boolean isEditable;
    public static final int defaultFieldType = SimpleIndentedField.classFieldType;

    public Note(Context context) {
        this(false,context);
    }

    public Note(Context context, AttributeSet attrs) {
        this(false,context, attrs);
    }
    public Note(boolean isEditable,Context context) {
        super(context);
        init(isEditable);
    }

    public Note(boolean isEditable,Context context, boolean isNewNoteWithOneDefaultField) {
        super(context);
        init(isEditable);
        if(isNewNoteWithOneDefaultField)newNoteWithOneDefaultField();
    }

    public Note(boolean isEditable,Context context, AttributeSet attrs) {
        super(context, attrs);
        init(isEditable);
    }

    private void init(boolean isEditable) {

        setOrientation(VERTICAL);
        setPadding(0,0,0,0);
        setBackgroundColor(Color.WHITE);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        this.isEditable = isEditable;
    }

    public void setIsEditable(boolean isEditable){

        if(this.isEditable == isEditable)return;
        this.isEditable = isEditable;

        for(int c = 0; c < getChildCount(); c++){
            Field f = getField(c);
            if(f != null)f.setIsEditable(isEditable);
        }

    }

    public void newNoteWithOneDefaultField(){
        removeAllViews();
        addView(FieldsFactory.createNewField(defaultFieldType,true,getContext()));
    }

    public boolean getIsEditable() {
        return isEditable;
    }


    public Field getField(int index){
        Field f = null;
        try{
            f = (Field)getChildAt(index);
        }catch (Exception e){}
        return f;
    }

    /**
     * We make sure that the added child is a Field, because a Note is intended to hold only Fields as child views.
     * It would cause bugs in various classes if a non-Field child is available as a child in the Note. So here we prevent this.
     *
     * We make the added Field is editable if the note is editable and vice versa.
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){

        Field f = null;
        try{
            f = (Field)child;
        }catch (Exception e){}

        if(f == null) return;

        f.setIsEditable(isEditable);

        super.addView(child,index,params);

    }
}
