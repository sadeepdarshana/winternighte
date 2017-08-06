package com.example.sadeep.winternightd.textboxes;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.activities.NoteContainingActivity;
import com.example.sadeep.winternightd.field.SingleText;
import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.selection.CursorPosition;
import com.example.sadeep.winternightd.selection.XSelection;
import com.example.sadeep.winternightd.temp.d;

/**
 * Created by Sadeep on 10/13/2016.
 */
public class XTextView extends TextView{

    private Field boundField;

    public XTextView(Field boundField,Context context){
        this(context);
        this.boundField = boundField;
    }
    private XTextView(Context context) {
        super(context);
        init();
    }
    private void init() {

        //setPadding(0,0,0,3*Globals.dp2px); dunno why?
        setPadding(0,0,0,0);
        setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setTextSize(TypedValue.COMPLEX_UNIT_FRACTION,  Globals.defaultFontSize);
        setTextColor(0xff333333);
        //setTextIsSelectable(true);
    }

    /**
     * called by the system when user makes a selection (by double tapping/long tapping etc)
     * we need to make XSelection based on the ordinary selection if possible
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        attemptXSelection();
    }

    /**
     * make an XSelection based on the ordinary selection.
     * the ordinary selection is deselected at XSelection.newSelection method.
     *
     * returns true if XSelection is made
     */
    private boolean attemptXSelection(){
        if(getSelectionEnd()==getSelectionStart())return false;  //no selection available, cursor at a single particular position

        //initiating a XSelection is only possible if this XEditText belongs to a SingleText Field..
        if (boundField == null) return false;
        if (!(boundField instanceof SingleText)) return false;

        //..and this XEditText is the main textbox of the Field
        SingleText field = (SingleText) boundField;
        if (field.getMainTextBox()!=this) return false;

        //get CursorPositions corresponding to the start and end of the ordinary selection
        CursorPosition start = new CursorPosition(boundField.getFieldIndex(),getSelectionStart());
        CursorPosition end = new CursorPosition(boundField.getFieldIndex(),getSelectionEnd());

        //make the XSelection
        //XSelection.newSelection(boundField.getNote(),start,end,this);
        //isFocused();


        return true;
    }
}
