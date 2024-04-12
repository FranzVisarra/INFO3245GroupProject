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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class GameMenu extends AppCompatActivity {
    public File fileName;
    File root;
    ListView listView;
    ArrayAdapter<String> stats;
    List<String> statValues = new ArrayList<String>();
    public String mode;
    public List<String> lines;//the lines that are in the file
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
        fileName = new File(getIntent().getStringExtra("fileName"));
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
                        //TODO add dialog that switches to calculator intent
                        Intent newIntent = new Intent(view.getContext(), CalculatorMenu.class);
                        newIntent.putExtra("mode", "add");
                        startActivity(new Intent(newIntent));
                        Toast.makeText(getApplicationContext(), "Add New clicked", Toast.LENGTH_SHORT).show();
                        System.out.println(selectedFromList);
                        break;
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
        Thread setFiles = new Thread(SetFiles);
        getFiles.start();

        mode = getIntent().getStringExtra("mode");
        String[] tempParent;
        String[] tempChild;
        String tempString = null;
        String tempParentString = null;
        String temp = null;
        switch (mode){
            case "none":
                //Literally nothing. like you came from the main screen
                break;
            case "edit"://when you edit a value
                List<String> receivedEdited = new ArrayList<String>();
                String receivedEditedName = getIntent().getStringExtra("name");
                receivedEdited.add(receivedEditedName+"|");
                receivedEdited.add(getIntent().getStringExtra("base value")+"|");
                receivedEdited.add(getIntent().getStringExtra("edited value")+"|");
                receivedEdited.add(getIntent().getStringExtra("formula")+"|");
                tempParent = getIntent().getStringArrayExtra("parent");
                //clear child associations
                for(int n = 0; n < lines.size(); n++){
                    String[] test = lines.get(n).split("|");
                    //clear child
                    test[5] = "";
                }
                //TODO what if length empty
                //add parent association to this string from recieved array from file
                for (int i = 0; i < tempParent.length; i++){
                    if (i !=0){
                        tempParentString += tempParent[i]+",";
                    }else{
                        tempParentString = tempParent[i];
                    }
                }
                //add parent to received list
                receivedEdited.add(tempParentString+"|");
                //add all of it to a string to be done
                for(String what : receivedEdited){
                    temp+=receivedEdited;
                }
                //search through list for entry
                for(int i = 0; i < lines.size();i++){
                    String[] test = lines.get(i).split("|");
                    //append entry
                    if (test[0].equals(receivedEditedName)){
                        //set it
                        lines.set(i,temp);
                        break;
                    }
                }
                //set children in everything
                //TODO check if lines has 0 size
                for(int i = 0; i< lines.size(); i++){
                    //split line into parts
                    String[] test = lines.get(i).split("|");
                    //split parent part into individual
                    String[] parent = test[4].split(",");
                    //loop through each individual parent call
                    for (int n = 0; n <= parent.length; n++){
                        //loop through each line for the purpose of checking if parent equals line name
                        for(int d = 0; d< lines.size(); d++){
                            //split current line
                            String[] tms = lines.get(i).split("|");
                            //check if name equals parent ref
                            if (tms[0].equals(parent[n])){
                                //check if child reference is empty
                                if (lines.get(i).charAt(lines.get(i).length()-1) == '|'){
                                    //make this first parent reference
                                    lines.set(i,lines.get(i)+test[0]);
                                }else{
                                    //append original line name to end of line;
                                    lines.set(i,lines.get(i)+","+test[0]);
                                }
                                break;
                            }
                        }
                    }
                }
                //set values
                for (String line: lines){
                    String[] formula = line.split("|");
                    //todo calculation with formula[3]
                }
                //set file
                setFiles.start();
            case "create"://when you create a stat
                //TODO write new stat to file
                List<String> recieved = new ArrayList<String>();
                String receivedName = getIntent().getStringExtra("name");
                recieved.add(getIntent().getStringExtra("base value")+"|");
                recieved.add(getIntent().getStringExtra("edited value")+"|");
                recieved.add(getIntent().getStringExtra("formula")+"|");
                tempParent = getIntent().getStringArrayExtra("parent");
                //clear child associations
                for(int n = 0; n < lines.size(); n++){
                    String[] test = lines.get(n).split("|");
                    //clear child
                    test[5] = "";
                }
                //TODO what if length empty
                //add parent association to this string from recieved array from file
                for (int i = 0; i < tempParent.length; i++){
                    if (i !=0){
                        tempParentString += tempParent[i]+",";
                    }else{
                        tempParentString = tempParent[i];
                    }
                }
                //add parent to received list
                recieved.add(tempParentString+"|");
                //add all of it to a string to be done
                for(String what : recieved){
                    temp+=recieved;
                }
                //add all to list
                lines.add(temp);
                //set children in everything
                //TODO check if lines has 0 size
                for(int i = 0; i< lines.size(); i++){
                    //split line into parts
                    String[] test = lines.get(i).split("|");
                    //split parent part into individual
                    String[] parent = test[4].split(",");
                    //loop through each individual parent call
                    for (int n = 0; n <= parent.length; n++){
                        //loop through each line for the purpose of checking if parent equals line name
                        for(int d = 0; d< lines.size(); d++){
                            //split current line
                            String[] tms = lines.get(i).split("|");
                            //check if name equals parent ref
                            if (tms[0].equals(parent[n])){
                                //check if child reference is empty
                                if (lines.get(i).charAt(lines.get(i).length()-1) == '|'){
                                    //make this first parent reference
                                    lines.set(i,lines.get(i)+test[0]);
                                }else{
                                    //append original line name to end of line;
                                    lines.set(i,lines.get(i)+","+test[0]);
                                }
                                break;
                            }
                        }
                    }
                }
                //set values
                for (String line: lines){
                    String[] formula = line.split("|");
                    //todo calculation with formula[3]
                }
                //set file
                setFiles.start();
                break;
        }
    }

    private Runnable GetFiles = new Runnable() {
        @Override
        public void run() {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/StatCalcfiles/"+fileName);
            try(Scanner s = new Scanner(new FileReader(root))) {
                int i = 0;
                while(s.hasNext()){
                    lines.add(s.nextLine());
                    stats.add(lines.get(i));
                    i++;
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    };
    private Runnable SetFiles = new Runnable() {
        @Override
        public void run() {
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/StatCalcfiles/"+fileName);
            try {
                FileWriter fileOut = new FileWriter(root);
                fileOut = new FileWriter(root, true);
                BufferedWriter buff = new BufferedWriter(fileOut);
                PrintWriter print = new PrintWriter(buff);
                for (String line : lines){
                    if (Objects.equals(line, lines.get(lines.size()-1))){
                        //print line
                        print.print(line);
                    }else{
                        //add space after this line
                        print.println(line);
                    }
                }
                print.flush();
                print.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Thread getFiles = new Thread(GetFiles);
            getFiles.start();
        }
    };

}
