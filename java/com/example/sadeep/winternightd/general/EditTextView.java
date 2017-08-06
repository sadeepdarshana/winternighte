package com.example.sadeep.winternightd.general;

/**
 * Created by Sadeep on 10/14/2016.
 */

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sadeep.winternightd.misc.RichText;

/**
 * A class created to unit XEditText and XTextView, behaves as either upon request(by calling setIsEditable).
 * Fields do not have to account separately for editable and non-editable textboxes because they use this class like this class wraps these 2 classes.
 */
public class EditTextView {

    private Context context;
    private boolean isEditable;

    private XEditText xEditText;
    private XTextView xTextView;

    public EditTextView(Context context){
        this(false,context);
    }

    public EditTextView(boolean isEditable,Context context){
        this.context=context;
        this.isEditable=isEditable;

        xEditText = isEditable ? new XEditText(context) : null;
        xTextView = isEditable ? null : new XTextView(context);
    }

    // returns whatever the active view (XEditText if editable, XTextView otherwise)
    public TextView get(){ //return type is TextView because TextView is the last common ancestor of XEditText and XTextView
        if(isEditable)return xEditText;
        return xTextView;
    }

    /**
     * suppose isEditable is changed  from true to false
     * in that case this method,
     *      replaces the XEditText with a new XTextView in the parent view
     *      copies the text from XEditText to the new XTextView
     *
     *      now the XEditText has become an XTextView !!!
     */
    public void setIsEditable(boolean isEditable){

        if(this.isEditable == isEditable)return;

        TextView oldView = this.isEditable ? xEditText : xTextView;
        TextView newView = this.isEditable ? new XTextView(context) : new XEditText(context);

        RichText richTextPrev = RichText.generateRichText((Spanned) oldView.getText());
        SpannableStringBuilder spannableStringBuilder = richTextPrev.getCharSequence();

        newView.setText(spannableStringBuilder);

        xTextView = this.isEditable ? (XTextView) newView : null;
        xEditText = this.isEditable ?  null : (XEditText)newView;

        ViewGroup parent = ((ViewGroup)oldView.getParent());
        parent.addView(newView,parent.indexOfChild(oldView));
        parent.removeView(oldView);

        this.isEditable = isEditable;
    }

    public boolean getIsEditable() {
        return isEditable;
    }
}
