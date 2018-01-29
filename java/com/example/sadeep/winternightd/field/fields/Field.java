package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.field.FieldFactory;
import com.example.sadeep.winternightd.field.SingleText;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.textboxes.XEditText;
import com.example.sadeep.winternightd.selection.CursorPosition;
import com.example.sadeep.winternightd.selection.XSelection;

/**
 * Created by Sadeep on 10/14/2016.
 */

/**
 * Field is the fundamental unit Notes are made of. A Note may contain 1 or more Fields.
 * A Field does one specific task.
 *      ex: an instance of the BulletedField(an override of Field) contains one bullet and its content textbox.
 *
 * [not to be confused with java.lang.reflect.Field / class variables]
 */

/** PS
 * Do not change the background color of a Field, add a new container and change its bgcolor if the effect is needed.
 * This is because it would conflict with some methods (regarding highlighting selected content) in XSelection class.
 *
 */
public abstract class Field extends LinearLayout {


    /** we have given a numbers to each of the overrides of Field.
     *
     * fieldType    Class
     *
     *  0           Field                   (abstract)
     *  1           IndentedField           (abstract)
     *  2           SimpleIndentedField
     *  3           BulletedField
     *  4           CheckedField
     *  5           NumberedField
     */
    public static final int classFieldType = 1044930821;

    protected int fieldType;//see Field.init() for info

    private GestureDetector gestureDetector;

    private boolean isEditable;

    public Field(Context context) {
        super(context);
        init();
    }

