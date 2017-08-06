package com.example.sadeep.winternightd.textboxes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.clipboard.XClipboard;
import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.field.SingleText;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.spans.SpansController;
import com.example.sadeep.winternightd.selection.CursorPosition;
import com.example.sadeep.winternightd.selection.XSelection;
import com.example.sadeep.winternightd.temp.d;

/**
 * Created by Sadeep on 10/12/2016.
 */

/***
 * XEditText is EditText extended to suit the requirements of WN. Major additions are,
 *  1. Support Bold, Italic, Underline, Highlight spans related functionalities
 *  2. Detect enterkey and backspacekey presses from hardware/software keyboard
 *  3. Detecting Cut, Copy, Paste actions and directing to take necessary actions
 */
public class XEditText extends EditText implements View.OnKeyListener {


    //TextChanged events of TextWatcher are not executed if the flag is true
    private boolean noTextChangedEventFlag = false; /** @see #setText(CharSequence text, BufferType type)    for more details**/

    //needed to call methods on the Field such as boundField.onEnterKeyPressed()
    public Field boundField; //the Field the EditText is attached to, usually same as getParent()

    public XEditText(Field boundField,Context context){
        this(context);
        this.boundField = boundField;
    }
    private XEditText(Context context) {
        super(context);

        init();
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(){

        setPadding(0,0,0,0);
        setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        setTextSize(TypedValue.COMPLEX_UNIT_FRACTION, Globals.defaultFontSize);

        setImeOptions(getImeOptions()| EditorInfo.IME_FLAG_NO_EXTRACT_UI); //disables (fullscreen keyboard when device at landscape mode)

        addTextChangedListener(new TextWatcher() {
            //textBeforeChange is assigned the text immediately before the text change event happens(in beforeTextChanged() method) because
            //textBeforeChange is by the onTextChanged() method
            CharSequence textBeforeChange="";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(noTextChangedEventFlag) return;//ignore the event if the flag is on
                textBeforeChange = s.subSequence(0,s.length()); //textBeforeChange = duplicate of s
            }

            //{enterkey detection} & {managing spans to suit Toolbar settings as user types characters} done here
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(noTextChangedEventFlag){ //if the flag is on, ignore the event and reset the flag
                    noTextChangedEventFlag = false;
                    return;
                }

                int changeInLength = length()- textBeforeChange.length(); //the increase of the length of the text due to the change

                //if an XSelection is available we need to replace the entire XSelected rigion with the newly entered character.
                //that is delete the entire XSelection an insert the new character in its place
                if(XSelection.isSelectionAvailable()){

                    SpansController.updateSpansOnRegion(getText(),start,start+count);//add, remove or modify the spans considering the new character and toolbar BIUH settings

                    //getSelectionStart()!=0 is just to be secure this doesn't raise an exception
                    CharSequence newchr = (getSelectionStart()!=0)?s.subSequence(getSelectionStart()-1,getSelectionStart()):"";

                    setText(textBeforeChange);  //revert text to the text before the change event

                    if(changeInLength >= 1) XSelection.replaceSelectionWith(newchr);
                    return;
                }
                if(getText().toString().equals(textBeforeChange.toString()))return;
                if (changeInLength<0) return; //no need to account for spans/enterkey if onTextChanged is due to backspace key

                if(s.charAt(start) == '\n')
                    //newline character => enterkey has been pressed, calling onEnterKeyPressed...
                    if(onEnterKeyPressed())return; //if the character is a command that we handled we don't need to think about spans -->return

                SpansController.updateSpansOnRegion(getText(),start,start+count);//add, remove or modify the spans considering the new character and toolbar BIUH settings
            }
            @Override public void afterTextChanged(Editable s) {}

        });

        //needed for onKey method to be triggered on hardware keyboard input
        setOnKeyListener(this);

        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(boundField!=null)boundField.onFocused();
                XClipboard.updateLastCopyTime(XEditText.this);
            }
        });
    }
    //We override this built-in EdiText method to detect menu button clicks.
    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id){
            case android.R.id.cut:
                XClipboard.requestCut();
                break;
            case android.R.id.paste:
                XClipboard.requestPaste(getContext(),this);
                break;
            case android.R.id.copy:
                XClipboard.requestCopy();
            case android.R.id.selectAll:
                d.wow(getContext()); //todo not implemented
        }
        return true;
    }


    /**
     * TextWatcher will trigger TextChanged events due to setText even though the change in text is not due to
     * natural causes(user typing, deleting)
     *
     * therefore we need to make sure the usual procedure (configuring spans for newly entered text etc) is
     * not followed for this event.
     *
     * So we acknowledge the code at TextWatcher using this flag
     */
    @Override
    public void setText(CharSequence text, BufferType type)  {
        noTextChangedEventFlag=true;
        super.setText(text, type);
    }

    /**
     * called by the system when user makes a selection (by double tapping/long tapping etc)
     * we need to make XSelection based on the ordinary selection if possible
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        attemptXSelection();

        if(selStart==selEnd)SpansController.updateToolbarForCurrentPosition(getText(),selStart);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        XClipboard.updateLastCopyTime(this);
        return super.dispatchTouchEvent(event);
    }




    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if(focused && boundField!=null)boundField.onFocused();
        super.onFocusChanged(focused,direction,previouslyFocusedRect);
    }

    /**
     * Called when backspace is pressed while XEditText is in focus. (by hardware & software
     * keypad key event catching codes in this)
     *
     * don't have faith this method always gets executed when backspace is pressed, it just works fine
     * for all my concerns,
     */
    private boolean backspacePressed(){

        if(XSelection.isSelectionAvailable()){
            XSelection.replaceSelectionWith("");
            return true;
        }

        if(getSelectionStart()==0  && getSelectionEnd()==0)
            //no selections, cursor is at the beginning of the XEditText, therefore nothing to backspace within the XEditText
            onBackspaceKeyPressedAtStart();  //trigger a chain of method calls that might eventually take actions(such as delete the Field above this)

        return false;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN&& keyCode==KeyEvent.KEYCODE_DEL) {
            return backspacePressed();//consumes the event if backspacePressed() is true
        }
        return false;
    }

     //region backspace key (software keypad)
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new XInputConnection(super.onCreateInputConnection(outAttrs),true);
    }


    private class XInputConnection extends InputConnectionWrapper {

        public XInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                if  (!backspacePressed()) return super.deleteSurroundingText(beforeLength, afterLength);
                return true;
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }

    }

    //endregion


    //Called when backspace is called in the beginning of the XEditText.
    //There's nothing to backspace since the cursor is already at the beginning of the textbox
    //Now we inform this to the parent Field to take necessary actions.
    //      (Such as, deleting the Field immediately above or merging this's text to the above Field deleting this Field)
    private void onBackspaceKeyPressedAtStart() {

        if (boundField==null)return;

        boundField.onBackspaceKeyPressedAtStart(this);
    }

    /**
     * Called when enter key is pressed while XEditText is in focus. (by TextWatcher ( @see init()))
     * Here we need to notify the parent Field that enter key has been pressed within the Field
     */
    private boolean onEnterKeyPressed() {

        if (boundField==null)return false;

        return boundField.onEnterKeyPressed(this);
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
        XSelection.newSelection(boundField.getNote(),start,end,this);
        //isFocused();


        return true;
    }

}
