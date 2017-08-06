package com.example.sadeep.winternightd.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.catalog.Catalog;
import com.example.sadeep.winternightd.clipboard.XClipboard;
import com.example.sadeep.winternightd.localstorage.CatalogDataHandler;
import com.example.sadeep.winternightd.localstorage.DataConnection;
import com.example.sadeep.winternightd.misc.Globals;
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

        FrameLayout catalogspace = (FrameLayout)findViewById(R.id.catalogspace);
        CatalogDataHandler.createCatalogTable();
        catalog = new Catalog(this);
        catalogspace.addView(catalog);


        EditText titlebox = (EditText)findViewById(R.id.title);
        titlebox.setFocusableInTouchMode(true);
    }

    public void onClick(View view) {
        EditText titlebox = (EditText)findViewById(R.id.title);
        NotebookInfo newnotebookinfo = NotebookInfo.newNotebookInfoForCurrentTime(titlebox.getText().toString());
        CatalogDataHandler.addNotebook(newnotebookinfo);
        catalog.refresh();
    }
}
