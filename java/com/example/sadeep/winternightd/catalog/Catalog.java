package com.example.sadeep.winternightd.catalog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.sadeep.winternightd.localstorage.CatalogCursorReader;
import com.example.sadeep.winternightd.localstorage.CatalogDataHandler;
import com.example.sadeep.winternightd.localstorage.NotebookCursorReader;
import com.example.sadeep.winternightd.localstorage.NotebookDataHandler;


/**
 * Created by Sadeep on 7/13/2017.
 */



public class Catalog extends RecyclerView {

    private CatalogDataHandler dataHandler;
    private LinearLayoutManager layoutManager;
    private Context context;

    public Catalog(Context context) {
        super(context);
        this.context = context;

        layoutManager = new LinearLayoutManager(context);
        dataHandler = new CatalogDataHandler();

        setLayoutManager(layoutManager);
        setAdapter(new CatalogAdapter(context,new CatalogCursorReader(dataHandler.getCursor())));
    }

    public void refresh() {
        dataHandler = new CatalogDataHandler();
        setAdapter(new CatalogAdapter(context,new CatalogCursorReader(dataHandler.getCursor())));
    }

}
