package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.notebook.NoteHolderModes;
import com.example.sadeep.winternightd.spans.RichText;
import com.example.sadeep.winternightd.textboxes.XEditText;
import com.example.sadeep.winternightd.misc.Globals;

import static com.example.sadeep.winternightd.note.Note.STATE_EDITED;
import static com.example.sadeep.winternightd.notebook.NoteHolderModes.ModeView.*;

/**
 * Created by Sadeep on 10/15/2016.
 */

/**
 * A SimpleIndentedField that has
 */
public class CheckedField extends SimpleIndentedField {

    public static final int classFieldType = 1855431776;


    private CheckBox checkedCheckView;

    public CheckedField(Context context) {
        this(false,context);
    }

    public CheckedField(boolean isEditable, Context context) {
        super(isEditable, context);
        init();
    }

    private void init(){

        fieldType = classFieldType;

        checkedCheckView = (CheckBox) LayoutInflater.from(getContext()).inflate(R.layout.checkbox,this,false);
        checkedCheckView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(getNote().isInNotebook() && getNote().getNoteHolder().getMode()== NoteHolderModes.MODE_VIEW) {
                    ((NotebookActivity)getContext()).notebook.getNotebookDataHandler().addExistingNote(getNote());
                    getNote().noteState = STATE_EDITED;
                    ((ViewLower)getNote().getNoteHolder().getLowerChamber().getChamberContent()).setDateTime(System.currentTimeMillis());
                }
            }
        });

        checkedCheckView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getNote().isInNotebook() && getNote().getNoteHolder().getMode()== NoteHolderModes.MODE_VIEW) {
                    ((NotebookActivity)getContext()).notebook.getNotebookDataHandler().addExistingNote(getNote());
                    getNote().noteState = STATE_EDITED;
                    getNote().getNoteHolder().setMode(NoteHolderModes.MODE_VIEW,false);
                    ((ViewLower)getNote().getNoteHolder().getLowerChamber().getChamberContent()).setDateTime(System.currentTimeMillis());

                }
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins( -7* Globals.dp2px,0 , 3* Globals.dp2px,0);
        checkedCheckView.setLayoutParams(lp);

        checkedCheckView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        addView(checkedCheckView,0);
    }

    /**
     * backspace has been pressed at the start of the Fields main textbox.
     *
     * Here we need to remove ('backspace') the CheckBox [of the CheckedField]. So we convert this into a SimpleIndentedField
     */
    @Override
    public void onBackspaceKeyPressedAtStart(XEditText xEditText)
    {
        revertToSimpleIndentedField();
    }



//dumping related methods

    @Override
    public void writeToFieldDataStream(FieldDataStream stream) {
        super.writeToFieldDataStream(stream);

        stream.putInt(false,checkedCheckView.isChecked()?1:0);   //0  checked?
    }

    @Override
    public void readFromFieldDataStream(FieldDataStream stream) {
        super.readFromFieldDataStream(stream);

        checkedCheckView.setChecked(stream.getInt(false) == 1);   //0  checked?
    }
}
