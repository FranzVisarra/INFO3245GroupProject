package com.example.info3245groupproject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class CalculatorMenu extends AppCompatActivity {

    //private static Pattern alpha ;
    public String thisValue;
    public Dictionary<String, Float> varDict;
    List<String> listItems = new ArrayList<String>();
    ListView variables;
    ArrayAdapter<String> list;
    List<String> curForm;//the current formula on the screen, represented as a list
    int listIndex;


    // all the button
    Button ButtonOne = findViewById(R.id.one);
    Button ButtonTwo = findViewById(R.id.two);
    Button ButtonThree = findViewById(R.id.three);
    Button ButtonFour = findViewById(R.id.four);
    Button ButtonFive = findViewById(R.id.five);
    Button ButtonSix = findViewById(R.id.six);
    Button ButtonSeven = findViewById(R.id.seven);
    Button ButtonEight = findViewById(R.id.eight);
    Button ButtonNine = findViewById(R.id.nine);
    Button ButtonDecimal = findViewById(R.id.decimal);
    Button ButtonLeft = findViewById(R.id.left);
    Button ButtonRight = findViewById(R.id.right);
    Button ButtonZero = findViewById(R.id.zero);
    Button ButtonBracketStart = findViewById(R.id.bracketStart);
    Button ButtonBracketEnd = findViewById(R.id.bracketEnd);
    Button ButtonRoundUpStart = findViewById(R.id.RoundUpStart);
    Button ButtonRoundUpEnd = findViewById(R.id.RoundUpEnd);
    Button ButtonRoundDownStart = findViewById(R.id.RoundDownStart);
    Button ButtonRoundDownEnd = findViewById(R.id.RoundDownEnd);
    Button ButtonPlus = findViewById(R.id.plus);
    Button ButtonMinus = findViewById(R.id.minus);
    Button ButtonTime = findViewById(R.id.times);
    Button ButtonDivide = findViewById(R.id.divide);
    Button ButtonCalcu = findViewById(R.id.Calculate);
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
        //get value
        thisValue = getIntent().getStringExtra("this value");
        listIndex = 0;
        variables = findViewById(R.id.variables);
        list = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        variables.setAdapter(list);
        variables.setClickable(true);
        variables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO add variable to text on screen

            }
        });

    }

    public void ParseFormula(String form)
    {
        List<String> formList;
        formList = StringToList(form);
        //TODO make string get sent to file
        //this is result
        ShortenFormula(formList,"search");

    }
    public List<String> StringToList(String form){
        List<String> formList = new ArrayList<>();
        int position = 0;//start position
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
                formList.add(form.substring(position,i));//end is exclusive so it wont add the current index
                //add special character that was detected
                formList.add(String.valueOf(form.charAt(i)));
                //set position to start from at next index
                position = i+1;
            } else if (i == form.length()-1){//edge case
                formList.add(form.substring(position,i));
            }

        }
        return formList;
    }
    public List<String> ShortenFormula(List<String> temp,String mode)
    {
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
                                //remove the brackets and everything inbetween
                                for (int n = groupStartPos; n <= endPos; n++) {
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
                                for (int n = upStartPos; n <= endPos; n++) {
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
                                for (int n = downStartPos; n <= endPos; n++) {
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
                            temp.remove(i-1);
                            temp.remove(i);
                            temp.remove(i+1);
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
                            temp.remove(i-1);
                            temp.remove(i);
                            temp.remove(i+1);
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

    private Runnable CheckInput = new Runnable() {
        @Override
        public void run() {
            List<String> testList = curForm;
            int bracket=0;
            int roundUp=0;
            int roundDown=0;
            boolean preOp = false;//is the last checked cell an operator
            for (int i = 0; i < testList.size();i++){
                switch (testList.get(i)){
                    case "(":
                        bracket++;
                        break;
                    case ")":
                        bracket--;
                        break;
                    case "[":
                        roundUp++;
                        break;
                    case "]":
                        roundUp--;
                        break;
                    case "{":
                        roundDown++;
                        break;
                    case "}":
                        roundDown--;
                        break;
                    case "/":
                    case "*":
                    case "+":
                    case "-":
                        if (preOp){
                            preOp = true;
                        } else {
                            //TODO warn user they wrong because you shouldn't have duplicate operators e.x. ++ -- /+
                        }
                        break;
                }
            }
            //TODO warn user they wrong because you shouldn't have an open bracket without a close
            if(bracket!=0){
            }
            if(roundUp!=0){
            }
            if(roundDown!=0){
            }
        }
    };
}
