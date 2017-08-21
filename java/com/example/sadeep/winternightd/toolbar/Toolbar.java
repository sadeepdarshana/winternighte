package com.example.sadeep.winternightd.toolbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.buttons.customizedbuttons.ToolbarButton;
import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.field.fields.IndentedField;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.selection.XSelection;
import com.example.sadeep.winternightd.spans.LiveFormattingStatus;
import com.example.sadeep.winternightd.spans.SpansController;
import com.example.sadeep.winternightd.spans.SpansFactory;

/**
 * Created by Sadeep on 10/12/2016.
 */
final public class Toolbar extends HorizontalScrollView{

    private static final int BUTTONS_COUNT = 6;


    private ToolbarButton[] toolbarButtons = new ToolbarButton[BUTTONS_COUNT];


    /**
     *  ButtonId    Button
     *
     *      0       bold
     *      1       italic
     *      2       underline
     *      3       highlight
     *      4       decrease indent
     *      5       increase indent
     */



    public Toolbar(Context context) {
        super(context);
        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

        LayoutInflater.from(context).inflate(R.layout.toolbar,this,true);
        LinearLayout buttonContainer = (LinearLayout) getChildAt(0);

        /**
         * We have stored the button's id in its tag property at the XML code. (toolbar.xml)
         * Below loop,
         *      1. We add the buttons to the array toolbarButtons so that toolbarButtons[x] = button with the button id x. (so we can the button by buttonId)
         *      2. Set OnClick event of each button (which is it calls toolbarButtonClicked(x) passing buttonId as x.)
         */
        for (int c = 0; c < buttonContainer.getChildCount(); c++)
        {
            ToolbarButton btn=null; try{ btn = (ToolbarButton)buttonContainer.getChildAt(c);}catch (Exception e){} // hope java will someday get C#'s 'as'

            if (btn != null)
            {
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolbarButtonClicked(Integer.parseInt((String)v.getTag()));
                    }
                });
                int tag = Integer.parseInt((String)btn.getTag());// button ID number stored(@xml code) in tag
                toolbarButtons[tag] = btn;
            }
        }

        updateStatus(LiveFormattingStatus.format);
    }

    private void toolbarButtonClicked(int buttonId) {



        if(XSelection.isSelectionAvailable()){
            if (buttonId < 3) {
                SpansController.formatRegion(XSelection.getSelectedNote(),XSelection.getSelectionStart(),XSelection.getSelectionEnd(),buttonId,LiveFormattingStatus.format[buttonId]*-1);
                Notebook.suspendScrollTemporary();
            }

        }

        if (buttonId < 3) {
            if (LiveFormattingStatus.format[buttonId] == 1)
                LiveFormattingStatus.format[buttonId] = -1;
            else if (LiveFormattingStatus.format[buttonId] == -1)
                LiveFormattingStatus.format[buttonId] = 1;
            ToolbarController.refreshToolbars();
        }

        if(buttonId==4||buttonId==5){
            Note note =((NotebookActivity)getContext()).activeNote;
            IndentedField field = null;
            try{
                field = (IndentedField) note.getFocusedChild();
            }catch (Exception e){}

            if(field!=null)field.setIndent(field.getIndent()+buttonId*2-9);
        }

    }


    public void updateStatus(int[] spanStatus) {
        if(toolbarButtons[0]==null)return;
        for(int i =0;i< SpansFactory.NO_OF_ORDINARY_SPAN_TYPES;i++) {
            if(i<3)   toolbarButtons[i].setMode((spanStatus[i]+1)/2);
        }
    }
}
