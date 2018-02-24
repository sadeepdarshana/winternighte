package com.example.sadeep.winternightd.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.attachbox.AttachBoxManager;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.selection.XSelection;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        go();
        findViewById(R.id.button2).setOnClickListener((View v)->go());

    }

    private void go(){
            String pathAuth = Environment.getExternalStorageDirectory()+"/WhatsNoteDocs/USJP2015/";
            if(!new File(pathAuth).exists()){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter access code?");
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().equals("USJP2015")){

                            Toast.makeText(MainActivity.this,"Access code error",Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else {
                            Utils.createDir(Environment.getExternalStorageDirectory()+"/WhatsNoteDocs");
                            Utils.createDir(pathAuth);

                            Intent intent = new Intent(MainActivity.this, DocumentViewer.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }else{

                Intent intent = new Intent(MainActivity.this, DocumentViewer.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }


    }

}
