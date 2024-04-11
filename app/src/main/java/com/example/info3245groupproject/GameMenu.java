package com.example.info3245groupproject;

import android.app.Dialog;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        Dialog dialog = new Dialog(GameMenu.this);
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
                        Intent newIntent = new Intent(view.getContext(), CalculatorMenu.class);
                        newIntent.putExtra("mode", "add");
                        startActivity(new Intent(newIntent));
                        Toast.makeText(getApplicationContext(), "Add New clicked", Toast.LENGTH_SHORT).show();
                        System.out.println(selectedFromList);
                        break;
                        /*
                    case "Game1":
                        Intent intent1=new Intent(GameMenu.this, Game1.class);
                        startActivities(new Intent[]{intent1});
                        Toast.makeText(getApplicationContext(), "Game1 clicked", Toast.LENGTH_SHORT).show();
                        System.out.println(selectedFromList);
                        break;
                         */
                    default:
                        //TODO open next activity with file name there is a reason this stuff is in default
                        editStatValue(view, dialog, selectedFromList);
                        break;
                }
            }
        });
    }

    private void editStatValue(View view, Dialog dialog, String selectedFromList){
        dialog.setContentView(R.layout.edit_stat_popup_menu);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button editValues = dialog.findViewById(R.id.btnEditStat);
        Button editFormula = dialog.findViewById(R.id.btnEditFormula);
        EditText editName = dialog.findViewById(R.id.editTextEditStatName);
        EditText editBaseValue = dialog.findViewById(R.id.editTextEditStatBaseValue);
        dialog.show();
        editValues.setOnClickListener(v -> {
            String newName = editName.getText().toString();
            String newBaseValue = editBaseValue.getText().toString();
            //TODO check for no input
            if(newBaseValue.matches("\\d+(?:\\.\\d+)?")){
                //TODO edit that stat in the file somehow then invoke getFiles again
            } else{
                Toast.makeText(GameMenu.this, "whoops, not a number", Toast.LENGTH_SHORT).show();
            }
        });
        editFormula.setOnClickListener(v -> {
            Intent EditIntent = new Intent(view.getContext(), CalculatorMenu.class);
            String[] splitStat = selectedFromList.split(",");
            //mode is sent so calculator can do different things
            EditIntent.putExtra("mode", "edit");
            EditIntent.putExtra("name", splitStat[0]);
            EditIntent.putExtra("base value", splitStat[1]);
            //EditIntent.putExtra("edited value", splitted[2]);
            EditIntent.putExtra("formula", splitStat[3]);
            //String[] splitParent = splitStat[4].split("|");
            //EditIntent.putExtra("parent", splitParent);
            String[] splitChild = splitStat[5].split("|");
            EditIntent.putExtra("child", splitChild);
            startActivity(new Intent(EditIntent));
            dialog.dismiss();
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
