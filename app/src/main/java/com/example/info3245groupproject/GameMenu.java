package com.example.info3245groupproject;

import android.content.Intent;
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
                        //TODO switch to calculator with data
                        System.out.println(selectedFromList);
                        //String tempString = pars
                        Intent NewIntent=new Intent(view.getContext(), CalculatorMenu.class);
                        NewIntent.putExtra("this name", selectedFromList);
                        //TODO make get in other thing
                        break;
                    default:
                        //TODO open next activity with file name
                        System.out.println(selectedFromList);
                        //String tempString = pars
                        Intent EditIntent=new Intent(view.getContext(), CalculatorMenu.class);
                        //split string with character
                        String[] splitted = selectedFromList.split(",");
                        EditIntent.putExtra("name", splitted[0]);
                        EditIntent.putExtra("base value", splitted[1]);
                        EditIntent.putExtra("edited value", splitted[2]);
                        EditIntent.putExtra("formula", splitted[3]);
                        EditIntent.putExtra("parent", splitted[4]);
                        EditIntent.putExtra("child", splitted[5]);
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
