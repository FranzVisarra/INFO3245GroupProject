package com.example.info3245groupproject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;

public class CalculatorMenu extends AppCompatActivity implements View.OnClickListener {

    // only for test
    //private static Pattern alpha ;
    //stored values
    public String statName;
    public float baseValue;
    List<String> child;
    //stored values
    public Dictionary<String, Float> varDict;
    List<String> listItems = new ArrayList<String>();
    ListView variables;
    ArrayAdapter<String> list;
    List<String> curForm;//the current formula on the screen, represented as a list
    public String mode;
    public List<String> formList;//this is what is on the screen
    public int index;


    // All buttons declared
    Button ButtonOne, ButtonTwo, ButtonThree, ButtonFour, ButtonFive, ButtonSix, ButtonSeven, ButtonEight,
            ButtonNine, ButtonDecimal, ButtonLeft, ButtonRight, ButtonZero, ButtonBracketStart, ButtonBracketEnd,
            ButtonRoundUpStart, ButtonRoundUpEnd, ButtonRoundDownStart, ButtonRoundDownEnd, ButtonPlus, ButtonMinus,
            ButtonTime, ButtonDivide, ButtonCalcu;

    private void initViews() {
        // Initialize each button and set the click listener
        ButtonOne = findViewById(R.id.one);
        ButtonTwo = findViewById(R.id.two);
        ButtonThree = findViewById(R.id.three);
        ButtonFour = findViewById(R.id.four);
        ButtonFive = findViewById(R.id.five);
        ButtonSix = findViewById(R.id.six);
        ButtonSeven = findViewById(R.id.seven);
        ButtonEight = findViewById(R.id.eight);
        ButtonNine = findViewById(R.id.nine);
        ButtonDecimal = findViewById(R.id.decimal);
        ButtonLeft = findViewById(R.id.left);
        ButtonRight = findViewById(R.id.right);
        ButtonZero = findViewById(R.id.zero);
        ButtonBracketStart = findViewById(R.id.bracketStart);
        ButtonBracketEnd = findViewById(R.id.bracketEnd);
        ButtonRoundUpStart = findViewById(R.id.RoundUpStart);
        ButtonRoundUpEnd = findViewById(R.id.RoundUpEnd);
        ButtonRoundDownStart = findViewById(R.id.RoundDownStart);
        ButtonRoundDownEnd = findViewById(R.id.RoundDownEnd);
        ButtonPlus = findViewById(R.id.plus);
        ButtonMinus = findViewById(R.id.minus);
        ButtonTime = findViewById(R.id.times);
        ButtonDivide = findViewById(R.id.divide);
        ButtonCalcu = findViewById(R.id.Calculate);

        // Set this class as onClick listener for each button
        ButtonOne.setOnClickListener(this);
        ButtonTwo.setOnClickListener(this);
        ButtonThree.setOnClickListener(this);
        ButtonFour.setOnClickListener(this);
        ButtonFive.setOnClickListener(this);
        ButtonSix.setOnClickListener(this);
        ButtonSeven.setOnClickListener(this);
        ButtonEight.setOnClickListener(this);
        ButtonNine.setOnClickListener(this);
        ButtonDecimal.setOnClickListener(this);
        ButtonLeft.setOnClickListener(this);
        ButtonRight.setOnClickListener(this);
        ButtonZero.setOnClickListener(this);
        ButtonBracketStart.setOnClickListener(this);
        ButtonBracketEnd.setOnClickListener(this);
        ButtonRoundUpStart.setOnClickListener(this);
        ButtonRoundUpEnd.setOnClickListener(this);
        ButtonRoundDownStart.setOnClickListener(this);
        ButtonRoundDownEnd.setOnClickListener(this);
        ButtonPlus.setOnClickListener(this);
        ButtonMinus.setOnClickListener(this);
        ButtonTime.setOnClickListener(this);
        ButtonDivide.setOnClickListener(this);
        ButtonCalcu.setOnClickListener(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calculator_menu);
        initViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get value
        String mode = getIntent().getStringExtra("mode");
        statName = getIntent().getStringExtra("name");
        //TODO move to onstart method
        switch (mode){
            case "add":
                break;
            case "edit":
                baseValue = Float.parseFloat(getIntent().getStringExtra("base value"));
                String tempForm = getIntent().getStringExtra("formula");
                String[] tempChild = getIntent().getStringArrayExtra("child");
                child = Arrays.asList(tempChild);
                curForm = StringToList(tempForm);
                index = curForm.size()-1;
                curForm.add("|");
                break;
        }
        statName = getIntent().getStringExtra("this value");
        index = 0;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();  // Get the ID of the clicked view

        if (id == R.id.Calculate) {
            // TODO check the calculation process
            // Handle the Calculate button: perform calculation
            String formula = curForm.stream().collect(Collectors.joining());
            ParseFormula(formula);
            updateDisplay();
        } else if (id == R.id.plus) {
            curForm.add(index,"+");//this ensures that the index marker "|" is moved forward by inserting at previous
            index++;
        } else if (id == R.id.minus) {
            curForm.add(index,"-");
            index++;
        } else if (id == R.id.times) {
            curForm.add(index,"*");
            index++;
        } else if (id == R.id.divide) {
            curForm.add(index,"/");
            index++;
        } else if (id == R.id.bracketStart) {
            curForm.add(index,"(");
            index++;
        } else if (id == R.id.bracketEnd) {
            curForm.add(index,")");
            index++;
        } else if (id == R.id.RoundUpStart) {
            curForm.add(index,"[");
            index++;
        } else if (id == R.id.RoundUpEnd) {
            curForm.add(index,"]");
            index++;
        } else if (id == R.id.RoundDownStart) {
            curForm.add(index,"{");
            index++;
        } else if (id == R.id.RoundDownEnd) {
            curForm.add(index,"}");
            index++;
        } else if (id == R.id.left) {
            if (index > 0) {
                curForm.remove("|");
                index++;
                curForm.add(index, "|");
            }
        } else if (id == R.id.right) {
            if (index < curForm.size()-1){
                curForm.remove("|");
                index--;
                curForm.add(index,"|");
                curForm.add(">");
            }
        } else if (id == R.id.zero || id == R.id.one || id == R.id.two || id == R.id.three ||
                id == R.id.four || id == R.id.five || id == R.id.six || id == R.id.seven ||
                id == R.id.eight || id == R.id.nine || id == R.id.decimal) {

            //TODO Handle number and decimal button presses
            //TODO if   a | if index -1 is less than 0 //you dont want to end up at index-1
            //TODO if   b | if index - 1 is number
            //TODO step 1: add new input to the previous index, like making 2 and 2 be 22
            //TODO else b | else if index-1 is decimal
            //TODO if   c | if this input is decimal
            //TODO step 1: do nothing because cant double decimal
            //TODO else c |
            //TODO step 1: do nothing again
            //TODO else a |
            //TODO step 1: add this variable as list item
            Button b = (Button) v;
            String input = b.getText().toString();
            if (index <= 0 || curForm.isEmpty()) { // Check if there is no previous index or the list is empty
                // Case 'a': No valid previous index
                curForm.add(index,input); // Add input as a new item at the beginning or in an empty list
                index++; // Update index to new size
            } else {
                String lastEntry = curForm.get(index - 1);
                if (Character.isDigit(lastEntry.charAt(lastEntry.length() - 1))) { // Check if the last entry ends with a number
                    // Case 'b': Last entry is a number
                    if (input.equals(".")) {
                        if (lastEntry.contains(".")) {
                            // Case 'c': Input is a decimal and last entry already contains a decimal
                            // Do nothing to prevent double decimals
                        } else {
                            // Add decimal to the existing number
                            curForm.set(index - 1, lastEntry + input);
                        }
                    } else {
                        // Append the number to form a larger number (e.g., 2 and 2 to form 22)
                        curForm.set(index - 1, lastEntry + input);
                    }
                } else if (lastEntry.equals(".") && input.equals(".")) {
                    // Case 'b' and 'c' together: last entry is decimal and input is also decimal
                    // Do nothing to prevent double decimals
                } else {
                    // Case 'else a': Last entry is not a number suitable for combining
                    curForm.add(index, input); // Add the input as a new separate item
                    index++; // Move index forward to after the new entry
                }
            }
        }


        // update the display after modifying curForm
        updateDisplay();
    }



    public void ParseFormula(String form)
    {
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

    private void updateDisplay() {
        TextView display = findViewById(R.id.ShowFomula);
        StringBuilder displayText = new StringBuilder();
        for (String s : curForm) {
            displayText.append(s);
        }
        display.setText(displayText.toString());  // Update the TextView to show the current formula
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

                            runOnUiThread(() -> Toast.makeText(CalculatorMenu.this, "Error: Consecutive operators are not allowed.", Toast.LENGTH_LONG).show());
                        }
                            preOp = true;
                            break;
                            //TODO warn user they wrong because you shouldn't have duplicate operators e.x. ++ -- /+

                    default:
                        preOp = false;
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
