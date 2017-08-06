package com.example.sadeep.winternightd.fields;

import android.content.Context;
import android.provider.Settings;

import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/14/2016.
 */

/**
 * A field that supports indenting. Changing indentation is done by changing the paddingLeft of the Field.
 */
public abstract class IndentedField extends Field {

    public static final int classFieldType = 1;

    private int indent;
    private final int indentSize = Globals.dp2px * 30;

    private int defaultPaddingLeft;
    /** defaultPaddingLeft-----
     * this offset will be added to the actual paddingLeft. (actual Field padding value = this value when indent=0)
     *  actual paddingLeft = defaultPaddingLeft + indent * indentSize
     *  This value is because we want the IndentedField to act normal for setPadding() calls from outside though we use paddingLeft for a special purpose here.
     *
     */
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

}
