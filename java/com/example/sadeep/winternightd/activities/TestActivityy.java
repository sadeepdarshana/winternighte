package com.example.sadeep.winternightd.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.bottombar.BottomBar;
import com.example.sadeep.winternightd.misc.NotebookIcon;

public class TestActivityy extends AppCompatActivity {


    LinearLayout test;
    EditText edit;
    BottomBar combined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.initialize(this);
        LinearLayout i= new LinearLayout(this);
        setContentView(i);
        i.addView(new NotebookIcon(this,"1f6e2"));
    }

    private void Click(View v) {

    }

}
