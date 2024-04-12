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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
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
    public Dictionary<String, Float> varDict;
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
                receivedEdited.add("|");
                receivedEdited.add(getIntent().getStringExtra("formula")+"|");
                tempParent = getIntent().getStringArrayExtra("parent");
                //clear child associations
                for(int n = 0; n < lines.size(); n++){
                    String[] test = lines.get(n).split("|");
                    //clear child
                    test[5] = "";
                }
                //what if length empty
                //add parent association to this string from recieved array from file
                if(Objects.requireNonNull(tempParent).length!=0) {
                    for (int i = 0; i < tempParent.length; i++) {
                        if (i != tempParent.length-1) {
                            tempParentString += tempParent[i] + ",";
                        } else {
                            tempParentString += tempParent[i];
                        }
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
                for (int i = 0; i < lines.size(); i++) {
                    //calculation with formula[3]
                    String line = lines.get(i);
                    String[] formula = line.split("|");
                    formula[2] = StringToList(formula[3]).toString();
                    lines.set(i, Arrays.toString(formula));
                }
                //set file
                setFiles.start();
            case "create"://when you create a stat
                //recieve values from other intent
                List<String> received = new ArrayList<String>();
                String receivedName = getIntent().getStringExtra("name");
                received.add(receivedName+"|");
                received.add(getIntent().getStringExtra("base value")+"|");
                received.add("|");
                received.add(getIntent().getStringExtra("formula")+"|");
                tempParent = getIntent().getStringArrayExtra("parent");
                //clear child associations
                for(int n = 0; n < lines.size(); n++){
                    String[] test = lines.get(n).split("|");
                    //clear child
                    //test[5] = "";//new so no children
                }
                //what if length empty
                //add parent association to this string from recieved array from file
                if (Objects.requireNonNull(tempParent).length!=0) {
                    for (int i = 0; i < tempParent.length; i++) {
                        if (i != tempParent.length-1) {
                            tempParentString += tempParent[i] + ",";
                        } else {
                            tempParentString += tempParent[i];
                        }
                    }
                }
                //add parent to received list
                received.add(tempParentString+"|");
                //add all of it to a string to be done
                for(String what : received){
                    temp+=received;
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
                for (int i = 0; i < lines.size(); i++) {
                    //calculation with formula[3]
                    String line = lines.get(i);
                    String[] formula = line.split("|");
                    formula[2] = StringToList(formula[3]).toString();
                    lines.set(i, Arrays.toString(formula));
                }
                //set file
                setFiles.start();
                break;
        }
    }

    public void onDestroy(){
        super.onDestroy();
        //TODO all dialog should die here
    }

    private Runnable GetFiles = new Runnable() {
        @Override
        public void run() {
            Dictionary<String, Float> emptDict = null;
            varDict = emptDict;
            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/StatCalcfiles/"+fileName);
            try(Scanner s = new Scanner(new FileReader(root))) {
                int i = 0;
                while(s.hasNext()){
                    lines.add(s.nextLine());
                    String[] linArr = lines.get(i).split("|");
                    varDict.put(linArr[0], Float.valueOf(linArr[2]));
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
    public List<String> StringToList(String form){
        List<String> formList = new ArrayList<>();
        int position = 0;//start position
        boolean prevOp=false;
        for (int i = 0; i < form.length(); i++){
            String temp = String.valueOf(form.charAt(i));
            //check for special characters
            if (temp.equals("[") ||//round up start
                    temp.equals("]") ||//round up end
                    temp.equals("{") ||//round down start
                    temp.equals("}") ||//round down end
                    temp.equals("(") ||//bracket start
                    temp.equals(")") ||//bracket end
                    temp.equals("/") ||//division
                    temp.equals("*") ||//multiplication
                    temp.equals("+") ||//addition
                    temp.equals("-") //subtraction
            ){
                //add characters between start position and current index
                if (i!=0&&!prevOp) {//make sure you do not double book
                    formList.add(form.substring(position, i));//end is exclusive so it wont add the current index
                }
                //add special character that was detected
                formList.add(String.valueOf(form.charAt(i)));
                //set position to start from at next index
                position = i+1;
                prevOp = true;
            } else if (i == form.length()-1){//edge case
                formList.add(form.substring(position,i+1));
            }else{
                prevOp=false;
            }
            System.out.println(formList);
        }
        return formList;
    }
    public List<String> ShortenFormula(List<String> temp,String mode){
        System.out.println(temp);
        //remove cursor
        temp.remove("|");
        //initialize the formula that will be handled
        List<String> formula = new ArrayList<>();
        float calc;
        switch (mode){
            case "search"://check through list
                //brackets first
                if (temp.contains("(")||temp.contains("[")||temp.contains("{")) {//this better get an exact match
                    //set bracket positions
                    int groupStartPos = 0;
                    int upStartPos = 0;
                    int downStartPos = 0;
                    int endPos;
                    for (int i = 0; i < temp.size(); i++) {
                        switch (temp.get(i)) {
                            case "(":
                                //always gets last start bracket
                                groupStartPos = i;
                                break;
                            case "[":
                                //always gets last start bracket
                                upStartPos = i;
                                break;
                            case "{":
                                //always gets last start bracket
                                downStartPos = i;
                                break;
                        }
                        switch (temp.get(i)) {
                            case ")":
                                //set end position
                                endPos = i;
                                //get stuff between brackets
                                for (int n = groupStartPos + 1; n < i; n++) {
                                    formula.add(temp.get(n));
                                }
                                if (endPos >= groupStartPos) {
                                    temp.subList(groupStartPos, endPos + 1).clear();
                                }
                                //run it back to do stuff between
                                //add new result
                                temp.add(groupStartPos, ShortenFormula(formula, "search").get(0));
                                break;
                            case "]":
                                //set end position
                                endPos = i;
                                //get stuff between brackets
                                for (int n = upStartPos + 1; n < i; n++) {
                                    formula.add(temp.get(n));
                                }
                                //remove the brackets and everything inbetween
                                if (endPos >= upStartPos) {
                                    temp.subList(upStartPos, endPos + 1).clear();
                                }
                                //run it back to do stuff between
                                //add new result
                                temp.add(upStartPos, ShortenFormula(formula, "roundUp").get(0));
                                break;
                            case "}":
                                //set end position
                                endPos = i;
                                //get stuff between brackets
                                for (int n = downStartPos + 1; n < i; n++) {
                                    formula.add(temp.get(n));
                                }
                                //remove the brackets and everything inbetween
                                if (endPos >= downStartPos) {
                                    temp.subList(downStartPos, endPos + 1).clear();
                                }
                                //run it back to do stuff between
                                //add new result
                                temp.add(downStartPos, ShortenFormula(formula, "roundDown").get(0));
                                break;
                        }
                    }
                    //run it again
                    temp = ShortenFormula(temp, "search");
                }
                //math next
                //division multiplication next
                else if (temp.contains("/")||temp.contains("*")){
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).equals("/")||temp.get(i).equals("*")){
                            //add to temp
                            for (int n = -1; n < 2; n++){
                                formula.add(temp.get(i+n));
                            }
                            temp.remove(i+1);
                            temp.remove(i);
                            temp.remove(i-1);
                            temp.add(i-1,ShortenFormula(formula,"calculate").get(0));
                            break;
                        }
                    }
                    //run it again
                    temp = ShortenFormula(temp, "search");
                }
                //addition subtraction next
                else if (temp.contains("+")||temp.contains("-")){
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).equals("+")||temp.get(i).equals("-")){
                            //add to temp
                            for (int n = -1; n < 2; n++){
                                formula.add(temp.get(i+n));
                            }
                            temp.remove(i+1);
                            temp.remove(i);
                            temp.remove(i-1);
                            temp.add(i-1,ShortenFormula(formula,"calculate").get(0));
                            break;
                        }
                    }
                }
                break;
            case "roundUp":
                formula.add(ShortenFormula(temp,"search").get(0));
                if (formula.get(0).matches("\\d+(?:\\.\\d+)?")){
                    calc = (float) Math.ceil(Double.parseDouble(formula.get(0)));
                    formula.set(0, String.valueOf(calc));
                } else {
                    formula.set(0, "["+formula.get(0)+"]");
                }
                return formula;
            case "roundDown":
                formula.add(ShortenFormula(temp,"search").get(0));
                if (formula.get(0).matches("\\d+(?:\\.\\d+)?")){
                    calc = (float) Math.floor(Double.parseDouble(formula.get(0)));
                    formula.set(0, String.valueOf(calc));
                } else {
                    formula.set(0, "{"+formula.get(0)+"}");
                }
                return formula;
            case "calculate"://this method will always have 3 parts
                //turn variables into the numbers they represent
                //so the match method returns true for 2.0 and 0002.0000001 but false if .2 or 2. so fixing THAT
                if(temp.get(0).charAt(temp.get(0).length()-1)=='.'){
                    temp.set(0,temp.get(0)+"0");
                } else if (temp.get(0).charAt(0)=='.'){
                    //add 0 to end if last character is "."
                    temp.set(0,"0"+temp.get(0));
                }
                if(temp.get(2).charAt(temp.get(2).length()-1)=='.'){
                    temp.set(0,temp.get(0)+"0");
                } else if (temp.get(2).charAt(0)=='.'){
                    //add 0 to end if last character is "."
                    temp.set(2,"0"+temp.get(2));
                }
                if (!temp.get(0).matches("\\d+(?:\\.\\d+)?")){
                    temp.set(0, String.valueOf(varDict.get(temp.get(0))));
                }
                if (!temp.get(2).matches("\\d+(?:\\.\\d+)?")){
                    temp.set(2, String.valueOf(varDict.get(temp.get(2))));
                }
                switch(temp.get(1)){
                    case "/":
                        calc = Float.parseFloat(temp.get(0))/Float.parseFloat(temp.get(2));
                        formula.add(String.valueOf(calc));
                        return formula;
                    case "*":
                        calc = Float.parseFloat(temp.get(0))*Float.parseFloat(temp.get(2));
                        formula.add(String.valueOf(calc));
                        return formula;
                    case "+":
                        calc = Float.parseFloat(temp.get(0))+Float.parseFloat(temp.get(2));
                        formula.add(String.valueOf(calc));
                        return formula;
                    case "-":
                        calc = Float.parseFloat(temp.get(0))-Float.parseFloat(temp.get(2));
                        formula.add(String.valueOf(calc));
                        return formula;
                }
                break;
        }
        //nothing found so its in its most simplistic state
        return temp;
    }
}
