package com.example.info3245groupproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> strings = new ArrayList<String>();
    LinearLayout layout;

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
        //TODO add filenames to list
        File root = new File("/data/data/com.example.info3245groupproject/files");
        for(File f : root.listFiles()){
            System.out.println(f);
        }
        //default add button
        strings.add("ADD NEW");
        strings.add("Test");
        strings.add("Tasdsgfeifhew");

        layout = findViewById(R.id.LinLay1);
        Dialog dialog = new Dialog(MainActivity.this);
        //this is where the menu is held
        for(String s : strings)
        {
            TextView newTextView = new TextView(this);
            newTextView.setText(s);
            newTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    switch (s) {
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
                                    strings.add(input);
                                    System.out.println(input);
                                    if (CheckUserInput(input)) {
                                        try {
                                            FileOutputStream fos = openFileOutput(input, Context.MODE_PRIVATE);
                                            fos.flush();
                                            fos.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                    else{
                                        //TODO make the user know their input is invalid
                                    }
                                }
                            });
                            System.out.println(s);
                            break;
                    }
                }
            });
            layout.addView(newTextView);
        }
        strings.remove("ADD NEW");
    }
    private boolean CheckUserInput(String inp){
        return true;
    }
}