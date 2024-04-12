package com.example.info3245groupproject;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class TestCalculatorMenu extends AppCompatActivity {
    public Dictionary<String, Float> varDict;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calculator_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        testShorteningFormula();
    }
    public void testShorteningFormula(){
        List<String> testList = new ArrayList<>();
        testList.add("4");
        testList.add("/");
        testList.add("2");
        System.out.println(testList+"="+ShortenFormula(testList,"search"));
        String testString = "[2/3]+1";
        System.out.println(ShortenFormula(StringToList(testString),"search"));
        testString = "(3+4)/2";
        System.out.println(ShortenFormula(StringToList(testString),"search"));

    }
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
    public List<String> ShortenFormula(List<String> temp,String mode)
    {
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
                                for (int n = endPos; n >= groupStartPos; n--) {
                                    temp.remove(n);
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
                                for (int n = endPos; n >= upStartPos; n--) {
                                    temp.remove(n);
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
                                for (int n = endPos; n >= downStartPos; n--) {
                                    temp.remove(n);
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
                /*this stays here in case we ever make it so input can be invalid
                if (!temp.get(0).matches("\\d+(?:\\.\\d+)?") ||
                    !temp.get(2).matches("\\d+(?:\\.\\d+)?")){
                    //TODO check for variables then substitute them on an if else
                    varDict.get(temp.get(0));
                    //add brackets to the string if it cannot be separated into the calculation
                    formula.add("("+temp.get(0)+temp.get(1)+temp.get(2)+")");
                    return formula;
                }
                 */
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