    private void init() {

        /**
         *  fieldType property is reassigned at the init() method of each derived Field.
         *  So because the init() method of the most derived(the actual class of the Field) class
         *  is executed at last, the classFieldType of the most  derived class (the actual class
         *  of the Field) becomes the instance's fieldType that would be retrieved by getFieldType() method.
         */
        fieldType = classFieldType;


        setOrientation(HORIZONTAL);

        setPadding(0,0,0,0);
        setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //because we want to detect Field's onSingleTapUp to clear (if any) Selections (we have our own text selection system)
        gestureDetector = new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {}

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                XSelection.clearSelections();
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
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

//general & misc methods

    /**because we want to detect Field's onSingleTapUp to clear (if any) Selections (we have our own text selection system)
    [this built-in method detects touch events of the whole ViewGroup before they are dispatched to children.]**/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector. onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public int getFieldType(){
        return fieldType;
    }

    //the position of the Field in the Note.
    public int getFieldIndex(){
        return ((Note)getParent()).indexOfChild(this);
    }

    public Note getNote(){
        if (getParent()==null)return null;
        return (Note)getParent();
    }

    /**
     *  Called by the Note. Override this to implement works that need to be done when this Field is attached to a Note
     */
    public void onAttachedToNote() {

    }

    public void onFocused() {
        Note note = getNote();
        if(note!=null)note.onFocused();
    }



//editability methods

    public void setIsEditable(boolean isEditable){
        this.isEditable = isEditable;
    }

    public boolean getIsEditable(){
        return isEditable;
    }




//refresh methods

    /**
     * The method does not do anything in most Fields, used only by some Fields (only NumberedField as at 8JUNE'17)
     * Overrides of this method in respective classes make changes to the properties that might need to be changes
     * due to the changes in other areas(Fields) of the note.
     *  (ex: when NumberedField numbered 3 is deleted the NumberedField below the deleted Field should change its number from 4 to 3)
     */
    public void refreshToAccountForNoteChanges(){}

    /**
     * Calls refreshToAccountForNoteChanges() on all the Fields below starting from this Field
     */
    public void refreshThisAndAllFieldsBelowToAccountForNoteChanges() {
        if(getNote()==null)return;
        for(int i = getFieldIndex();i<getNote().getFieldCount();i++)
            getNote().getFieldAt(i).refreshToAccountForNoteChanges();
    }



//methods relating to backspace & enter keys


    /**
     * Backspace has been pressed at the start of the Field.
     * We need to call the backspaceField() of the Field immediately above this Field and notify it to
     *      erase itself or merge this Field's text to it.
     *
     * (Overrides of this method does various things at derived classes of Field [ex. BulletedField erase bullet], but
     *  at this point(it has come all the way to the superest method 'Field.onBackspaceKeyPressedAtStart()' itself) there is
     *  nothing we should do other than to notify the previous Field to backspace.
     */
    public void onBackspaceKeyPressedAtStart(XEditText xEditText) {
        if(getFieldIndex()==0)return;
        getNote().getFieldAt(getFieldIndex()-1).backspaceField();
    }

    /**
     * Next Field (Field immediately below this) calls this method when backspace is pressed at its start.
     *
     * Here we need to either,
     *      remove this Field(ex. if this were a picture)
     *      or merger the next Field's text to this Field (ex. this Field is a BulletedField)
     *
     * Extended text based classes of Field (ex. SimpleTextField) may override this method to do things like text merging
     * of the two Fields.
     *
     * getNote().removeView(this) is kept as the default case when not overridden.
     */
    public  void backspaceField(){
        getNote().removeView(this);
    }

    public boolean onEnterKeyPressed(TextView textView) {
        return false;
    }





//selection & clipboard related methods

    //get the coordinate corresponding to the given characterIndex in absolute coordinate system
    public Point getAbsoluteCoordinatesForCharacterIndex(int characterIndex) {

        if( this instanceof SingleText &&
                characterIndex!=CursorPosition.CHARACTERINDEX_FIELDSTART &&
                characterIndex!=CursorPosition.CHARACTERINDEX_FIELDEND)
            //characterIndex refers to a cursor position within the text. We'll delegate the work to SingleText interface
            return ((SingleText)this).getAbsoluteCoordinatesForSingleTextCharacterIndex(characterIndex);

        int[] xy = new int[2];
        getLocationInWindow(xy);  //this's top left coordinate is stored in xy now

        int x = xy[0];
        int y = xy[1];

        y += getHeight(); //cursor will be at the bottom of the Field

        if(characterIndex == CursorPosition.CHARACTERINDEX_FIELDSTART);//x is already the begin (FIELDSTART) of the Field;nothing to do
        else if(characterIndex == CursorPosition.CHARACTERINDEX_FIELDEND)x+=getWidth();//add the width of Field to x to get x coord at end

        return new Point(x,y);
    }

    //returns the characterPosition corresponding to the coordinate(in absolute) given
    public int characterPositionForCoordinate(Point absoluteCoordinate) {

        int rawX = absoluteCoordinate.x;
        int rawY = absoluteCoordinate.y;


        if(this instanceof SingleText)
            //this is a SingleText. We'll delegate the work to SingleText interface
            return ((SingleText)this).getSingleTextCharacterIndexForAbsoluteCoordinates(rawX,rawY);



        //follows code for non-SingleText type Fields

        int[] xy = new int[2];
        this.getLocationInWindow(xy);  //this's top left coordinate is stored in xy now

        if (rawX < xy[0] + getWidth() / 5) return CursorPosition.CHARACTERINDEX_FIELDSTART;
        if (rawX > xy[0] + 4 * getWidth() / 5) return CursorPosition.CHARACTERINDEX_FIELDEND;
        //notice we have kept a getWidth()/5 threshold from both left and right of the Field to before declaring FIELDSTART/FIELDEND
        //(so that user doesn't have to move the finger to the very edge to be considered as FIELDSTART)

        return CursorPosition.CHARACTERINDEX_ERROR;//coord does not belong to any valid cursorposition
    }

    public void setCursorVisible(boolean visible) {
        //overridden in sub classes
    }

    /**
     * Create a Field identical to this
     */
    public Field duplicate(){
        //we dump the content of this to a FieldDataStream and creates a new Field out of the dumped data
        FieldDataStream fd = new FieldDataStream(getWritableByteArraySize());
        this.writeToFieldDataStream(fd);

        return FieldFactory.fromFieldDataStream(getContext(), fd, getIsEditable());
    }

    public Object duplicateSelection(int characterIndex1, int characterIndex2) {
        if(characterIndex1==-2&&characterIndex2==-1)return duplicate();
        //other cases are handled in overridden method of subclasses
        return  null;
    }





//dumping related methods


    public void writeToFieldDataStream(FieldDataStream stream){
        stream.putFieldType(this.getFieldType());
    }

    public void readFromFieldDataStream(FieldDataStream stream){

    }

    public int getWritableByteArraySize(){
        return 0;
    }

}
