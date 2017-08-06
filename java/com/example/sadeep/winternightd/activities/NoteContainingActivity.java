package com.example.sadeep.winternightd.activities;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.misc.NoteContainingActivityRootView;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.selection.XSelection;

/**
 * Created by Sadeep on 6/17/2017.
 */

public abstract class NoteContainingActivity extends AppCompatActivity {


    public static final int ACTIONBAR_NORMAL = 0;
    public static final int ACTIONBAR_SELECT = 1;

    private int mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarMode(ACTIONBAR_NORMAL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onMenuItemPressed(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if(mode == ACTIONBAR_NORMAL) getMenuInflater().inflate(R.menu.notebook, menu);
        if(mode == ACTIONBAR_SELECT) {
            getMenuInflater().inflate(R.menu.selectmenu, menu);
            menu.findItem(R.id.action_cut).setVisible(XSelection.getSelectedNote().getIsEditable());
        }

        return super.onPrepareOptionsMenu(menu);
    }


    public void setActionBarMode(int i) {

        mode =i;

        if (i == ACTIONBAR_NORMAL) {
            this.setTitle(getActionBarTitle());

            getSupportActionBar().setBackgroundDrawable(getActionBarDrawable());

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            invalidateOptionsMenu();
        }

        if (i == ACTIONBAR_SELECT) {
            this.setTitle("Select");

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff949494));

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            invalidateOptionsMenu();
        }
    }



    public void onRootLayoutSizeChanged(){}

    protected abstract void onMenuItemPressed(int menuItem);
    protected abstract Drawable getActionBarDrawable();
    protected abstract String getActionBarTitle();
    public abstract NoteContainingActivityRootView getRootView();

}
