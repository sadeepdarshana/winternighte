package com.example.sadeep.winternightd.general;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.factories.SpansFactory;
import com.example.sadeep.winternightd.controllers.ToolbarController;

import java.util.ArrayList;

/**
 * Created by Sadeep on 10/12/2016.
 */

/***
 * XEditText is EditText extended to suit the requirements of WN. Major additions are,
 *  1. Support Bold, Italic, Underline, Highlight spans related functionalities
 *  2. Detect enterkey and backspacekey presses from hardware/software keyboard
 *  3.
 */
public class XEditText extends EditText {
    public XEditText(Context context) {
        super(context);
        init();
    }

    private void init(){

        setPadding(0,0,0,0);
        setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setTextSize(TypedValue.COMPLEX_UNIT_FRACTION, Globals.defaultFontSize);

        setImeOptions(getImeOptions()| EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}


            //{enterkey detection} & {managing spans to suit Toolbar BIUH settings as user types characters} done here
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 1) return; //no need to account for spans/enterkey if onTextChanged is due to backspace key or text pasting into XEditText

                if(onEnterKeyPressed())return; //if the character is a command handled we don't need to think about spans -->return
                updateSpans(start); //add, remove or modify the spans considering the new character and toolbar BIUH settings
            }

        });
    }

    /***
     * We need to add, remove or modify the spans considering the new character and toolbar BIUH settings. It's done here.
     * This function only handles span changes that occur due to the user typing in the XEditText.
     *
     * @param charIndex the index of the new character entered.
     */
    private void updateSpans(int charIndex) {
        Spannable text = getText();
        SpansFactory.RecognizedSpan[] spans = text.getSpans(charIndex - 1, charIndex, SpansFactory.RecognizedSpan.class);

        /***
         * categorize spans into span types
         * @see SpansFactory
        * */
        ArrayList<ArrayList<SpansFactory.RecognizedSpan>>spansCategory = new ArrayList<ArrayList<SpansFactory.RecognizedSpan>>();
        for(int c=0;c<4;c++)spansCategory.add(new ArrayList<SpansFactory.RecognizedSpan>());

        for (SpansFactory.RecognizedSpan span:spans) {
            spansCategory.get(span.getSpanType()).add(span);
        }

        // i here is the span type
        for (int i = 0; i < 4; i++)
        {
            //case 1 : relevant toolbar button 'on' and relevant spans not found on the previous character--> we create and add a new span to the newchar here
            if (ToolbarController.BIUH[i]) if (spansCategory.get(i).size()==0) text.setSpan(SpansFactory.createSpan(i), charIndex, charIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (int c = 0; c < spansCategory.get(i).size(); c++)
            {
                int begin = text.getSpanStart(spansCategory.get(i).get(c));
                int end = text.getSpanEnd(spansCategory.get(i).get(c));
                if (end == charIndex)
                {
                    if (ToolbarController.BIUH[i])
                    {
                        //case 2 : relevant toolbar button 'on' and a span of relevant type ends from the previous character-->we extend the span to cover this character too.
                        text.removeSpan(spansCategory.get(i).get(c));
                        text.setSpan(SpansFactory.createSpan(i), begin, charIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                else if (end > charIndex)
                {
                    //case 3 : relevant toolbar button 'off' and a span of relevant type covers the new character-->we split the span into 2 spans, before and after new char, but not including the char.
                    if (!ToolbarController.BIUH[i])
                    {
                        text.removeSpan(spansCategory.get(i).get(c));
                        text.setSpan(SpansFactory.createSpan(i), begin, charIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        text.setSpan(SpansFactory.createSpan(i), charIndex + 1, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }

    }

    private boolean onEnterKeyPressed() {
        return false;
    }


}
