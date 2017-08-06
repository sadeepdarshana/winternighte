package com.example.sadeep.winternightd.notebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.misc.NotebookItemChamber;
import com.example.sadeep.winternightd.misc.SexyCard;

/**
 * Created by Sadeep on 7/24/2017.
 */

public class NotebookItem  extends SexyCard {

    private LinearLayout notebookItemInnerLayout;
    private NotebookItemChamber lowerChamber;
    private NotebookItemChamber upperChamber;
    private LinearLayout noteSpace;

    public NotebookItem(Context context) {
        super(context);


        notebookItemInnerLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.notebookitem,null);
        addView(notebookItemInnerLayout);

        SexyCard.LayoutParams params = new SexyCard.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        notebookItemInnerLayout.setLayoutParams(params);

        lowerChamber = (NotebookItemChamber) notebookItemInnerLayout.findViewById(R.id.lowerchamber);
        upperChamber = (NotebookItemChamber) notebookItemInnerLayout.findViewById(R.id.upperchamber);
        noteSpace = (LinearLayout) notebookItemInnerLayout.findViewById(R.id.notespace);


    }


    public NotebookItemChamber getLowerChamber() {
        return lowerChamber;
    }

    public NotebookItemChamber getUpperChamber() {
        return upperChamber;
    }

    public LinearLayout getNoteSpace() {
        return noteSpace;
    }

}
