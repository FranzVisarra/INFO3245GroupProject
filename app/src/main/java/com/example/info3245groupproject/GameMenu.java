package com.example.info3245groupproject;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameMenu extends AppCompatActivity {
    public File fileName;
    File root;
    ListView listView;
    ArrayAdapter<String> stats;
    List<String> statValues = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.game_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        statValues.add("ADD NEW");
        listView = findViewById(R.id.ListView2);
        stats = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,statValues);
        listView.setAdapter(stats);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = listView.getItemAtPosition(position).toString();
                switch (selectedFromList){
                    case "ADD NEW":
                        //TODO switch to calculator intent
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

    private Runnable GetFiles = new Runnable() {
        @Override
        public void run() {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/StatCalcfiles/"+fileName);
            //TODO iterate through file to get stuff
            try(Scanner s = new Scanner(new FileReader(root))) {
                while(s.hasNext()){
                    stats.add(s.nextLine());
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    };
}
