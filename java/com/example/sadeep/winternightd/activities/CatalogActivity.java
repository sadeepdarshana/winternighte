package com.example.sadeep.winternightd.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.catalog.Catalog;
import com.example.sadeep.winternightd.clipboard.XClipboard;
import com.example.sadeep.winternightd.localstorage.CatalogDataHandler;
import com.example.sadeep.winternightd.localstorage.DataConnection;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.notebook.NotebookInfo;

/**
 * Created by Sadeep on 7/13/2017.
 */

public class CatalogActivity extends AppCompatActivity {
    private Catalog catalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_activity);

        Globals.initialize(this);
        DataConnection.initialize(this);
        XClipboard.initialize(this);

        RelativeLayout catalogspace = (RelativeLayout) findViewById(R.id.catalogspace);
        CatalogDataHandler.createCatalogTable();
        catalog = new Catalog(this);
        catalogspace.addView(catalog);



    }

    public void onClick(View view) {
        /*
        EditText titlebox = (EditText)findViewById(R.id.title);
        NotebookInfo newnotebookinfo = NotebookInfo.newNotebookInfoForCurrentTime(this,titlebox.getText().toString());
        CatalogDataHandler.addNotebook(newnotebookinfo);
        catalog.refresh();*/


        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);


        final Dialog dialog = new Dialog(this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.new_note_dialog);
        dialog.show();
        dialog.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText)dialog.findViewById(R.id.notebook_name);
                if(text.length()==0)return;
                NotebookInfo newnotebookinfo = NotebookInfo.newNotebookInfoForCurrentTime(CatalogActivity.this,text.getText().toString());
                CatalogDataHandler.addNotebook(newnotebookinfo);
                catalog.refresh();
                dialog.dismiss();
            }
        });
    }
}
