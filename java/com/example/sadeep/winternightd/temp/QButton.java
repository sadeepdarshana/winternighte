package com.example.sadeep.winternightd.temp;

import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * Created by Sadeep on 10/13/2016.
 */

//QButton is for debugging, testing only. A quick way to add the code that should be executed, no need to worry about setting OnClickListeners.
public abstract class QButton extends Button implements Button.OnClickListener {
    public QButton(Context context) {
        super(context);
        setOnClickListener(this);
    }

    @Override
    public abstract void onClick(View v);
}
