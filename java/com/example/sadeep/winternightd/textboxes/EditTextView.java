package com.example.sadeep.winternightd.textboxes;

/**
 * Created by Sadeep on 10/14/2016.
 */

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.spans.RichText;

/**
 * A class created to unite XEditText and XTextView, behaves as either upon request(by calling setEditable).
 *
 * Fields do not have to account separately for editable and non-editable textboxes because they use this class.
 * (like this class wraps the 2 classes)
 *
 * This class itself is not an android View, but a java class that stores references to a object of
 * either XTextView or XEditText.
 *
 * A XEditText or XTextView (depending on the isEditable attribute passed to constructor) is created when an instance
 * of this class is created which can be fetched through get(). Now you can attach the fetched android view to a parent.
 * If you change the editability of this EditTextView(using setEditable) this class replaces the former View with a
 * new XEditText/XTextView with the same text as the former view.
 */
public class EditTextView {

    private Context context;
    private boolean isEditable;

    private XEditText xEditText;
    private XTextView xTextView;

    //the Field which the XEditText/XTextView will be attached to
    //we need to keep track of this because we want to pass it to the constructor each time a XTextView/XEditText is created
    private Field boundField;

    public EditTextView(Context context){
        this(context, false, null);
    }
    public EditTextView(Context context,Field boundField){
        this(context, false, boundField);
    }
    public EditTextView(Context context,boolean isEditable){
        this(context, isEditable, null);
    }

    public EditTextView(Context context, boolean isEditable, Field boundField){
        this.context=context;
        this.isEditable=isEditable;
        this.boundField = boundField;

        //create an XEditText if editable, XTextView otherwise
        xEditText = isEditable ? new XEditText(boundField,context) : null;
        xTextView = isEditable ? null : new XTextView(boundField,context);
    }

    // returns the View (XEditText if editable, XTextView otherwise)
    public TextView get(){ //return type is TextView because TextView is the last common ancestor of XEditText and XTextView
        if(isEditable)return xEditText;
        return xTextView;
    }

    /**
     * suppose isEditable is changed  from true to false
     * in that case this method,
     *      replaces the XEditText with a new XTextView in the same position in parent view
     *      copies the text from XEditText to the new XTextView
     *
     *      now the XEditText has become an XTextView !
     */
    public void setIsEditable(boolean isEditable){

        if(this.isEditable == isEditable)return; //already set, no need to set again

        TextView oldView = this.isEditable ? xEditText : xTextView;
        TextView newView = this.isEditable ? new XTextView(boundField,context) : new XEditText(boundField,context);

        newView.setTextColor(oldView.getTextColors());
        newView.setGravity(oldView.getGravity());
        newView.setTypeface(oldView.getTypeface());

        RichText richTextPrev;
        try {
            richTextPrev = RichText.generateRichText((Spanned) oldView.getText()); //dump the content of text(+spans) to a RichText
        }catch(Exception e){richTextPrev = RichText.generateRichText(new SpannableStringBuilder(""));}
        SpannableStringBuilder spannableStringBuilder = richTextPrev.getCharSequence(); //generate string back

        newView.setText(spannableStringBuilder);

        xTextView = this.isEditable ? (XTextView) newView : null;
        xEditText = this.isEditable ?  null : (XEditText)newView;

        //replace the old View with the new one in the relevant position in the parent
        ViewGroup parent = ((ViewGroup)oldView.getParent());
        parent.addView(newView,parent.indexOfChild(oldView));
        parent.removeView(oldView);

        this.isEditable = isEditable;

    }
    public CharSequence getText(){
        if(isEditable)return xEditText.getText();
        return xTextView.getText();
    }
    public boolean getIsEditable() {
        return isEditable;
    }

}
