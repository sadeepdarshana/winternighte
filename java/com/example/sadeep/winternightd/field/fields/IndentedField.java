package com.example.sadeep.winternightd.field.fields;

import android.content.Context;

import com.example.sadeep.winternightd.textboxes.XEditText;
import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/14/2016.
 */

/**
 * A field that supports indenting. Changing indentation is done by changing the paddingLeft of the Field.
 */
public abstract class IndentedField extends Field {

    public static final int classFieldType = 1761816019;

    private int indent;  //the current indent of the Field. this is 0 for no indentation
    private final int indentSize = Globals.dp2px * 30; //the offset due to change of indent by 1 (i.e. width of a single indentation)

    /** defaultPaddingLeft-----
     *  this offset will be added to the paddingLeft in addition to padding due to indentation
     *      (i.e. actual Field padding value = this value when indent is 0)
     *
     *  paddingLeft = defaultPaddingLeft + indent * indentSize
     *
     *  This value is because we want the IndentedField to act normal for setPadding() calls
     *  from outside though we use paddingLeft for a special purpose here.
     */
    private int defaultPaddingLeft;



    public IndentedField(Context context) {
        super(context);
        init();
    }

    private void init(){
        fieldType = classFieldType;
        defaultPaddingLeft = getPaddingLeft();
    }

    public int getIndent(){
        return indent;
    }

    public void setIndent(int indent){

        if(this.indent == indent)return;

        indent = Math.max(indent,0);

        this.indent = indent;

        setPadding( defaultPaddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());

        refreshThisAndAllFieldsBelowToAccountForNoteChanges();
    }

    /**
     * setPadding has been overridden to make sure that an outsider calling the setPadding method on the Field will not ruin the
     * indentations
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom){
        defaultPaddingLeft = left;
        super.setPadding( defaultPaddingLeft+indent*indentSize, top, right, bottom);
    }

    /**We go erasing(decreasing) tabs(indents) till we've erased all the indents, then we pass the
    // command to the super (where it is called to backspace the previous Field).**/
    @Override
    public void onBackspaceKeyPressedAtStart(XEditText xEditText) {

        if(getIndent()>0)
            setIndent(getIndent()-1);
        else
            super.onBackspaceKeyPressedAtStart(xEditText);
    }


    @Override
    public void writeToFieldDataStream(FieldDataStream stream) {
        super.writeToFieldDataStream(stream);
        stream.putInt(false,indent);                                                 //0 indent
    }
    @Override
    public void readFromFieldDataStream(FieldDataStream stream) {
        super.readFromFieldDataStream(stream);
        setIndent(stream.getInt(false));                              //0 indent
    }
}
