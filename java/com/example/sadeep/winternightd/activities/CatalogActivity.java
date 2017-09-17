package com.example.sadeep.winternightd.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.catalog.Catalog;
import com.example.sadeep.winternightd.clipboard.XClipboard;
import com.example.sadeep.winternightd.localstorage.CatalogDataHandler;
import com.example.sadeep.winternightd.localstorage.DataConnection;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.NotebookIcon;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.notebook.NotebookInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sadeep on 7/13/2017.
 */

public class CatalogActivity extends AppCompatActivity {
    private Catalog catalog;
    private static final String REGISTER_URL = "http://wnbeta0.azurewebsites.net/feedback.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_activity);
        //if(true)return;
        String destPath = getFilesDir().getPath();
        String dbpath =  destPath.substring(0, destPath.lastIndexOf("/")) + "/databases";;

        try{
            if(!new File(dbpath+"/winternightd.db").exists()) copyDataBase(dbpath);
        }catch (Exception e) {

        }



        Globals.initialize(this);
        DataConnection.initialize(this);
        XClipboard.initialize(this);

        RelativeLayout catalogspace = (RelativeLayout) findViewById(R.id.catalogspace);
        CatalogDataHandler.createCatalogTable();
        catalog = new Catalog(this);
        catalogspace.addView(catalog);



    }

    public Object lst(String path){

        File f = new File(path);
        return f.list();

    }
    private void copyDataBase(String dbPath){
        try{
            InputStream assestDB = getAssets().open("databases/winternightd.db");

            final File newFile = new File(dbPath);
            newFile.mkdir();

            OutputStream appDB = new FileOutputStream(dbPath+"/winternightd.db",false);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = assestDB.read(buffer)) > 0) {
                appDB.write(buffer, 0, length);
            }

            appDB.flush();
            appDB.close();
            assestDB.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }


    public void onClick(View view) {
        /*
        EditText titlebox = (EditText)findViewById(R.id.title);
        NotebookInfo newnotebookinfo = NotebookInfo.newNotebookInfoForCurrentTime(this,titlebox.getText().toString());
        CatalogDataHandler.addNotebook(newnotebookinfo);
        catalog.refresh();*/

        Utils.showKeyboard(view);

        displayAddEditDialog(NotebookInfo.newNotebookInfoForCurrentTime(CatalogActivity.this,""),false);
    }

    public void displayFeedbackDialog(View v){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.feedback_dialog);
        String msg = ((EditText)dialog.findViewById(R.id.msg)).getText().toString();
        dialog.show();DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((7 * width)/7, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(CatalogActivity.this,"Thanks",Toast.LENGTH_LONG).show();
                                dialog.dismiss();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(CatalogActivity.this,"Error! Please check internet connection",Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        String msg=((EditText)dialog.findViewById(R.id.msg)).getText().toString();
                        String name=((EditText)dialog.findViewById(R.id.name)).getText().toString();
                        params.put("msg",msg);
                        params.put("name", name);
                        return params;
                    }

                };

                RequestQueue requestQueue = Volley.newRequestQueue(CatalogActivity.this);
                requestQueue.add(stringRequest);
            }
        });

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void displayAddEditDialog(final NotebookInfo info, boolean delButton){

        final Dialog dialog = new Dialog(this,R.style.Theme_Dialog);
        dialog.setContentView(R.layout.new_note_dialog);
        ((ImageButton)dialog.findViewById(R.id.image)).setImageDrawable(NotebookIcon.getIcon(this,info.icon));
        EditText bookName = ((EditText)dialog.findViewById(R.id.notebook_name));
        bookName.setText(info.title);
        bookName.setSelection(bookName.length());
        dialog.show();
        if(delButton)dialog.findViewById(R.id.delete).setVisibility(View.VISIBLE);

        dialog.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText)dialog.findViewById(R.id.notebook_name);
                if(text.length()==0)return;
                info.title=text.getText().toString();
                CatalogDataHandler.addNotebook(info);
                catalog.refresh();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogDataHandler.deleteNotebook(info.notebookUUID);
                dialog.dismiss();
                catalog.refresh();
            }
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}
