package com.example.info3245groupproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ListAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> strings = new ArrayList<String>();
    ListView files;
    ArrayAdapter<String> list;
    public File root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Dialog dialog = new Dialog(MainActivity.this);
        //default add button
        strings.add("ADD NEW");

        files = findViewById(R.id.ListView1);
        list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,strings);
        files.setAdapter(list);
        files.setClickable(true);
        files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = files.getItemAtPosition(position).toString();
                switch (selectedFromList){
                    case "ADD NEW":
                        dialog.setContentView(R.layout.add_file_popup_menu);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        Button create = dialog.findViewById(R.id.btnCreateNewFile);
                        EditText txtInput = dialog.findViewById(R.id.editTextUserInputNewFileName);
                        dialog.show();
                        create.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO string validation. make sure users don't enter anything stupid.
                                String input = txtInput.getText().toString();
                                if (CheckUserInput(input)) {

                                    try {
                                        File file = new File(root + "/"+input+".txt");
                                        file.createNewFile();
                                        strings.add(input);
                                        list.notifyDataSetChanged();
                                        System.out.println(input);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    dialog.dismiss();
                                    //Intent intent = getIntent();
                                    //finish();
                                    //startActivity(intent);
                                }
                                else{
                                    //TODO make the user know their input is invalid
                                }
                            }
                        });
                        System.out.println(selectedFromList);
                        break;
                    default:
                        //TODO open next activity with file name
                        break;
                }
            }
        });
    }
    public void onStart() {
        super.onStart();
        Thread getFiles = new Thread(GetFiles);
        getFiles.start();


    }
    public void onStop(){
        super.onStop();
    }
    private boolean CheckUserInput(String inp){
        return true;
    }
    //TODO figure out closing thread
    private Runnable GetFiles = new Runnable() {
        @Override
        public void run() {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/StatCalcfiles");
            if(!root.exists())
            {
                System.out.println("root not found");
                if(!root.mkdir()){
                    System.out.println("Failed to make directory");
                }
                else{
                    System.out.println("Directory made");
                }
            }
            else{
                System.out.println("root found");
            }
            for(File f : root.listFiles()){
                //TODO add filenames to list
                strings.add(f.getName());
                System.out.println(f.getName());
            }
        }
    };
    private Runnable UpdateUI = new Runnable() {
        @Override
        public void run() {

        }
    };
}